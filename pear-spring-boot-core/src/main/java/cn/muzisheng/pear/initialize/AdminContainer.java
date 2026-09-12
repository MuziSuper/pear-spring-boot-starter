package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.CamelToSnakeUtil;
import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import cn.muzisheng.pear.exception.AdminErrorException;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.handler.*;
import cn.muzisheng.pear.model.*;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 容器类，用于存储管理所有adminObject
 **/
@Component
public class AdminContainer {
    private static final Logger LOG = LoggerFactory.getLogger(AdminContainer.class);
    private static final List<AdminObject> adminObjects = new LinkedList<>();
    private static final Map<String, AdminObject> adminObjectMap = new HashMap<>();
    private static final Map<Class<?>, AdminObject> adminObjectClassMap = new HashMap<>();

    /**
     * 获取所有adminObject
     **/
    public static List<AdminObject> getAllAdminObjects() {
        return adminObjects;
    }

    /**
     * 添加adminObject进入Pear容器
     **/
    protected static void addAdminObject(AdminObject adminObject) {
        if (adminObject == null || adminObject.getModel() == null) {
            throw new AdminErrorException("AdminObject or model cannot be null.");
        }
        String tableName = normalizeIdentifier(adminObject.getTableName());
        if (tableName.isEmpty()) {
            throw new AdminErrorException("AdminObject tableName cannot be empty. model: " + adminObject.getModel().getName());
        }
        adminObject.setTableName(tableName);
        AdminObject existing = adminObjectMap.get(tableName);
        if (existing != null) {
            throw new AdminErrorException("The database table named " + tableName + " has already been injected into the Pear container. conflict models: " + existing.getModel().getName() + " and " + adminObject.getModel().getName());
        }
        adminObjects.add(adminObject);
        adminObjectMap.put(tableName, adminObject);
        adminObjectClassMap.put(adminObject.getModel(), adminObject);
    }
    /**
     * 添加多个adminObject进入Pear容器
     */
    protected static void addAdminObjects(List<AdminObject> objects) {
        for (AdminObject adminObject : objects) {
            addAdminObject(adminObject);
        }
    }



