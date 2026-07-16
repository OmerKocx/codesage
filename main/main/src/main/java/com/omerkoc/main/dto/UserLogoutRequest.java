package com.omerkoc.main.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Kullanıcının sistemden güvenli bir şekilde çıkış yapabilmesi (logout) için
 * gerekli olan Refresh Token bilgisini taşıyan DTO sınıfıdır.
 */
@Data
public class UserLogoutRequest {

    @NotBlank(message = "Refresh token cannot be empty")
    private String refreshToken;
}
