package com.jg.SimulatorData.Service;


import com.jg.SimulatorData.GoogleService.GoogleService;
import com.jg.SimulatorData.Model.SimulatorData;
import com.jg.SimulatorData.Repository.SimulatorDataRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
            String lapAVGFormatted = formatLapTimeWithJavaTime(simulatorDataRepository.getLapTimeAVG(simulatorData.getCar(),
                    simulatorData.getTrack(), simulatorData.getTrackStateEnum(), simulatorData.getDriver()));
            GoogleService.writeData(simulatorData, fuelAVG, lapAVGFormatted);
        }
        return(simulatorData);
    }

    public static String formatLapTimeWithJavaTime(BigDecimal lapTime) {
        if (lapTime == null || lapTime.compareTo(BigDecimal.ZERO) <= 0) {
            return "00:00:00.000";
        }

        lapTime = lapTime.setScale(3, RoundingMode.HALF_UP);

        long totalSeconds = lapTime.longValue();
        Duration duration = Duration.ofSeconds(totalSeconds);

        long milliseconds = lapTime.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(1000)).longValue();
        duration = duration.plusMillis(milliseconds);

        LocalTime localTime = LocalTime.ofSecondOfDay(duration.getSeconds()).plusNanos(duration.getNano());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        return localTime.format(formatter);
    }

}
