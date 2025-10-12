package vn.techbox.techbox_store.voucher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.voucher.dto.*;
import vn.techbox.techbox_store.voucher.model.UserVoucher;
import vn.techbox.techbox_store.voucher.service.VoucherService;

import java.util.List;

@RestController
@RequestMapping("/vouchers")
@RequiredArgsConstructor
@Tag(name = "Voucher Management", description = "APIs for managing vouchers and voucher usage")
public class VoucherController {
    
    private final VoucherService voucherService;
    
    // CRUD Operations
    
    @PostMapping
    @Operation(summary = "Create a new voucher", description = "Create a new voucher with specified details")
    public ResponseEntity<VoucherResponse> createVoucher(
            @Valid @RequestBody VoucherCreateRequest request) {
        try {
            VoucherResponse response = voucherService.createVoucher(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{code}")
    @Operation(summary = "Update a voucher", description = "Update an existing voucher by ID")
    public ResponseEntity<VoucherResponse> updateVoucher(
            @Parameter(description = "Voucher code") @PathVariable String code,
            @Valid @RequestBody VoucherUpdateRequest request) {
        try {
            VoucherResponse response = voucherService.updateVoucher(code, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{code}")
    @Operation(summary = "Get voucher by ID", description = "Retrieve voucher details by ID")
    public ResponseEntity<VoucherResponse> getVoucherByCode(
            @Parameter(description = "Voucher code") @PathVariable String code) {
        try {
            VoucherResponse response = voucherService.getVoucherByCode(code);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // NOTE: consolidated GET by code is implemented at @GetMapping("/{code}") above.
    
    @GetMapping("/code/exists")
    @Operation(summary = "Check if voucher code exists", description = "Check if a voucher code already exists in the system")
    public ResponseEntity<Boolean> checkVoucherCodeExists(
            @Parameter(description = "Voucher code to check") @RequestParam String code) {
        boolean exists = voucherService.existsByCode(code);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping
    @Operation(summary = "Get all vouchers", description = "Retrieve all vouchers with pagination")
    public ResponseEntity<Page<VoucherResponse>> getAllVouchers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<VoucherResponse> response = voucherService.getAllVouchers(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/valid")
    @Operation(summary = "Get valid vouchers", description = "Retrieve all currently valid vouchers with pagination")
    public ResponseEntity<Page<VoucherResponse>> getValidVouchers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "validUntil") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<VoucherResponse> response = voucherService.getValidVouchers(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search vouchers", description = "Search vouchers by code pattern")
    public ResponseEntity<Page<VoucherResponse>> searchVouchers(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<VoucherResponse> response = voucherService.searchVouchers(searchTerm, pageable);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{code}")
    @Operation(summary = "Delete a voucher", description = "Soft delete a voucher by ID")
    public ResponseEntity<Void> deleteVoucher(
            @Parameter(description = "Voucher code") @PathVariable String code) {
        try {
            voucherService.deleteVoucherByCode(code);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{code}/restore")
    @Operation(summary = "Restore a deleted voucher", description = "Restore a soft-deleted voucher by ID")
    public ResponseEntity<Void> restoreVoucher(
            @Parameter(description = "Voucher code") @PathVariable String code) {
        try {
            voucherService.restoreVoucherByCode(code);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Voucher Validation and Usage
    
    @PostMapping("/validate")
    @Operation(summary = "Validate a voucher", description = "Validate if a voucher can be used for an order")
    public ResponseEntity<VoucherValidationResponse> validateVoucher(
            @Valid @RequestBody VoucherValidationRequest request) {
        VoucherValidationResponse response = voucherService.validateVoucher(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/use")
    @Operation(summary = "Use a voucher", description = "Mark a voucher as used for an order")
    public ResponseEntity<String> useVoucher(
            @Valid @RequestBody VoucherUseRequest request) {
        try {
            voucherService.useVoucher(request);
            return ResponseEntity.ok("Voucher used successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Reporting and Analytics
    
    @GetMapping("/expired")
    @Operation(summary = "Get expired vouchers", description = "Retrieve all expired vouchers")
    public ResponseEntity<List<VoucherResponse>> getExpiredVouchers() {
        List<VoucherResponse> response = voucherService.getExpiredVouchers();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/expiring-soon")
    @Operation(summary = "Get vouchers expiring soon", description = "Retrieve vouchers expiring within specified days")
    public ResponseEntity<List<VoucherResponse>> getVouchersExpiringSoon(
            @Parameter(description = "Number of days") @RequestParam(defaultValue = "7") int days) {
        List<VoucherResponse> response = voucherService.getVouchersExpiringSoon(days);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/usage/user/{userId}")
    @Operation(summary = "Get user voucher usage", description = "Retrieve all vouchers used by a specific user")
    public ResponseEntity<List<UserVoucher>> getUserVoucherUsage(
            @Parameter(description = "User ID") @PathVariable Integer userId) {
        List<UserVoucher> response = voucherService.getUserVoucherUsage(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{voucherCode}/usage-count")
    @Operation(summary = "Get voucher usage count", description = "Get the number of times a voucher has been used")
    public ResponseEntity<Long> getVoucherUsageCount(
            @Parameter(description = "Voucher code") @PathVariable String voucherCode) {
        Long count = voucherService.getVoucherUsageCount(voucherCode);
        return ResponseEntity.ok(count);
    }
}