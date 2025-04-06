package com.example.sispas.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ActivationRequestDTO {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
}
