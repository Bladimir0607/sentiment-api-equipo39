-- V3_create_users_table.sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'USER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar usuario admin por defecto (password: admin123)
INSERT INTO users (username, email, password, full_name, role) VALUES
('admin', 'admin@hackaton.com', '$2a$10$X7H8O5nzL9KjM2N1B3V4C6D7E8F9G0H1I2J3K4L5M6N7O8P9Q0R1S2T3U4V5W', 'Administrador', 'ADMIN'),
('user1', 'user1@hackaton.com', '$2a$10$X7H8O5nzL9KjM2N1B3V4C6D7E8F9G0H1I2J3K4L5M6N7O8P9Q0R1S2T3U4V5W', 'Usuario Uno', 'USER')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;