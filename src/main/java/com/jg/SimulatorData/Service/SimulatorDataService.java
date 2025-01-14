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
            System.out.println(fuelAVG);
            String lapAVGFormatted = formatLapTime(simulatorDataRepository.getLapTimeAVG(simulatorData.getCar(),
                    simulatorData.getTrack(), simulatorData.getTrackStateEnum(), simulatorData.getDriver()));
            GoogleService.writeData(simulatorData, fuelAVG, lapAVGFormatted);
        }
        return(simulatorData);
    }

    private String formatLapTime(BigDecimal lapTime) {
        if (lapTime == null || lapTime.compareTo(BigDecimal.ZERO) <= 0) {
            return "0:00.0"; // Retorna um tempo padrão se o valor for inválido.
        }

        // Arredondar o valor para 3 casas decimais (milissegundos)
        lapTime = lapTime.setScale(3, RoundingMode.HALF_UP);

        // Separar os valores em inteiros e decimais
        int totalSeconds = lapTime.intValue(); // Parte inteira
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // Milissegundos
        BigDecimal fractionalPart = lapTime.remainder(BigDecimal.ONE); // Parte decimal
        int milliseconds = fractionalPart.multiply(BigDecimal.valueOf(1000)).intValue();

        // Formatar o tempo no formato "minutos:segundos.milissegundos"
        return String.format("0%d:%02d.%d", minutes, seconds, milliseconds / 100);
    }
}
