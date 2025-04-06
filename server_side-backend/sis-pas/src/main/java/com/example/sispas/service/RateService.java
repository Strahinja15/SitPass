package com.example.sispas.service;

import com.example.sispas.dto.RateDTO;
import com.example.sispas.model.Rate;

public interface RateService {
    RateDTO createRate(RateDTO rateDTO);
    RateDTO toDTO(Rate rate);
    Rate toEntity(RateDTO rateDTO);
}
