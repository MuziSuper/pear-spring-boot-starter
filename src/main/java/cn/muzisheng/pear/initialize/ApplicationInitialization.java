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
import cn.muzisheng.pear.handler.*;
import cn.muzisheng.pear.model.*;
import cn.muzisheng.pear.properties.CacheProperties;
import cn.muzisheng.pear.utils.CamelToSnakeUtil;
import cn.muzisheng.pear.utils.ExpiredCache;
import cn.muzisheng.pear.core.Logger.LogService;
import cn.muzisheng.pear.utils.PluralUtil;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import org.apache.commons.cli.*;
import org.apache.commons.cli.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


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

        /*
          此处案例有填充filed字段,暂保留
         */
        parseFields(adminObject,adminObject.getModel());

        if((adminObject.getPrimaryKeys() == null|| adminObject.getPrimaryKeys().isEmpty())&&(adminObject.getUniqueKeys()==null||adminObject.getUniqueKeys().isEmpty())){
            throw new GeneralException(adminObject.getName()+" not has primaryKey or uniqueKeys");
        }
    }
    /**
     * 解析字段，将所有字段经过判断处理后存入AdminObject的fields字段中
     * @param adminObject 通用类对象
     * @param model 实体类的Class对象
     **/
    public void parseFields(AdminObject adminObject, Class<?> model) {
        // 此adminObject的AminField集合
        List<AdminField> list=new ArrayList<>();

        // 获取model的所有字段
        for (Field field : model.getDeclaredFields()) {
            boolean isForeignID=false;
            AdminField adminField=new AdminField();
            // 忽略钩子方法
            if(field.getType()== BeforeCreate.class||field.getType()== BeforeUpdate.class||field.getType()== BeforeDelete.class||field.getType()== BeforeRender.class||field.getType()== AccessCheck.class||field.getType()== AdminViewOnSite.class||field.getType()== BeforeQueryRenderFunc.class){
                continue;
            }
            // adminField存入name
            adminField.setName(field.getName());
            // adminField存入type
            if(field.getType()==Date.class||field.getType()== LocalDate.class||field.getType()== LocalDateTime.class||field.getType()== LocalTime.class) {
                adminField.setType("timestamp");
            }else{
                adminField.setType(field.getType().getSimpleName());
            }
            // adminField存入label
            if(adminField.getLabel()==null){
                adminField.setLabel(adminField.getName().replaceAll("_", " "));
            }
            // adminField存入isArray
            if(field.getType().isArray()){
                adminField.setCanNull(true);
                adminField.setIsArray(true);
            }

            if(field.getAnnotations()!=null){
                // adminField存入此字段所有注解类型
                List<String> annotationList=new ArrayList<>();
                for(Annotation annotation : field.getAnnotations()){
                    annotationList.add(annotation.annotationType().getSimpleName());
                }
                adminField.setAnnotation(annotationList.toArray(String[]::new));
                // adminField存入label
                Transient ignore = field.getAnnotation(Transient.class);
                if(ignore!=null){
                    adminField.setNotColumn(true);
                }

                // adminField将主键存入primary
                if(field.getAnnotation(Id.class)!=null){
                    adminField.setPrimary(true);
                }

                Column column = field.getAnnotation(Column.class);
                if(column!=null){
                    // adminField存入unique
                    if(column.unique()){
                        // adminField将唯一字段存入primary
                        adminField.setPrimary(true);
                    }
                    // adminField存入canNull
                    if(column.nullable()){
                        adminField.setCanNull(true);
                    }
                    // adminField将外键与主键字段存入primaryKeyMap
                    if(column.unique()||field.getAnnotation(Id.class)!=null){
                        String keyName=adminField.getName();
                        // 如果字段以id结尾，则其为外键
                        if(field.getName().endsWith("id")||field.getName().endsWith("Id")||field.getName().endsWith("ID")){
                            String idName=field.getName().substring(0,field.getName().length()-2);
                            if(!idName.isEmpty()){
                                // 获取model的外键对应的实体类字段
                                for(Field foreignField : model.getDeclaredFields()){
                                    if(foreignField.getName().equals(idName)) {
                                        // 外键不放入adminField中
                                        isForeignID=true;
                                        // 忽略外键
                                        adminObject.getIgnores().put(field.getName(), true);
                                        AdminForeign adminForeign=new AdminForeign();
                                        adminForeign.setFieldName(CamelToSnakeUtil.toSnakeCase(foreignField.getName()));
                                        adminForeign.setField(foreignField.getName());
                                        keyName = CamelToSnakeUtil.toSnakeCase(foreignField.getName());
                                    }
                                }
                            }
                        }
                        if(adminObject.getPrimaryKeyMap()!=null){
                            adminObject.getPrimaryKeyMap().put(keyName,adminField.getName());
                        }

                        // adminField将主键存入primary
                        if(field.getAnnotation(Id.class)!=null){
                            adminObject.getPrimaryKeys().add(keyName);
                        }else{
                            adminObject.getUniqueKeys().add(keyName);
                        }
                    }

                }
                // adminField存入isAutoId
                GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                if(generatedValue!=null){
                    adminField.setIsAutoId(true);
                }
                if(adminField.getAttribute()!=null) {
                    adminObject.getAttributes().forEach((key, value) -> {
                        if (key.equals(field.getName())) {
                            adminField.setAttribute(value);
                        }
                    });
                }
            }
            if(isForeignID) {
                list.add(adminField);
            }
        }
        adminObject.setFields(list);
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
        userObject.setShows(new ArrayList<>(List.of("id", "email", "displayName", "isStaff", "isSuperUser", "enabled", "activated", "gmtModified", "lastLogin", "lastLoginIp", "source", "locale", "timezone")));
        userObject.setEdits(new ArrayList<>(List.of("email","password","displayName","firstName","lastName","isStaff","isSuperUser","enabled","activated","profile","source","locale","timezone")));
        userObject.setFilterables(new ArrayList<>(List.of("gmtCreated", "gmtModified", "isStaff", "isSuperUser", "enabled", "activated")));
        userObject.setOrderables(new ArrayList<>(List.of("gmtCreated", "gmtModified", "enabled", "activated")));
        userObject.setSearches(new ArrayList<>(List.of("email", "displayName")));
        userObject.setOrders(new ArrayList<>(List.of(new Order("gmtModified", "desc"))));
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
        groupObject.setShows(new ArrayList<>(List.of("id","name","extra","gmtCreated","gmtModified")));
        groupObject.setEdits(new ArrayList<>(List.of("id","name","extra","gmtModified")));
        groupObject.setOrderables(new ArrayList<>(List.of("gmtModified")));
        groupObject.setSearches(new ArrayList<>(List.of("name")));
        groupObject.setRequires(new ArrayList<>(List.of("name")));
        AdminIcon groupIcon =new AdminIcon();
        groupIcon.setSvg("待定");
        groupObject.setIcon(groupIcon);
        adminObjects.add(groupObject);

        AdminObject groupMemberObject =new AdminObject();
        groupMemberObject.setModel(GroupMember.class);
        groupMemberObject.setGroup("Settings");
        groupMemberObject.setName("GroupMember");
        groupMemberObject.setDesc("Group members");
        groupMemberObject.setShows(new ArrayList<>(List.of("id","userId","group","role","gmtCreated")));
        groupMemberObject.setFilterables(new ArrayList<>(List.of("group","role","gmtCreated")));
        groupMemberObject.setOrderables(new ArrayList<>(List.of("gmtCreated")));
        groupMemberObject.setSearches(new ArrayList<>(List.of("user","group")));
        groupMemberObject.setRequires(new ArrayList<>(List.of("user","group","role")));
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
        configObject.setShows(new ArrayList<>(List.of("key","value","autoload","pub","desc")));
        configObject.setEdits(new ArrayList<>(List.of("key","value","autoload","pub","desc")));
        configObject.setOrderables(new ArrayList<>(List.of("key")));
        configObject.setSearches(new ArrayList<>(List.of("key","desc","value")));
        configObject.setRequires(new ArrayList<>(List.of("key","value")));
        configObject.setFilterables(new ArrayList<>(List.of("autoload","pub")));
        AdminIcon configIcon =new AdminIcon();
        configIcon.setSvg("待定");
        configObject.setIcon(configIcon);
        adminObjects.add(configObject);
        return adminObjects.toArray(new AdminObject[0]);
    }
}
