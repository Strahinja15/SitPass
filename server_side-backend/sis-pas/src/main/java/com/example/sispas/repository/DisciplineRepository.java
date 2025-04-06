package com.example.sispas.repository;

import com.example.sispas.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, Long> {
    List<Discipline> findByFacilityId(Long facilityId);

}
