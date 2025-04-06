package com.example.sispas.service;

import com.example.sispas.dto.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    List<ImageDTO> getImagesByFacilityId(Long facilityId);
    ImageDTO saveImage(Long facilityId, MultipartFile file);
    void deleteImage(Long imageId);
}
