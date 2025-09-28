package vn.techbox.techbox_store.voucher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.voucher.dto.*;
import vn.techbox.techbox_store.voucher.model.UserVoucher;
import vn.techbox.techbox_store.voucher.model.Voucher;
import vn.techbox.techbox_store.voucher.repository.UserVoucherRepository;
import vn.techbox.techbox_store.voucher.repository.VoucherRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VoucherService {
    
    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    
    // CRUD Operations
    
    public VoucherResponse createVoucher(VoucherCreateRequest request) {
        log.info("Creating voucher with code: {}", request.getCode());
        
        // Check if voucher code already exists
        if (voucherRepository.existsByCodeAndNotDeleted(request.getCode(), null)) {
            throw new RuntimeException("Voucher with code '" + request.getCode() + "' already exists");
        }
        
        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .voucherType(request.getVoucherType())
                .value(request.getValue())
                .minOrderAmount(request.getMinOrderAmount())
                .usageLimit(request.getUsageLimit())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .build();
        
        voucher = voucherRepository.save(voucher);
        log.info("Voucher created successfully with ID: {}", voucher.getId());
        
        return VoucherResponse.fromEntity(voucher);
    }
    
    public VoucherResponse updateVoucher(Integer id, VoucherUpdateRequest request) {
        log.info("Updating voucher with ID: {}", id);
        
        Voucher voucher = voucherRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found with ID: " + id));
        
        // Check if new code already exists (excluding current voucher)
        if (request.getCode() != null && 
            voucherRepository.existsByCodeAndNotDeleted(request.getCode(), id)) {
            throw new RuntimeException("Voucher with code '" + request.getCode() + "' already exists");
        }
        
        // Update fields if provided
        if (request.getCode() != null) {
            voucher.setCode(request.getCode());
        }
        if (request.getVoucherType() != null) {
            voucher.setVoucherType(request.getVoucherType());
        }
        if (request.getValue() != null) {
            voucher.setValue(request.getValue());
        }
        if (request.getMinOrderAmount() != null) {
            voucher.setMinOrderAmount(request.getMinOrderAmount());
        }
        if (request.getUsageLimit() != null) {
            voucher.setUsageLimit(request.getUsageLimit());
        }
        if (request.getValidFrom() != null) {
            voucher.setValidFrom(request.getValidFrom());
        }
        if (request.getValidUntil() != null) {
            voucher.setValidUntil(request.getValidUntil());
        }
        
        voucher = voucherRepository.save(voucher);
        log.info("Voucher updated successfully with ID: {}", voucher.getId());
        
        return VoucherResponse.fromEntity(voucher);
    }
    
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherById(Integer id) {
        Voucher voucher = voucherRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found with ID: " + id));
        
        return VoucherResponse.fromEntity(voucher);
    }
    
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherByCode(String code) {
        Voucher voucher = voucherRepository.findByCodeAndNotDeleted(code)
                .orElseThrow(() -> new RuntimeException("Voucher not found with code: " + code));
        
        return VoucherResponse.fromEntity(voucher);
    }
    
    @Transactional(readOnly = true)
    public Page<VoucherResponse> getAllVouchers(Pageable pageable) {
        return voucherRepository.findAllActive(pageable)
                .map(VoucherResponse::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<VoucherResponse> getValidVouchers(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return voucherRepository.findValidVouchers(now, pageable)
                .map(VoucherResponse::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<VoucherResponse> searchVouchers(String searchTerm, Pageable pageable) {
        return voucherRepository.searchByCode(searchTerm, pageable)
                .map(VoucherResponse::fromEntity);
    }
    
    public void deleteVoucher(Integer id) {
        log.info("Soft deleting voucher with ID: {}", id);
        
        Voucher voucher = voucherRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found with ID: " + id));
        
        voucher.delete();
        voucherRepository.save(voucher);
        
        log.info("Voucher soft deleted successfully with ID: {}", id);
    }
    
    public void restoreVoucher(Integer id) {
        log.info("Restoring voucher with ID: {}", id);
        
        Optional<Voucher> voucherOpt = voucherRepository.findById(id);
        if (voucherOpt.isEmpty()) {
            throw new RuntimeException("Voucher not found with ID: " + id);
        }
        
        Voucher voucher = voucherOpt.get();
        if (!voucher.isDeleted()) {
            throw new RuntimeException("Voucher is not deleted, cannot restore");
        }
        
        voucher.restore();
        voucherRepository.save(voucher);
        
        log.info("Voucher restored successfully with ID: {}", id);
    }
    
    // Voucher Validation and Usage
    
    @Transactional(readOnly = true)
    public VoucherValidationResponse validateVoucher(VoucherValidationRequest request) {
        log.info("Validating voucher with code: {} for user: {}", request.getCode(), request.getUserId());
        
        // Find voucher by code
        Optional<Voucher> voucherOpt = voucherRepository.findByCodeAndNotDeleted(request.getCode());
        if (voucherOpt.isEmpty()) {
            return VoucherValidationResponse.invalid("Voucher not found or expired");
        }
        
        Voucher voucher = voucherOpt.get();
        
        // Check if voucher is valid (not expired)
        if (!voucher.isValid()) {
            return VoucherValidationResponse.invalid("Voucher has expired");
        }
        
        // Check if voucher has usage left
        if (!voucher.hasUsageLeft()) {
            return VoucherValidationResponse.invalid("Voucher usage limit exceeded");
        }
        
        // Check if user has already used this voucher
        Optional<UserVoucher> userVoucherOpt = userVoucherRepository
                .findByUserIdAndVoucherId(request.getUserId(), voucher.getId());
        if (userVoucherOpt.isPresent()) {
            return VoucherValidationResponse.invalid("You have already used this voucher");
        }
        
        // Check minimum order amount
        if (request.getOrderAmount().compareTo(voucher.getMinOrderAmount()) < 0) {
            return VoucherValidationResponse.invalid(
                "Order amount must be at least $" + voucher.getMinOrderAmount().toPlainString()
            );
        }
        
        // Calculate discount
        BigDecimal discountAmount = voucher.calculateDiscount(request.getOrderAmount());
        BigDecimal finalAmount = request.getOrderAmount().subtract(discountAmount);
        
        log.info("Voucher validation successful for code: {}", request.getCode());
        return VoucherValidationResponse.valid(
                VoucherResponse.fromEntity(voucher),
                discountAmount,
                finalAmount
        );
    }
    
    public void useVoucher(VoucherUseRequest request) {
        log.info("Using voucher with code: {} for user: {} and order: {}", 
                request.getCode(), request.getUserId(), request.getOrderId());
        
        // Find voucher by code
        Voucher voucher = voucherRepository.findByCodeAndNotDeleted(request.getCode())
                .orElseThrow(() -> new RuntimeException("Voucher not found or expired"));
        
        // Check if voucher is valid
        if (!voucher.isValid()) {
            throw new RuntimeException("Voucher has expired");
        }
        
        // Check if voucher has usage left
        if (!voucher.hasUsageLeft()) {
            throw new RuntimeException("Voucher usage limit exceeded");
        }
        
        // Check if user has already used this voucher
        Optional<UserVoucher> existingUsage = userVoucherRepository
                .findByUserIdAndVoucherId(request.getUserId(), voucher.getId());
        if (existingUsage.isPresent()) {
            throw new RuntimeException("You have already used this voucher");
        }
        
        // Create voucher usage record
        UserVoucher userVoucher = UserVoucher.builder()
                .userId(request.getUserId())
                .voucherId(voucher.getId())
                .orderId(request.getOrderId())
                .usedAt(LocalDateTime.now())
                .build();
        
        userVoucherRepository.save(userVoucher);
        
        log.info("Voucher used successfully: {} by user: {}", request.getCode(), request.getUserId());
    }
    
    // Reporting and Analytics
    
    @Transactional(readOnly = true)
    public List<VoucherResponse> getExpiredVouchers() {
        LocalDateTime now = LocalDateTime.now();
        return voucherRepository.findExpiredVouchers(now)
                .stream()
                .map(VoucherResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<VoucherResponse> getVouchersExpiringSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate = now.plusDays(days);
        
        return voucherRepository.findVouchersExpiringSoon(now, expirationDate)
                .stream()
                .map(VoucherResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserVoucher> getUserVoucherUsage(Integer userId) {
        return userVoucherRepository.findByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public Long getVoucherUsageCount(Integer voucherId) {
        return userVoucherRepository.countByVoucherId(voucherId);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return voucherRepository.existsByCodeAndNotDeleted(code, null);
    }
}