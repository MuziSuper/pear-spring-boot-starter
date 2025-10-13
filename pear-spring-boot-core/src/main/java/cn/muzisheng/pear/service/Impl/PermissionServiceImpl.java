package cn.muzisheng.pear.service.Impl;

import cn.muzisheng.pear.CacheContainer;
import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.dao.PermissionDAO;
import cn.muzisheng.pear.dao.RoleDAO;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.entity.Permission;
import cn.muzisheng.pear.entity.Role;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PermissionDAO permissionDAO;
    private final CacheContainer cacheContainer;
    private final Logger LOG = LoggerFactory.getLogger(PermissionServiceImpl.class);
    private final Config config;

    public PermissionServiceImpl(UserDAO userDAO, RoleDAO roleDAO, PermissionDAO permissionDAO, CacheContainer cacheContainer, Config config) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.permissionDAO = permissionDAO;
        this.cacheContainer = cacheContainer;
        this.config = config;
    }

    @Override
    public long[] getPermissionIds(long userId) {
        long[] permissionsIds;
        if (cacheContainer.contains(Constant.PEAR_PERMISSION_CACHE_NAME)) {
            permissionsIds = cacheContainer.get(Constant.PEAR_PERMISSION_CACHE_NAME, userId);
            if(permissionsIds != null&&permissionsIds.length!=0) return permissionsIds;
        }
        User user = userDAO.getUserById(userId);
        if (user == null) throw new AuthorizationException();
        long[] roleIds = roleDAO.getUserRole(user.getId());
        if (roleIds == null) return null;
        HashSet<Long> tempSet = new HashSet<>();
        for (long roleId : roleIds) {
            long[] permissionId = permissionDAO.getPermissions(roleId);
            if (permissionId != null) {
                for (long permission : permissionId) {
                    tempSet.add(permission);
                }
            }
        }
        permissionsIds = tempSet.stream().mapToLong(Long::longValue).toArray();
        if (cacheContainer.contains(Constant.PEAR_PERMISSION_CACHE_NAME)) {
            cacheContainer.put(Constant.PEAR_PERMISSION_CACHE_NAME, userId, permissionsIds);
        }
        return permissionsIds;

    }

    @Override
    public Permission getPermissionById(long permissionId) {
        return permissionDAO.getPermissionById(permissionId);
    }

    @Override
    public Permission getPermissionByCode(String permissionCode) {
        return permissionDAO.getPermissionByCode(permissionCode);
    }

    @Override
    public boolean hasPermission(long userId, long permissionId) {
        long[] permissionsIds = getPermissionIds(userId);
        if (permissionsIds == null) {
            return false;
        }
        for (long permission : permissionsIds) {
            if (permission == permissionId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean createPermission(String permissionCode, String description) {
        if (permissionDAO.getPermissionByCode(permissionCode) != null) return true;
        Permission permission = new Permission();
        permission.setPermissionCode(permissionCode);
        permission.setDescription(description);
        return permissionDAO.createPermission(permission);
    }

    @Override
    public boolean deletePermission(long permissionId) {
        if (!permissionDAO.deletePermission(permissionId)) {
            return false;
        }
        if (cacheContainer.contains(Constant.PEAR_PERMISSION_CACHE_NAME)) {
            cacheContainer.clear(Constant.PEAR_PERMISSION_CACHE_NAME);
        }
        return true;
    }

    @Override
    public boolean addPermission(long roleId, long permissionId) {
        Role role = roleDAO.getRoleById(roleId);
        if (role == null) return false;
        Permission permission = permissionDAO.getPermissionById(permissionId);
        if (permission == null) return false;
        if (permissionDAO.addPermission(roleId, permissionId)) {
            if (cacheContainer.contains(Constant.PEAR_PERMISSION_CACHE_NAME)) {
                cacheContainer.remove(Constant.PEAR_PERMISSION_CACHE_NAME, roleId);
            }
            return true;
        }
        return false;
    }


    private boolean addPermission(long roleId, String permissionCode) {
        Permission permission = permissionDAO.getPermissionByCode(permissionCode);
        if (permission == null) return false;
        return permissionDAO.addPermission(roleId, permission.getId());
    }

    @Override
    public void addPermissions(String roleName, String... permissionCodes) {
        if (permissionCodes == null) return;
        Role role = roleDAO.getRoleByName(roleName);
        if (role == null) return;
        for (String permissionCode : permissionCodes) {
            if (!addPermission(role.getId(), permissionCode))
                LOG.warn("addPermission fail, roleName: {} permissionCode: {}", roleName, permissionCode);
        }
    }


    @Override
    public boolean addPermissions(long roleId, long[] permissionIds) {
        Role role = roleDAO.getRoleById(roleId);
        if (role == null) return false;
        return permissionDAO.addPermissions(roleId, permissionIds);
    }

    @Override
    public boolean removePermission(long roleId, long permissionId) {
        return permissionDAO.removePermission(roleId, permissionId);
    }

    @Override
    public boolean removePermissions(long roleId, long[] permissionIds) {
        return permissionDAO.removePermissions(roleId, permissionIds);
    }


}
