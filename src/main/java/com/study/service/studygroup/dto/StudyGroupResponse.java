package com.study.service.studygroup.dto;

import com.study.service.studygroup.domain.StudyGroup;

import java.time.LocalDateTime;

public class StudyGroupResponse {

    private Long groupId;   // ← 통일!
    private Long leaderId;
    private String title;
    private String description;
    private Integer maxMembers;
    private String category;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    public StudyGroupResponse(Long groupId, Long leaderId, String title, String description,
                              Integer maxMembers, String category,
                              Double latitude, Double longitude,
                              LocalDateTime createdAt) {
        this.groupId = groupId;
        this.leaderId = leaderId;
        this.title = title;
        this.description = description;
        this.maxMembers = maxMembers;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    // ⭐ 엔티티 → DTO 변환용 정적 메서드
    public static StudyGroupResponse fromEntity(StudyGroup group) {
        return new StudyGroupResponse(
                group.getGroupId(),
                group.getLeader() != null ? group.getLeader().getUserId() : null,
                group.getTitle(),
                group.getDescription(),
                group.getMaxMembers(),
                group.getCategory(),
                group.getLatitude() != null ? group.getLatitude().doubleValue() : null,
                group.getLongitude() != null ? group.getLongitude().doubleValue() : null,
                group.getCreatedAt()
        );
    }

    // getters
    public Long getGroupId() { return groupId; }
    public Long getLeaderId() { return leaderId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getMaxMembers() { return maxMembers; }
    public String getCategory() { return category; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}