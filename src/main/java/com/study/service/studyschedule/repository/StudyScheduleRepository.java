package com.study.service.studyschedule.repository;

import com.study.service.studyschedule.domain.StudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Long> {

    // 오늘 일정
    List<StudySchedule> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime start, LocalDateTime end);

    // 다가올 일정 (현재 이후)
    List<StudySchedule> findByStartTimeAfterOrderByStartTimeAsc(LocalDateTime now);

    // 🔹 특정 그룹의 모든 일정
    List<StudySchedule> findByGroupGroupId(Long groupId);
}