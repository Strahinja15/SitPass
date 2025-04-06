package com.example.sispas.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private LocalDateTime createdAt;
    private double rating;
    private boolean active;
    private List<Long> imageIds;
    private List<Long> exerciseIds;
    private List<Long> disciplinesIds;
    private List<Long> workdays;
    private boolean isDeleted = false;
}