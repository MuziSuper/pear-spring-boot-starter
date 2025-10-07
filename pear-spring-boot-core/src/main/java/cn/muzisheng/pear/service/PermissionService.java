package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.Permission;

public interface PermissionService {
    /**
     * 获取用户对应权限ID集,若缓存命中则直接返回
     *
     * @param userId 用户ID
     **/
    long[] getPermissionIds(long userId);

    /**
     * 根据权限Id获取权限对象
     *
     * @param permissionId 权限ID
     **/
    Permission getPermissionById(long permissionId);

    /**
     * 根据权限限定词获取权限对象
     *
     * @param permissionCode 权限限定词
     **/
    Permission getPermissionByCode(String permissionCode);

    /**
     * 验证用户的角色集是否持有此权限,缓存未命中则从数据库中查询
     *
     * @param userId       用户ID
     * @param permissionId 权限ID
     **/
    boolean hasPermission(long userId, long permissionId);

    /**
     * 创建新权限
     *
     * @param permissionCode 权限限定词
     **/
    boolean createPermission(String permissionCode, String description);

    /**
     * 删除权限,并清空所有权限缓存
     *
     * @param permissionId 权限ID
     **/
    boolean deletePermission(long permissionId);

    /**
     * 角色添加权限，并清空此角色
     *
     * @param roleId       角色ID
     * @param permissionId 权限ID
     **/
    boolean addPermission(long roleId, long permissionId);

    /**
     * 角色添加权限组,若权限或角色不存在则输出日志并跳过
     *
     * @param roleName        角色名
     * @param permissionCodes 权限限定词
     **/
    void addPermissions(String roleName, String... permissionCodes);

    /**
     * 角色添加一组权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限组ID
     **/
    boolean addPermissions(long roleId, long[] permissionIds);

    /**
     * 角色删除权限
     *
     * @param roleId       角色ID
     * @param permissionId 权限ID
     **/
    boolean removePermission(long roleId, long permissionId);

    /**
     * 角色删除一组权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限组ID
     **/
    boolean removePermissions(long roleId, long[] permissionIds);

}
