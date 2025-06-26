package com.jg.SimulatorData.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor

public class PitStopRequest {
    private LocalDateTime pitTime;
    private String message;
    private String sheetID;

    public PitStopRequest() {}

}

