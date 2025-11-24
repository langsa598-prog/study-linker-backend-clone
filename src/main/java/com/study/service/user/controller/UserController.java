package com.study.service.user.controller;

import com.study.service.user.domain.User;
import com.study.service.user.dto.*;
import com.study.service.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ============================
    // GET /api/users
    // 사용자 전체 조회
    // ============================
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<User> users = service.findAll();
        List<UserResponse> response = users.stream()
                .map(UserResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ============================
    // GET /api/users/{userId}
    // 사용자 단건 조회
    // ============================
    @GetMapping("/{userId}")
    public ResponseEntity<?> getById(@PathVariable Long userId) {
        try {
            User user = service.findById(userId);
            return ResponseEntity.ok(UserResponse.fromEntity(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ============================
    // GET /api/users/{userId}/groups
    // 사용자가 가입한 스터디 그룹 목록 조회
    // ============================
    @GetMapping("/{userId}/groups")
    public ResponseEntity<List<UserGroupResponse>> getUserGroups(@PathVariable Long userId) {
        List<UserGroupResponse> groups = service.findGroupsByUserId(userId);
        return ResponseEntity.ok(groups);
    }

    // ============================
    // GET /api/users/profile
    // 로그인한 사용자 본인 정보 조회 (JWT 토큰 기반)
    // ============================
    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        User user = service.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    // ============================
    // POST /api/users
    // 사용자 생성(회원가입)
    // ============================
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        User created = service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.fromEntity(created));
    }

    // ============================
    // PUT /api/users/{userId}
    // 사용자 정보 수정
    // ============================
    @PutMapping("/{userId}")
    public ResponseEntity<?> update(
            @PathVariable Long userId,
            @RequestBody UserRequest request
    ) {
        try {
            User updated = service.update(userId, request);
            return ResponseEntity.ok(UserResponse.fromEntity(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ============================
    // PATCH /api/users/{userId}/password
    // 비밀번호 변경
    // ============================
    @PatchMapping("/{userId}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable Long userId,
            @RequestBody PasswordUpdateRequest request
    ) {
        try {
            service.updatePassword(userId, request.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ============================
    // PATCH /api/users/{userId}/location
    // 사용자 위치 업데이트
    // ============================
    @PatchMapping("/{userId}/location")
    public ResponseEntity<?> updateLocation(
            @PathVariable Long userId,
            @RequestBody LocationUpdateRequest request
    ) {
        try {
            User updated = service.updateLocation(userId, request);
            return ResponseEntity.ok(UserResponse.fromEntity(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ============================
    // DELETE /api/users/{userId}
    // 사용자 삭제
    // ============================
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable Long userId) {
        try {
            service.deleteById(userId);
            return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}