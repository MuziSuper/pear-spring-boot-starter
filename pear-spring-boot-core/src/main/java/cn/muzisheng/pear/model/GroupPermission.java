package cn.muzisheng.pear.model;

import lombok.Data;

/**
 * 组权限
 **/
@Data
public class GroupPermission {
    /**
     * permission format
     * users.read,users.create,users.update,users.delete, user.*
     * pages.publish,pages.update,page.delete,page.*
     **/
    private String[] permissions;
}
