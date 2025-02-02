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
import com.jg.SimulatorData.DTO.FuelAverageDTO;
import com.jg.SimulatorData.Model.SimulatorData;
import com.jg.SimulatorData.Utils.TokenManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

public class GoogleService {
    private static final String APP_NAME = "Sheets Automation";
    private static final String SPREADSHEET_ID = "1A8DfijhqqgSZGANv0H7_oh8Z0lFOpT-PjOu46pyOhiA";
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


    public static void writeData(SimulatorData simulatorData, FuelAverageDTO avgFuel, String avgLap) throws GeneralSecurityException, IOException {
        Sheets service = getGoogleSheetService();

        String range = "DriversDB!A:A";
        ValueRange response = service.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();

        int nextEmptyRow = findNextEmptyRow(values);
        String formattedAvgFuelDry = String.format(Locale.US, "%.2f", avgFuel.getFuelDry());
        String formattedAvgFuelWet = String.format(Locale.US, "%.2f", avgFuel.getFuelRain());
        int nameExistsRow = checkIfNameExists(values, simulatorData.getDriver());

        BatchUpdateValuesRequest body;
        if (nameExistsRow == -1) {
            // Novo registro
            body = new BatchUpdateValuesRequest()
                    .setValueInputOption("USER_ENTERED")
                    .setData(Arrays.asList(
                            new ValueRange()
                                    .setRange("DriversDB!A" + nextEmptyRow + ":A" + nextEmptyRow)
                                    .setValues(List.of(Collections.singletonList(simulatorData.getDriver()))),
                            new ValueRange()
                                    .setRange("DriversDB!C" + nextEmptyRow + ":F" + nextEmptyRow)
                                    .setValues(List.of(List.of("", 2600, formattedAvgFuelDry, formattedAvgFuelWet))),
                            fillConditionsColumn(simulatorData.getTrackStateEnum(), avgLap, nextEmptyRow)
                    ));
        } else {
            // Atualização de registro existente
            body = new BatchUpdateValuesRequest()
                    .setValueInputOption("USER_ENTERED")
                    .setData(Arrays.asList(
                            new ValueRange()
                                    .setRange("DriversDB!E" + nameExistsRow + ":F" + nameExistsRow)
                                    .setValues(List.of(List.of(formattedAvgFuelDry, formattedAvgFuelWet))),
                            fillConditionsColumn(simulatorData.getTrackStateEnum(), avgLap, nameExistsRow)
                    ));
        }
        service.spreadsheets().values().batchUpdate(SPREADSHEET_ID, body).execute();
    }
    public static int findNextEmptyRow(List<List<Object>> values) {
        if (values == null || values.isEmpty()) {
            return 1; //
        }
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).isEmpty() || values.get(i).get(0).toString().trim().isEmpty()) {
                return i + 1;
            }
        }
        return values.size() + 1;
    }

    public static ValueRange fillConditionsColumn(String condition, String avgLap, int row){
        return switch (condition.toLowerCase()) {
            case "dry" ->
                    new ValueRange().setRange("DriversDB!H" + row + ":H" + row).setValues(
                            List.of(Collections.singletonList(avgLap))
                    );
            case "mostly dry" -> new ValueRange().setRange("DriversDB!J" + row + ":J" + row).setValues(
                    List.of(Collections.singletonList(avgLap))
            );
            case "very lightly wet" -> new ValueRange().setRange("DriversDB!K" + row + ":K" + row).setValues(
                    List.of(Collections.singletonList(avgLap))
            );
            case "lightly wet" -> new ValueRange().setRange("DriversDB!L" + row + ":L" + row).setValues(
                    List.of(Collections.singletonList(avgLap))
            );
            case "moderately wet" -> new ValueRange().setRange("DriversDB!M" + row + ":M" + row).setValues(
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

    public static void unfilteredData(SimulatorData simulatorData) throws GeneralSecurityException, IOException {
        Sheets sheetsService = getGoogleSheetService();
        String range = "DB!A:A";
        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
        List<List<Object>> objects = response.getValues();
        int nextEmptyRow = findNextEmptyRow(objects);
        List<Object> insertableValues = getValuesList(simulatorData);

        BatchUpdateValuesRequest request = new BatchUpdateValuesRequest()
                .setValueInputOption("USER_ENTERED")
                .setData(
                        Arrays.asList(
                               new ValueRange()
                                       .setRange("DB!A" + nextEmptyRow + ":N" + nextEmptyRow)
                                       .setValues(Collections.singletonList(insertableValues)
                                       )
                        )
                );
        sheetsService.spreadsheets().values().batchUpdate(SPREADSHEET_ID, request).execute();
    }

    public static int checkIfNameExists(List<List<Object>> values, String name) {
        if (values == null || values.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (!row.isEmpty()) {
                String cellValue = row.get(0).toString().trim().toLowerCase();
                if (cellValue.equals(name.trim().toLowerCase())) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    private static List<Object> getValuesList(SimulatorData simulatorData){
        List<Object> values = Arrays.stream(simulatorData.getClass().getDeclaredFields())
                .filter(field -> !field.getName().equalsIgnoreCase("id"))
                .peek(field -> field.setAccessible(true))
                .map(field -> {
                    try {
                        return field.get(simulatorData);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
        return values;
    }

    public static List<String> getCarAndTrack() throws GeneralSecurityException, IOException {
        Sheets sheetsService = getGoogleSheetService();
        String carRange = "Stints!J10:J10";
        String trackRange = "Stints!L10:L10";
        List<String> carTrackList = new ArrayList<>();

        ValueRange carValueRange = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, carRange).execute();
        ValueRange trackValueRange = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, trackRange).execute();

        List<List<Object>> carValues = carValueRange.getValues();
        List<List<Object>> trackValues = trackValueRange.getValues();

        for (int i = 0; i < carValues.size(); i++){
            List<Object> carRow = carValues.get(i);
            List<Object> trackRow = trackValues.get(i);

            if (!carRow.isEmpty() && !trackRow.isEmpty()){
                carTrackList.add(carRow.get(i).toString());
                carTrackList.add(trackRow.get(i).toString());
            }
        }
        return carTrackList;
    }

}
