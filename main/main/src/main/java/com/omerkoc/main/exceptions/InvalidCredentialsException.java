package com.omerkoc.main.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Kullanıcı giriş işlemlerinde hatalı kimlik bilgileri (kullanıcı adı veya şifre)
 * girildiğinde fırlatılan özel istisna sınıfıdır.
 */
public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
