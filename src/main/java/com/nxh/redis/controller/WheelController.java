package com.nxh.redis.controller;

import com.nxh.redis.dto.*;
import com.nxh.redis.service.WheelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/wheels")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class WheelController {

    private final WheelService wheelService;

    // ============ WHEEL CRUD ============

    /**
     * Tạo vòng quay mới
     * POST /api/wheels
     * Body: { "name": "Vòng quay 1", "items": ["huy", "nam", "lan", "minh"] }
     */
    @PostMapping
    public ResponseEntity<WheelResponse> createWheel(@RequestBody CreateWheelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wheelService.createWheel(request));
    }

    /**
     * Lấy thông tin vòng quay (bao gồm preset hiện tại nếu có)
     * GET /api/wheels/{wheelId}
     */
    @GetMapping("/{wheelId}")
    public ResponseEntity<WheelResponse> getWheel(@PathVariable Long wheelId) {
        return ResponseEntity.ok(wheelService.getWheel(wheelId));
    }

    /**
     * Lấy danh sách vòng quay (không có items, dùng GET /api/wheels/{wheelId} để xem đầy đủ).
     * - Không truyền id: trả 10 vòng quay mới nhất
     * - Truyền id: trả únh vòng quay có id đó (tìm kiếm)
     * GET /api/wheels
     * GET /api/wheels?id=5
     */
    @GetMapping
    public ResponseEntity<List<WheelSummaryResponse>> getAllWheels(
            @RequestParam(required = false) Long id
    ) {
        return ResponseEntity.ok(wheelService.getAllWheels(id));
    }

    /**
     * Cập nhật danh sách items
     * PUT /api/wheels/{wheelId}/items
     * Body: { "items": ["huy", "nam", "lan", "minh", "tuan"] }
     */
    @PutMapping("/{wheelId}/items")
    public ResponseEntity<WheelResponse> updateItems(
            @PathVariable Long wheelId,
            @RequestBody UpdateItemsRequest request
    ) {
        return ResponseEntity.ok(wheelService.updateItems(wheelId, request));
    }

    /**
     * Xóa vòng quay
     * DELETE /api/wheels/{wheelId}
     */
    @DeleteMapping("/{wheelId}")
    public ResponseEntity<Void> deleteWheel(@PathVariable Long wheelId) {
        wheelService.deleteWheel(wheelId);
        return ResponseEntity.noContent().build();
    }

    // ============ PRESET (ADMIN) ============

    /**
     * [Admin] Đặt trước kết quả lượt quay tiếp theo
     * POST /api/wheels/{wheelId}/preset
     * Body: { "result": "huy" }
     *
     * => Lượt quay tiếp theo của vòng quay này CHẮC CHẮN ra "huy"
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{wheelId}/preset")
    public ResponseEntity<String> setPreset(
            @PathVariable Long wheelId,
            @RequestBody PresetResultRequest request
    ) {
        wheelService.setPreset(wheelId, request);
        return ResponseEntity.ok("Đã đặt preset cho vòng quay id=" + wheelId + " -> '" + request.getResult() + "'");
    }

    /**
     * [Admin] Xoá preset - lượt sau sẽ quay ngẫu nhiên
     * DELETE /api/wheels/{wheelId}/preset
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{wheelId}/preset")
    public ResponseEntity<String> clearPreset(@PathVariable Long wheelId) {
        wheelService.clearPreset(wheelId);
        return ResponseEntity.ok("Đã xoá preset của vòng quay id=" + wheelId + ". Lượt sau sẽ random.");
    }

    // ============ SPIN ============

    /**
     * Thực hiện quay vòng quay
     * POST /api/wheels/{wheelId}/spin
     *
     * Logic:
     * - Nếu có preset (admin đặt trước) -> trả về preset, xoá preset
     * - Nếu không có preset -> random trong danh sách items
     */
    @PostMapping("/{wheelId}/spin")
    public ResponseEntity<SpinResponse> spin(@PathVariable Long wheelId) {
        return ResponseEntity.ok(wheelService.spin(wheelId));
    }

    // ============ HISTORY ============

    /**
     * Lấy lịch sử các lượt quay của vòng quay
     * GET /api/wheels/{wheelId}/history
     */
    @GetMapping("/{wheelId}/history")
    public ResponseEntity<List<SpinHistoryResponse>> getHistory(@PathVariable Long wheelId) {
        return ResponseEntity.ok(wheelService.getHistory(wheelId));
    }
}
