package com.jg.SimulatorData.Controler;

import com.jg.SimulatorData.DTO.PitStopRequest;
import com.jg.SimulatorData.GoogleService.GoogleService;
import com.jg.SimulatorData.Model.SimulatorData;
import com.jg.SimulatorData.Service.SimulatorDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/data")
public class SimulatorDataControler {
    private final SimulatorDataService simulatorDataService;
    private final GoogleService googleService;

    public SimulatorDataControler(SimulatorDataService simulatorDataService, GoogleService googleService) {
        this.simulatorDataService = simulatorDataService;
        this.googleService = googleService;
    }

    @PostMapping
    public ResponseEntity<SimulatorData> sendData(@RequestBody SimulatorData simulatorData) throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(simulatorDataService.saveData(simulatorData));
    }
    @PostMapping("/pitstop")
    public ResponseEntity<String> sendPitTime(@RequestBody LocalDateTime pitTime) throws GeneralSecurityException, IOException {
        boolean isRaceDay = googleService.checkRaceDay(pitTime.toLocalDate());

        if (!isRaceDay) {
            return ResponseEntity.badRequest().body("A corrida não está acontecendo hoje.");
        }

        boolean success = googleService.updatePitStopOffset(pitTime);

        if (success) {
            return ResponseEntity.ok("Offset atualizado com sucesso.");
        } else {
            return ResponseEntity.badRequest().body("Não foi possível atualizar o offset.");
        }
    }
}
