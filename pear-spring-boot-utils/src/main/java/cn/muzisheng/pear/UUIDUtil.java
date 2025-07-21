package cn.muzisheng.pear;

import java.util.Random;
import java.util.UUID;

public class UUIDUtil {
    /**
     * 生成UUID
     **/
    public static String generateId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }
}
