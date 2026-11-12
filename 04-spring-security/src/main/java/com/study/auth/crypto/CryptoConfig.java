package com.study.auth.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class CryptoConfig {

    @Bean
    public AesGcmCipher aesGcmCipher(@Value("${app.crypto.aes-key}") String base64Key) {
        return new AesGcmCipher(Base64.getDecoder().decode(base64Key));
    }

    @Bean
    public BlindIndex blindIndex(@Value("${app.crypto.hmac-key}") String base64Key) {
        return new BlindIndex(Base64.getDecoder().decode(base64Key));
    }
}
