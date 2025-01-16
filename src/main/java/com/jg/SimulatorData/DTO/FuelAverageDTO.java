package com.jg.SimulatorData.DTO;

public class FuelAverageDTO {
    private Double fuelRain;
    private Double fuelDry;

    public FuelAverageDTO(Double fuelRain, Double fuelDry) {
        this.fuelRain = fuelRain;
        this.fuelDry = fuelDry;
    }

    public FuelAverageDTO() {
    }

    public Double getFuelRain() {
        return fuelRain;
    }

    public void setFuelRain(Double fuelRain) {
        this.fuelRain = fuelRain;
    }

    public Double getFuelDry() {
        return fuelDry;
    }

    public void setFuelDry(Double fuelDry) {
        this.fuelDry = fuelDry;
    }
}
