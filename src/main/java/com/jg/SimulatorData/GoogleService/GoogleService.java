package com.jg.SimulatorData.GoogleService;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.jg.SimulatorData.Model.SimulatorData;
import com.jg.SimulatorData.Utils.TokenManager;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleService {
    private static final String APP_NAME = "Sheets Automation";
    private static final String SPREADSHEET_ID = "16u-rvkNvLXTB-US37NbG-rhrcl622seuuFvwFMFWOSw";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKEN = TokenManager.getGoogleCredentials();

    public static Sheets getGoogleSheetService() throws GeneralSecurityException, IOException {;
        GoogleCredentials credentials;
        try {
            credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(TOKEN.getBytes(StandardCharsets.UTF_8))
            ).createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar as credenciais do Google a partir do JSON: " + e.getMessage(), e);
        }

        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credentials);
        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                httpRequestInitializer
        ).setApplicationName(APP_NAME).build();
    }


    public static void writeData(SimulatorData simulatorData, Double avgFuel, String avgLap) throws GeneralSecurityException, IOException {
        Sheets service = getGoogleSheetService();

        String range =  "DriversDB!A:A";

        ValueRange response = service.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();
        int nextEmptyRow = (values == null || values.isEmpty()) ? 1 : values.size() + 1;
        String formattedAvgFuel = String.format(Locale.US, "%.2f", avgFuel);
        int nameExistsRow = checkIfNameExists(values, simulatorData.getDriver());


        BatchUpdateValuesRequest body;
        if (nameExistsRow == - 1) {
            body = new BatchUpdateValuesRequest()
                    .setValueInputOption("RAW")
                    .setData(Arrays.asList(
                            new ValueRange()
                                    .setRange("DriversDB!A" + nextEmptyRow + ":A" + nextEmptyRow)
                                    .setValues(List.of(Collections.singletonList(simulatorData.getDriver())
                                    )),
                            new ValueRange()
                                    .setRange("DriversDB!C" + nextEmptyRow + ":F" + nextEmptyRow)
                                    .setValues(List.of(List.of("", 2600, formattedAvgFuel, formattedAvgFuel))),
                            fillConditionsColumn(simulatorData.getTrackStateEnum(), avgLap, nextEmptyRow)
                    ));
        }else{
            body = new BatchUpdateValuesRequest()
                    .setValueInputOption("RAW")
                    .setData(Arrays.asList(
                            new ValueRange()
                                    .setRange("DriversDB!D" + nameExistsRow + ":F" + nameExistsRow)
                                    .setValues(List.of(List.of(2600, formattedAvgFuel, formattedAvgFuel))),
                            fillConditionsColumn(simulatorData.getTrackStateEnum(), avgLap, nameExistsRow)
                    ));
        }
        service.spreadsheets().values().batchUpdate(SPREADSHEET_ID, body).execute();
    }

    public static ValueRange fillConditionsColumn(String condition, String avgLap, int row){
        return switch (condition.toLowerCase()) {
            case "dry" ->
                    new ValueRange().setRange("DriversDB!H" + row + ":H" + row).setValues(List.of(Collections.singletonList(
                            avgLap
                    )));
            case "mostly dry" -> new ValueRange().setRange("DriversDB!J" + row + ":J" + row).setValues(
                    List.of(Collections.singletonList(avgLap))
            );
            case "very lightly wet" -> new ValueRange().setRange("DriversDB!K" + row + ":K" + row).setValues(
                    List.of(Collections.singletonList(avgLap))
            );
            case "lightly wet" -> new ValueRange().setRange("DriversDB!L" + row + ":L" + row).setValues(
                    List.of(Collections.singletonList(avgLap))
            );
            case "wet" -> new ValueRange().setRange("DriversDB!M" + row + ":M" + row).setValues(
                    List.of(Collections.singletonList(avgLap))
            );
            case "very wet" -> new ValueRange().setRange("DriversDB!N" + row + ":N" + row).setValues(
                    List.of(Collections.singletonList(avgLap))
            );
            case "extremely wet" -> new ValueRange().setRange("DriversDB!O" + row + ":O" + row).setValues(
                    List.of(Collections.singletonList(avgLap))
            );
            default -> null;
        };
    }

    public static int checkIfNameExists(List<List<Object>> values, String name){
        int rowIndex = -1;

        for (int i = 0; i < values.size(); i++){
            List<Object> row = values.get(i);
            if (!row.isEmpty() && row.get(0).toString().equalsIgnoreCase(name)){
                return i + 1;
            }
        }
        return rowIndex;
    }


}
