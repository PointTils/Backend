package com.pointtils.pointtils.src.infrastructure.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_SERVICE_ACCOUNT:}")
    private String base64Key;

    @PostConstruct
    public void initFirebase() throws IOException {
        if (base64Key.isBlank()) {
            throw new IllegalStateException("Variável de ambiente FIREBASE_SERVICE_ACCOUNT não encontrada");
        }

        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        try (InputStream serviceAccount = new ByteArrayInputStream(decodedKey)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase inicializado com sucesso para envio de notificacoes push");
            }
        }
    }
}
