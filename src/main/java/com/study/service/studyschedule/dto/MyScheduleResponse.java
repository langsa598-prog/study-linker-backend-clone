package com.study.service.studyschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class MyScheduleResponse {
    private Long scheduleId;
    private String title;
    private Timestamp startTime;
    private Timestamp endTime;
    private String location;
    private Double latitude;
    private Double longitude;
    private Long groupId;
}
