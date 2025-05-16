package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.handler.*;
import cn.muzisheng.pear.model.*;
import cn.muzisheng.pear.utils.CamelToSnakeUtil;
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
import java.util.*;

/**
 * 容器类，用于存储管理所有adminObject
 **/
@Component
public class AdminContainer {
    private static final Logger LOG = LoggerFactory.getLogger(AdminContainer.class);
    private static final List<AdminObject> adminObjects = new ArrayList<>();
    private static final Map<String, AdminObject> adminObjectMap = new HashMap<>();

    /**
     * 获取所有adminObject
     **/
    public static List<AdminObject> getAllAdminObjects() {
        return adminObjects;
    }

    /**
     * 添加adminObject的tableName字段进入adminObjectMap
     **/
    public static void addAdminObject(AdminObject adminObject) {
        if (adminObjectMap.containsKey(adminObject.getTableName())) {
            LOG.warn("The database table named \"" + adminObject.getTableName() + "\" already exists.");
        }
        adminObjects.add(adminObject);
        // tableName为唯一,覆盖掉原先的表名重复的adminObject
        adminObjectMap.put(adminObject.getTableName(), adminObject);
    }

    /**
     * 获取adminObject
     *
     * @param tableName 数据库表名
     **/
    public static AdminObject getAdminObject(String tableName) {
        if (adminObjectMap.containsKey(tableName)) {
            return adminObjectMap.get(tableName);
        }
        return null;
    }

    /**
     * 判断是否存在此adminObject
     *
     * @param tableName 数据库表名
     **/
    public static boolean existsAdminObject(String tableName) {
        return adminObjectMap.containsKey(tableName);
    }

