package vn.techbox.techbox_store.product.service.sync;

import vn.techbox.techbox_store.product.dto.productDto.ProductDetailResponse;

public interface SyncService {
    void syncProductUpdate(ProductDetailResponse product);
    void syncProductDelete(Integer productId);
}