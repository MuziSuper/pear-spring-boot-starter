package cn.muzisheng.pear.service;

import cn.muzisheng.pear.entity.Role;

public interface RoleService {
    /**
     * 创建角色
     * @return 创建成功则返回true
     */
    boolean createRole(String roleName, String description);
    /**
     * 根据RoleId更新角色
     * @return 更新成功则返回true
     */
    boolean updateRole(Role role);
    /**
     * 根据RoleId删除角色
     * @return 创建成功则返回true
     */
    boolean deleteRole(long roleId);
    /**
     * 根据roleId查询角色
     * @return 角色
     */
    Role getRole(long roleId);
    /**
     * 根据roleName查询角色
     * @return 角色
     */
    Role getRoleByName(String roleName);

    /**
     * 用户添加角色
     * @param userId 用户Id
     * @param roleId 角色Id
     * @return 角色
     */
    boolean addUserRole(long userId, long roleId);
    /**
     * 用户添加角色
     * @param userId 用户Id
     * @param roleName 角色名
     * @return 角色
     */
    boolean addUserRole(long userId, String roleName);
    /**
     * 用户添加角色组
     **/
    void addUserRoleGroup(long userId, long[] roleIds);
    /**
     * 用户删除角色
     **/
    boolean deleteUserRole(long userId, long roleId);

    /**
     * 用户删除角色组
     **/
    void deleteUserRoleGroup(long userId, long[] roleIds);

    void clearUserRole(long userId);
    /**
     *  用户更新整个角色组
     */
    void updateUserRoleGroup(long userId, long[] roleIds);

    /**
     * 获取用户角色组Ids
     * @return 角色组Ids
     **/
    long[] getRoles(long userId);
    /**
     * 获取用户角色名组
     * @return 角色名组
     **/
    String[] getRoleNames(long userId);
}
