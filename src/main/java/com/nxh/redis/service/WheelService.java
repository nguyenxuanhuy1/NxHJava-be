package com.nxh.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxh.redis.dto.*;
import com.nxh.redis.entity.SpinHistory;
import com.nxh.redis.entity.Wheel;
import com.nxh.redis.repository.SpinHistoryRepository;
import com.nxh.redis.repository.WheelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class WheelService {

    private final WheelRepository wheelRepository;
    private final SpinHistoryRepository spinHistoryRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // Key pattern trong Redis để lưu preset: "wheel:preset:{wheelId}"
    private static final String REDIS_PRESET_KEY = "wheel:preset:";

    // ===================== WHEEL CRUD =====================

    /**
     * Tạo vòng quay mới
     */
    @Transactional
    public WheelResponse createWheel(CreateWheelRequest request) {
        String itemsJson = toJson(request.getItems());
        Wheel wheel = Wheel.builder()
                .name(request.getName())
                .itemsJson(itemsJson)
                .build();
        wheel = wheelRepository.save(wheel);
        log.info("Created wheel id={} name={}", wheel.getId(), wheel.getName());
        return toWheelResponse(wheel);
    }

    /**
     * Lấy thông tin vòng quay theo ID
     */
    public WheelResponse getWheel(Long wheelId) {
        Wheel wheel = findWheelById(wheelId);
        // Lấy preset từ Redis (nếu có)
        String preset = redisTemplate.opsForValue().get(REDIS_PRESET_KEY + wheelId);
        wheel.setPresetResult(preset);
        return toWheelResponse(wheel);
    }

    /**
     * Cập nhật danh sách items của vòng quay
     */
    @Transactional
    public WheelResponse updateItems(Long wheelId, UpdateItemsRequest request) {
        Wheel wheel = findWheelById(wheelId);
        wheel.setItemsJson(toJson(request.getItems()));
        wheel = wheelRepository.save(wheel);
        log.info("Updated items for wheel id={}", wheelId);
        return toWheelResponse(wheel);
    }

    /**
     * Lấy tất cả vòng quay
     */
    public List<WheelResponse> getAllWheels() {
        return wheelRepository.findAll().stream()
                .map(this::toWheelResponse)
                .toList();
    }

    /**
     * Xóa vòng quay
     */
    @Transactional
    public void deleteWheel(Long wheelId) {
        findWheelById(wheelId); // check tồn tại
        wheelRepository.deleteById(wheelId);
        redisTemplate.delete(REDIS_PRESET_KEY + wheelId);
        log.info("Deleted wheel id={}", wheelId);
    }

    // ===================== PRESET (ADMIN) =====================

    /**
     * [Admin] Đặt trước kết quả cho lượt quay tiếp theo của vòng quay có ID = wheelId.
     * Kết quả preset được lưu vào Redis.
     * Ví dụ: admin đẩy vào "huy" thì lượt quay tiếp theo chắc chắn ra "huy".
     */
    public void setPreset(Long wheelId, PresetResultRequest request) {
        Wheel wheel = findWheelById(wheelId);

        // Kiểm tra xem "result" có nằm trong danh sách items không
        List<String> items = fromJson(wheel.getItemsJson());
        if (!items.contains(request.getResult())) {
            throw new IllegalArgumentException(
                    "Kết quả preset '" + request.getResult() + "' không nằm trong danh sách items của vòng quay."
            );
        }

        // Lưu vào DB MySQL/Postgres
        wheel.setPresetResult(request.getResult());
        wheelRepository.save(wheel);

        redisTemplate.opsForValue().set(REDIS_PRESET_KEY + wheelId, request.getResult());
        log.info("[Admin] Set preset for wheel id={} -> result='{}'", wheelId, request.getResult());
    }

    /**
     * [Admin] Xoá preset - lượt sau sẽ quay random
     */
    public void clearPreset(Long wheelId) {
        Wheel wheel = findWheelById(wheelId);
        
        // Xoá trong DB
        wheel.setPresetResult(null);
        wheelRepository.save(wheel);

        redisTemplate.delete(REDIS_PRESET_KEY + wheelId);
        log.info("[Admin] Cleared preset for wheel id={}", wheelId);
    }

    // ===================== SPIN =====================

    /**
     * Thực hiện quay vòng quay:
     * - Nếu có preset trong Redis -> trả về preset đó và xóa preset
     * - Nếu không -> random ngẫu nhiên trong danh sách items
     */
    @Transactional
    public SpinResponse spin(Long wheelId) {
        Wheel wheel = findWheelById(wheelId);
        List<String> items = fromJson(wheel.getItemsJson());

        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Vòng quay id=" + wheelId + " chưa có items nào.");
        }

        String presetKey = REDIS_PRESET_KEY + wheelId;
        String presetValue = redisTemplate.opsForValue().get(presetKey);

        String result;
        boolean wasPreset;

        if (presetValue != null) {
            // Có preset -> dùng preset và xóa đi (chỉ dùng 1 lần)
            result = presetValue;
            wasPreset = true;

            // Xoá trong DB ngay lập tức
            wheel.setPresetResult(null);
            wheelRepository.save(wheel);

            redisTemplate.delete(presetKey);
            log.info("Wheel id={} spin -> PRESET result='{}'", wheelId, result);
        } else {
            // Không có preset -> random
            result = items.get(new Random().nextInt(items.size()));
            wasPreset = false;
            log.info("Wheel id={} spin -> RANDOM result='{}'", wheelId, result);
        }

        // Lưu lịch sử
        SpinHistory history = SpinHistory.builder()
                .wheelId(wheelId)
                .result(result)
                .wasPreset(wasPreset)
                .build();
        spinHistoryRepository.save(history);

        return SpinResponse.builder()
                .wheelId(wheelId)
                .result(result)
                .wasPreset(wasPreset)
                .spunAt(LocalDateTime.now())
                .build();
    }

    // ===================== HISTORY =====================

    /**
     * Lấy lịch sử quay của vòng quay
     */
    public List<SpinHistoryResponse> getHistory(Long wheelId) {
        findWheelById(wheelId);
        return spinHistoryRepository.findByWheelIdOrderBySpunAtDesc(wheelId).stream()
                .map(h -> SpinHistoryResponse.builder()
                        .id(h.getId())
                        .wheelId(h.getWheelId())
                        .result(h.getResult())
                        .wasPreset(h.isWasPreset())
                        .spunAt(h.getSpunAt())
                        .build())
                .toList();
    }

    // ===================== HELPERS =====================

    private Wheel findWheelById(Long wheelId) {
        return wheelRepository.findById(wheelId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vòng quay với id=" + wheelId));
    }

    private WheelResponse toWheelResponse(Wheel wheel) {
        String preset = redisTemplate.opsForValue().get(REDIS_PRESET_KEY + wheel.getId());
        return WheelResponse.builder()
                .id(wheel.getId())
                .name(wheel.getName())
                .items(fromJson(wheel.getItemsJson()))
                .presetResult(preset)
                .createdAt(wheel.getCreatedAt())
                .updatedAt(wheel.getUpdatedAt())
                .build();
    }

    private String toJson(List<String> items) {
        try {
            return objectMapper.writeValueAsString(items != null ? items : new ArrayList<>());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi chuyển đổi items sang JSON", e);
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi đọc items từ JSON", e);
        }
    }
}
