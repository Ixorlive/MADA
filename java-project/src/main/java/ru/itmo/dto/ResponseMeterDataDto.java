package ru.itmo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ResponseMeterDataDto
{
    private boolean success;
    private List<String> meters;
    private int dateFrom;
    private int dateTo;
}
