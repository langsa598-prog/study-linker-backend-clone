package com.study.service.studypost.service;

import com.study.service.studypost.domain.BoardType;
import com.study.service.studypost.domain.StudyPost;
import com.study.service.studypost.domain.StudyReview;
import com.study.service.studypost.dto.*;
import com.study.service.studypost.repository.StudyPostRepository;
import com.study.service.studypost.repository.StudyReviewRepository;
import com.study.service.user.domain.User;
import com.study.service.user.repository.UserRepository;
// 필요하면 Group/StudyGroup 리포지토리 import

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyPostService {

    private final StudyPostRepository postRepository;
    private final StudyReviewRepository reviewRepository;
    private final UserRepository userRepository;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StudyPostService(StudyPostRepository postRepository,
                            StudyReviewRepository reviewRepository,
                            UserRepository userRepository) {
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    // 게시글 전체 목록 조회
    public List<StudyPostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(StudyPostResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    public StudyPostResponse getPost(Long postId) {
        StudyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));
        return StudyPostResponse.fromEntity(post);
    }

    // 게시글 생성
    @Transactional
    public StudyPostResponse createPost(StudyPostCreateRequest request) {
        User leader = userRepository.findById(request.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("리더 유저를 찾을 수 없습니다. id=" + request.getLeaderId()));

        StudyPost post = new StudyPost();
        post.setLeader(leader);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setLocation(request.getLocation());

        // maxMembers
        post.setMaxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 0);

        // currentMembers는 새로 생성 시 0으로 시작 (원하면 요청으로 받게 바꿔도 됨)
        post.setCurrentMembers(0);

        // studyDate
        if (request.getStudyDate() != null) {
            post.setStudyDate(LocalDateTime.parse(request.getStudyDate(), formatter));
        }

        // type
        if (request.getType() != null) {
            post.setType(BoardType.valueOf(request.getType().toUpperCase()));
        }

        // groupId -> 연관관계 (Group/StudyGroup 리포지토리 있으면 여기서 찾아서 setGroup)
        // if (request.getGroupId() != null) {
        //     Group group = groupRepository.findById(request.getGroupId())
        //             .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다. id=" + request.getGroupId()));
        //     post.setGroup(group);
        // }

        // 위도/경도
        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());

        // createdAt / updatedAt 은 엔티티 기본값 또는 @PrePersist 로 처리한다고 가정

        StudyPost saved = postRepository.save(post);
        return StudyPostResponse.fromEntity(saved);
    }

    // 게시글 수정 (PATCH, 부분 업데이트)
    @Transactional
    public StudyPostResponse updatePost(Long postId, StudyPostUpdateRequest request) {
        StudyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getLocation() != null) {
            post.setLocation(request.getLocation());
        }
        if (request.getMaxMembers() != null) {
            post.setMaxMembers(request.getMaxMembers());
        }
        if (request.getStudyDate() != null) {
            post.setStudyDate(LocalDateTime.parse(request.getStudyDate(), formatter));
        }
        if (request.getType() != null) {
            post.setType(BoardType.valueOf(request.getType().toUpperCase()));
        }

        // currentMembers 수정
        if (request.getCurrentMembers() != null) {
            post.setCurrentMembers(request.getCurrentMembers());
        }

        // groupId 수정 (실제 Group 엔티티/리포지토리 준비되면 주석 풀고 사용)
        // if (request.getGroupId() != null) {
        //     Group group = groupRepository.findById(request.getGroupId())
        //             .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다. id=" + request.getGroupId()));
        //     post.setGroup(group);
        // }

        // 위도/경도 수정
        if (request.getLatitude() != null) {
            post.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            post.setLongitude(request.getLongitude());
        }

        // updatedAt 수동으로 수정(엔티티에 @PreUpdate 있으면 생략 가능)
        post.setUpdatedAt(LocalDateTime.now());

        return StudyPostResponse.fromEntity(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    // 특정 게시글의 리뷰 목록 조회
    public List<StudyReviewResponse> getReviewsByPost(Long postId) {
        return reviewRepository.findByPost_PostId(postId).stream()
                .map(StudyReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 리뷰 생성
    @Transactional
    public StudyReviewResponse createReview(Long postId, StudyReviewCreateRequest request) {
        StudyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + request.getUserId()));

        StudyReview review = new StudyReview();
        review.setPost(post);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        // createdAt은 엔티티 기본값

        StudyReview saved = reviewRepository.save(review);
        return StudyReviewResponse.fromEntity(saved);
    }

    // 리뷰 수정
    @Transactional
    public StudyReviewResponse updateReview(Long postId, Long reviewId, StudyReviewUpdateRequest request) {
        StudyReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        if (!review.getPost().getPostId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글의 리뷰가 아닙니다.");
        }

        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

        return StudyReviewResponse.fromEntity(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long postId, Long reviewId) {
        StudyReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        if (!review.getPost().getPostId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글의 리뷰가 아닙니다.");
        }

        reviewRepository.delete(review);
    }
}