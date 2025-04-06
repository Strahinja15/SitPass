package com.example.sispas.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateDTO {
    private Long id;
    private int equipment;
    private int staff;
    private int hygiene;
    private int space;
    private Long reviewId;
    private boolean isDeleted;
}
