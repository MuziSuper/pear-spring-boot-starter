package cn.muzisheng.pear;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class SnowIdUtil {

    private static final long START_TIMESTAMP = 1557489395327L;
    /**
     * 序列号占用位数
     */
    private static final long SEQUENCE_BIT = 12;
    /**
     * 机器标识占用位数
     */
    private static final long MACHINE_BIT = 10;
    /**
     * 时间戳位移位数
     */
    private static final long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    /**
     * 最大序列号  （4095）
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    /**
     * 最大机器编号 （1023）
     */
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_BIT);
    /**
     * 生成id机器标识部分
     */
    private static long machineIdPart;
    /**
     * 序列号
     */
    private static long sequence = 0L;
    /**
     * 上一次时间戳
     */
    private static long lastStamp = -1L;

    /**
     * 构造函数初始化机器编码
     */
    private SnowIdUtil() {
        //模拟这里获得本机机器编码
        long localIp = 4321;
        //localIp & MAX_MACHINE_ID最大不会超过1023,在左位移12位
        machineIdPart = (localIp & MAX_MACHINE_ID) << SEQUENCE_BIT;
    }

    /**
     * 获取雪花ID
     */
    public synchronized static long nextId() {
        long currentStamp = timeGen();
        //避免机器时钟回拨
        while (currentStamp < lastStamp) {
            // //服务器时钟被调整了,ID生成器停止服务.
            throw new RuntimeException(String.format("时钟已经回拨.  Refusing to generate id for %d milliseconds", lastStamp - currentStamp));
        }
        if (currentStamp == lastStamp) {
            // 每次+1
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                currentStamp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置0
            sequence = 0L;
        }
        lastStamp = currentStamp;
        //时间戳部分+机器标识部分+序列号部分
        return (currentStamp - START_TIMESTAMP) << TIMESTAMP_LEFT | machineIdPart | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     */
    private static long getNextMill() {
        long mill = timeGen();
        //
        while (mill <= lastStamp) {
            mill = timeGen();
        }
        return mill;
    }

    /**
     * 返回以毫秒为单位的当前时间
     */
    protected static long timeGen() {
        return System.currentTimeMillis();
    }


    /**
     * 获取long类型雪花ID
     */
    public static long uniqueLong() {
        return SnowIdUtil.nextId();
    }

    /**
     * 获取String类型雪花ID
     */
    public static String uniqueLongHex() {
        return String.format("%016x", uniqueLong());
    }

    /**
     * 测试
     */
    public static void main(String[] args) throws InterruptedException {
        //计时开始时间
        long start = System.currentTimeMillis();
        //让100个线程同时进行
        final CountDownLatch latch = new CountDownLatch(100);
        //判断生成的20万条记录是否有重复记录
        final Map<Long, Integer> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 100; i++) {
            //创建100个线程
            new Thread(() -> {
                for (int s = 0; s < 2000; s++) {
                    long snowID = SnowIdUtil.uniqueLong();
                    System.out.println("生成雪花ID=" + snowID);
                    Integer put = map.put(snowID, 1);
                    if (put != null) {
                        throw new RuntimeException("主键重复");
                    }
                }
                latch.countDown();
            }).start();
        }
        //让上面100个线程执行结束后，在走下面输出信息
        latch.await();
        System.out.println("生成20万条雪花ID总用时=" + (System.currentTimeMillis() - start));
    }
}