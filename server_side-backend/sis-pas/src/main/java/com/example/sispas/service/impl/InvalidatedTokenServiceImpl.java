package com.example.sispas.service.impl;

import com.example.sispas.service.InvalidatedTokenService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class InvalidatedTokenServiceImpl implements InvalidatedTokenService {

    private Set<String> invalidatedTokens = new HashSet<>();

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }
}
