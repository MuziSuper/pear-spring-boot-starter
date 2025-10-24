package cn.muzisheng.pear.service.Impl;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.dao.RoleDAO;
import cn.muzisheng.pear.entity.Role;
import cn.muzisheng.pear.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final static Logger LOG = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final RoleDAO roleDAO;
    public RoleServiceImpl(RoleDAO roleDAO){
        this.roleDAO = roleDAO;
    }


    @Override
    public boolean createRole(String roleName,String description) {
        Role role = new Role();
        role.setRoleName(roleName);
        role.setDescription(description);
        return roleDAO.createRole(role);
    }

    @Override
    public boolean updateRole(Role role) {
        return roleDAO.updateRole(role);
    }

    @Override
    public boolean deleteRole(long roleId) {
        Role role = getRole(roleId);
        if(role==null) return false;
        if (pearHasRole(role)) return false;
        return roleDAO.deleteRole(roleId);
    }
    private boolean pearHasRole(Role role) {
        String roleName = role.getRoleName();
        return Constant.ROLE_SUPERUSER.equals(roleName)||Constant.ROLE_STAFF.equals(roleName)||Constant.ROLE_SECRETARY.equals(roleName)||Constant.ROLE_FINANCE.equals(roleName)||Constant.ROLE_CUSTOMER.equals(roleName);
    }
    @Override
    public Role getRole(long roleId) {
        return roleDAO.getRoleById(roleId);
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleDAO.getRoleByName(roleName);
    }

    @Override
    public boolean addUserRole(long userId, long roleId) {
        return roleDAO.addRole(userId, roleId);
    }
    @Override
    public boolean addUserRole(long userId, String roleName) {
        Role role = getRoleByName(roleName);
        if (role==null) return false;
        return roleDAO.addRole(userId,role.getId());
    }
    @Override
    public void addUserRoleGroup(long userId, long[] roleIds) {
        for (long roleId : roleIds) {
            addUserRole(userId, roleId);
        }
    }

    @Override
    public boolean deleteUserRole(long userId, long roleId) {
        Role role = getRole(roleId);
        if (role==null||Constant.ROLE_SUPERUSER.equals(role.getRoleName())) return false;
        return roleDAO.removeRole(userId, roleId);
    }

    @Override
    public void deleteUserRoleGroup(long userId, long[] roleIds) {
        for (long roleId : roleIds) {
            deleteUserRole(userId, roleId);
        }
    }

    @Override
    public void clearUserRole(long userId) {
        long[] roleIds =getRoles(userId);
        for(long roleId:roleIds){
            Role role = getRole(roleId);
            if (Constant.ROLE_SUPERUSER.equals(role.getRoleName())) return;
        }
        roleDAO.clearUserRole(userId);
    }

    @Override
    public void updateUserRoleGroup(long userId, long[] roleIds) {
        clearUserRole(userId);
        addUserRoleGroup(userId, roleIds);
    }

    @Override
    public long[] getRoles(long userId) {
        return roleDAO.getUserRole(userId);
    }

    @Override
    public String[] getRoleNames(long userId) {
        return roleDAO.getUserRoleName(userId);
    }
}
