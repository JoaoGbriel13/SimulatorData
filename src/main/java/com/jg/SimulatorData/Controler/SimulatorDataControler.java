package com.jg.SimulatorData.Controler;

import com.jg.SimulatorData.DTO.PitStopRequest;
import com.jg.SimulatorData.GoogleService.GoogleService;
import com.jg.SimulatorData.Model.SimulatorData;
import com.jg.SimulatorData.Service.SimulatorDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PutMapping("/pitstop")
    public ResponseEntity<String> sendPitTime(@RequestBody PitStopRequest request) throws GeneralSecurityException, IOException {
        LocalDateTime pitTime = request.getPitTime();
        String sheetID = "1Ap0oMDUE7vsikIjnc7YndZYIZgeTp42CeIA2Mx7Gu9U";

        if (pitTime == null) {
            return ResponseEntity.badRequest().body("O campo pitTime é obrigatório.");
        }

        boolean isRaceDay = googleService.checkRaceDay(pitTime.toLocalDate(), sheetID);

        if (!isRaceDay) {
            return ResponseEntity.badRequest().body("A corrida não está acontecendo hoje.");
        }

        boolean success = googleService.updatePitStopOffset(pitTime, sheetID);

        if (!success) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Aguarde antes de chamar novamente.");
        }

        return ResponseEntity.ok("Offset atualizado com sucesso.");
    }
}
