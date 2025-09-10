package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    SUPERUSER(0,"SUPERUSER"),
    ACCOUNTANT(1,"ACCOUNTANT"),
    STAFF(2,"STAFF"),
    CUSTOMER(3,"CUSTOMER");
    
    private final Integer code;
    private final String role;
    @Override
    public String toString(){
        return role;
    }
    /**
     * 角色权限检查, 判断当前角色是否有当前行为的权限
     **/
    public boolean checkPermissions(RoleEnum role){
        return this.code < role.code;
    }

    public static boolean contains(String role){
        for (RoleEnum value : RoleEnum.values()){
            if(value.role.equals(role)){
                return true;
            }
        }
        return false;
    }
    public static RoleEnum roleOf(String role){
        for (RoleEnum value : RoleEnum.values()) {
            if(value.role.equals(role)){
                return value;
            }
        }
        return null;
    }
    public static RoleEnum codeOf(Integer code){
        for (RoleEnum value : RoleEnum.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        return null;
    }
}
