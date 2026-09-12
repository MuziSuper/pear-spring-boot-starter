package cn.muzisheng.pear.service;

import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.exception.IllegalException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordServiceTest {

    @Test
    void encodeUsesVersionedBcryptHashAndMatchesPassword() {
        PasswordService passwordService = passwordService();

        String hash = passwordService.encode("secret-password");

        assertTrue(hash.startsWith("{bcrypt}"));
        assertNotEquals("secret-password", hash);
        assertFalse(passwordService.isLegacySha256Hash(hash));
        assertTrue(passwordService.matches("secret-password", hash));
        assertFalse(passwordService.matches("wrong-password", hash));
        assertFalse(passwordService.needsUpgrade(hash));
    }

    @Test
    void legacyHashIsRecognizedAndNeedsUpgrade() {
        PasswordService passwordService = passwordService();

        String legacyHash = passwordService.legacyHash("secret-password");

        assertTrue(legacyHash.matches("^[0-9a-f]{64}$"));
        assertTrue(passwordService.isLegacySha256Hash(legacyHash));
        assertTrue(passwordService.matches("secret-password", legacyHash));
        assertFalse(passwordService.matches("wrong-password", legacyHash));
        assertTrue(passwordService.needsUpgrade(legacyHash));
    }

    @Test
    void rejectsInvalidPasswordConfiguration() {
        assertDoesNotThrow(() -> new PasswordService(new UserConfig.Builder().bcryptStrength(4).build()));
        assertDoesNotThrow(() -> new PasswordService(new UserConfig.Builder().bcryptStrength(31).build()));
        assertThrows(IllegalException.class, () -> new PasswordService(new UserConfig.Builder().bcryptStrength(3).build()));
        assertThrows(IllegalException.class, () -> new PasswordService(new UserConfig.Builder().bcryptStrength(32).build()));
        assertThrows(IllegalException.class, () -> new PasswordService(new UserConfig.Builder().passwordAlgorithm("sha256").build()));
    }

    @Test
    void rejectsBlankRawPassword() {
        PasswordService passwordService = passwordService();

        assertThrows(IllegalException.class, () -> passwordService.encode(" "));
        assertThrows(IllegalException.class, () -> passwordService.matches("", "{bcrypt}hash"));
        assertThrows(IllegalException.class, () -> passwordService.legacyHash(null));
    }

    private static PasswordService passwordService() {
        return new PasswordService(new UserConfig.Builder()
                .passwordSalt("legacy-test-salt")
                .bcryptStrength(4)
                .build());
    }
}
