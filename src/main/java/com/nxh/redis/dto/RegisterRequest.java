package com.nxh.redis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Tài khoản không được để trống")
    @Size(min = 3, max = 9, message = "Tài khoản phải từ 3 đến 9 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Tài khoản không được chứa ký tự đặc biệt")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 9, message = "Mật khẩu phải từ 6 đến 9 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Mật khẩu không được chứa ký tự đặc biệt")
    private String password;
}
