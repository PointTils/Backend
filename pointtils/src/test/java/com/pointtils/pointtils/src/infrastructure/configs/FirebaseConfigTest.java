package com.pointtils.pointtils.src.infrastructure.configs;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FirebaseConfigTest {

    private final FirebaseConfig firebaseConfig = new FirebaseConfig();

    @Test
    void shouldThrowIllegalStateExceptionIfFirebaseServiceAccountIsBlank() {
        ReflectionTestUtils.setField(firebaseConfig, "base64Key", "");

        assertThrows(IllegalStateException.class, firebaseConfig::initFirebase);
    }
}
