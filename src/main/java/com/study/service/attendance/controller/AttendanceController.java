package com.study.service.attendance.controller;

import com.study.service.attendance.dto.AttendanceRequest;
import com.study.service.attendance.dto.AttendanceResponse;
import com.study.service.attendance.dto.AttendanceStatusUpdateRequest;
import com.study.service.attendance.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // 출석 전체 조회: GET /api/attendance
    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAll() {
        return ResponseEntity.ok(attendanceService.findAll());
    }

    // 출석 단건 조회: GET /api/attendance/{attendanceId}
    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> getOne(@PathVariable Long attendanceId) {
        return attendanceService.findById(attendanceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 특정 사용자의 출석 기록 전체 조회: GET /api/attendance/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttendanceResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.findByUser(userId));
    }

    // (참고) 스케줄별 출석 조회가 필요하면 유지 가능
    // GET /api/attendance/schedule/{scheduleId}
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<AttendanceResponse>> getBySchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(attendanceService.findBySchedule(scheduleId));
    }

    // 출석 기록 생성 또는 상태 갱신: POST /api/attendance
    @PostMapping
    public ResponseEntity<AttendanceResponse> recordAttendance(@RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.checkIn(request));
    }

    // 출석 상태 변경: PATCH /api/attendance/{attendanceId}
    @PatchMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> updateStatus(
            @PathVariable Long attendanceId,
            @RequestBody AttendanceStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(attendanceService.updateStatus(attendanceId, request));
    }

    // 출석 삭제: DELETE /api/attendance/{attendanceId}
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<String> deleteAttendance(@PathVariable Long attendanceId) {
        attendanceService.deleteById(attendanceId);
        return ResponseEntity.ok("출석이 성공적으로 삭제되었습니다!");
    }
}