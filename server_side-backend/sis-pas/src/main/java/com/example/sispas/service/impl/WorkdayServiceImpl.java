package com.example.sispas.service.impl;

import com.example.sispas.dto.WorkdaysDTO;
import com.example.sispas.model.DaysOfWeek;
import com.example.sispas.model.Facility;
import com.example.sispas.model.Workday;
import com.example.sispas.repository.FacilityRepository;
import com.example.sispas.repository.WorkdayRepository;
import com.example.sispas.service.WorkdayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkdayServiceImpl implements WorkdayService {

    private final WorkdayRepository workdayRepository;
    private  final FacilityRepository facilityRepository;

    @Autowired
    public WorkdayServiceImpl(WorkdayRepository workdayRepository, FacilityRepository facilityRepository) {
        this.workdayRepository = workdayRepository;
        this.facilityRepository = facilityRepository;
    }

    @Override
    public List<WorkdaysDTO> getWorkdaysByFacilityId(Long facilityId) {
        List<Workday> workdays = workdayRepository.findByFacilityId(facilityId);
        return workdays.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public WorkdaysDTO createWorkday(WorkdaysDTO workdayDTO) {
        if (workdayDTO.getFacilityId() == null) {
            throw new IllegalArgumentException("Facility ID must not be null");
        }

        Facility facility = facilityRepository.findById(workdayDTO.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        // Check if the day already exists for the facility
        DaysOfWeek newWorkdayDay = DaysOfWeek.valueOf(workdayDTO.getDays());
        boolean dayExists = workdayRepository.findByFacilityId(facility.getId()).stream()
                .anyMatch(wd -> wd.getDays().equals(newWorkdayDay));

        if (dayExists) {
            throw new IllegalStateException("Workday for " + workdayDTO.getDays() + " already exists for this facility.");
        }

        System.out.println("Creating new workday with facility ID: " + workdayDTO.getFacilityId());
        Workday workday = new Workday();
        workday.setValidFrom(workdayDTO.getValidFrom());
        workday.setDays(newWorkdayDay);
        workday.setFrom(workdayDTO.getFrom());
        workday.setUntil(workdayDTO.getUntil());
        workday.setFacility(facility);
        Workday savedWorkday = workdayRepository.save(workday);
        return toDTO(savedWorkday);
    }


    @Override
    public void deleteWorkday(Long id) {
        workdayRepository.deleteById(id);
    }

    private WorkdaysDTO toDTO(Workday workday) {
        WorkdaysDTO dto = new WorkdaysDTO();
        dto.setId(workday.getId());
        dto.setValidFrom(workday.getValidFrom());
        dto.setDays(String.valueOf(workday.getDays()));
        dto.setFrom(workday.getFrom());
        dto.setUntil(workday.getUntil());
        dto.setFacilityId(workday.getFacility().getId());
        return dto;
    }
}
