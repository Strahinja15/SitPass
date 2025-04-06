package com.example.sispas.service.impl;

import com.example.sispas.dto.RateDTO;
import com.example.sispas.model.Rate;
import com.example.sispas.model.Review;
import com.example.sispas.repository.RateRepository;
import com.example.sispas.repository.ReviewRepository;
import com.example.sispas.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RateServiceImpl implements RateService {
    private final RateRepository rateRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public RateServiceImpl(RateRepository rateRepository, ReviewRepository reviewRepository) {
        this.rateRepository = rateRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public RateDTO createRate(RateDTO rateDTO) {
        Rate rate = new Rate();
        rate.setEquipment(rateDTO.getEquipment());
        rate.setStaff(rateDTO.getStaff());
        rate.setHygiene(rateDTO.getHygiene());
        rate.setSpace(rateDTO.getSpace());
        rate.setIsDeleted(false);

        Optional<Review> review = reviewRepository.findById(rateDTO.getReviewId());

        if (review.isPresent()) {
            rate.setReview(review.get());
        } else {
            throw new IllegalArgumentException("Review not found");
        }

        Rate savedRate = rateRepository.save(rate);
        return toDTO(savedRate);
    }

    public RateDTO toDTO(Rate rate) {
        RateDTO rateDTO = new RateDTO();
        rateDTO.setId(rate.getId());
        rateDTO.setEquipment(rate.getEquipment());
        rateDTO.setStaff(rate.getStaff());
        rateDTO.setHygiene(rate.getHygiene());
        rateDTO.setSpace(rate.getSpace());
        rateDTO.setReviewId(rate.getReview().getId());
        rateDTO.setDeleted(rate.isIsDeleted());
        return rateDTO;
    }

    public Rate toEntity(RateDTO rateDTO) {
        Rate rate = new Rate();
        rate.setId(rateDTO.getId());
        rate.setEquipment(rateDTO.getEquipment());
        rate.setStaff(rateDTO.getStaff());
        rate.setHygiene(rateDTO.getHygiene());
        rate.setSpace(rateDTO.getSpace());
        rate.setIsDeleted(rateDTO.isDeleted());
        Optional<Review> review = reviewRepository.findById(rateDTO.getReviewId());
        review.ifPresent(rate::setReview);
        return rate;
    }
}

