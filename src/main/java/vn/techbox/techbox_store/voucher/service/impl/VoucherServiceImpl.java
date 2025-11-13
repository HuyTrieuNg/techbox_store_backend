package vn.techbox.techbox_store.voucher.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.techbox.techbox_store.voucher.dto.*;
import vn.techbox.techbox_store.voucher.exception.*;
import vn.techbox.techbox_store.voucher.model.UserVoucher;
import vn.techbox.techbox_store.voucher.model.Voucher;
import vn.techbox.techbox_store.voucher.model.VoucherType;
import vn.techbox.techbox_store.voucher.repository.UserVoucherRepository;
import vn.techbox.techbox_store.voucher.repository.VoucherRepository;
import vn.techbox.techbox_store.voucher.service.VoucherService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VoucherServiceImpl implements VoucherService {
    
    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    
    // CRUD Operations
    
    @Override
    public VoucherResponse createVoucher(VoucherCreateRequest request) {
        log.info("Creating voucher with code: {}", request.getCode());
        
        try {
            // Check if voucher code already exists
            if (voucherRepository.existsByCodeAndNotDeleted(request.getCode(), null)) {
                throw new VoucherAlreadyExistsException("Voucher with code '" + request.getCode() + "' already exists");
            }
            
            // Additional business logic validation
            validateVoucherBusinessRules(request);
            
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
        } catch (VoucherException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while creating voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to create voucher due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to create voucher due to unexpected error", e);
        }
    }
    
    private void validateVoucherBusinessRules(VoucherCreateRequest request) {
        // Validate date range (defensive check, DTO validation should catch this first)
        if (request.getValidFrom() != null && request.getValidUntil() != null) {
            if (request.getValidUntil().isBefore(request.getValidFrom()) || 
                request.getValidUntil().isEqual(request.getValidFrom())) {
                throw new VoucherValidationException("Valid until date must be after valid from date");
            }
        }
        
        // Validate percentage voucher value (defensive check, DTO validation should catch this first)
        if (request.getVoucherType() != null && request.getValue() != null) {
            if (request.getVoucherType() == VoucherType.PERCENTAGE) {
                if (request.getValue().compareTo(BigDecimal.ONE) < 0 || 
                    request.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                    throw new VoucherValidationException("Percentage voucher value must be between 1 and 100");
                }
            }
            
            // Validate fixed amount voucher
            if (request.getVoucherType() == VoucherType.FIXED_AMOUNT) {
                if (request.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new VoucherValidationException("Fixed amount voucher value must be greater than 0");
                }
            }
        }
    }
    
    @Override
    public VoucherResponse updateVoucher(String code, VoucherUpdateRequest request) {
        log.info("Updating voucher with code: {}", code);

        try {
            Voucher voucher = voucherRepository.findByCodeAndNotDeleted(code)
                    .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with code: " + code));
            
            // Check if new code already exists (excluding current voucher)
            if (request.getCode() != null && 
                !request.getCode().equals(code) &&
                voucherRepository.existsByCodeAndNotDeleted(request.getCode(), null)) {
                throw new VoucherAlreadyExistsException("Voucher with code '" + request.getCode() + "' already exists");
            }
            
            // Validate update request
            validateUpdateRequest(request, voucher);
            
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
            log.info("Voucher updated successfully with code: {}", voucher.getCode());
            
            return VoucherResponse.fromEntity(voucher);
        } catch (VoucherException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while updating voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to update voucher due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to update voucher due to unexpected error", e);
        }
    }
    
    private void validateUpdateRequest(VoucherUpdateRequest request, Voucher voucher) {
        LocalDateTime validFrom = request.getValidFrom() != null ? request.getValidFrom() : voucher.getValidFrom();
        LocalDateTime validUntil = request.getValidUntil() != null ? request.getValidUntil() : voucher.getValidUntil();
        
        // Validate date range
        if (validUntil.isBefore(validFrom) || validUntil.isEqual(validFrom)) {
            throw new VoucherValidationException("Valid until date must be after valid from date");
        }
        
        // Validate percentage voucher value
        VoucherType voucherType = request.getVoucherType() != null ? request.getVoucherType() : voucher.getVoucherType();
        BigDecimal value = request.getValue() != null ? request.getValue() : voucher.getValue();
        
        if (voucherType == VoucherType.PERCENTAGE) {
            if (value.compareTo(BigDecimal.ONE) < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new VoucherValidationException("Percentage voucher value must be between 1 and 100");
            }
        }
        
        // Validate fixed amount voucher
        if (voucherType == VoucherType.FIXED_AMOUNT) {
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new VoucherValidationException("Fixed amount voucher value must be greater than 0");
            }
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public VoucherResponse getVoucherByCode(String code) {
        try {
            Voucher voucher = voucherRepository.findByCodeAndNotDeleted(code)
                    .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with code: " + code));

            return VoucherResponse.fromEntity(voucher);
        } catch (VoucherException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while getting voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to get voucher due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while getting voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to get voucher due to unexpected error", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VoucherResponse> getAllVouchers(Pageable pageable) {
        return voucherRepository.findAllActive(pageable)
                .map(VoucherResponse::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VoucherResponse> getValidVouchers(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return voucherRepository.findValidVouchers(now, pageable)
                .map(VoucherResponse::fromEntity);
    }

    
    @Override
    public void deleteVoucherByCode(String code) {
        log.info("Soft deleting voucher with code: {}", code);

        try {
            Voucher voucher = voucherRepository.findByCodeAndNotDeleted(code)
                    .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with code: " + code));

            voucher.delete();
            voucherRepository.save(voucher);

            log.info("Voucher soft deleted successfully with code: {}", code);
        } catch (VoucherException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while deleting voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to delete voucher due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to delete voucher due to unexpected error", e);
        }
    }
    
    @Override
    public void restoreVoucherByCode(String code) {
        log.info("Restoring voucher with code: {}", code);

        try {
            Optional<Voucher> voucherOpt = voucherRepository.findByCode(code);
            if (voucherOpt.isEmpty()) {
                throw new VoucherNotFoundException("Voucher not found with code: " + code);
            }

            Voucher voucher = voucherOpt.get();
            if (!voucher.isDeleted()) {
                throw new VoucherValidationException("Voucher is not deleted, cannot restore");
            }
            
            voucher.restore();
            voucherRepository.save(voucher);
            
            log.info("Voucher restored successfully with code: {}", voucher.getCode());
        } catch (VoucherException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while restoring voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to restore voucher due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while restoring voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to restore voucher due to unexpected error", e);
        }
    }
    
    // Voucher Validation and Usage
    
    @Override
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
        .findByUserIdAndVoucherCode(request.getUserId(), voucher.getCode());
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
    
    @Override
    public void useVoucher(VoucherUseRequest request) {
        log.info("Using voucher with code: {} for user: {} and order: {}", 
                request.getCode(), request.getUserId(), request.getOrderId());
        
        try {
            // Find voucher by code
            Voucher voucher = voucherRepository.findByCodeAndNotDeleted(request.getCode())
                    .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with code: " + request.getCode()));
            
            // Check if voucher is valid
            if (!voucher.isValid()) {
                throw new VoucherValidationException("Voucher has expired");
            }
            
            // Check if voucher has usage left
            if (!voucher.hasUsageLeft()) {
                throw new VoucherValidationException("Voucher usage limit exceeded");
            }
            
            // Check if user has already used this voucher
            Optional<UserVoucher> existingUsage = userVoucherRepository
                .findByUserIdAndVoucherCode(request.getUserId(), voucher.getCode());
                if (existingUsage.isPresent()) {
                    throw new VoucherValidationException("You have already used this voucher");
                }
            
            // Create voucher usage record
            UserVoucher userVoucher = UserVoucher.builder()
                    .userId(request.getUserId())
                    .voucherCode(voucher.getCode())
                    .orderId(request.getOrderId())
                    .usedAt(LocalDateTime.now())
                    .build();
            
            userVoucherRepository.save(userVoucher);
            
            log.info("Voucher used successfully: {} by user: {}", request.getCode(), request.getUserId());
        } catch (VoucherException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while using voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to use voucher due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while using voucher: {}", e.getMessage(), e);
            throw new VoucherSystemException("Failed to use voucher due to unexpected error", e);
        }
    }
    
    // Reporting and Analytics
    
    @Override
    @Transactional(readOnly = true)
    public List<VoucherResponse> getExpiredVouchers() {
        LocalDateTime now = LocalDateTime.now();
        return voucherRepository.findExpiredVouchers(now)
                .stream()
                .map(VoucherResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VoucherResponse> getVouchersExpiringSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate = now.plusDays(days);
        
        return voucherRepository.findVouchersExpiringSoon(now, expirationDate)
                .stream()
                .map(VoucherResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserVoucher> getUserVoucherUsage(Integer userId) {
        return userVoucherRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getVoucherUsageCount(String voucherCode) {
        return userVoucherRepository.countByVoucherCode(voucherCode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return voucherRepository.existsByCodeAndNotDeleted(code, null);
    }
}
