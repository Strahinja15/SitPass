package com.example.sispas.service;

import com.example.sispas.dto.FacilityDTO;
import java.time.LocalDate;
import java.util.List;

public interface FacilityService {
    FacilityDTO createFacility(FacilityDTO facilityDTO);
    FacilityDTO getFacilityById(Long id);
    List<FacilityDTO> getAllFacilities();
    FacilityDTO deactivateFacility(Long id);
    FacilityDTO activateFacility(Long id, Long userId, LocalDate startDate, LocalDate endDate);
    FacilityDTO updateFacility(Long id, FacilityDTO facilityDTO);
    FacilityDTO deleteFacility(Long id);
    boolean isManagerOfFacility(Long facilityId, Long userId);
    List<FacilityDTO> filterFacilities(String city, String discipline, Double minRating, Double maxRating, String workday);
    List<Long> findFacilityIdsByManagerId(Long userId);
}
