package com.example.sispas.service.impl;

import com.example.sispas.dto.CommentDTO;
import com.example.sispas.dto.RateDTO;
import com.example.sispas.dto.ReviewDTO;
import com.example.sispas.model.*;
import com.example.sispas.repository.*;
import com.example.sispas.service.CommentService;
import com.example.sispas.service.RateService;
import com.example.sispas.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ExerciseRepository exerciseRepository;
    private final RateService rateService;
    private final CommentService commentService;
    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final RateRepository rateRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ExerciseRepository exerciseRepository,
                             RateService rateService, CommentService commentService,
                             UserRepository userRepository, FacilityRepository facilityRepository,
                             RateRepository rateRepository, CommentRepository commentRepository) {
        this.reviewRepository = reviewRepository;
        this.exerciseRepository = exerciseRepository;
        this.rateService = rateService;
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.facilityRepository = facilityRepository;
        this.rateRepository = rateRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public boolean userHasExercises(Long userId, Long facilityId) {
        return !exerciseRepository.findByUserIdAndFacilityId(userId, facilityId).isEmpty();
    }

    @Override
    public boolean userHasReview(Long userId, Long facilityId) {
        return reviewRepository.findByUserIdAndFacilityIdAndIsDeletedFalse(userId, facilityId).isPresent();
    }

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Review review = new Review();
        review.setCreatedAt(LocalDateTime.now());
        review.setExerciseCount(reviewDTO.getExerciseCount());
        review.setHidden(false);
        review.setDeleted(false);
        review.setComments(new ArrayList<>());

        Optional<User> userOptional = userRepository.findById(reviewDTO.getUserId());
        Optional<Facility> facilityOptional = facilityRepository.findById(reviewDTO.getFacilityId());

        if (userOptional.isPresent() && facilityOptional.isPresent()) {
            review.setUser(userOptional.get());
            review.setFacility(facilityOptional.get());
        } else {
            throw new IllegalArgumentException("User or Facility not found");
        }

        Review savedReview = reviewRepository.save(review);

        RateDTO rateDTO = reviewDTO.getRateDTO();
        if (rateDTO != null) {
            rateDTO.setReviewId(savedReview.getId());
            Rate rate = rateService.toEntity(rateDTO);
            rate.setReview(savedReview);
            Rate savedRate = rateRepository.save(rate);
            savedReview.setRate(savedRate);
        }

        if (reviewDTO.getComments() != null && !reviewDTO.getComments().isEmpty()) {
            CommentDTO commentDTO = reviewDTO.getComments().get(0);
            commentDTO.setReviewId(savedReview.getId());
            Comment comment = commentService.toEntity(commentDTO);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setReview(savedReview);
            Comment savedComment = commentRepository.save(comment);
            savedReview.getComments().add(savedComment);
        }

        savedReview = reviewRepository.save(savedReview);

        return toDTO(savedReview);
    }

    @Override
    public Optional<ReviewDTO> getReviewByUserIdAndFacilityId(Long userId, Long facilityId) {
        Optional<Review> review = reviewRepository.findByUserIdAndFacilityIdAndIsDeletedFalse(userId, facilityId);
        return review.map(this::toDTO);
    }

    @Override
    public List<ReviewDTO> getReviewsForFacility(Long facilityId) {
        List<Review> reviews = reviewRepository.findByFacilityIdAndIsDeletedFalse(facilityId);
        return reviews.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Map<String, Double> getAverageRatingsForFacility(Long facilityId) {
        var reviews = reviewRepository.findByFacilityIdAndIsDeletedFalse(facilityId);
        if (reviews.isEmpty()) {
            return null;
        }

        double totalEquipmentRating = 0;
        double totalStaffRating = 0;
        double totalHygieneRating = 0;
        double totalSpaceRating = 0;
        int count = 0;

        for (Review review : reviews) {
            Rate rate = review.getRate();
            if (rate != null) {
                totalEquipmentRating += rate.getEquipment();
                totalStaffRating += rate.getStaff();
                totalHygieneRating += rate.getHygiene();
                totalSpaceRating += rate.getSpace();
                count++;
            }
        }

        double averageEquipmentRating = totalEquipmentRating / count;
        double averageStaffRating = totalStaffRating / count;
        double averageHygieneRating = totalHygieneRating / count;
        double averageSpaceRating = totalSpaceRating / count;
        double overallAverageRating = (averageEquipmentRating + averageStaffRating + averageHygieneRating + averageSpaceRating) / 4;

        Optional<Facility> facilityOptional = facilityRepository.findById(facilityId);
        if (facilityOptional.isPresent()) {
            Facility facility = facilityOptional.get();
            facility.setRating(overallAverageRating);
            facilityRepository.save(facility);
        }

        Map<String, Double> averageRatings = new HashMap<>();
        averageRatings.put("equipment", averageEquipmentRating);
        averageRatings.put("staff", averageStaffRating);
        averageRatings.put("hygiene", averageHygieneRating);
        averageRatings.put("space", averageSpaceRating);
        averageRatings.put("overall", overallAverageRating);

        return averageRatings;
    }

    @Override
    public Page<ReviewDTO> getAllReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllAndIsDeletedFalse(pageable);
        return reviews.map(this::toDTO);
    }

    @Override
    public Optional<ReviewDTO> getReviewById(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        return review.map(this::toDTO);
    }

    @Override
    public Page<ReviewDTO> getAllReviewsByFacilityID(Long facilityId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByFacilityIdAndIsDeletedFalse(facilityId, pageable);
        return reviews.map(this::toDTO);
    }

    @Override
    public Page<ReviewDTO> getReviewsByFacilityIds(List<Long> facilityIds, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByFacilityIdInAndIsDeletedFalse(facilityIds, pageable);
        return reviews.map(this::toDTO);
    }

    @Override
    public void hideReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setHidden(true);
        reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        review.setDeleted(true);

        // Find and mark Rate as deleted
        Rate rate = rateRepository.findByReviewId(reviewId);
        if (rate != null) {
            rate.setIsDeleted(true);
            rateRepository.save(rate);
        }

        // Find and mark Comments as deleted
        List<Comment> comments = commentRepository.findByReviewId(reviewId);
        for (Comment comment : comments) {
            comment.setIsDeleted(true);
            commentRepository.save(comment);
        }

        reviewRepository.save(review);
    }



    private ReviewDTO toDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setCreatedAt(review.getCreatedAt());
        reviewDTO.setExerciseCount(review.getExerciseCount());
        reviewDTO.setHidden(review.isHidden());
        reviewDTO.setDeleted(review.isDeleted());
        reviewDTO.setUserId(review.getUser().getId());
        reviewDTO.setFacilityId(review.getFacility().getId());
        if (review.getRate() != null) {
            reviewDTO.setRateDTO(rateService.toDTO(review.getRate()));
        }
        if (review.getComments() != null) {
            reviewDTO.setComments(review.getComments().stream()
                    .map(commentService::toDTO)
                    .collect(Collectors.toList()));
        } else {
            reviewDTO.setComments(Collections.emptyList());
        }
        return reviewDTO;
    }
}
