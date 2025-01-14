package com.jg.SimulatorData.Controler;

import com.jg.SimulatorData.Model.SimulatorData;
import com.jg.SimulatorData.Service.SimulatorDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/data")
public class SimulatorDataControler {
    private final SimulatorDataService simulatorDataService;

    public SimulatorDataControler(SimulatorDataService simulatorDataService) {
        this.simulatorDataService = simulatorDataService;
    }

    @PostMapping
    public ResponseEntity<SimulatorData> sendData(@RequestBody SimulatorData simulatorData) throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(simulatorDataService.saveData(simulatorData));
    }
}
