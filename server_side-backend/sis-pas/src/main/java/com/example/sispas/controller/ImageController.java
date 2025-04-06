package com.example.sispas.controller;

import com.example.sispas.dto.ImageDTO;
import com.example.sispas.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/images")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    @PreAuthorize("hasAnyRole('ROLE_administrator','ROLE_user')")
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<ImageDTO>> getImagesByFacilityId(@PathVariable Long facilityId) {
        List<ImageDTO> images = imageService.getImagesByFacilityId(facilityId);
        return ResponseEntity.ok(images);
    }
    @PostMapping("/upload/{facilityId}")
    public ResponseEntity<ImageDTO> uploadImage(@PathVariable Long facilityId, @RequestParam("file") MultipartFile file) {
        ImageDTO savedImage = imageService.saveImage(facilityId, file);
        return ResponseEntity.status(CREATED).body(savedImage);
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

}
