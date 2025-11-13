package vn.techbox.techbox_store.cloudinary.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techbox.techbox_store.cloudinary.service.CloudinaryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    /**
     * Upload multiple images to Cloudinary
     * Requires authentication
     * @param files the image files to upload
     * @param folderName the folder name on Cloudinary (optional, defaults to "uploads")
     * @return List of Maps containing "url" and "publicId" for each file
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "folderName", defaultValue = "uploads") String folderName) {

        try {
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No files provided"));
            }

            List<Map<String, String>> responses = new ArrayList<>();

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue; // Skip empty files
                }

                // Upload to Cloudinary
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinaryService.uploadFile(file, folderName);

                // Extract url and public_id
                String url = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");

                // Create response for this file
                Map<String, String> response = new HashMap<>();
                response.put("url", url);
                response.put("publicId", publicId);
                responses.add(response);
            }

            if (responses.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "All files were empty"));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(responses);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload files: " + e.getMessage(), "type", "IO_ERROR"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid file format or size: " + e.getMessage(), "type", "VALIDATION_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error during upload: " + e.getMessage(), "type", "UNKNOWN_ERROR"));
        }
    }
}