package com.jg.SimulatorData.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_data")
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

    public SimulatorData(Long id, String driver, String track,
                         String car, String trackStateEnum,
                         BigDecimal lapTimeNumeric,
                         String lapTimeFormatted, BigDecimal topSpeed,
                         String session, double fuelTank, double fuelUsed, double trackTemp,
                         double waterTemp, double oilTemp, String createdAt) {
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
    }

    public SimulatorData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getTrackStateEnum() {
        return trackStateEnum;
    }

    public void setTrackStateEnum(String trackStateEnum) {
        this.trackStateEnum = trackStateEnum;
    }

    public BigDecimal getLapTimeNumeric() {
        return lapTimeNumeric;
    }

    public void setLapTimeNumeric(BigDecimal lapTimeNumeric) {
        this.lapTimeNumeric = lapTimeNumeric;
    }

    public String getLapTimeFormatted() {
        return lapTimeFormatted;
    }

    public void setLapTimeFormatted(String lapTimeFormatted) {
        this.lapTimeFormatted = lapTimeFormatted;
    }

    public BigDecimal getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(BigDecimal topSpeed) {
        this.topSpeed = topSpeed;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public double getFuelTank() {
        return fuelTank;
    }

    public void setFuelTank(double fuelTank) {
        this.fuelTank = fuelTank;
    }

    public double getFuelUsed() {
        return fuelUsed;
    }

    public void setFuelUsed(double fuelUsed) {
        this.fuelUsed = fuelUsed;
    }

    public double getTrackTemp() {
        return trackTemp;
    }

    public void setTrackTemp(double trackTemp) {
        this.trackTemp = trackTemp;
    }

    public double getWaterTemp() {
        return waterTemp;
    }

    public void setWaterTemp(double waterTemp) {
        this.waterTemp = waterTemp;
    }

    public double getOilTemp() {
        return oilTemp;
    }

    public void setOilTemp(double oilTemp) {
        this.oilTemp = oilTemp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
