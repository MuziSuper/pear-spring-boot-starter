package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CacheStrategyEnum {
    LRU(0,"LRU"),
    LFU(1,"LFU"),
    REDIS(2,"REDIS");
    private final Integer code;
    private final String strategy;

    public static boolean contains(String strategy){
        for (CacheStrategyEnum value : CacheStrategyEnum.values()){
            if(value.strategy.equals(strategy)){
                return true;
            }
        }
        return false;
    }
   public static CacheStrategyEnum strategyOf(String strategy){
       for (CacheStrategyEnum value : CacheStrategyEnum.values()) {
           if(value.strategy.equals(strategy)){
               return value;
           }
       }
       return null;
   }
   public static CacheStrategyEnum codeOf(Integer code){
        for (CacheStrategyEnum value : CacheStrategyEnum.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        return null;
   }
}
