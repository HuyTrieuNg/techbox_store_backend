package vn.techbox.techbox_store.voucher.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.techbox.techbox_store.voucher.dto.*;
import vn.techbox.techbox_store.voucher.model.UserVoucher;

import java.util.List;

public interface VoucherService {

    VoucherResponse createVoucher(VoucherCreateRequest request);

    VoucherResponse updateVoucher(String code, VoucherUpdateRequest request);

    VoucherResponse getVoucherByCode(String code);

    Page<VoucherResponse> getAllVouchers(Pageable pageable);

    Page<VoucherResponse> getValidVouchers(Pageable pageable);

    void deleteVoucherByCode(String code);

    void restoreVoucherByCode(String code);

    VoucherValidationResponse validateVoucher(VoucherValidationRequest request);

    void useVoucher(VoucherUseRequest request);

    List<VoucherResponse> getExpiredVouchers();

    List<VoucherResponse> getVouchersExpiringSoon(int days);

    List<UserVoucher> getUserVoucherUsage(Integer userId);

    Long getVoucherUsageCount(String voucherCode);

    boolean existsByCode(String code);
}
