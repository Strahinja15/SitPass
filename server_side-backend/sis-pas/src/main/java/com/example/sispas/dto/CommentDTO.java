package com.example.sispas.dto;

import com.example.sispas.model.Review;
import com.example.sispas.model.User;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private Long reviewId;
    private Long userId;
    private boolean IsDeleted;
}
