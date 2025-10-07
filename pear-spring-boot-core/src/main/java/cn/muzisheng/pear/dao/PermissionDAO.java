package cn.muzisheng.pear.dao;

import cn.muzisheng.pear.entity.Permission;
import cn.muzisheng.pear.entity.RolePermissionAss;
import cn.muzisheng.pear.mapper.PermissionMapper;
import cn.muzisheng.pear.mapper.RolePermissionAssMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionDAO {
    private static final Logger LOG = LoggerFactory.getLogger(PermissionDAO.class);
    private final PermissionMapper permissionMapper;
    private final RolePermissionAssMapper rolePermissionAssMapper;
    private final RoleDAO roleDAO;

    public PermissionDAO(PermissionMapper permissionMapper, RolePermissionAssMapper rolePermissionAssDAO, RoleDAO roleDAO) {
        this.permissionMapper = permissionMapper;
        this.rolePermissionAssMapper = rolePermissionAssDAO;
        this.roleDAO = roleDAO;
    }
    /**
     * 创建权限
     * @param permission 权限
     **/
    public boolean createPermission(Permission permission) {
        if(getPermissionByCode(permission.getPermissionCode())!=null) return true;
        return permissionMapper.insert(permission) > 0;
    }
    /**
     * 根据权限Id获取权限对象
     * @param permissionId 权限ID
     **/
    public Permission getPermissionById(long permissionId) {
        return permissionMapper.selectById(permissionId);
    }
    /**
     * 根据权限限定词获取权限对象
     * @param permissionCode 权限限定词
     **/
    public Permission getPermissionByCode(String permissionCode) {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_code", permissionCode);
        return permissionMapper.selectOne(queryWrapper);
    }
    /**
     * 获取权限Id集
     * @param roleId 角色Id
     **/
    public long[] getPermissions(long roleId) {
        QueryWrapper<RolePermissionAss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        List<RolePermissionAss> rolePermissionAsses =rolePermissionAssMapper.selectList(queryWrapper);
        long[] permissionIds = new long[rolePermissionAsses.size()];
        for (int i = 0; i < rolePermissionAsses.size(); i++) {
            permissionIds[i] = rolePermissionAsses.get(i).getPermissionId();
        }
        return permissionIds;
    }
    /**
     * 更新权限,主键Id为主键进行查找更新描述与权限限定词
     * @param permission 权限
     **/
    public boolean updatePermission(Permission permission) {
        if(permission.getId()==null) return false;
        UpdateWrapper<Permission> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", permission.getId());
        updateWrapper.set("description", permission.getDescription());
        updateWrapper.set("permission_code", permission.getPermissionCode());
        return permissionMapper.update(permission,updateWrapper)>0;
    }

    /**
     * 删除权限
     * @param permissionId 权限ID
     **/
    public boolean deletePermission(long permissionId) {
        // 删除权限
       if(permissionMapper.deleteById(permissionId)==0){
           return false;
       }
       // 删除角色权限关联表中存在的权限
       QueryWrapper<RolePermissionAss> queryWrapper = new QueryWrapper<>();
       queryWrapper.eq("permission_id", permissionId);
       return rolePermissionAssMapper.delete(queryWrapper) > 0;
    }
    /**
     * 添加角色的权限组一个权限
     *
     * @param roleId       角色ID
     * @param permissionId 权限ID
     **/
    public boolean addPermission(long roleId, long permissionId) {
        QueryWrapper<RolePermissionAss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        queryWrapper.eq("permission_id", permissionId);
        if(rolePermissionAssMapper.selectOne(queryWrapper)!=null) return true;
        RolePermissionAss rolePermissionAss = new RolePermissionAss();
        rolePermissionAss.setRoleId(roleId);
        rolePermissionAss.setPermissionId(permissionId);
        return rolePermissionAssMapper.insert(rolePermissionAss) > 0;
    }
    /**
     * 添加角色权限组的一组权限,若角色不存在则返回false,若权限不存在则输出日志并跳过
     * @param roleId 角色ID
     * @param permissionIds 权限ID组
     **/
    public boolean addPermissions(long roleId, long[] permissionIds) {
        if (permissionIds==null||permissionIds.length==0){
            return false;
        }
        if (roleDAO.getRoleById(roleId)==null) return false;
        for (long permissionId:permissionIds){
            if (permissionMapper.selectById(permissionId)==null){
                LOG.warn("addPermission fail, permissionId: {}", permissionId);
                continue;
            }
            addPermission(roleId, permissionId);
        }
        return true;
    }
    /**
     * 删除角色的权限组中某一权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     **/
    public boolean removePermission(long roleId, long permissionId) {
        QueryWrapper<RolePermissionAss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        queryWrapper.eq("permission_id", permissionId);
        return rolePermissionAssMapper.delete(queryWrapper) > 0;
    }
    /**
     * 删除角色的权限组中一组权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID组
     **/
    public boolean removePermissions(long roleId, long[] permissionIds) {
       if (permissionIds==null||permissionIds.length==0){
           return false;
       }
        QueryWrapper<RolePermissionAss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        queryWrapper.in("permission_id", permissionIds);
        return rolePermissionAssMapper.delete(queryWrapper) > 0;
    }

}