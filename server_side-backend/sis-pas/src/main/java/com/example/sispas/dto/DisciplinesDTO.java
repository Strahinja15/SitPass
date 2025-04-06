package com.example.sispas.dto;

import com.example.sispas.model.Facility;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisciplinesDTO {
    private Long id;
    private String name;
    private Long facility;
}
