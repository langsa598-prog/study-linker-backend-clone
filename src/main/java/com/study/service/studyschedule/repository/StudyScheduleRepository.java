package com.study.service.studyschedule.repository;

import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.dto.MyScheduleResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Long> {

    // 오늘 일정
    List<StudySchedule> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime start, LocalDateTime end);

    // 다가올 일정 (현재 이후)
    List<StudySchedule> findByStartTimeAfterOrderByStartTimeAsc(LocalDateTime now);

    // 🔹 특정 그룹의 모든 일정
    List<StudySchedule> findByGroupGroupId(Long groupId);


    @Query(
            value = """
            SELECT
                s.schedule_id,
                s.title,
                s.start_time,
                s.end_time,
                s.location,
                s.group_id
            FROM Study_schedules s
            JOIN Group_members gm
                ON s.group_id = gm.group_id
            WHERE gm.user_id = :userId
              AND gm.status = 'APPROVED'
            ORDER BY s.start_time DESC
        """,
            nativeQuery = true
    )
    List<Object[]> findRawMySchedules(Long userId);

    default List<MyScheduleResponse> getMySchedules(Long userId) {
        List<Object[]> rows = findRawMySchedules(userId);

        return rows.stream()
                .map(r -> new MyScheduleResponse(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (Timestamp) r[2],
                        (Timestamp) r[3],
                        (String) r[4],
                        (Double) r[5],
                        (Double) r[6],
                        ((Number) r[7]).longValue()
                ))
                .toList();
    }

   // List<MyScheduleResponse> getMySchedules(Long userId);
}