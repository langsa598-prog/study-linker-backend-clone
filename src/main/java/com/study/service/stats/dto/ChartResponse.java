// dto/ChartResponse.java
package com.study.service.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChartResponse {
    private List<String> labels;
    private List<Long> data;
}


