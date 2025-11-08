-- ============================================
-- DATA DUMMY FOR INSURANCE APPLICATION
-- ============================================

-- Clean existing data (optional, uncomment if needed)
-- DELETE FROM claim;
-- DELETE FROM ordered_plan;
-- DELETE FROM policy;
-- DELETE FROM insurance_plan;

-- ============================================
-- INSERT INSURANCE PLANS
-- ============================================
INSERT INTO insurance_plan (id, provider_id, plan_name, price, coverage, coverage_details, applicable_service, expired_by_days, created_at, updated_at, deleted_at)
VALUES 
-- Travel Insurance Plans
('INS001', 'PROV001', 'Basic Travel Protection', 150000, 5000000, 'Basic coverage for flight delays and cancellations', 'FLIGHT', 180, NOW(), NOW(), NULL),
('INS002', 'PROV001', 'Premium Travel Shield', 350000, 15000000, 'Comprehensive travel coverage including medical emergency', 'FLIGHT,TOUR_PACKAGE', 365, NOW(), NOW(), NULL),
('INS003', 'PROV002', 'Flight Safety Plus', 200000, 8000000, 'Enhanced protection for flight-related incidents', 'FLIGHT', 180, NOW(), NOW(), NULL),

-- Accommodation Insurance Plans
('INS004', 'PROV002', 'Hotel Protection Basic', 100000, 3000000, 'Basic hotel accommodation insurance', 'ACCOMMODATION', 90, NOW(), NOW(), NULL),
('INS005', 'PROV003', 'Staycation Premium', 250000, 10000000, 'Premium accommodation coverage with extended benefits', 'ACCOMMODATION', 365, NOW(), NOW(), NULL),

-- Rental Insurance Plans
('INS006', 'PROV003', 'Car Rental Essential', 180000, 6000000, 'Essential coverage for car rental services', 'RENTAL', 180, NOW(), NOW(), NULL),
('INS007', 'PROV004', 'Vehicle Protection Pro', 400000, 20000000, 'Professional vehicle rental protection', 'RENTAL', 365, NOW(), NOW(), NULL),

-- Tour Package Insurance Plans
('INS008', 'PROV004', 'Tour Package Basic', 200000, 7000000, 'Basic tour package insurance coverage', 'TOUR_PACKAGE', 180, NOW(), NOW(), NULL),
('INS009', 'PROV005', 'Adventure Tour Shield', 500000, 25000000, 'Comprehensive adventure tour protection', 'TOUR_PACKAGE', 365, NOW(), NOW(), NULL),

-- Multi-Service Insurance Plans
('INS010', 'PROV005', 'All-in-One Travel', 600000, 30000000, 'Complete travel protection for all services', 'FLIGHT,ACCOMMODATION,RENTAL,TOUR_PACKAGE', 365, NOW(), NOW(), NULL);

-- ============================================
-- INSERT POLICIES
-- ============================================
INSERT INTO policy (id, booking_id, user_id, start_date, status, service, total_price, total_coverage, created_at, updated_at, deleted_at)
VALUES 
-- Policies for August 2024
('POL001', 'BOOK001', 'USER001', '2024-08-01', 'PAID', 'FLIGHT', 350000, 15000000, '2024-08-01 10:00:00', '2024-08-01 10:00:00', NULL),
('POL002', 'BOOK002', 'USER002', '2024-08-05', 'PAID', 'ACCOMMODATION', 250000, 10000000, '2024-08-05 11:30:00', '2024-08-05 11:30:00', NULL),
('POL003', 'BOOK003', 'USER003', '2024-08-10', 'PAID', 'RENTAL', 180000, 6000000, '2024-08-10 14:20:00', '2024-08-10 14:20:00', NULL),
('POL004', 'BOOK004', 'USER004', '2024-08-15', 'PAID', 'TOUR_PACKAGE', 500000, 25000000, '2024-08-15 09:45:00', '2024-08-15 09:45:00', NULL),
('POL005', 'BOOK005', 'USER005', '2024-08-20', 'PAID', 'FLIGHT', 150000, 5000000, '2024-08-20 16:10:00', '2024-08-20 16:10:00', NULL),

