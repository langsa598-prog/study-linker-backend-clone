package com.study.service.user.dto;

import com.study.service.user.domain.Role;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponse {

    private Long userId;
    private String username;
    private String name;
    private String email;
    private Role role;

    private List<String> interestTags;

    private Double latitude;
    private Double longitude;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== Constructor =====
    public UserResponse(Long userId, String username, String name, String email, Role role,
                        List<String> interestTags, Double latitude, Double longitude,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
        this.interestTags = interestTags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ===== Static factory: Entity → DTO =====
    public static UserResponse fromEntity(com.study.service.user.domain.User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getInterestTags(),
                user.getLatitude(),
                user.getLongitude(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // ===== Getter =====
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public List<String> getInterestTags() { return interestTags; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}