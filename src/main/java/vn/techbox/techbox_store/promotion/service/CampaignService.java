package vn.techbox.techbox_store.promotion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.promotion.dto.CampaignCreateRequest;
import vn.techbox.techbox_store.promotion.dto.CampaignResponse;
import vn.techbox.techbox_store.promotion.dto.CampaignUpdateRequest;
import vn.techbox.techbox_store.promotion.model.Campaign;
import vn.techbox.techbox_store.promotion.repository.CampaignRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CampaignService {
    
    private final CampaignRepository campaignRepository;
    
    public CampaignResponse createCampaign(CampaignCreateRequest request) {
        log.info("Creating new campaign: {}", request.getName());
        
        // Validate campaign name uniqueness
        if (campaignRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Campaign with name '" + request.getName() + "' already exists");
        }
        
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        Campaign campaign = Campaign.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .imageID(request.getImageID())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        
        Campaign savedCampaign = campaignRepository.save(campaign);
        log.info("Campaign created successfully with ID: {}", savedCampaign.getId());
        
        return mapToResponse(savedCampaign);
    }
    
    public CampaignResponse updateCampaign(Integer id, CampaignUpdateRequest request) {
        log.info("Updating campaign with ID: {}", id);
        
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found with ID: " + id));
        
        // Validate name uniqueness if name is being updated
        if (request.getName() != null && !request.getName().equals(campaign.getName())) {
            if (campaignRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Campaign with name '" + request.getName() + "' already exists");
            }
            campaign.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            campaign.setDescription(request.getDescription());
        }
        
        // Handle image fields - only update if explicitly provided in request
        if (request.getImage() != null) {
            campaign.setImage(request.getImage());
        }
        
        if (request.getImageID() != null) {
            campaign.setImageID(request.getImageID());
        }
        
        if (request.getStartDate() != null) {
            campaign.setStartDate(request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            campaign.setEndDate(request.getEndDate());
        }
        
        // Validate dates after update
        if (campaign.getEndDate().isBefore(campaign.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        Campaign savedCampaign = campaignRepository.save(campaign);
        log.info("Campaign updated successfully with ID: {}", savedCampaign.getId());
        
        return mapToResponse(savedCampaign);
    }
    
    @Transactional(readOnly = true)
    public CampaignResponse getCampaignById(Integer id) {
        log.info("Retrieving campaign with ID: {}", id);
        
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found with ID: " + id));
        
        return mapToResponse(campaign);
    }
    
    @Transactional(readOnly = true)
    public Page<CampaignResponse> getAllCampaigns(Pageable pageable) {
        log.info("Retrieving all campaigns with pagination");
        
        return campaignRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public List<CampaignResponse> getActiveCampaigns() {
        log.info("Retrieving active campaigns");
        
        LocalDateTime now = LocalDateTime.now();
        return campaignRepository.findAll().stream()
                .filter(campaign -> campaign.isActive())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CampaignResponse> getScheduledCampaigns() {
        log.info("Retrieving scheduled campaigns");
        
        return campaignRepository.findAll().stream()
                .filter(campaign -> campaign.isScheduled())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CampaignResponse> getExpiredCampaigns() {
        log.info("Retrieving expired campaigns");
        
        return campaignRepository.findAll().stream()
                .filter(campaign -> campaign.isExpired())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public void deleteCampaign(Integer id) {
        log.info("Soft deleting campaign with ID: {}", id);
        
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found with ID: " + id));
        
        campaign.setDeletedAt(LocalDateTime.now());
        campaignRepository.save(campaign);
        
        log.info("Campaign soft deleted successfully with ID: {}", id);
    }
    
    public void restoreCampaign(Integer id) {
        log.info("Restoring campaign with ID: {}", id);
        
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found with ID: " + id));
        
        if (campaign.getDeletedAt() == null) {
            throw new IllegalArgumentException("Campaign is not deleted, cannot restore");
        }
        
        campaign.setDeletedAt(null);
        campaign.setUpdatedAt(LocalDateTime.now());
        campaignRepository.save(campaign);
        
        log.info("Campaign restored successfully with ID: {}", id);
    }
    
    private CampaignResponse mapToResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .description(campaign.getDescription())
                .image(campaign.getImage())
                .imageID(campaign.getImageID())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .status(campaign.getStatus())
                .isActive(campaign.isActive())
                .isScheduled(campaign.isScheduled())
                .isExpired(campaign.isExpired())
                .promotionCount(campaign.getPromotions() != null ? campaign.getPromotions().size() : 0)
                .build();
    }
}