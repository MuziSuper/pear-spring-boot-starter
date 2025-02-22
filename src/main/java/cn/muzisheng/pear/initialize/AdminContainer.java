package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.entity.Group;
import cn.muzisheng.pear.entity.GroupMember;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.handler.*;
import cn.muzisheng.pear.model.*;
import cn.muzisheng.pear.utils.CamelToSnakeUtil;
import cn.muzisheng.pear.utils.PluralUtil;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class AdminContainer {
    private final Logger logService= LoggerFactory.getLogger(AdminContainer.class);
    public static List<AdminObject> adminObjects;

    @PostConstruct
    public void buildAdminObjects() {
        adminObjects=getPearAdminObjects();
        buildAdminObjects(adminObjects);
    }
    /**
     * 处理AdminObject数据
     **/
    public void buildAdminObjects(List<AdminObject> adminObjects){
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
            if(field.getType()== Date.class||field.getType()== LocalDate.class||field.getType()== LocalDateTime.class||field.getType()== LocalTime.class) {
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
    public List<AdminObject> getPearAdminObjects(){
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
                new AdminSelectOption("Admin",Constant.GROUP_ROLE_ADMIN),
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
        configObject.setDesc("System config with database backend, You can change it in Admin page, and it will take effect immediately without restarting the server");
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
        return adminObjects;
    }
}
