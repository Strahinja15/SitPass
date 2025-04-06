package com.example.sispas.repository;

import com.example.sispas.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserIdAndFacilityIdAndIsDeletedFalse(Long userId, Long facilityId);
    List<Review> findByFacilityIdAndIsDeletedFalse(Long facilityId);

    @Query("SELECT r FROM Review r WHERE r.isDeleted = false")
    Page<Review> findAllAndIsDeletedFalse(Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.facility.id = :facilityId AND r.isDeleted = false")
    Page<Review> findAllByFacilityIdAndIsDeletedFalse(@Param("facilityId") Long facilityId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.facility.id IN :facilityIds AND r.isDeleted = false")
    Page<Review> findAllByFacilityIdInAndIsDeletedFalse(@Param("facilityIds") List<Long> facilityIds, Pageable pageable);
}

