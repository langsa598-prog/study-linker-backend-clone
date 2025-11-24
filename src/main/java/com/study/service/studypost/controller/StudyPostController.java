package com.study.service.studypost.controller;

import com.study.service.studypost.dto.*;
import com.study.service.studypost.service.StudyPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-posts")
public class StudyPostController {

    private final StudyPostService studyPostService;

    public StudyPostController(StudyPostService studyPostService) {
        this.studyPostService = studyPostService;
    }

    // GET /api/study-posts - 게시글 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<StudyPostResponse>> getAllPosts() {
        return ResponseEntity.ok(studyPostService.getAllPosts());
    }

    // GET /api/study-posts/{postId} - 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<StudyPostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(studyPostService.getPost(postId));
    }

    // POST /api/study-posts - 게시글 생성(leaderId 포함)
    @PostMapping
    public ResponseEntity<StudyPostResponse> createPost(@RequestBody StudyPostCreateRequest request) {
        return ResponseEntity.ok(studyPostService.createPost(request));
    }

    // PATCH /api/study-posts/{postId} - 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<StudyPostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody StudyPostUpdateRequest request
    ) {
        return ResponseEntity.ok(studyPostService.updatePost(postId, request));
    }

    // DELETE /api/study-posts/{postId} - 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        studyPostService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/study-posts/{postId}/reviews - 특정 게시글의 리뷰 목록 조회
    @GetMapping("/{postId}/reviews")
    public ResponseEntity<List<StudyReviewResponse>> getReviews(@PathVariable Long postId) {
        return ResponseEntity.ok(studyPostService.getReviewsByPost(postId));
    }

    // POST /api/study-posts/{postId}/reviews - 특정 게시글에 리뷰 생성
    @PostMapping("/{postId}/reviews")
    public ResponseEntity<StudyReviewResponse> createReview(
            @PathVariable Long postId,
            @RequestBody StudyReviewCreateRequest request
    ) {
        return ResponseEntity.ok(studyPostService.createReview(postId, request));
    }

    // PATCH /api/study-posts/{postId}/reviews/{reviewId} - 리뷰 내용 수정
    @PatchMapping("/{postId}/reviews/{reviewId}")
    public ResponseEntity<StudyReviewResponse> updateReview(
            @PathVariable Long postId,
            @PathVariable Long reviewId,
            @RequestBody StudyReviewUpdateRequest request
    ) {
        return ResponseEntity.ok(studyPostService.updateReview(postId, reviewId, request));
    }

    // DELETE /api/study-posts/{postId}/reviews/{reviewId} - 리뷰 삭제
    @DeleteMapping("/{postId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long postId,
            @PathVariable Long reviewId
    ) {
        studyPostService.deleteReview(postId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
