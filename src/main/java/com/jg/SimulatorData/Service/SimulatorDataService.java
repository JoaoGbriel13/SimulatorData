package com.jg.SimulatorData.Service;


import com.jg.SimulatorData.GoogleService.GoogleService;
import com.jg.SimulatorData.Model.SimulatorData;
import com.jg.SimulatorData.Repository.SimulatorDataRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;

@Service
public class SimulatorDataService {
    private final SimulatorDataRepository simulatorDataRepository;

    public SimulatorDataService(SimulatorDataRepository simulatorDataRepository) {
        this.simulatorDataRepository = simulatorDataRepository;
    }

    public SimulatorData saveData(SimulatorData simulatorData) throws GeneralSecurityException, IOException {
       simulatorDataRepository.save(simulatorData);
        if (simulatorData.getFuelUsed() > 0){
            Double fuelAVG = simulatorDataRepository.getFuelAVG(simulatorData.getCar(), simulatorData.getTrack());
            String lapAVGFormatted = formatLapTime(simulatorDataRepository.getLapTimeAVG(simulatorData.getCar(),
                    simulatorData.getTrack(), simulatorData.getTrackStateEnum(), simulatorData.getDriver()));
            GoogleService.writeData(simulatorData, fuelAVG, lapAVGFormatted);
        }
        return(simulatorData);
    }

    private String formatLapTime(BigDecimal lapTime) {
        if (lapTime == null || lapTime.compareTo(BigDecimal.ZERO) <= 0) {
            return "00:00:00.000";
        }

        lapTime = lapTime.setScale(3, RoundingMode.HALF_UP);

        int totalSeconds = lapTime.intValue();

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        BigDecimal fractionalPart = lapTime.remainder(BigDecimal.ONE); // Parte decimal
        int milliseconds = fractionalPart.multiply(BigDecimal.valueOf(1000)).intValue();

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

}
