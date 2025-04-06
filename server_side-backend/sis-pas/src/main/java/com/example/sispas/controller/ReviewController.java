package com.example.sispas.controller;

import com.example.sispas.dto.ReviewDTO;
import com.example.sispas.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @PostMapping("/createReview")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        boolean hasExercises = reviewService.userHasExercises(reviewDTO.getUserId(), reviewDTO.getFacilityId());
        if (!hasExercises) {
            return ResponseEntity.badRequest().build();
        }

        boolean hasReview = reviewService.userHasReview(reviewDTO.getUserId(), reviewDTO.getFacilityId());
        if (hasReview) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        ReviewDTO createdReview = reviewService.createReview(reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator', 'ROLE_user')")
    @GetMapping("/user/{userId}/facility/{facilityId}")
    public ResponseEntity<ReviewDTO> getReviewByUserIdAndFacilityId(@PathVariable Long userId, @PathVariable Long facilityId) {
        Optional<ReviewDTO> reviewDTO = reviewService.getReviewByUserIdAndFacilityId(userId, facilityId);
        return reviewDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasAnyRole('ROLE_administrator', 'ROLE_user')")
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsForFacility(@PathVariable Long facilityId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForFacility(facilityId);
        return ResponseEntity.ok(reviews);
    }
    @PreAuthorize("hasAnyRole('ROLE_administrator', 'ROLE_user')")
    @GetMapping("/{facilityId}/average-ratings")
    public ResponseEntity<Map<String, Double>> getAverageRatings(@PathVariable Long facilityId) {
        Map<String, Double> averageRatings = reviewService.getAverageRatingsForFacility(facilityId);
        if (averageRatings != null) {
            return ResponseEntity.ok(averageRatings);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_administrator')")
    @GetMapping("/all")
    public ResponseEntity<Page<ReviewDTO>> getAllReviews(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator', 'ROLE_user')")
    @GetMapping("/allById/{facilityId}")
    public ResponseEntity<Page<ReviewDTO>> getAllReviewsByFacilityId(@PathVariable Long facilityId, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getAllReviewsByFacilityID(facilityId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasAnyRole('ROLE_administrator', 'ROLE_user')")
    @GetMapping("/facility/reviews")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByFacilityIds(
            @RequestParam List<Long> facilityIds,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getReviewsByFacilityIds(facilityIds, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewId}/hide")
    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    public ResponseEntity<Void> hideReview(@PathVariable Long reviewId) {
        Optional<ReviewDTO> reviewDTO = reviewService.getReviewById(reviewId);
        if (reviewDTO.isPresent()) {
            reviewService.hideReview(reviewId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{reviewId}/delete")
    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        Optional<ReviewDTO> reviewDTO = reviewService.getReviewById(reviewId);
        if (reviewDTO.isPresent()) {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
