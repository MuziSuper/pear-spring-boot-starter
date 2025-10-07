package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.entity.Role;
import cn.muzisheng.pear.entity.UserRoleAss;
import cn.muzisheng.pear.mapper.RoleMapper;
import cn.muzisheng.pear.mapper.UserRoleAssMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RoleDAO {
    private final RoleMapper roleMapper;
    private final UserRoleAssMapper userRoleAssMapper;
    public RoleDAO(RoleMapper roleMapper, UserRoleAssMapper userRoleAssMapper) {
        this.roleMapper = roleMapper;
        this.userRoleAssMapper = userRoleAssMapper;
    }
    /**
     * 创建角色
     * @param role 角色
     * @return 创建成功则返回true
     **/
    public boolean createRole(Role role) {
        if(getRoleByName(role.getRoleName())!=null) return true;
        return roleMapper.insert(role) > 0;
    }
    /**
     * 删除角色
     * @param id 角色id
     * @return 如果删除成功则返回true
     **/
    public boolean deleteRole(long id) {
        return roleMapper.deleteById(id) > 0;
    }
    /**
     * 更新角色
     * @param role 角色
     * @return 如果更新成功则返回true
     **/
    public boolean updateRole(Role role) {
        return roleMapper.updateById(role) > 0;
    }

    /**
     * 根据RoleId获取角色
     * @param roleId 角色Id
     * @return Role 角色
     **/
    public Role getRoleById(long roleId) {
        return roleMapper.selectById(roleId);
    }
    /**
     * 根据角色名获取角色
     * @param roleName 角色名
     * @return 角色
     **/
    public Role getRoleByName(String roleName) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name", roleName);
        List<Role> roles =roleMapper.selectList(queryWrapper);
        return roleMapper.selectOne(queryWrapper);
    }

    /**
     * 用户添加角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 如果添加角色成功或该角色存在则返回true
     **/
    public boolean addRole(long userId, long roleId){
        UserRoleAss userRoleAss = new UserRoleAss();
        userRoleAss.setUserId(userId);
        userRoleAss.setRoleId(roleId);
        QueryWrapper<UserRoleAss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("role_id", roleId);
        if (userRoleAssMapper.selectOne(queryWrapper) == null) {
            return userRoleAssMapper.insert(userRoleAss)> 0;
        }
        return true;
    }
    /**
     * 用户删除角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 如果删除角色成功或该角色不存在则返回true
     **/
    public boolean removeRole(long userId, long roleId) {
        QueryWrapper<UserRoleAss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("role_id", roleId);
        return userRoleAssMapper.delete(queryWrapper) > 0;
    }
    /**
     * 用户清空
     * @param userId 用户ID
     **/
    public void clearUserRole(long userId) {
        QueryWrapper<UserRoleAss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        userRoleAssMapper.delete(queryWrapper);
    }
    /**
     * 根据userId获取用户角色Id组
     * @param userId 用户Id
     * @return 角色id组
     **/
    public long[] getUserRole(long userId) {
        QueryWrapper<UserRoleAss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<UserRoleAss> userRoleAsses = userRoleAssMapper.selectList(queryWrapper);
        ArrayList<Long> roleIds = new ArrayList<>(userRoleAsses.size());
        for (UserRoleAss userRoleAss : userRoleAsses) {
            roleIds.add(userRoleAss.getRoleId());
        }
        return roleIds.stream().mapToLong(Long::longValue).toArray();
    }
    /**
     * 根据userId获取用户角色名组
     * @param userId 用户Id
     * @return 角色名组
     **/
    public String[] getUserRoleName(long userId) {
        long[] roleIds = getUserRole(userId);
        String[] roleNames = new String[roleIds.length];
        Role role;
        for (int i = 0; i < roleIds.length; i++) {
            role = getRoleById(roleIds[i]);
            roleNames[i] = role.getRoleName();
        }
        return roleNames;
    }

}
