package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.core.config.ConfigService;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.entity.GroupMember;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.entity.Group;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.exception.UserException;
import cn.muzisheng.pear.model.*;
import cn.muzisheng.pear.properties.CacheProperties;
import cn.muzisheng.pear.utils.CamelToSnakeUtil;
import cn.muzisheng.pear.utils.ExpiredCache;
import cn.muzisheng.pear.core.Logger.LogService;
import cn.muzisheng.pear.utils.PluralUtil;
import org.apache.commons.cli.*;
import org.apache.commons.cli.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class ApplicationInitialization implements CommandLineRunner {
    private final UserDAO userDAO;
    private final ConfigService configService;
    private final static Logger logService= LoggerFactory.getLogger(ApplicationInitialization.class);
    public static AdminObject[] adminObjects;
    public static ExpiredCache<String,String> ConfigCache;
    public static ExpiredCache<String,String> EnvCache;

    @Autowired
    public ApplicationInitialization(UserDAO userDAO, CacheProperties cacheProperties, ConfigService configService) {
        this.userDAO = userDAO;
        this.configService = configService;
        ConfigCache=new ExpiredCache<String,String>().newExpiredCache(cacheProperties.getSize());
        EnvCache=new ExpiredCache<String,String>().newExpiredCache(cacheProperties.getSize());
    }

    @Override
    public void run(String... args) {
        // 命令行参数
        Options options = new Options();
        List<Option> optionList=new ArrayList<>();
        optionList.add(new Option("u", "superuser", true, "username"));
        optionList.add(new Option("p", "password", true, "user password"));
        optionList.add(new Option("h", "help", false, "help Information"));
        optionList.add(new Option("d", "driver", true, "database driven"));
        optionList.add(new Option("m", "memory", true, "sqlite is memory-enabled"));
        for(Option option:optionList){
            options.addOption(option);
        }
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;


        try {
            cmd = parser.parse(options, args, true);

            // 打印帮助信息
            if (cmd.hasOption("h")) {
                logService.info(String.format("%-15s %-15s%-15s %-15s", Constant.HELP_OPTION,Constant.HELP_LONG_OPTION,Constant.HELP_ARGUMENTS,Constant.HELP_DESCRIPTION));
                for (Option option : optionList) {
                    logService.info(String.format("-%-15s %-15s %-15s %-15s",option.getOpt(),option.getLongOpt(),option.getArgs()==1,option.getDescription()));
                }
                System.exit(1);
            }

            // 处理数据库参数，可以使用mysql与sqlite，并且配合memory参数使用sqlite内存模式。


            // 创建超级用户
            if (cmd.hasOption("u")&&cmd.hasOption("p")) {
                String email = cmd.getOptionValue("u");
                String password=cmd.getOptionValue("p");
                User user=userDAO.getUserByEmail(email);
                if(user!=null){
                     if(!userDAO.setPassword(user,password)){
                         throw new UserException(user.getEmail(), "failed to update password");
                     }
                     logService.warn("superuser updates passwords, the superuser is "+email);
                     System.exit(1);
                }else{
                    user=userDAO.createUser(email,password);
                    if(user==null){
                        logService.error("failed to create a user: "+email);
                        System.exit(1);
                    }
                }
                user.setStaff(true);
                user.setSuperUser(true);
                user.setActivated(true);
                user.setEnabled(true);
                if(!userDAO.save(user)){
                    throw new UserException(user.getEmail(), "failed to save superuser");
                }
                logService.warn("create a super administrator: "+email);
                System.exit(1);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        configService.checkValue(Constant.KEY_SITE_NAME, "pear", Constant.ConfigFormatText, true, true);
        adminObjects=getPearAdminObjects();
        buildAdminObjects(adminObjects);

    }
    /**
     * 处理AdminObject数据
     **/
    private void buildAdminObjects(AdminObject[] adminObjects){
        List<String> existsTableNames=new ArrayList<>();
        for (AdminObject adminObject : adminObjects) {
            try {
                build(adminObject);
            } catch (GeneralException e) {
                logService.error(e.getMessage());
                continue;
            }
            if (existsTableNames.contains(adminObject.getTableName())) {
                logService.warn(adminObject.getTableName() + " is exists");
                continue;
            }
            existsTableNames.add(adminObject.getTableName());
        }
    }
    /**
     * 处理AdminObject数据
     **/
    private void build(AdminObject adminObject) {
        if("".equals(adminObject.getPath())||adminObject.getPath()==null){
            adminObject.setPath(adminObject.getName().toLowerCase());
        }
        if("_".equals(adminObject.getPath())||"".equals(adminObject.getPath())){
            throw new GeneralException("invalid path");
        }
        adminObject.setTableName(CamelToSnakeUtil.toSnakeCase(adminObject.getName()));
        adminObject.setPluralName(PluralUtil.pluralize(adminObject.getName()));

        /**
         * 此处案例有填充filed字段,暂保留
         * */

        if((adminObject.getPrimaryKeys() == null||adminObject.getPrimaryKeys().length==0 )&&(adminObject.getUniqueKeys()==null||adminObject.getUniqueKeys().length==0)){
            throw new GeneralException(adminObject.getName()+" not has primaryKey or uniqueKeys");
        }
    }
    /**
     * 获取pear所有的内置类
     **/
    public AdminObject[] getPearAdminObjects(){
        List<AdminObject> adminObjects=new ArrayList<>();
        AdminObject userObject =new AdminObject();
        userObject.setModel(User.class);
        userObject.setGroup("Settings");
        userObject.setName("User");
        userObject.setDesc("Builtin user management system");
        userObject.setShows(new String[]{"id","email","displayName","isStaff","isSuperUser","enabled","activated","gmtModified","lastLogin","lastLoginIp","source","locale","timezone"});
        userObject.setEdits(new String[]{"email","password","displayName","firstName","lastName","isStaff","isSuperUser","enabled","activated","profile","source","locale","timezone"});
        userObject.setFilterables(new String[]{"gmtCreated", "gmtModified", "isStaff", "isSuperUser", "enabled", "activated"});
        userObject.setOrderables(new String[]{"gmtCreated", "gmtModified", "enabled", "activated"});
        userObject.setSearches(new String[]{"email", "displayName"});
        userObject.setOrders(new Order[]{new Order("gmtModified", "desc")});
        AdminIcon userIcon =new AdminIcon();
        userIcon.setSvg(Constant.ICON_SVG_ADDRESS);
        userObject.setIcon(userIcon);
        AdminAttribute IconAttribute =new AdminAttribute();
        IconAttribute.setWidget("password");
        HashMap<String, AdminAttribute> map=new HashMap<>();
        map.put("password", IconAttribute);
        userObject.setAttributes(map);
        adminObjects.add(userObject);

        AdminObject groupObject =new AdminObject();
        groupObject.setModel(Group.class);
        groupObject.setGroup("Settings");
        groupObject.setName("Group");
        groupObject.setDesc("A group describes a group of users. One user can be part of many groups and one group can have many users");
        groupObject.setShows(new String[]{"id","name","extra","gmtCreated","gmtModified"});
        groupObject.setEdits(new String[]{"id","name","extra","gmtModified"});
        groupObject.setOrderables(new String[]{"gmtModified"});
        groupObject.setSearches(new String[]{"name"});
        groupObject.setRequires(new String[]{"name"});
        AdminIcon groupIcon =new AdminIcon();
        groupIcon.setSvg("待定");
        groupObject.setIcon(groupIcon);
        adminObjects.add(groupObject);

        AdminObject groupMemberObject =new AdminObject();
        groupMemberObject.setModel(GroupMember.class);
        groupMemberObject.setGroup("Settings");
        groupMemberObject.setName("GroupMember");
        groupMemberObject.setDesc("Group members");
        groupMemberObject.setShows(new String[]{"id","userId","group","role","gmtCreated"});
        groupMemberObject.setFilterables(new String[]{"group","role","gmtCreated"});
        groupMemberObject.setOrderables(new String[]{"gmtCreated"});
        groupMemberObject.setSearches(new String[]{"user","group"});
        groupMemberObject.setRequires(new String[]{"user","group","role"});
        AdminIcon groupMemberIcon =new AdminIcon();
        groupMemberIcon.setSvg("待定");
        groupMemberObject.setIcon(groupMemberIcon);
        HashMap<String, AdminAttribute> groupMemberMap=new HashMap<>();
        AdminAttribute groupMemberAttribute =new AdminAttribute();
        groupMemberAttribute.setDefaultValue(Constant.GROUP_ROLE_ADMIN);
        AdminSelectOption[] groupMemberSelectOptions=new AdminSelectOption[]{
                new AdminSelectOption("admin",Constant.GROUP_ROLE_ADMIN),
                new AdminSelectOption("member",Constant.GROUP_ROLE_MEMBER)
        };
        groupMemberAttribute.setChoices(groupMemberSelectOptions);
        groupMemberMap.put("role", groupMemberAttribute);
        groupMemberObject.setAttributes(groupMemberMap);
        adminObjects.add(groupMemberObject);

        AdminObject configObject =new AdminObject();
        configObject.setModel(Config.class);
        configObject.setGroup("Settings");
        configObject.setName("Config");
        configObject.setDesc("System config with database backend, You can change it in admin page, and it will take effect immediately without restarting the server");
        configObject.setShows(new String[]{"key","value","autoload","pub","desc"});
        configObject.setEdits(new String[]{"key","value","autoload","pub","desc"});
        configObject.setOrderables(new String[]{"key"});
        configObject.setSearches(new String[]{"key","desc","value"});
        configObject.setRequires(new String[]{"key","value"});
        configObject.setFilterables(new String[]{"autoload","pub"});
        AdminIcon configIcon =new AdminIcon();
        configIcon.setSvg("待定");
        configObject.setIcon(configIcon);
        adminObjects.add(configObject);
        return adminObjects.toArray(new AdminObject[0]);
    }
}
