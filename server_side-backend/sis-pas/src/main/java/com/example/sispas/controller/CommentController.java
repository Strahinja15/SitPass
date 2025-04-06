package com.example.sispas.controller;

import com.example.sispas.dto.CommentDTO;
import com.example.sispas.dto.RateDTO;
import com.example.sispas.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @PostMapping("/createComment")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        try {
            CommentDTO comment = commentService.createComment(commentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
