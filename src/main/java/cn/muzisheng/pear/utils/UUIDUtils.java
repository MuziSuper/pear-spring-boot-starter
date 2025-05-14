package cn.muzisheng.pear.utils;

import java.util.Collections;
import java.util.Random;

public class UUIDUtils {
    public static String generateId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
