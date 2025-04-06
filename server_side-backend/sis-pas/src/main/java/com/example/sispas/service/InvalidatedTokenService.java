package com.example.sispas.service;

public interface InvalidatedTokenService {
    void invalidateToken(String token);
    boolean isTokenInvalidated(String token);
}