    /**
     * 获取adminObject
     * @param tableName 数据库表名
     **/
   private static AdminObject getAdminObject(String tableName) {
        if (adminObjectMap.containsKey(tableName)) {
            return adminObjectMap.get(tableName);
        }
        return null;
    }
    /**
     * 获取adminObject
     * @param clazz 类对象
     **/
    private static AdminObject getAdminObject(Class<?> clazz) {
        return adminObjectClassMap.get(clazz);
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
            // 处理AdminObject数据；元数据错误必须启动期 fail-fast。
            build(adminObject);
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
        for (Field field : scanModelFields(model)) {
            // 忽略钩子方法
            if (field.getType() == BeforeCreate.class || field.getType() == BeforeUpdate.class || field.getType() == BeforeDelete.class || field.getType() == BeforeRender.class || field.getType() == AccessCheck.class || field.getType() == AfterCreate.class) {
                continue;
            }
            AdminField adminField = new AdminField();
            // adminField存入name字段与临时的fieldName
            adminField.setName(field.getName());
            adminField.setFieldName(resolveColumnName(field));
            /*
             * 若为实体类对象，则递归，这部分还没做，先非对象
             */

            // adminField存入type
            adminField.setType(field.getType());
            // adminField存入isArray
            if (field.getType().isArray()) {
                adminField.setCanNull(true);
                adminField.setIsArray(true);
            }
            // adminField存入此字段所有注解类型
            List<String> annotationList = new ArrayList<>();
            for (Annotation annotation : field.getAnnotations()) {
                annotationList.add(annotation.annotationType().getSimpleName());
            }
            adminField.setAnnotation(annotationList.toArray(String[]::new));

            // 先根据PearField的参数填充adminField
            if (field.isAnnotationPresent(PearField.class)) {
                PearField pearField = field.getAnnotation(PearField.class);
                // pearField填充label字段
                adminField.setLabel(pearField.label());
                // pearField填充placeholder字段
                adminField.setPlaceholder(pearField.placeholder());
                adminField.setIsAutoInsertTime(pearField.isAutoInsertTime());
                adminField.setIsAutoUpdateTime(pearField.isAutoUpdateTime());
            }
            if(field.isAnnotationPresent(TableField.class)){
                TableField tableField = field.getAnnotation(TableField.class);
                if(tableField.fill()== FieldFill.INSERT_UPDATE){
                    adminField.setIsAutoInsertTime(true);
                    adminField.setIsAutoUpdateTime(true);
                }
                if(tableField.fill()== FieldFill.INSERT){
                    adminField.setIsAutoInsertTime(true);
                }
                if(tableField.fill()== FieldFill.UPDATE){
                    adminField.setIsAutoUpdateTime(true);
                }
            }

            // 若pearField没有属性label则默认
            if (adminField.getLabel() == null || adminField.getLabel().isEmpty()) {
                adminField.setLabel(field.getName().replaceAll("_", " "));
            }

            // adminField存入NotColumn
            if (isNotColumn(field)) {
                adminField.setNotColumn(true);
            }

            // adminField将主键存入primary
            if (isPrimaryField(field) && !Boolean.TRUE.equals(adminField.getNotColumn())) {
                adminField.setPrimary(true);
            }

            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                // adminField存入unique
                if (column.unique() && !Boolean.TRUE.equals(adminField.getNotColumn())) {
                    // adminField将唯一字段存入primary
                    adminField.setPrimary(true);
                }
                // adminField存入canNull
                if (column.nullable()) {
                    adminField.setCanNull(true);
                }
            }
            // adminField存入isAutoId
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            if (generatedValue != null) {
                // 主键自增后，不可编辑
                if(adminObject.getEdits()!=null){
                    adminObject.getEdits().removeIf(fieldName -> fieldName.equals(adminField.getFieldName()));
                }
                adminField.setIsAutoId(true);
            }
            // 当字段标记为唯一键或主键时，如果以id结尾则将字段名以键值对方式存入primaryKeyMap中，
            // 并且如果当前类中存在此字段对应类属性，则将类属性名与字段名存入primaryKeyMap中，
            // 最后将主键和唯一键分别存入primaryKeys和uniqueKeys中
            if ((isPrimaryField(field) || isUniqueField(field)) && !Boolean.TRUE.equals(adminField.getNotColumn())) {
                String keyName = adminField.getFieldName();
                // 如果字段以id结尾，则其为外键
                if (field.getName().endsWith("id") || field.getName().endsWith("Id") || field.getName().endsWith("ID")) {
                    String idName = field.getName().substring(0, field.getName().length() - 2);
                    if (!idName.isEmpty()) {
                        // 获取model的外键对应的实体类字段
                        try {
                            Field foreignField = findModelField(model, idName);
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
                if (isPrimaryField(field)) {
                    ensurePrimaryKeys(adminObject);
                    if (!adminObject.getPrimaryKeys().contains(keyName)) {
                        adminObject.getPrimaryKeys().add(keyName);
                    }
                } else {
                    ensureUniqueKeys(adminObject);
                    if (!adminObject.getUniqueKeys().contains(keyName)) {
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
                        Field foreignField = findModelField(model, idName);
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
            if (adminObject.getIgnores() != null) {
                for (String key : adminObject.getIgnores().keySet()) {
                    list.removeIf(f -> f.getName().equals(key));
                }
            }
            adminObject.setFields(list);
        }
    }

    /**
     * 根据实体注解解析表名，按 JPA Table、Entity、MyBatis-Plus TableName、PearObject、类名 snake_case 的优先级处理。
     **/
    public static String resolveTableName(Class<?> clazz, PearObject pearObject) {
        String tableName = "";
        Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            tableName = normalizeIdentifier(table.name());
        }
        if (tableName.isEmpty()) {
            Entity entity = clazz.getAnnotation(Entity.class);
            if (entity != null) {
                tableName = normalizeIdentifier(entity.name());
            }
        }
        if (tableName.isEmpty()) {
            TableName tableNameAnnotation = clazz.getAnnotation(TableName.class);
            if (tableNameAnnotation != null) {
                tableName = normalizeIdentifier(tableNameAnnotation.value());
            }
        }
        if (tableName.isEmpty() && pearObject != null) {
            tableName = normalizeIdentifier(pearObject.TableName());
        }
        if (tableName.isEmpty()) {
            tableName = normalizeIdentifier(CamelToSnakeUtil.toSnakeCase(clazz.getSimpleName()));
        }
        if (tableName.isEmpty()) {
            throw new AdminErrorException("Cannot resolve table name for model: " + clazz.getName());
        }
        return tableName;
    }

    /**
     * 扫描模型字段：父类到子类顺序稳定，子类同名字段覆盖父类字段。
     **/
    public static List<Field> scanModelFields(Class<?> model) {
        LinkedList<Class<?>> hierarchy = new LinkedList<>();
        Class<?> current = model;
        while (current != null && current != Object.class) {
            hierarchy.addFirst(current);
            current = current.getSuperclass();
        }
        LinkedHashMap<String, Field> fields = new LinkedHashMap<>();
        for (Class<?> clazz : hierarchy) {
            for (Field field : clazz.getDeclaredFields()) {
                if (fields.containsKey(field.getName())) {
                    fields.remove(field.getName());
                }
                fields.put(field.getName(), field);
            }
        }
        return new ArrayList<>(fields.values());
    }

    /**
     * 解析数据库列名，空注解值不会覆盖默认字段名。
     **/
    public static String resolveColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            String name = normalizeIdentifier(column.name());
            if (!name.isEmpty()) {
                return name;
            }
        }
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null) {
            String name = normalizeIdentifier(tableField.value());
            if (!name.isEmpty()) {
                return name;
            }
        }
        return normalizeIdentifier(CamelToSnakeUtil.toSnakeCase(field.getName()));
    }

    /**
     * 判断字段是否不映射数据库列。
     **/
    public static boolean isNotColumn(Field field) {
        TableField tableField = field.getAnnotation(TableField.class);
        return field.isAnnotationPresent(Transient.class) || (tableField != null && !tableField.exist());
    }

    /**
     * 查找模型字段，支持继承字段与子类覆盖。
     **/
    public static Field findModelField(Class<?> model, String fieldName) throws NoSuchFieldException {
        for (Field field : scanModelFields(model)) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    /**
     * 清理注解中的 SQL 标识符值。
     **/
    public static String normalizeIdentifier(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replace("`", "");
    }

    /**
     * 判断字段是否为主键来源。
     **/
    public static boolean isPrimaryField(Field field) {
        PearField pearField = field.getAnnotation(PearField.class);
        return field.isAnnotationPresent(Id.class) || (pearField != null && pearField.isPrimaryKey());
    }

    /**
     * 判断字段是否为唯一键来源。
     **/
    public static boolean isUniqueField(Field field) {
        PearField pearField = field.getAnnotation(PearField.class);
        Column column = field.getAnnotation(Column.class);
        return (column != null && column.unique()) || (pearField != null && pearField.isUniqueKey());
    }

    private static void ensurePrimaryKeys(AdminObject adminObject) {
        if (adminObject.getPrimaryKeys() == null) {
            adminObject.setPrimaryKeys(new ArrayList<>());
        }
    }

    private static void ensureUniqueKeys(AdminObject adminObject) {
        if (adminObject.getUniqueKeys() == null) {
            adminObject.setUniqueKeys(new ArrayList<>());
        }
    }

    @Getter
    // 对外提供用于修改adminObject的属性
    public static class AdminFactory {
        private final AdminObject adminObject;
        public AdminFactory(String model) {
            AdminObject adminObject= AdminContainer.getAdminObject(model);
            if(adminObject == null){
                throw new GeneralException("AdminObject of "+model+" is null.");
            }
            this.adminObject=adminObject;
        }
       public AdminFactory(Class<?> clazz){
            AdminObject adminObject= AdminContainer.getAdminObject(clazz);
            if(adminObject == null){
                throw new GeneralException("AdminObject of "+clazz.getName()+" is null.");
            }
            this.adminObject=adminObject;
       }
        public void setShows(List<String> shows) {
            adminObject.setShows(shows);
        }
        public void addShow(String show) {
            if(adminObject.getShows()==null){
                adminObject.setShows(new ArrayList<>());
            }
            adminObject.getShows().add(show);
        }
        public void setEdits(List<String> edits) {
            adminObject.setEdits(edits);
        }
        public void addEdit(String edit) {
            if(adminObject.getEdits()==null){
                adminObject.setEdits(new ArrayList<>());
            }
            adminObject.getEdits().add(edit);
        }
        public void setFilterables(List<String> filterables) {
            adminObject.setFilterables(filterables);
        }
        public void addFilterable(String filterable) {
            if(adminObject.getFilterables()==null){
                adminObject.setFilterables(new ArrayList<>());
            }
            adminObject.getFilterables().add(filterable);
        }
        public void setOrders(List<Order> orders){
            adminObject.setOrders(orders);
        }
        public void addOrder(Order order){
            if(adminObject.getOrders()==null){
                adminObject.setOrders(new ArrayList<>());
            }
            adminObject.getOrders().add(order);
        }
        public void setSearches(List<String> searches) {
            adminObject.setSearches(searches);
        }
        public void addSearch(String search) {
            if(adminObject.getSearches()==null){
                adminObject.setSearches(new ArrayList<>());
            }
            adminObject.getSearches().add(search);
        }
        public void setRequires(List<String> requires) {
            adminObject.setRequires(requires);
        }
        public void addRequire(String require) {
            if(adminObject.getRequires()==null){
                adminObject.setRequires(new ArrayList<>());
            }
            adminObject.getRequires().add(require);
        }
        public void setPluralName(String pluralName) {
            adminObject.setPluralName(pluralName);
        }
        public void setEditPage(String editPage) {
            adminObject.setEditPage(editPage);
        }
        public void setListPage(String listPage) {
            adminObject.setListPage(listPage);
        }
        public void setStyles(List<String> styles){
            adminObject.setStyles(styles);
        }
        public void addStyle(String style){
            if(adminObject.getStyles()==null){
                adminObject.setStyles(new ArrayList<>());
            }
            adminObject.getStyles().add(style);
        }
        public void setAdminScripts(List<AdminScript> AdminScripts){
            adminObject.setAdminScripts(AdminScripts);
        }
        public void addAdminScript(AdminScript AdminScript){
            if(adminObject.getAdminScripts()==null){
                adminObject.setAdminScripts(new ArrayList<>());
            }
            adminObject.getAdminScripts().add(AdminScript);

        }
        public void setAdminIcon(AdminIcon adminIcon){
            adminObject.setIcon(adminIcon);
        }

        public void setAttributes(Map<String, AdminAttribute> Attributes){
            adminObject.setAttributes(Attributes);
        }
       public void addAttribute(String fieldName, AdminAttribute value){
            if(adminObject.getAttributes()==null){
                adminObject.setAttributes(new HashMap<>());
            }
            adminObject.getAttributes().put(fieldName, value);
        }
        public void setInvisible(boolean invisible){
            adminObject.setInvisible(invisible);
        }
        public void setIgnores(Map<String, Boolean> ignores){
            adminObject.setIgnores(ignores);
        }
        public void addIgnore(String fieldName,boolean ignore){
            if(adminObject.getIgnores()==null){
                adminObject.setIgnores(new HashMap<>());
            }
            adminObject.getIgnores().put(fieldName, ignore);
        }
        public void setAccessCheck(AccessCheck accessCheck){
            adminObject.setAccessCheck(accessCheck);
            
        }
        public void setBeforeCreate(BeforeCreate beforeCreate){
            adminObject.setBeforeCreate(beforeCreate);
            
        }
        public void setBeforeUpdate(BeforeUpdate beforeUpdate){
            adminObject.setBeforeUpdate(beforeUpdate);
            
        }
        public void setBeforeDelete(BeforeDelete beforeDelete){
            adminObject.setBeforeDelete(beforeDelete);
            
        }
        public void setBeforeRender(BeforeRender beforeRender){
            adminObject.setBeforeRender(beforeRender);
        }
        public void setAfterCreate(AfterCreate afterCreate){
            adminObject.setAfterCreate(afterCreate);
        }
    }

}
