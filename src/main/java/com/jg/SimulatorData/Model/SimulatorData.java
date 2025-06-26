package com.jg.SimulatorData.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_data")
@Data
public class SimulatorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("driver")
    private String driver;

    @JsonProperty("track")
    private String track;
    @JsonProperty("car")
    private String car;
    @JsonProperty("trackState")
    private String trackStateEnum;

    @JsonProperty("lapTimeNumeric")
    private BigDecimal lapTimeNumeric;

    @JsonProperty("lapTimeFormatted")
    private String lapTimeFormatted;

    @JsonProperty("topSpeed")
    private BigDecimal topSpeed;

    @JsonProperty("session")
    private String session;

    @JsonProperty("fuelTank")
    private double fuelTank;

    @JsonProperty("fuelUsed")
    private double fuelUsed;

    @JsonProperty("trackTemp")
    private double trackTemp;

    @JsonProperty("waterTemp")
    private double waterTemp;

    @JsonProperty("oilTemp")
    private double oilTemp;

    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("sheetID")
    private String sheetID;

    public SimulatorData(Long id, String driver, String track,
                         String car, String trackStateEnum,
                         BigDecimal lapTimeNumeric,
                         String lapTimeFormatted, BigDecimal topSpeed,
                         String session, double fuelTank, double fuelUsed, double trackTemp,
                         double waterTemp, double oilTemp, String createdAt, String sheetID) {
        this.id = id;
        this.driver = driver;
        this.track = track;
        this.car = car;
        this.trackStateEnum = trackStateEnum;
        this.lapTimeNumeric = lapTimeNumeric;
        this.lapTimeFormatted = lapTimeFormatted;
        this.topSpeed = topSpeed;
        this.session = session;
        this.fuelTank = fuelTank;
        this.fuelUsed = fuelUsed;
        this.trackTemp = trackTemp;
        this.waterTemp = waterTemp;
        this.oilTemp = oilTemp;
        this.createdAt = createdAt;
        this.sheetID = sheetID;
    }

    public SimulatorData() {
    }

}
