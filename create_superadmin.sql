-- Создание суперадмина
INSERT INTO users (first_name, last_name, email, password, role, active, created_at, updated_at) 
VALUES ('Super', 'Admin', 'admin@foodsave.kz', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'SUPER_ADMIN', true, NOW(), NOW());

-- Создание владельца магазина для тестирования
INSERT INTO users (first_name, last_name, email, password, role, active, created_at, updated_at) 
VALUES ('Store', 'Owner', 'owner@foodsave.kz', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'STORE_OWNER', true, NOW(), NOW());