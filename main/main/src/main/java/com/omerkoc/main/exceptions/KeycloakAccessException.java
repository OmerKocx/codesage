package com.omerkoc.main.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Keycloak sunucusu ile iletişim sırasında veya kimlik doğrulama süreçlerinde
 * meydana gelen hataları temsil eden özel istisna (exception) sınıfıdır.
 */
public class KeycloakAccessException extends BaseException {

    public KeycloakAccessException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }

    public KeycloakAccessException(String message, HttpStatus status) {
        super(message, status);
    }
}
