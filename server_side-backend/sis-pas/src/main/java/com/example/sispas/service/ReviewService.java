package com.example.sispas.service;

import com.example.sispas.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewService {
    boolean userHasExercises(Long userId, Long facilityId);
    boolean userHasReview(Long userId, Long facilityId);
    ReviewDTO createReview(ReviewDTO reviewDTO);
    Optional<ReviewDTO> getReviewByUserIdAndFacilityId(Long userId, Long facilityId);
    Map<String, Double> getAverageRatingsForFacility(Long facilityId);
    List<ReviewDTO> getReviewsForFacility(Long facilityId);
    Page<ReviewDTO> getAllReviews(Pageable pageable);
    Page<ReviewDTO> getAllReviewsByFacilityID(Long facilityId, Pageable pageable);
    Page<ReviewDTO> getReviewsByFacilityIds(List<Long> facilityIds, Pageable pageable);
    void hideReview(Long reviewId);
    void deleteReview(Long reviewId);
    Optional<ReviewDTO> getReviewById(Long reviewId);
}

