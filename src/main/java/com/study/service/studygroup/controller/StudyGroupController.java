package com.study.service.studygroup.controller;

import com.study.service.security.CustomUserDetails;
import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.studygroup.dto.*;
import com.study.service.studygroup.dto.StudyGroupRequest;
import com.study.service.groupmember.dto.GroupMemberResponse;
import com.study.service.studyschedule.dto.StudyScheduleRequest;
import com.study.service.studyschedule.dto.StudyScheduleResponse;
import com.study.service.studygroup.service.StudyGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class StudyGroupController {

    private final StudyGroupService service;

    public StudyGroupController(StudyGroupService service) {
        this.service = service;
    }

    // ============================
    // GET /api/study-groups
    // 스터디 그룹 전체 조회
    // ============================
    @GetMapping("/study-groups")
    public ResponseEntity<List<StudyGroupResponse>> getAll() {
        List<StudyGroup> groups = service.findAll();
        List<StudyGroupResponse> response = groups.stream()
                .map(StudyGroupResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ============================
    // GET /api/study-groups/{groupId}
    // 스터디 그룹 단건 조회
    // ============================
    @GetMapping("/study-groups/{groupId}")
    public ResponseEntity<?> getById(@PathVariable Long groupId) {
        try {
            StudyGroup group = service.findById(groupId);
            return ResponseEntity.ok(StudyGroupResponse.fromEntity(group));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("스터디 그룹을 찾을 수 없습니다. ID: " + groupId);
        }
    }

    // ============================
    // POST /api/study-groups
    // 스터디 그룹 생성 (요청자 = leader)
    //  - JWT에서 userId 꺼내 leader_id로 사용
    // ============================
    @PostMapping("/study-groups")
    public ResponseEntity<?> create(
            @RequestBody StudyGroupRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        // 🔹 1. JWT 인증 실패 or Authorization 헤더 없음
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다. Authorization: Bearer <token> 헤더를 추가하세요.");
        }

        try {
            // 🔹 2. JWT에서 userId 추출
            Long userId = user.getUserId();

            // 🔹 3. 서비스 호출
            StudyGroup created = service.createGroup(request, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(StudyGroupResponse.fromEntity(created));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================
    // DELETE /api/study-groups/{id}
    // 스터디 그룹 삭제 (리더만)
    // ============================
    @DeleteMapping("/study-groups/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        try {
            Long requesterId = user.getUserId();
            service.deleteById(id, requesterId);  // 리더 체크 서비스에서
            return ResponseEntity.ok("스터디 그룹이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("삭제할 스터디 그룹이 존재하지 않습니다. ID: " + id);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }

    // ============================
    // GET /api/study-group-recommendations
    // 추천 그룹 목록 조회
    // ============================
    @GetMapping("/study-group-recommendations")
    public ResponseEntity<?> getRecommendedGroups(
            @RequestParam Double userLat,
            @RequestParam Double userLon,
            @RequestParam(name = "tags") String tags
    ) {
        try {
            List<String> tagList = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            List<RecommendedGroupDto> recommendedGroups =
                    service.findRecommendedGroups(userLat, userLon, tagList, 5.0);

            return ResponseEntity.ok(recommendedGroups);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("추천 그룹을 조회하는 중 오류가 발생했습니다.");
        }
    }

    // =====================================================================
    // 멤버 관련 API (Group_members)
    // =====================================================================

    // 그룹 멤버 전체 조회
    @GetMapping("/study-groups/{groupId}/members")
    public ResponseEntity<?> getGroupMembers(@PathVariable Long groupId) {
        try {
            List<GroupMemberResponse> members = service.getGroupMembers(groupId);
            return ResponseEntity.ok(members);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("그룹 멤버를 찾을 수 없습니다. groupId: " + groupId);
        }
    }

    // 특정 멤버 조회
    @GetMapping("/study-groups/{groupId}/members/{userId}")
    public ResponseEntity<?> getGroupMember(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        try {
            GroupMemberResponse member = service.getGroupMember(groupId, userId);
            return ResponseEntity.ok(member);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 멤버를 찾을 수 없습니다. groupId: " + groupId + ", userId: " + userId);
        }
    }

    // 리더 조회
    @GetMapping("/study-groups/{groupId}/leader")
    public ResponseEntity<?> getGroupLeader(@PathVariable Long groupId) {
        try {
            GroupMemberResponse leader = service.getGroupLeader(groupId);
            return ResponseEntity.ok(leader);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("리더 정보를 찾을 수 없습니다. groupId: " + groupId);
        }
    }

    // 가입 신청 (현재 로그인 유저 기준)
    @PostMapping("/study-groups/{groupId}/members")
    public ResponseEntity<?> requestJoinGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        try {
            Long userId = user.getUserId();
            GroupMemberResponse pendingMember = service.requestJoinGroup(groupId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(pendingMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 가입 승인 (리더만)
    @PostMapping("/study-groups/{groupId}/members/{userId}/approve")
    public ResponseEntity<?> approveMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        try {
            Long leaderId = currentUser.getUserId();
            service.approveMember(groupId, userId, leaderId);
            return ResponseEntity.ok("회원 가입이 승인되었습니다. groupId: " + groupId + ", userId: " + userId);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 가입 거절 (리더만)
    @PostMapping("/study-groups/{groupId}/members/{userId}/reject")
    public ResponseEntity<?> rejectMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        try {
            Long leaderId = currentUser.getUserId();
            service.rejectMember(groupId, userId, leaderId);
            return ResponseEntity.ok("회원 가입이 거절되었습니다. groupId: " + groupId + ", userId: " + userId);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =====================================================================
    // 스케줄 관련 API (Study_schedules)
    // =====================================================================

    // 일정 목록 조회
    @GetMapping("/study-groups/{groupId}/schedules")
    public ResponseEntity<?> getGroupSchedules(@PathVariable Long groupId) {
        try {
            List<StudyScheduleResponse> schedules = service.getGroupSchedules(groupId);
            return ResponseEntity.ok(schedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("일정 정보를 찾을 수 없습니다. groupId: " + groupId);
        }
    }

    // 일정 생성 (리더만)
    @PostMapping("/study-groups/{groupId}/schedules")
    public ResponseEntity<?> createSchedule(
            @PathVariable Long groupId,
            @RequestBody StudyScheduleRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        try {
            Long leaderId = currentUser.getUserId();
            StudyScheduleResponse created = service.createSchedule(groupId, leaderId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}