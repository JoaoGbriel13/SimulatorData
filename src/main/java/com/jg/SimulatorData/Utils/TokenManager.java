package com.jg.SimulatorData.Utils;

public class TokenManager {

    public static String getGoogleCredentials() {
        String credentialsJson = System.getenv("GOOGLE_CREDENTIALS");
        if (credentialsJson == null || credentialsJson.isEmpty()) {
            throw new IllegalStateException("A variável de ambiente 'GOOGLE_CREDENTIALS' não está configurada ou está vazia.");
        }
        return credentialsJson;
    }
}
