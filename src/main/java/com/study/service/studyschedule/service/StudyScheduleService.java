package com.study.service.studyschedule;

import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.domain.StudyScheduleStatus; // ★ 추가
import com.study.service.studyschedule.dto.MyScheduleResponse;
import com.study.service.studyschedule.dto.StudyScheduleRequest;
import com.study.service.studyschedule.dto.StudyScheduleStatusUpdateRequest;
import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.studygroup.repository.StudyGroupRepository;
import com.study.service.studyschedule.repository.StudyScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StudyScheduleService {

    private final StudyScheduleRepository scheduleRepository;
    private final StudyGroupRepository groupRepository;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StudyScheduleService(StudyScheduleRepository scheduleRepository,
                                StudyGroupRepository groupRepository) {
        this.scheduleRepository = scheduleRepository;
        this.groupRepository = groupRepository;
    }

    public List<StudySchedule> findAll() {
        return scheduleRepository.findAll();
    }

    public StudySchedule findById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new IllegalArgumentException("스터디 스케줄을 찾을 수 없습니다. ID: " + scheduleId));
    }

    @Transactional
    public StudySchedule save(StudyScheduleRequest request) {
        StudyGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() ->
                        new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다. ID: " + request.getGroupId()));

        StudySchedule schedule = new StudySchedule();
        schedule.setGroup(group);
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setLocation(request.getLocation());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        // status는 Entity 기본값 SCHEDULED 사용 (Enum)

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public StudySchedule update(Long scheduleId, StudyScheduleRequest request) {
        StudySchedule schedule = findById(scheduleId);

        if (request.getGroupId() != null) {
            StudyGroup group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다. ID: " + request.getGroupId()));
            schedule.setGroup(group);
        }

        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setLocation(request.getLocation());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteById(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }

    // 오늘 날짜의 모든 일정
    public List<StudySchedule> findTodaySchedules() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        return scheduleRepository.findByStartTimeBetweenOrderByStartTimeAsc(startOfDay, endOfDay);
    }

    // 다가올 일정 목록 (현재 이후)
    public List<StudySchedule> findUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        return scheduleRepository.findByStartTimeAfterOrderByStartTimeAsc(now);
    }

    // 상태 변경
    @Transactional
    public StudySchedule updateStatus(Long scheduleId, StudyScheduleStatusUpdateRequest request) {
        StudySchedule schedule = findById(scheduleId);

        // 요청 DTO는 String status라고 가정: "SCHEDULED", "IN_PROGRESS", "COMPLETED" 등
        String statusStr = request.getStatus();
        if (statusStr == null || statusStr.isBlank()) {
            throw new IllegalArgumentException("status 값이 비어 있습니다.");
        }

        try {
            // 문자열 → Enum으로 변환
            StudyScheduleStatus newStatus = StudyScheduleStatus.valueOf(statusStr.toUpperCase());
            schedule.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 상태 값입니다: " + statusStr);
        }

        return scheduleRepository.save(schedule);
    }


    public List<MyScheduleResponse> getMySchedules(Long userId) {
        return scheduleRepository.findMySchedules(userId);
    }
}