    /**
     * 构建入口
     **/
    public static void buildAdminObjects(List<AdminObject> objects) {
        for (AdminObject adminObject : objects) {
            try {
                // 处理AdminObject数据
                build(adminObject);
            } catch (GeneralException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    /**
     * 处理AdminObject数据
     **/
    private static void build(AdminObject adminObject) {
        // 解析字段，将所有字段经过判断处理后存入AdminObject的fields字段中
        parseFields(adminObject, adminObject.getModel());
        // 主键与唯一键不能为空
        if ((adminObject.getPrimaryKeys() == null || adminObject.getPrimaryKeys().isEmpty()) && (adminObject.getUniqueKeys() == null || adminObject.getUniqueKeys().isEmpty())) {
            throw new GeneralException(adminObject.getName() + " not has primaryKey or uniqueKeys");
        }
    }

    /**
     * 解析字段，将所有字段经过判断处理后存入AdminObject的fields字段中
     *
     * @param adminObject 通用类对象
     * @param model       实体类的Class对象
     **/
    private static void parseFields(AdminObject adminObject, Class<?> model) {
        // 此adminObject的AminField集合
        List<AdminField> list = new ArrayList<>();

        // 获取model的所有字段
        for (Field field : model.getDeclaredFields()) {
            AdminField adminField = new AdminField();
            // 先根据PearField的参数填充adminField
            if (field.isAnnotationPresent(PearField.class)) {
                PearField pearField = field.getAnnotation(PearField.class);
                // pearField填充label字段
                adminField.setLabel(pearField.label());
                // pearField填充placeholder字段
                adminField.setPlaceholder(pearField.placeholder());
                // pearField填充name字段
                adminField.setFieldName(CamelToSnakeUtil.toSnakeCase(field.getName()));
            }
            // 若pearField没有属性label则默认
            if (adminField.getLabel() == null || adminField.getLabel().isEmpty()) {
                adminField.setLabel(field.getName().replaceAll("_", " "));
            }

            /*
             * 若为实体类对象，则递归，这部分还没做，先非对象
             */

            // 忽略钩子方法
            if (field.getType() == BeforeCreate.class || field.getType() == BeforeUpdate.class || field.getType() == BeforeDelete.class || field.getType() == BeforeRender.class || field.getType() == AccessCheck.class || field.getType() == AdminViewOnSite.class || field.getType() == BeforeQueryRenderFunc.class) {
                continue;
            }
            // adminField存入name
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                adminField.setName(column.name().replaceAll("`", ""));
            } else {
                adminField.setName(field.getName());
            }
            // adminField存入type
            if (field.getType() == Date.class || field.getType() == LocalDate.class || field.getType() == LocalDateTime.class || field.getType() == LocalTime.class) {
                adminField.setType("timestamp");
            } else {
                adminField.setType(field.getType().getSimpleName());
            }
            // adminField存入isArray
            if (field.getType().isArray()) {
                adminField.setCanNull(true);
                adminField.setIsArray(true);
            }

            if (field.getAnnotations() != null) {
                // adminField存入此字段所有注解类型
                List<String> annotationList = new ArrayList<>();
                for (Annotation annotation : field.getAnnotations()) {
                    annotationList.add(annotation.annotationType().getSimpleName());
                }
                adminField.setAnnotation(annotationList.toArray(String[]::new));
                // adminField存入NotColumn
                Transient ignore = field.getAnnotation(Transient.class);
                if (ignore != null) {
                    adminField.setNotColumn(true);
                }

                // adminField将主键存入primary
                if (field.getAnnotation(Id.class) != null) {
                    adminField.setPrimary(true);
                }
                // adminField存入isAutoId
                GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                if (generatedValue != null) {
                    adminField.setIsAutoId(true);
                }
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    // adminField存入unique
                    if (column.unique()) {
                        // adminField将唯一字段存入primary
                        adminField.setPrimary(true);
                    }
                    // adminField存入canNull
                    if (column.nullable()) {
                        adminField.setCanNull(true);
                    }
                }

                // 当字段标记为唯一键或主键时，如果以id结尾则将字段名以键值对方式存入primaryKeyMap中，
                // 并且如果当前类中存在此字段对应类属性，则将类属性名与字段名存入primaryKeyMap中，
                // 最后将主键和唯一键分别存入primaryKeys和uniqueKeys中
                if ((column != null && column.unique()) || field.getAnnotation(Id.class) != null) {
                    String keyName = adminField.getName();
                    // 如果字段以id结尾，则其为外键
                    if (field.getName().endsWith("id") || field.getName().endsWith("Id") || field.getName().endsWith("ID")) {
                        String idName = field.getName().substring(0, field.getName().length() - 2);
                        if (!idName.isEmpty()) {
                            // 获取model的外键对应的实体类字段
                            try {
                                Field foreignField = model.getDeclaredField(idName);
                                keyName = CamelToSnakeUtil.toSnakeCase(foreignField.getName());
                            } catch (NoSuchFieldException e) {
                                LOG.warn("No such foreign field: {} in model: {}", idName, model.getName());
                            }
                        }
                        if (adminObject.getPrimaryKeyMap() == null) {
                            adminObject.setPrimaryKeyMap(new HashMap<>());
                        }
                        if (!adminObject.getPrimaryKeyMap().containsKey(keyName)) {
                            adminObject.getPrimaryKeyMap().put(keyName, adminField.getName());
                        }
                    }
                    // 将主键和唯一键分别存入primaryKeys和uniqueKeys中
                    if (field.getAnnotation(Id.class) != null) {
                        if (adminObject.getPrimaryKeys() == null) {
                            adminObject.setPrimaryKeys(new ArrayList<>());
                        }
                        if (!adminObject.getPrimaryKeys().contains(keyName)) {
                            adminObject.getPrimaryKeys().add(keyName);
                        }
                    } else {
                        if (adminObject.getUniqueKeys() == null) {
                            adminObject.setUniqueKeys(new ArrayList<>());
                        }
                        if (!adminObject.getPrimaryKeys().contains(keyName)) {
                            adminObject.getUniqueKeys().add(keyName);
                        }
                    }
                }

                // 如果当前字段后缀为id（非主键），扫描所有属性获取其类属性，如果存在则构建AdminForeign
                // 对于类属性字段需要在adminObject的AdminField列表中删除，只保留外键id字段
                if (field.getName().endsWith("id") || field.getName().endsWith("Id") || field.getName().endsWith("ID")) {
                    String idName = field.getName().substring(0, field.getName().length() - 2);
                    if (!idName.isEmpty()) {
                        // 获取model的外键对应的实体类字段
                        try {
                            Field foreignField = model.getDeclaredField(idName);
                            // 忽略外键对应的类属性
                            if (adminObject.getIgnores() == null) {
                                adminObject.setIgnores(new HashMap<>());
                            }
                            adminObject.getIgnores().put(foreignField.getName(), true);
                            AdminForeign adminForeign = new AdminForeign();
                            adminForeign.setFieldName(CamelToSnakeUtil.toSnakeCase(field.getName()));
                            adminForeign.setField(field.getName());
                            adminForeign.setForeignField(CamelToSnakeUtil.toSnakeCase(foreignField.getName()));
                            adminForeign.setPath("/" + foreignField.getName());
                            // adminField存入外键信息
                            adminField.setForeign(adminForeign);
                        } catch (NoSuchFieldException e) {
                            LOG.warn("No such foreign field: {} in model: {}", idName, model.getName());
                        }
                    }

                    // adminField存入属性
                    if (adminField.getAttribute() != null) {
                        adminObject.getAttributes().forEach((key, value) -> {
                            if (key.equals(field.getName())) {
                                adminField.setAttribute(value);
                            }
                        });
                    }
                }
                list.add(adminField);
            }
            if(adminObject.getIgnores()!=null) {
                for (String key : adminObject.getIgnores().keySet()) {
                    list.removeIf(f -> f.getName().equals(key));
                }
            }
            adminObject.setFields(list);
        }
    }
}
