package com.example.sispas.repository;

import com.example.sispas.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, Long> {
    Rate findByReviewId(Long reviewId);
}
