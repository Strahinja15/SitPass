package com.example.sispas.service.impl;

import com.example.sispas.dto.ExerciseDTO;
import com.example.sispas.model.Exercise;
import com.example.sispas.model.Facility;
import com.example.sispas.model.User;
import com.example.sispas.model.Workday;
import com.example.sispas.model.DaysOfWeek;
import com.example.sispas.repository.ExerciseRepository;
import com.example.sispas.repository.FacilityRepository;
import com.example.sispas.repository.UserRepository;
import com.example.sispas.repository.WorkdayRepository;
import com.example.sispas.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final FacilityRepository facilityRepository;
    private final WorkdayRepository workdayRepository;
    private final UserRepository userRepository;

    @Autowired
    public ExerciseServiceImpl(ExerciseRepository exerciseRepository, FacilityRepository facilityRepository,
                               WorkdayRepository workdayRepository, UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.facilityRepository = facilityRepository;
        this.workdayRepository = workdayRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ExerciseDTO> getExercisesByFacilityId(Long facilityId) {
        List<Exercise> exercises = exerciseRepository.findByFacilityId(facilityId);
        return exercises.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Exercise saveExercise(Long facilityId, ExerciseDTO exerciseDTO) {
        Optional<Facility> facilityOptional = facilityRepository.findById(facilityId);
        if (!facilityOptional.isPresent()) {
            throw new IllegalArgumentException("Facility not found");
        }
        Facility facility = facilityOptional.get();

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = exerciseDTO.getStartDate();

        if (startDate.isBefore(currentDate)) {
            throw new IllegalArgumentException("Cannot create exercise for a past date");
        }

        DayOfWeek day = startDate.getDayOfWeek();
        DaysOfWeek daysOfWeek = DaysOfWeek.valueOf(day.name().toLowerCase());
        Workday workDay = workdayRepository.findByDaysAndFacilityId(daysOfWeek, facilityId);

        if (workDay == null) {
            throw new IllegalArgumentException("No workday found for the specified date");
        }

        LocalTime from = workDay.getFrom();
        LocalTime until = workDay.getUntil();
        LocalTime startTime = LocalTime.parse(exerciseDTO.getFrom());
        LocalTime endTime = LocalTime.parse(exerciseDTO.getUntil());

        System.out.println("Workday from: " + from + ", until: " + until);
        System.out.println("Requested start time: " + startTime + ", end time: " + endTime);

        if (startTime.isBefore(from) || endTime.isAfter(until) || startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("You must be within working hours and the time range must be valid.");
        }

        Optional<User> userOptional = userRepository.findById(exerciseDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        Exercise exercise = new Exercise();
        exercise.setUser(user);
        exercise.setFacility(facility);
        exercise.setStartDate(startDate);
        exercise.setFrom(startTime);
        exercise.setUntil(endTime);

        return exerciseRepository.save(exercise);
    }
    @Override
    public Page<ExerciseDTO> getExercisesByUserId(Long userId, Pageable pageable) {
        Page<Exercise> exercisePage = exerciseRepository.findByUserId(userId, pageable);
        return exercisePage.map(this::toDTO);
    }
    @Override
    public List<ExerciseDTO> getExercisesByUserId(Long userId) {
        List<Exercise> exercises = exerciseRepository.findByUserId(userId);
        return exercises.stream().map(this::toDTO).collect(Collectors.toList());
    }
    private ExerciseDTO toDTO(Exercise exercise) {
        ExerciseDTO dto = new ExerciseDTO();
        dto.setId(exercise.getId());
        dto.setStartDate(exercise.getStartDate());
        dto.setFrom(exercise.getFrom().toString());
        dto.setUntil(exercise.getUntil().toString());
        dto.setFacilityId(exercise.getFacility().getId());
        dto.setFacilityName(exercise.getFacility().getName());
        dto.setUserId(exercise.getUser().getId());
        return dto;
    }
}
