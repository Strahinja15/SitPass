package com.example.sispas.controller;

import com.example.sispas.dto.RateDTO;
import com.example.sispas.model.Rate;
import com.example.sispas.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rates")
public class RateController {
    private final RateService rateService;

    @Autowired
    public RateController(RateService rateService) {
        this.rateService = rateService;
    }
    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @PostMapping("/createRate")
    public ResponseEntity<RateDTO> createRate(@RequestBody RateDTO rateDTO) {
        try {
            RateDTO rate = rateService.createRate(rateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(rate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
