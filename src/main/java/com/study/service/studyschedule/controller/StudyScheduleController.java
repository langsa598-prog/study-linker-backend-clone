package com.study.service.studyschedule.controller;

import com.study.service.security.CustomUserDetails;
import com.study.service.studyschedule.service.StudyScheduleService;
import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.dto.MyScheduleResponse;
import com.study.service.studyschedule.dto.StudyScheduleRequest;
import com.study.service.studyschedule.dto.StudyScheduleResponse;
import com.study.service.studyschedule.dto.StudyScheduleStatusUpdateRequest;
import com.study.service.studyschedule.repository.StudyScheduleRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/study-schedules")
public class StudyScheduleController {

    private final StudyScheduleService service;
    private final StudyScheduleRepository studyScheduleRepository;

    public StudyScheduleController(StudyScheduleService service, StudyScheduleRepository studyScheduleRepository) {
        this.service = service;
        this.studyScheduleRepository = studyScheduleRepository;
    }

    // 일정 전체 조회: GET /api/study-schedules
    @GetMapping
    public List<StudyScheduleResponse> getAll() {
        return service.findAll().stream()
                .map(StudyScheduleResponse::new)
                .collect(Collectors.toList());
    }

    // 일정 단건 조회: GET /api/study-schedules/{scheduleId}
    @GetMapping("/{scheduleId}")
    public StudyScheduleResponse getById(@PathVariable Long scheduleId) {
        StudySchedule schedule = service.findById(scheduleId);
        return new StudyScheduleResponse(schedule);
    }

    // 오늘 날짜의 일정 조회: GET /api/study-schedules/today
    @GetMapping("/today")
    public List<StudyScheduleResponse> getTodaySchedules() {
        return service.findTodaySchedules().stream()
                .map(StudyScheduleResponse::new)
                .collect(Collectors.toList());
    }

    // 다가올 일정 목록 조회: GET /api/study-schedules/upcoming
    @GetMapping("/upcoming")
    public List<StudyScheduleResponse> getUpcomingSchedules() {
        return service.findUpcomingSchedules().stream()
                .map(StudyScheduleResponse::new)
                .collect(Collectors.toList());
    }

    // 일정 생성: POST /api/study-schedules
    @PostMapping
    public StudyScheduleResponse create(@RequestBody StudyScheduleRequest request) {
        StudySchedule schedule = service.save(request);
        return new StudyScheduleResponse(schedule);
    }

    // 일정 수정: PUT /api/study-schedules/{scheduleId}
    @PutMapping("/{scheduleId}")
    public StudyScheduleResponse update(@PathVariable Long scheduleId,
                                        @RequestBody StudyScheduleRequest request) {
        StudySchedule schedule = service.update(scheduleId, request);
        return new StudyScheduleResponse(schedule);
    }

    // 일정 상태 변경: PATCH /api/study-schedules/{scheduleId}/status
    @PatchMapping("/{scheduleId}/status")
    public StudyScheduleResponse updateStatus(@PathVariable Long scheduleId,
                                              @RequestBody StudyScheduleStatusUpdateRequest request) {
        StudySchedule schedule = service.updateStatus(scheduleId, request);
        return new StudyScheduleResponse(schedule);
    }

    // 일정 삭제: DELETE /api/study-schedules/{scheduleId}
    @DeleteMapping("/{scheduleId}")
    public void delete(@PathVariable Long scheduleId) {
        service.deleteById(scheduleId);
    }

    // 📌 내 전체 일정 조회
    @GetMapping("/me")
    public List<MyScheduleResponse> getMySchedules(CustomUserDetails user) {

        return studyScheduleRepository.getMySchedules(user.getUserId());
    }
}