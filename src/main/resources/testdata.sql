-- Test Data SQL for Voucher API Testing
-- This file seeds test data for automated API testing
-- Run this after the application has created roles and permissions via UserDataSeeder

-- Note: Users and accounts are created by InitialUserSeeder, but we'll add test users here
-- for testing purposes. The password hash is BCrypt with strength 12.
-- Password: admin123 -> Hash: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYx8h2xKzF2
-- Password: staff123 -> Hash: $2a$12$K8v3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYx8h2xKzF3  
-- Password: customer123 -> Hash: $2a$12$M9v3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYx8h2xKzF4

-- IMPORTANT: These hashes are examples. In production, use actual BCrypt hashes.
-- For testing, you can use the application's seeder which will hash passwords properly.
-- Or generate BCrypt hashes using: BCryptPasswordEncoder with strength 12

-- Clean up existing test data (optional, for clean state)
-- DELETE FROM user_vouchers WHERE voucher_code IN (SELECT code FROM vouchers WHERE code = 'EXISTINGCODE');
-- DELETE FROM vouchers WHERE code = 'EXISTINGCODE';

-- Insert test voucher for duplicate code test (TC_BE_CREVOU_017)
-- This voucher should exist before running the duplicate code test
INSERT INTO vouchers (
    code, 
    voucher_type, 
    value, 
    min_order_amount, 
    usage_limit, 
    reserved_quantity,
    is_active,
    valid_from, 
    valid_until, 
    created_at, 
    updated_at,
    deleted_at,
    version
) VALUES (
    'EXISTINGCODE',
    'FIXED_AMOUNT',
    50000.00,
    0.00,
    100,
    0,
    true,
    '2025-12-01 00:00:00',
    '2025-12-31 23:59:59',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL,
    0
) ON CONFLICT (code) DO NOTHING;

-- ============================================================================
-- USER ACCOUNTS Create in seeder
-- ============================================================================
-- The seeder creates:
-- - admin@techbox.vn / admin123 (ROLE_ADMIN)
-- - staff1@techbox.vn / staff123 (ROLE_STAFF) 
-- - staff2@techbox.vn / staff123 (ROLE_STAFF)
-- - 50 random customer accounts (ROLE_CUSTOMER)
-- ============================================================================


-- Ensure test data is ready
-- Check if EXISTINGCODE voucher exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM vouchers WHERE code = 'EXISTINGCODE') THEN
        RAISE NOTICE 'Voucher EXISTINGCODE does not exist. Please check if it was created.';
    ELSE
        RAISE NOTICE 'Voucher EXISTINGCODE exists and is ready for testing.';
    END IF;
END $$;
