package com.jg.SimulatorData.DTO;

import java.time.LocalDateTime;

public class PitStopRequest {
    private LocalDateTime pitTime;
    private String message;

    public PitStopRequest() {}

    public PitStopRequest(String message) {
        this.message = message;
    }

    public LocalDateTime getPitTime() {
        return pitTime;
    }

    public void setPitTime(LocalDateTime pitTime) {
        this.pitTime = pitTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

