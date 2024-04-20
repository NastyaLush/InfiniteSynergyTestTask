package test.laba.server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class PasswordHandler {
    private PasswordHandler() {
    }

    public static String encryptPass(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(AppConfig.getProperty("password.algorithm"));
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder encryptionValue = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hexVal = Integer.toHexString(0xff & b);
                if (hexVal.length() == 1) {
                    encryptionValue.append('0');
                }
                encryptionValue.append(hexVal);
            }
            return encryptionValue.toString();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}

