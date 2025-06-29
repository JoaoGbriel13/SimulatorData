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
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class GoogleService {
    private static final String APP_NAME = "Sheets Automation";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKEN = TokenManager.getGoogleCredentials();
    private static final long COOLDOWN_PERIOD = 2 * 60 * 1000; // 2 minutos em milissegundos
    private final AtomicLong lastExecutionTime = new AtomicLong(0);

    public static Sheets getGoogleSheetService() throws GeneralSecurityException, IOException {
        ;
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


    public static void writeData(SimulatorData simulatorData, FuelAverageDTO avgFuel, String avgLap, String SPREADSHEET_ID) throws GeneralSecurityException, IOException {
        Sheets service = getGoogleSheetService();
        String range = "DriversDB!A:A";
        ValueRange response = service.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();

        int nextEmptyRow = findNextEmptyRow(values);
        String formattedAvgFuelDry = String.format(Locale.US, "%.2f",
                avgFuel.getFuelDry() != null ? avgFuel.getFuelDry() : 0.0);
        String formattedAvgFuelWet = String.format(Locale.US, "%.2f",
                avgFuel.getFuelRain() != null ? avgFuel.getFuelRain() : 0.0);
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

    public static ValueRange fillConditionsColumn(String condition, String avgLap, int row) {
        return switch (condition.toLowerCase()) {
            case "dry" -> new ValueRange().setRange("DriversDB!H" + row + ":H" + row).setValues(
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

    public static void unfilteredData(SimulatorData simulatorData, String SPREADSHEET_ID) throws GeneralSecurityException, IOException {
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

    private static List<Object> getValuesList(SimulatorData simulatorData) {
        return Arrays.stream(simulatorData.getClass().getDeclaredFields())
                .filter(field -> !field.getName().equalsIgnoreCase("id") &&
                        !field.getName().equalsIgnoreCase("sheetID"))
                .peek(field -> field.setAccessible(true))
                .map(field -> {
                    try {
                        return field.get(simulatorData);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    public static List<String> getCarAndTrack(String SPREADSHEET_ID) throws GeneralSecurityException, IOException {
        Sheets sheetsService = getGoogleSheetService();
        String carRange = "Stints Schedule!J10:J10";
        String trackRange = "Stints Schedule!L10:L10";
        List<String> carTrackList = new ArrayList<>();

        ValueRange carValueRange = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, carRange).execute();
        ValueRange trackValueRange = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, trackRange).execute();

        List<List<Object>> carValues = carValueRange.getValues();
        List<List<Object>> trackValues = trackValueRange.getValues();

        for (int i = 0; i < carValues.size(); i++) {
            List<Object> carRow = carValues.get(i);
            List<Object> trackRow = trackValues.get(i);

            if (!carRow.isEmpty() && !trackRow.isEmpty()) {
                carTrackList.add(carRow.get(i).toString());
                carTrackList.add(trackRow.get(i).toString());
            }
        }
        return carTrackList;
    }

    public boolean checkRaceDay(LocalDate eventDate, String SPREADSHEET_ID) throws GeneralSecurityException, IOException {
        Sheets service = getGoogleSheetService();
        String range = "Stints Schedule!J7";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        ValueRange response = service.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
        List<List<Object>> values = response.getValues();

        if (values != null && !values.isEmpty() && !values.get(0).isEmpty()) {
            LocalDate raceDay = LocalDate.parse(values.get(0).get(0).toString(), formatter);
            return raceDay.equals(eventDate);
        }

        return false;
    }

    public synchronized boolean updatePitStopOffset(LocalDateTime pitTime, String SPREADSHEET_ID) throws GeneralSecurityException, IOException {
        long currentTime = System.currentTimeMillis();

        // Verifica se o tempo decorrido desde a última execução é menor que o cooldown
        if (currentTime - lastExecutionTime.get() < COOLDOWN_PERIOD) {
            System.out.println("Aguarde antes de chamar novamente.");
            return false;
        }

        lastExecutionTime.set(currentTime); // Atualiza o tempo da última execução

        // Aqui vai a sua lógica original da função...
        Sheets service = getGoogleSheetService();

        String endTimeRange = "Stints Schedule!Q25:Q";
        String offSetRange = "Stints Schedule!L26:L";

        ValueRange endTimeResponse = service.spreadsheets().values().get(SPREADSHEET_ID, endTimeRange).execute();
        ValueRange offSetResponse = service.spreadsheets().values().get(SPREADSHEET_ID, offSetRange).execute();

        List<List<Object>> endTimesList = endTimeResponse.getValues();
        List<List<Object>> offSetList = offSetResponse.getValues();

        if (offSetList == null) {
            offSetList = new ArrayList<>();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 1; i < endTimesList.size(); i++) {
            if (i - 1 < offSetList.size() && !offSetList.get(i - 1).isEmpty()) {
                continue;
            }

            String previousEndTimeStr = endTimesList.get(i - 1).get(0).toString();
            LocalTime previousEndTime = LocalTime.parse(previousEndTimeStr, formatter);
            LocalDateTime previousEndDateTime = LocalDateTime.of(pitTime.toLocalDate(), previousEndTime);

            Duration offsetDuration = Duration.between(previousEndDateTime, pitTime);

            boolean isBefore = offsetDuration.isNegative();
            long hours = Math.abs(offsetDuration.toHours());
            long minutes = Math.abs(offsetDuration.toMinutes() % 60);
            long seconds = Math.abs(offsetDuration.getSeconds() % 60);

            String formattedOffset = String.format("%s%02d:%02d:%02d", isBefore ? "-" : "", hours, minutes, seconds);

            // Atualiza a célula correspondente na coluna L (Offset)
            String updateOffSetRange = "Stints Schedule!L" + (26 + i - 1);
            ValueRange body = new ValueRange().setValues(Collections.singletonList(Collections.singletonList(formattedOffset)));
            service.spreadsheets().values().update(SPREADSHEET_ID, updateOffSetRange, body).setValueInputOption("USER_ENTERED").execute();

            return true;
        }

        return false;
    }
}
