package com.example.sispas.service;

import com.example.sispas.dto.CommentDTO;
import com.example.sispas.model.Comment;

public interface CommentService{
    CommentDTO createComment(CommentDTO commentDTO);
    CommentDTO toDTO(Comment comment);
    Comment toEntity(CommentDTO commentDTO);
}
