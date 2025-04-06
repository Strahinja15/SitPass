package com.example.sispas.repository;

import com.example.sispas.model.DaysOfWeek;
import com.example.sispas.model.Workday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkdayRepository extends JpaRepository<Workday, Long> {
    List<Workday> findByFacilityId(Long facilityId);
    Workday findByDaysAndFacilityId(DaysOfWeek days, Long facilityId);
}
