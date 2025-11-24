package com.study.service.studypost.domain;

import com.study.service.user.domain.User;
import com.study.service.studygroup.domain.StudyGroup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Study_posts")
@Getter
@Setter
@DynamicUpdate
public class StudyPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;

    @Lob
    private String content;

    @Column(name = "max_members")
    private int maxMembers;

    @Column(name = "current_members")
    private int currentMembers = 0;

    private String location;

    @Column(name = "study_date")
    private LocalDateTime studyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    // 🔥 여기 타입은 StudyGroup 이어야 함!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroup group;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<StudyReview> reviews;

    @Enumerated(EnumType.STRING)
    private BoardType type; // FREE, STUDY, REVIEW

    private Double latitude;
    private Double longitude;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}