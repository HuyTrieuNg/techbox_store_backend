package vn.techbox.techbox_store.product.service;

/**
 * Service để tính toán và cập nhật giá hiển thị của sản phẩm dựa trên promotions
 */
public interface ProductPriceUpdateService {
    
    /**
     * Cập nhật giá hiển thị cho một sản phẩm cụ thể
     * @param productId ID của sản phẩm cần cập nhật
     */
    void updateProductPricing(Integer productId);
    
    /**
     * Cập nhật giá hiển thị cho tất cả sản phẩm
     */
    void updateAllProductPricing();
    
    /**
     * Cập nhật giá cho các sản phẩm có promotions trong campaign
     * @param campaignId ID của campaign
     */
    void updateProductPricingByCampaign(Integer campaignId);
}
