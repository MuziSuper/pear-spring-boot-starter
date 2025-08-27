package cn.muzisheng.pear;


import java.util.ArrayList;

public class RandomStrUtil {
    private static final char[] CHAR_LIST=new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    public static synchronized String getRandomStr(int length) {
        if (length<=0) {
            throw new IllegalArgumentException("length must be greater than 0");
        }else if (length>64) {
            length=64;
        }
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<length;i++) {
            int index=(int)(Math.random()*CHAR_LIST.length);
            sb.append(CHAR_LIST[index]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(getRandomStr(0));
    }
}
