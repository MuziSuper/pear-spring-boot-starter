package cn.muzisheng.pear.service;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.exception.ScaleException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用户密码哈希服务，负责新 BCrypt 哈希和旧 SHA-256 哈希兼容。
 **/
@Service
public class PasswordService {
    private static final String BCRYPT_PREFIX = "{bcrypt}";
    private static final String LEGACY_SHA256_PATTERN = "^[0-9a-fA-F]{64}$";

    private final UserConfig userConfig;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    public PasswordService(UserConfig userConfig) {
        this.userConfig = userConfig == null ? new UserConfig.Builder().build() : userConfig;
        normalizeConfig(this.userConfig);
        this.bcryptPasswordEncoder = new BCryptPasswordEncoder(this.userConfig.getBcryptStrength());
    }

    /**
     * 生成带版本标识的 BCrypt 密码哈希。
     **/
    public String encode(String raw) {
        validateRawPassword(raw);
        return BCRYPT_PREFIX + bcryptPasswordEncoder.encode(raw);
    }

    /**
     * 校验原始密码与存储哈希是否匹配。
     **/
    public boolean matches(String raw, String stored) {
        validateRawPassword(raw);
        if (stored == null || stored.isBlank()) {
            return false;
        }
        if (stored.startsWith(BCRYPT_PREFIX)) {
            try {
                return bcryptPasswordEncoder.matches(raw, stored.substring(BCRYPT_PREFIX.length()));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        if (isLegacySha256Hash(stored)) {
            return legacyHash(raw).equalsIgnoreCase(stored);
        }
        return false;
    }

    /**
     * 判断存储哈希是否需要升级为当前格式。
     **/
    public boolean needsUpgrade(String stored) {
        return stored == null || !stored.startsWith(BCRYPT_PREFIX);
    }

    /**
     * 生成旧 SHA-256(passwordSalt + password) 哈希，仅用于兼容校验。
     **/
    public String legacyHash(String raw) {
        validateRawPassword(raw);
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] digest = sha256.digest((userConfig.getPasswordSalt() + raw).getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new ScaleException("The password encryption algorithm is not supported", e);
        }
    }

    /**
     * 判断存储值是否为旧 64 位十六进制 SHA-256 哈希。
     **/
    public boolean isLegacySha256Hash(String stored) {
        return stored != null && stored.matches(LEGACY_SHA256_PATTERN);
    }

    private void validateRawPassword(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalException("Password cannot be empty.");
        }
    }

    private void normalizeConfig(UserConfig config) {
        if (config.getPasswordSalt() == null) {
            config.setPasswordSalt(Constant.APP_USER_PASSWORD_SALT);
        }
        if (config.getBcryptStrength() == null) {
            config.setBcryptStrength(10);
        }
        if (config.getBcryptStrength() < 4 || config.getBcryptStrength() > 31) {
            throw new IllegalException("bcryptStrength must be between 4 and 31.");
        }
        if (config.getPasswordAlgorithm() == null || config.getPasswordAlgorithm().isBlank()) {
            config.setPasswordAlgorithm(UserConfig.PASSWORD_ALGORITHM_BCRYPT);
        }
        if (!UserConfig.PASSWORD_ALGORITHM_BCRYPT.equalsIgnoreCase(config.getPasswordAlgorithm())) {
            throw new IllegalException("Unsupported passwordAlgorithm: " + config.getPasswordAlgorithm());
        }
    }
}
