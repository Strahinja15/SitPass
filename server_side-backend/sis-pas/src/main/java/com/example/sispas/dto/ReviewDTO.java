package com.example.sispas.dto;

import com.example.sispas.dto.CommentDTO;
import com.example.sispas.dto.RateDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private LocalDateTime createdAt;
    private int exerciseCount;
    private boolean hidden;
    private boolean isDeleted;
    private Long userId;
    private Long facilityId;
    private List<CommentDTO> comments;
    private RateDTO rateDTO;
}