-- Policies for September 2024
('POL006', 'BOOK006', 'USER006', '2024-09-02', 'PAID', 'FLIGHT', 200000, 8000000, '2024-09-02 10:15:00', '2024-09-02 10:15:00', NULL),
('POL007', 'BOOK007', 'USER007', '2024-09-05', 'PAID', 'ACCOMMODATION', 100000, 3000000, '2024-09-05 11:00:00', '2024-09-05 11:00:00', NULL),
('POL008', 'BOOK008', 'USER008', '2024-09-08', 'PAID', 'RENTAL', 400000, 20000000, '2024-09-08 13:45:00', '2024-09-08 13:45:00', NULL),
('POL009', 'BOOK009', 'USER009', '2024-09-12', 'PARTIALLY_CLAIMED', 'TOUR_PACKAGE', 200000, 7000000, '2024-09-12 15:30:00', '2024-09-12 15:30:00', NULL),
('POL010', 'BOOK010', 'USER010', '2024-09-15', 'PAID', 'FLIGHT', 350000, 15000000, '2024-09-15 09:20:00', '2024-09-15 09:20:00', NULL),
('POL011', 'BOOK011', 'USER011', '2024-09-18', 'PAID', 'ACCOMMODATION', 250000, 10000000, '2024-09-18 14:00:00', '2024-09-18 14:00:00', NULL),
('POL012', 'BOOK012', 'USER012', '2024-09-22', 'PAID', 'RENTAL', 180000, 6000000, '2024-09-22 10:45:00', '2024-09-22 10:45:00', NULL),
('POL013', 'BOOK013', 'USER013', '2024-09-25', 'PAID', 'FLIGHT', 150000, 5000000, '2024-09-25 16:30:00', '2024-09-25 16:30:00', NULL),

-- Policies for October 2024
('POL014', 'BOOK014', 'USER014', '2024-10-01', 'PAID', 'TOUR_PACKAGE', 500000, 25000000, '2024-10-01 10:00:00', '2024-10-01 10:00:00', NULL),
('POL015', 'BOOK015', 'USER015', '2024-10-03', 'PAID', 'FLIGHT', 200000, 8000000, '2024-10-03 11:15:00', '2024-10-03 11:15:00', NULL),
('POL016', 'BOOK016', 'USER016', '2024-10-05', 'PAID', 'ACCOMMODATION', 100000, 3000000, '2024-10-05 13:30:00', '2024-10-05 13:30:00', NULL),
('POL017', 'BOOK017', 'USER017', '2024-10-08', 'PARTIALLY_CLAIMED', 'RENTAL', 400000, 20000000, '2024-10-08 15:45:00', '2024-10-08 15:45:00', NULL),
('POL018', 'BOOK018', 'USER018', '2024-10-10', 'PAID', 'FLIGHT', 350000, 15000000, '2024-10-10 09:00:00', '2024-10-10 09:00:00', NULL),
('POL019', 'BOOK019', 'USER019', '2024-10-12', 'PAID', 'TOUR_PACKAGE', 200000, 7000000, '2024-10-12 10:30:00', '2024-10-12 10:30:00', NULL),
('POL020', 'BOOK020', 'USER020', '2024-10-15', 'PAID', 'ACCOMMODATION', 250000, 10000000, '2024-10-15 14:15:00', '2024-10-15 14:15:00', NULL),
('POL021', 'BOOK021', 'USER021', '2024-10-18', 'PAID', 'RENTAL', 180000, 6000000, '2024-10-18 11:00:00', '2024-10-18 11:00:00', NULL),
('POL022', 'BOOK022', 'USER022', '2024-10-20', 'PAID', 'FLIGHT', 150000, 5000000, '2024-10-20 16:45:00', '2024-10-20 16:45:00', NULL),
('POL023', 'BOOK023', 'USER023', '2024-10-22', 'FULLY_CLAIMED', 'TOUR_PACKAGE', 500000, 25000000, '2024-10-22 10:20:00', '2024-10-22 10:20:00', NULL),
('POL024', 'BOOK024', 'USER024', '2024-10-25', 'PAID', 'FLIGHT', 350000, 15000000, '2024-10-25 13:00:00', '2024-10-25 13:00:00', NULL),
('POL025', 'BOOK025', 'USER025', '2024-10-28', 'PAID', 'ACCOMMODATION', 100000, 3000000, '2024-10-28 15:30:00', '2024-10-28 15:30:00', NULL);

