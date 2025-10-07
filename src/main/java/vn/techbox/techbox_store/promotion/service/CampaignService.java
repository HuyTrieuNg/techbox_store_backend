package vn.techbox.techbox_store.promotion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.promotion.dto.CampaignCreateRequest;
import vn.techbox.techbox_store.promotion.dto.CampaignResponse;
import vn.techbox.techbox_store.promotion.dto.CampaignUpdateRequest;

import java.util.List;

public interface CampaignService {
    CampaignResponse createCampaign(CampaignCreateRequest request);

    CampaignResponse updateCampaign(Integer id, CampaignUpdateRequest request);

    CampaignResponse getCampaignById(Integer id);

    Page<CampaignResponse> getAllCampaigns(Pageable pageable);

    List<CampaignResponse> getActiveCampaigns();

    List<CampaignResponse> getScheduledCampaigns();

    List<CampaignResponse> getExpiredCampaigns();

    void deleteCampaign(Integer id);

    void restoreCampaign(Integer id);
}
