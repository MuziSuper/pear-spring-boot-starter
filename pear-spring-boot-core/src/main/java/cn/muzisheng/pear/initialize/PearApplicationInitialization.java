package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.CacheContainer;
import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.UserException;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.service.PermissionService;
import cn.muzisheng.pear.service.RoleService;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * pear框架初始化程序
 **/
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PearApplicationInitialization implements CommandLineRunner {
    private final UserDAO userDAO;
    private final ConfigService configService;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final CacheContainer cacheContainer;
    private final static Logger LOG = LoggerFactory.getLogger(PearApplicationInitialization.class);


    public PearApplicationInitialization(UserDAO userDAO, CacheConfig config, ConfigService configService, RoleService roleService, PermissionService permissionService,CacheContainer cacheContainer) {
        LOG.info("PearApplicationInitialization注册完成");
        this.userDAO = userDAO;
        this.configService = configService;
        this.permissionService = permissionService;
        this.roleService = roleService;
        this.cacheContainer = cacheContainer;

    }

    @Override
    public void run(String... args) {
        LOG.info("pear application initialization!");
        // 命令行参数
        Options options = new Options();
        List<Option> optionList = new ArrayList<>();
        optionList.add(new Option("u", "superuser", true, "username"));
        optionList.add(new Option("p", "password", true, "user password"));
        optionList.add(new Option("h", "help", false, "help Information"));
        optionList.add(new Option("d", "driver", true, "database driven"));
        optionList.add(new Option("m", "memory", true, "sqlite is memory-enabled"));
        for (Option option : optionList) {
            options.addOption(option);
        }
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args, true);
            // 打印帮助信息
            if (cmd.hasOption("h")) {
                LOG.info(String.format("%-15s %-15s%-15s %-15s", Constant.HELP_OPTION, Constant.HELP_LONG_OPTION, Constant.HELP_ARGUMENTS, Constant.HELP_DESCRIPTION));
                for (Option option : optionList) {
                    LOG.info(String.format("-%-15s %-15s %-15s %-15s", option.getOpt(), option.getLongOpt(), option.getArgs() == 1, option.getDescription()));
                }
                System.exit(1);
            }

            /*
             * 处理数据库参数，可以使用mysql与sqlite，并且配合memory参数使用sqlite内存模式
             */

            cacheContainer.createCache(Constant.PEAR_CONFIG_CACHE_NAME, String.class, String.class);
            cacheContainer.createCache(Constant.PEAR_ENV_CACHE_NAME, String.class, String.class);
            cacheContainer.createCache(Constant.PEAR_PERMISSION_CACHE_NAME, Long.class, long[].class);

            // Pear基础角色与权限入库
            permissionService.createPermission(Constant.ADMIN_CREATE,"ADMIN can be created.");
            permissionService.createPermission(Constant.ADMIN_DELETE,"ADMIN can be deleted.");
            permissionService.createPermission(Constant.ADMIN_UPDATE,"ADMIN can be updated.");
            permissionService.createPermission(Constant.ADMIN_SELECT,"ADMIN can be selected.");
            permissionService.createPermission(Constant.PERMISSION_WRITER,"Writable operation permission.");
            permissionService.createPermission(Constant.PERMISSION_SYNC,"The current permission group is locked and cannot be operated on.");
            permissionService.createPermission(Constant.ADMIN_NULL,"An empty operation indicates no operation permission at all.");
            roleService.createRole(Constant.ROLE_SUPERUSER,"Super Administrator, with all permissions.");
            roleService.createRole(Constant.ROLE_STAFF,"Employees have the permission to access the background system and query data.");
            roleService.createRole(Constant.ROLE_CUSTOMER,"Customers cannot log in to the background system.");
            roleService.createRole(Constant.ROLE_FINANCE,"The finance department can access the background system and read and write all data except for permissions.");
            roleService.createRole(Constant.ROLE_SECRETARY,"The secretary can access the background and read and write all data, but cannot modify the administrator's permissions.");
            permissionService.addPermissions(Constant.ROLE_SUPERUSER, Constant.ADMIN_CREATE, Constant.ADMIN_DELETE, Constant.ADMIN_UPDATE, Constant.ADMIN_SELECT, Constant.PERMISSION_WRITER);
            permissionService.addPermissions(Constant.ROLE_STAFF, Constant.ADMIN_SELECT);
            permissionService.addPermissions(Constant.ROLE_CUSTOMER, Constant.ADMIN_NULL);
            permissionService.addPermissions(Constant.ROLE_FINANCE, Constant.ADMIN_SELECT, Constant.ADMIN_UPDATE, Constant.ADMIN_CREATE, Constant.ADMIN_DELETE);
            permissionService.addPermissions(Constant.ROLE_SUPERUSER, Constant.ADMIN_CREATE, Constant.ADMIN_DELETE, Constant.ADMIN_UPDATE, Constant.ADMIN_SELECT, Constant.PERMISSION_WRITER);

            // 创建超级用户，不从这创建的，都是无任何权限的客户身份
            if (cmd.hasOption("u") && cmd.hasOption("p")) {
                String email = cmd.getOptionValue("u");
                String password = cmd.getOptionValue("p");
                if (password == null || password.isBlank()) {
                    LOG.error("superuser password cannot be empty, the superuser is {}", email);
                    throw new UserException(email, "password cannot be empty");
                }
                User user = userDAO.getUserByEmail(email);
                if (user != null) {
                    if (!userDAO.setPassword(user, password)) {
                        throw new UserException(user.getEmail(), "failed to update password");
                    }
                    LOG.warn("superuser updates passwords, the superuser is " + email);
                    System.exit(1);
                } else {
                    user = userDAO.createUser(email, password);
                    if (user == null) {
                        LOG.error("failed to create a user: " + email);
                        System.exit(1);
                    }
                }
               roleService.addUserRole(user.getId(), Constant.ROLE_SUPERUSER);
                user.setActivated(true);
                user.setEnabled(true);
                if (!userDAO.save(user)) {
                    throw new UserException(user.getEmail(), "failed to save superuser");
                }
                LOG.warn("create a super administrator: " + email);
                System.exit(1);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // pear配置入库
        configService.checkValue(Constant.KEY_SITE_NAME, "pear", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.ICON_SVG_ADDRESS, "../static/favicon.svg", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.ICON_SVG_ADDRESS, "../static/favicon.png", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_SIGNIN_URL, "/auth/login", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_SIGNUP_URL, "/auth/register", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_DASHBOARD_URL, "/auth/dashboard", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_SETTINGS_URL, "/auth/settings", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_LOGOUT_URL, "/auth/logout", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_RESET_PASSWORD_URL, "/auth/reset_password", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_SIGNIN_API, "/user/login", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_SIGNUP_API, "/user/register", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_INFO_API, "/user/info", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_RESET_PASSWORD_DONE_API, "/user/reset_password_done", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_CHANGE_EMAIL_DONE_API, "/user/change_email_done", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_LOGIN_NEXT, "/", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_USER_ID_TYPE, "email", Constant.ConfigFormatText, true, true);

        // 构建入口
        AdminContainer.buildAdminObjects(AdminContainer.getAllAdminObjects());
    }


}