-- ============================================
-- INSERT ORDERED PLANS
-- ============================================
INSERT INTO ordered_plan (id, status, expired_date, insurance_plan_id, policy_id, created_at, updated_at, deleted_at)
VALUES 
-- Ordered Plans for August 2024
('POL001-OP1', 'ORDERED', '2025-01-28', 'INS002', 'POL001', '2024-08-01 10:00:00', '2024-08-01 10:00:00', NULL),
('POL002-OP1', 'ORDERED', '2025-02-03', 'INS005', 'POL002', '2024-08-05 11:30:00', '2024-08-05 11:30:00', NULL),
('POL003-OP1', 'ORDERED', '2025-02-06', 'INS006', 'POL003', '2024-08-10 14:20:00', '2024-08-10 14:20:00', NULL),
('POL004-OP1', 'ORDERED', '2025-02-11', 'INS009', 'POL004', '2024-08-15 09:45:00', '2024-08-15 09:45:00', NULL),
('POL005-OP1', 'ORDERED', '2025-02-16', 'INS001', 'POL005', '2024-08-20 16:10:00', '2024-08-20 16:10:00', NULL),

-- Ordered Plans for September 2024
('POL006-OP1', 'ORDERED', '2025-03-01', 'INS003', 'POL006', '2024-09-02 10:15:00', '2024-09-02 10:15:00', NULL),
('POL007-OP1', 'ORDERED', '2024-12-04', 'INS004', 'POL007', '2024-09-05 11:00:00', '2024-09-05 11:00:00', NULL),
('POL008-OP1', 'ORDERED', '2025-03-07', 'INS007', 'POL008', '2024-09-08 13:45:00', '2024-09-08 13:45:00', NULL),
('POL009-OP1', 'CLAIMED', '2025-03-10', 'INS008', 'POL009', '2024-09-12 15:30:00', '2024-09-12 15:30:00', NULL),
('POL010-OP1', 'ORDERED', '2025-03-14', 'INS002', 'POL010', '2024-09-15 09:20:00', '2024-09-15 09:20:00', NULL),
('POL011-OP1', 'ORDERED', '2025-03-17', 'INS005', 'POL011', '2024-09-18 14:00:00', '2024-09-18 14:00:00', NULL),
('POL012-OP1', 'ORDERED', '2025-03-21', 'INS006', 'POL012', '2024-09-22 10:45:00', '2024-09-22 10:45:00', NULL),
('POL013-OP1', 'ORDERED', '2025-03-24', 'INS001', 'POL013', '2024-09-25 16:30:00', '2024-09-25 16:30:00', NULL),

-- Ordered Plans for October 2024
('POL014-OP1', 'ORDERED', '2025-03-30', 'INS009', 'POL014', '2024-10-01 10:00:00', '2024-10-01 10:00:00', NULL),
('POL015-OP1', 'ORDERED', '2025-04-01', 'INS003', 'POL015', '2024-10-03 11:15:00', '2024-10-03 11:15:00', NULL),
('POL016-OP1', 'ORDERED', '2025-01-03', 'INS004', 'POL016', '2024-10-05 13:30:00', '2024-10-05 13:30:00', NULL),
('POL017-OP1', 'CLAIMED', '2025-04-06', 'INS007', 'POL017', '2024-10-08 15:45:00', '2024-10-08 15:45:00', NULL),
('POL018-OP1', 'ORDERED', '2025-04-08', 'INS002', 'POL018', '2024-10-10 09:00:00', '2024-10-10 09:00:00', NULL),
('POL019-OP1', 'ORDERED', '2025-04-10', 'INS008', 'POL019', '2024-10-12 10:30:00', '2024-10-12 10:30:00', NULL),
('POL020-OP1', 'ORDERED', '2025-04-13', 'INS005', 'POL020', '2024-10-15 14:15:00', '2024-10-15 14:15:00', NULL),
('POL021-OP1', 'ORDERED', '2025-04-16', 'INS006', 'POL021', '2024-10-18 11:00:00', '2024-10-18 11:00:00', NULL),
('POL022-OP1', 'ORDERED', '2025-04-18', 'INS001', 'POL022', '2024-10-20 16:45:00', '2024-10-20 16:45:00', NULL),
('POL023-OP1', 'CLAIMED', '2025-04-20', 'INS009', 'POL023', '2024-10-22 10:20:00', '2024-10-22 10:20:00', NULL),
('POL024-OP1', 'ORDERED', '2025-04-23', 'INS002', 'POL024', '2024-10-25 13:00:00', '2024-10-25 13:00:00', NULL),
('POL025-OP1', 'ORDERED', '2025-01-26', 'INS004', 'POL025', '2024-10-28 15:30:00', '2024-10-28 15:30:00', NULL);

