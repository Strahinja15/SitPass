package com.example.sispas.service.impl;

import com.example.sispas.dto.ImageDTO;
import com.example.sispas.model.Facility;
import com.example.sispas.model.Image;
import com.example.sispas.model.User;
import com.example.sispas.repository.FacilityRepository;
import com.example.sispas.repository.ImageRepository;
import com.example.sispas.repository.UserRepository;
import com.example.sispas.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository, FacilityRepository facilityRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.facilityRepository = facilityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ImageDTO> getImagesByFacilityId(Long facilityId) {
        List<Image> images = imageRepository.findByFacilityId(facilityId);
        return images.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ImageDTO toDTO(Image image) {
        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setPath(image.getPath());
        dto.setFacility(image.getFacility().getId());
        return dto;
    }

    @Override
    public ImageDTO saveImage(Long facilityId, MultipartFile file) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        Optional<Image> existingImage = imageRepository.findByPath(originalFileName);

        if (existingImage.isPresent()) {
            Image image = existingImage.get();
            if (image.getFacility() != null && image.getFacility().getId().equals(facilityId)) {
                throw new RuntimeException("This image is already linked to the facility.");
            }

            // Check if the image is linked to any user
            List<User> usersWithImage = userRepository.findAllByImageId(image.getId());
            if (!usersWithImage.isEmpty()) {
                throw new RuntimeException("This image is already linked to a user.");
            }

            return toDTO(image);
        }

        Path path = Paths.get("src/main/resources/static/images/" + originalFileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + originalFileName, e);
        }

        Image image = new Image();
        image.setPath(originalFileName);
        image.setFacility(facility);

        Image savedImage = imageRepository.save(image);
        return toDTO(savedImage);
    }

    @Override
    public void deleteImage(Long imageId) {
        imageRepository.deleteById(imageId);
    }

}
