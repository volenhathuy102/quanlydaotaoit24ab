package com.example.quanlydaotao.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, AuthUser> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> resetTokens = new ConcurrentHashMap<>();
    private volatile String defaultPassword = DEFAULT_PASSWORD;

    public AuthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        ensureAccountsSchema();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.getOrDefault("username", "").trim();
        String password = request.getOrDefault("password", "");

        AuthUser user = findDatabaseUser(username, password);
        if (user == null && DEFAULT_USERNAME.equalsIgnoreCase(username) && defaultPassword.equals(password)) {
            user = new AuthUser(UUID.randomUUID().toString(), "admin", "Administrator", "ADMIN");
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Tên đăng nhập hoặc mật khẩu không đúng."));
        }

        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        sessions.put(accessToken, user);
        sessions.put(refreshToken, user);
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", Map.of(
                        "id", user.id(),
                        "username", user.username(),
                        "fullName", user.fullName(),
                        "roles", List.of(user.role())
                )
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        AuthUser user = authenticatedUser(authorization);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Phiên đăng nhập không hợp lệ."));
        }
        return ResponseEntity.ok(Map.of(
                "id", user.id(),
                "username", user.username(),
                "fullName", user.fullName(),
                "roles", List.of(user.role())
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody(required = false) Map<String, String> request) {
        if (request != null) {
            sessions.remove(request.get("refreshToken"));
            sessions.remove(request.get("accessToken"));
        }
        return ResponseEntity.ok(Map.of("message", "Đã đăng xuất."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> request) {
        String identifier = request.getOrDefault("identifier", "").trim();
        String resetToken = UUID.randomUUID().toString();
        resetTokens.put(resetToken, identifier);
        return ResponseEntity.ok(Map.of(
                "message", "Mã đặt lại mật khẩu đã được tạo.",
                "resetToken", resetToken
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.getOrDefault("token", "");
        String password = request.getOrDefault("password", "");
        String identifier = resetTokens.remove(token);
        if (identifier == null || password.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mã đặt lại không hợp lệ hoặc mật khẩu quá ngắn."));
        }
        if (DEFAULT_USERNAME.equalsIgnoreCase(identifier)
                || "admin".equalsIgnoreCase(identifier)
                || identifier.isBlank()) {
            defaultPassword = password;
        }
        try {
            String passwordHash = hashPassword(password);
            jdbcTemplate.update("""
                    UPDATE dbo.users
                    SET password_hash = ?, updated_at = SYSUTCDATETIME()
                    WHERE username = ? OR email = ? OR student_code = ? OR lecturer_code = ?
                    """, passwordHash, identifier, identifier, identifier, identifier);
        } catch (DataAccessException ignored) {
            // The default account has no database record to update.
        }
        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công."));
    }

    private AuthUser findDatabaseUser(String username, String password) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                    SELECT TOP (1) id, username, password_hash, full_name, role_name
                    FROM dbo.users
                    WHERE is_active = 1
                      AND (LOWER(username) = LOWER(?)
                        OR LOWER(email) = LOWER(?)
                        OR LOWER(student_code) = LOWER(?)
                        OR LOWER(lecturer_code) = LOWER(?))
                    """, username, username, username, username);
            if (rows.isEmpty()) {
                return null;
            }
            Map<String, Object> row = rows.get(0);
            String storedHash = (String) row.get("password_hash");
            if (!verifyPassword(password, storedHash)) {
                return null;
            }
            Object id = row.get("id");
            return new AuthUser(
                    id == null ? UUID.randomUUID().toString() : id.toString(),
                    (String) row.get("username"),
                    (String) row.getOrDefault("full_name", row.get("username")),
                    normalizeRole((String) row.get("role_name"))
            );
        } catch (DataAccessException | ClassCastException e) {
            return null;
        }
    }

    private AuthUser authenticatedUser(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return sessions.get(authorization.substring(7));
    }

    private void ensureAccountsSchema() {
        try {
            jdbcTemplate.execute("""
                    IF OBJECT_ID(N'dbo.users', N'U') IS NULL
                    BEGIN
                        CREATE TABLE dbo.users (
                            id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID() PRIMARY KEY,
                            username NVARCHAR(100) NOT NULL UNIQUE,
                            password_hash NVARCHAR(255) NOT NULL,
                            full_name NVARCHAR(255) NULL,
                            email NVARCHAR(255) NULL,
                            student_code NVARCHAR(100) NULL,
                            lecturer_code NVARCHAR(100) NULL,
                            role_name NVARCHAR(50) NOT NULL DEFAULT 'STUDENT',
                            is_active BIT NOT NULL DEFAULT 1,
                            created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
                            updated_at DATETIME2 NULL
                        );
                    END;
                    """);
            jdbcTemplate.update("""
                    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE LOWER(username) = 'admin')
                    BEGIN
                        INSERT INTO dbo.users (username, password_hash, full_name, role_name, is_active)
                        VALUES ('admin', ?, 'Administrator', 'ADMIN', 1);
                    END;
                    """, hashPassword(DEFAULT_PASSWORD));
        } catch (DataAccessException ignored) {
            return;
        }
    }

    private boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null) {
            return false;
        }
        if (!storedHash.startsWith("pbkdf2$")) {
            return MessageDigest.isEqual(
                    storedHash.getBytes(StandardCharsets.UTF_8),
                    password.getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] parts = storedHash.split("\\$", 4);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120000, 256);
            byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private String hashPassword(String password) throws DataAccessException {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120000, 256);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
            return "pbkdf2$120000$" + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | java.security.spec.InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot hash password", e);
        }
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "STUDENT";
        }
        return role.toUpperCase();
    }

    private record AuthUser(String id, String username, String fullName, String role) {
    }
}