-- ============================================
-- INSERT CLAIMS
-- ============================================
INSERT INTO claim (id, status, proof, ordered_plan_id, created_at, updated_at, deleted_at)
VALUES 
-- Claims for September
('CLM001', 'ACCEPTED', 'Flight cancellation proof document - Flight ABC123 cancelled due to weather', 'POL009-OP1', '2024-09-13 10:00:00', '2024-09-14 15:30:00', NULL),

-- Claims for October
('CLM002', 'ACCEPTED', 'Vehicle accident report - Minor collision, car rental damaged', 'POL017-OP1', '2024-10-09 14:30:00', '2024-10-10 16:45:00', NULL),
('CLM003', 'ACCEPTED', 'Tour cancellation notice - Tour operator cancelled trip', 'POL023-OP1', '2024-10-23 09:15:00', '2024-10-24 11:20:00', NULL),

-- Claims waiting for review (recent)
('CLM004', 'WAITING_FOR_REVIEW', 'Hotel booking issue - Double booking, no room available', 'POL020-OP1', '2024-10-16 16:20:00', '2024-10-16 16:20:00', NULL),
('CLM005', 'WAITING_FOR_REVIEW', 'Flight delay claim - 8 hours delay, missed connection', 'POL018-OP1', '2024-10-11 11:45:00', '2024-10-11 11:45:00', NULL),
('CLM006', 'WAITING_FOR_REVIEW', 'Rental car breakdown - Vehicle mechanical failure', 'POL021-OP1', '2024-10-19 13:30:00', '2024-10-19 13:30:00', NULL),

-- Rejected claims
('CLM007', 'REJECTED', 'Insufficient documentation provided', 'POL015-OP1', '2024-10-04 10:00:00', '2024-10-05 14:15:00', NULL);

-- ============================================
-- VERIFICATION QUERIES
-- ============================================
-- Check Insurance Plans Count
-- SELECT COUNT(*) as insurance_plans_count FROM insurance_plan WHERE deleted_at IS NULL;

-- Check Policies Count
-- SELECT COUNT(*) as policies_count FROM policy WHERE deleted_at IS NULL;

-- Check Ordered Plans Count
-- SELECT COUNT(*) as ordered_plans_count FROM ordered_plan WHERE deleted_at IS NULL;

-- Check Claims Count
-- SELECT COUNT(*) as claims_count FROM claim WHERE deleted_at IS NULL;

-- Check Statistics by Month
-- SELECT 
--     DATE_FORMAT(op.created_at, '%Y-%m') as month,
--     COUNT(*) as ordered_plans_count
-- FROM ordered_plan op
-- WHERE op.deleted_at IS NULL
-- GROUP BY DATE_FORMAT(op.created_at, '%Y-%m')
-- ORDER BY month;

-- Check Statistics by Service
-- SELECT 
--     p.service,
--     COUNT(DISTINCT op.id) as ordered_plans_count
-- FROM ordered_plan op
-- JOIN policy p ON op.policy_id = p.id
-- WHERE op.deleted_at IS NULL AND p.deleted_at IS NULL
-- GROUP BY p.service;
