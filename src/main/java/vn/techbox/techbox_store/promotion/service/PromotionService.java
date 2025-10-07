package vn.techbox.techbox_store.promotion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.promotion.dto.*;

import java.util.List;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionCreateRequest request);

    PromotionResponse updatePromotion(Integer id, PromotionUpdateRequest request);

    PromotionResponse getPromotionById(Integer id);

    Page<PromotionResponse> getAllPromotions(Pageable pageable);

    List<PromotionResponse> getPromotionsByCampaign(Integer campaignId);

    List<PromotionResponse> getPromotionsByProductVariation(Integer productVariationId);

    PromotionCalculationResponse calculatePromotions(PromotionCalculationRequest request);

    void deletePromotion(Integer id);
}