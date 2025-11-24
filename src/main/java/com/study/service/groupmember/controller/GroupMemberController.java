package com.study.service.groupmember.controller;

import com.study.service.groupmember.dto.GroupMemberResponse;
import com.study.service.groupmember.dto.GroupMemberStatusUpdateRequest;
import com.study.service.groupmember.service.GroupMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-members")
public class GroupMemberController {

    private final GroupMemberService service;

    public GroupMemberController(GroupMemberService service) {
        this.service = service;
    }

    // ============================
    // PATCH /api/group-members/{memberId}
    // 멤버 상태 변경 (Body: {"status": "..."})
    // ============================
    @PatchMapping("/{memberId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long memberId,
            @RequestBody GroupMemberStatusUpdateRequest request
    ) {
        try {
            GroupMemberResponse updated = service.updateStatus(memberId, request.getStatus());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            // 예: 멤버를 못 찾았거나 status 값이 잘못된 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ============================
    // DELETE /api/group-members/{memberId}
    // 멤버 삭제
    // ============================
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> delete(@PathVariable Long memberId) {
        try {
            service.deleteById(memberId);
            return ResponseEntity.ok("멤버가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("삭제할 멤버가 존재하지 않습니다. ID: " + memberId);
        }
    }
}