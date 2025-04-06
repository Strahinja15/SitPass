package com.example.sispas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("api/debug")
public class DebugController {

    @GetMapping("/images")
    public ResponseEntity<String> checkImages() {
        Path imagePath = Paths.get("src/main/resources/static/images/fitness1.jpeg");
        if (Files.exists(imagePath)) {
            return ResponseEntity.ok("Image fitness1.jpeg exists and is accessible.");
        } else {
            return ResponseEntity.status(404).body("Image fitness1.jpeg not found.");
        }
    }
}
