package com.hackaton.sentiment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SentimentStatsResponseDTO {

    private Long total;
    private Long positive;
    private Long negative;
    private Long neutral;
}
