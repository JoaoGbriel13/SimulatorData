package com.jg.SimulatorData.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SimulatorDataDTO {
    private String Driver;
    private String Track;
    private BigDecimal LapTimeNumeric;
    private String LapTimeFormatted;
    private BigDecimal TopSpeed;
    private String Session;
    private BigDecimal FuelTank;
    private BigDecimal FuelUsed;
    private double TrackTemp;
    private double WaterTemp;
    private double OilTemp;
    private String CreatedAt;
}
