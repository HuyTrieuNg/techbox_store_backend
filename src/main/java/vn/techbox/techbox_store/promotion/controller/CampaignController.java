package vn.techbox.techbox_store.promotion.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.techbox.techbox_store.cloudinary.service.CloudinaryService;
import vn.techbox.techbox_store.promotion.dto.CampaignCreateRequest;
import vn.techbox.techbox_store.promotion.dto.CampaignResponse;
import vn.techbox.techbox_store.promotion.dto.CampaignUpdateRequest;
import vn.techbox.techbox_store.promotion.model.Campaign;
import vn.techbox.techbox_store.promotion.repository.CampaignRepository;
import vn.techbox.techbox_store.promotion.service.CampaignService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
@Slf4j
public class CampaignController {
    
    private final CampaignService campaignService;
    private final CloudinaryService cloudinaryService;
    private final CampaignRepository campaignRepository;

    @PreAuthorize("hasAuthority('CAMPAIGN:WRITE')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createCampaign(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        log.info("REST request to create campaign: {}", name);
        
        try {
            CampaignCreateRequest request = CampaignCreateRequest.builder()
                    .name(name)
                    .description(description)
                    .startDate(LocalDateTime.parse(startDate))
                    .endDate(LocalDateTime.parse(endDate))
                    .build();
            
            // Upload image to Cloudinary if provided
            if (image != null && !image.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinaryService.uploadFile(image, "campaign_images");
                request.setImage((String) uploadResult.get("secure_url"));
                request.setImageID((String) uploadResult.get("public_id"));
            }
            
            CampaignResponse response = campaignService.createCampaign(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create campaign: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PreAuthorize("hasAuthority('CAMPAIGN:UPDATE')")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateCampaign(
            @PathVariable Integer id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        log.info("REST request to update campaign with ID: {}", id);
        
        try {
            // Get current campaign to check existing image
            CampaignResponse currentCampaign = campaignService.getCampaignById(id);
            
            CampaignUpdateRequest request = CampaignUpdateRequest.builder()
                    .name(name)
                    .description(description)
                    .startDate(startDate != null ? LocalDateTime.parse(startDate) : null)
                    .endDate(endDate != null ? LocalDateTime.parse(endDate) : null)
                    .build();
            
            // Handle image replacement - if new image provided, replace the old one
            if (image != null && !image.isEmpty()) {
                // Delete old image if exists
                if (currentCampaign.getImageID() != null) {
                    cloudinaryService.deleteFile(currentCampaign.getImageID());
                }
                
                // Upload new image
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinaryService.uploadFile(image, "campaign_images");
                request.setImage((String) uploadResult.get("secure_url"));
                request.setImageID((String) uploadResult.get("public_id"));
            }
            
            CampaignResponse response = campaignService.updateCampaign(id, request);
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Campaign not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update campaign: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // ========== Public APIs - Customer xem campaigns ==========
    
    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable Integer id) {
        log.info("REST request to get campaign with ID: {}", id);

        try {
            CampaignResponse response = campaignService.getCampaignById(id);
            Campaign campaign = campaignRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
            // Only return if campaign is active
            if (!campaign.isActive()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Campaign not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // ========== Admin APIs ==========

    @PreAuthorize("hasAuthority('CAMPAIGN:READ_ALL')")
    @GetMapping
    public ResponseEntity<Page<CampaignResponse>> getAllCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        log.info("REST request to get all campaigns with pagination");
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CampaignResponse> campaigns = campaignService.getAllCampaigns(pageable);
        return ResponseEntity.ok(campaigns);
    }

    // ========== Public APIs - Customer xem active campaigns ==========
    
    @GetMapping("/active")
    public ResponseEntity<List<CampaignResponse>> getActiveCampaigns() {
        log.info("REST request to get active campaigns");
        
        List<CampaignResponse> campaigns = campaignService.getActiveCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    // ========== Admin APIs - Quản lý campaigns ==========
    
    @PreAuthorize("hasAuthority('CAMPAIGN:READ')")
    @GetMapping("/scheduled")
    public ResponseEntity<List<CampaignResponse>> getScheduledCampaigns() {
        log.info("REST request to get scheduled campaigns");
        
        List<CampaignResponse> campaigns = campaignService.getScheduledCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    @PreAuthorize("hasAuthority('CAMPAIGN:READ')")
    @GetMapping("/expired")
    public ResponseEntity<List<CampaignResponse>> getExpiredCampaigns() {
        log.info("REST request to get expired campaigns");
        
        List<CampaignResponse> campaigns = campaignService.getExpiredCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    @PreAuthorize("hasAuthority('CAMPAIGN:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Integer id) {
        log.info("REST request to delete campaign with ID: {}", id);
        
        try {
            campaignService.deleteCampaign(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Campaign not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('CAMPAIGN:UPDATE')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restoreCampaign(@PathVariable Integer id) {
        log.info("REST request to restore campaign with ID: {}", id);
        
        try {
            campaignService.restoreCampaign(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Campaign not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
