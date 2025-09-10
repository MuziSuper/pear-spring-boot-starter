package cn.muzisheng.pear.model;

import cn.muzisheng.pear.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.tomcat.util.bcel.Const;

@Getter
@AllArgsConstructor
public enum OperationEnum {
    ADMIN_CREATE(0,Constant.ADMIN_CREATE),
    ADMIN_DELETE(1, Constant.ADMIN_DELETE),
    ADMIN_UPDATE(2, Constant.ADMIN_UPDATE),
    ADMIN_SELECT(3, Constant.ADMIN_SELECT),
    ADMIN_OTHER(4, Constant.ADMIN_OTHER),
    ADMIN_ALL(5, Constant.ADMIN_ALL),
    ADMIN_NULL(6, Constant.ADMIN_NULL);

    private final Integer code;
    private final String operation;
    @Override
    public String toString(){
        return operation;
    }

    public static boolean contains(String operation){
        for (OperationEnum value : OperationEnum.values()){
            if(value.operation.equals(operation)){
                return true;
            }
        }
        return false;
    }
    public static OperationEnum operationOf(String operation){
        for (OperationEnum value : OperationEnum.values()) {
            if(value.operation.equals(operation)){
                return value;
            }
        }
        return null;
    }
    public static OperationEnum codeOf(Integer code){
        for (OperationEnum value : OperationEnum.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        return null;
    }
}
