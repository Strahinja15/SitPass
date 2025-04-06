package com.example.sispas.service.impl;

import com.example.sispas.dto.CommentDTO;
import com.example.sispas.model.Comment;
import com.example.sispas.model.Review;
import com.example.sispas.model.User;
import com.example.sispas.repository.CommentRepository;
import com.example.sispas.repository.ReviewRepository;
import com.example.sispas.repository.UserRepository;
import com.example.sispas.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, ReviewRepository reviewRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = toEntity(commentDTO);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setIsDeleted(false);

        Comment savedComment = commentRepository.save(comment);
        return toDTO(savedComment);
    }

    @Override
    public CommentDTO toDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setReviewId(comment.getReview().getId());
        commentDTO.setUserId(comment.getUser().getId());
        commentDTO.setIsDeleted(comment.isIsDeleted());
        return commentDTO;
    }

    @Override
    public Comment toEntity(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setText(commentDTO.getText());

        Optional<Review> review = reviewRepository.findById(commentDTO.getReviewId());
        Optional<User> user = userRepository.findById(commentDTO.getUserId());

        if (review.isPresent() && user.isPresent()) {
            comment.setReview(review.get());
            comment.setUser(user.get());
        } else {
            throw new IllegalArgumentException("Review or User not found");
        }

        return comment;
    }
}
