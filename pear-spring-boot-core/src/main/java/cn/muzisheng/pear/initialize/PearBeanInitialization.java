package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.PluralUtil;
import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import cn.muzisheng.pear.model.AdminIcon;
import cn.muzisheng.pear.model.AdminObject;
import jakarta.annotation.Nullable;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 在bean被加载前通过PearObject与PearField注解填充AdminObject对象存入AdminContainer中
 **/
@Component
public class PearBeanInitialization implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, @Nullable String beanName) {
        // 捕获所有PearObject注解的Bean
        if (bean.getClass().isAnnotationPresent(PearObject.class)) {
            Class<?> clazz = bean.getClass();
            PearObject pearObjectAnnotation = clazz.getAnnotation(PearObject.class);
            if(!pearObjectAnnotation.isLoad()) return bean;

            AdminObject object = new AdminObject();
            object.setModel(clazz);
            object.setModelElem(bean);
            object.setName(clazz.getSimpleName());
            object.setDesc(pearObjectAnnotation.desc());
            object.setGroup(pearObjectAnnotation.group());
            // path字段最高优先级为PearObject注解的path字段，其次是类名
            object.setPath(defaultIfBlank(pearObjectAnnotation.path(), "/" + object.getName()));
            // pluralName字段最高优先级为PearObject注解的pluralName字段，其次是类名的复数
            object.setPluralName(defaultIfBlank(pearObjectAnnotation.pluralName(), PluralUtil.pluralize(object.getName())));
            object.setEditPage(emptyToNull(pearObjectAnnotation.editPage()));
            object.setListPage(emptyToNull(pearObjectAnnotation.listPage()));
            object.setInvisible(pearObjectAnnotation.isInvisible());
            if (!emptyToNull(pearObjectAnnotation.iconUrl()).isEmpty()) {
                AdminIcon icon = new AdminIcon();
                icon.setUrl(emptyToNull(pearObjectAnnotation.iconUrl()));
                object.setIcon(icon);
            }
            object.setTableName(AdminContainer.resolveTableName(clazz, pearObjectAnnotation));

            // 类信息填充完毕，以下对字段信息进行填充
            Set<String> shows = new LinkedHashSet<>();
            Set<String> edits = new LinkedHashSet<>();
            Set<String> filterables = new LinkedHashSet<>();
            Set<String> Orderables = new LinkedHashSet<>();
            Set<String> searches = new LinkedHashSet<>();
            Set<String> requires = new LinkedHashSet<>();
            Set<String> primaryKeys = new LinkedHashSet<>();
            Set<String> uniqueKeys = new LinkedHashSet<>();

            for (Field field : AdminContainer.scanModelFields(clazz)) {
                if (field.isAnnotationPresent(PearField.class)) {
                    PearField pearFieldAnnotation = field.getAnnotation(PearField.class);
                    if (AdminContainer.isNotColumn(field)) {
                        continue;
                    }
                    String name = AdminContainer.resolveColumnName(field);
                    if (pearFieldAnnotation.isEdit()) {
                        edits.add(name);
                    }
                    if (field.isAnnotationPresent(GeneratedValue.class)) {
                        edits.remove(name);
                    }
                    if (pearFieldAnnotation.isShow()) {
                        shows.add(name);
                    }
                    if (pearFieldAnnotation.isOrderable()) {
                        Orderables.add(name);
                    }
                    if (pearFieldAnnotation.isFilterable()) {
                        filterables.add(name);
                    }
                    if (pearFieldAnnotation.isSearchable()) {
                        searches.add(name);
                    }
                    if (pearFieldAnnotation.isRequire()) {
                        requires.add(name);
                    }
                    if (AdminContainer.isPrimaryField(field)) {
                        primaryKeys.add(name);
                    } else if (AdminContainer.isUniqueField(field)) {
                        uniqueKeys.add(name);
                    }
                }
            }
            object.setShows(new ArrayList<>(shows));
            object.setEdits(new ArrayList<>(edits));
            object.setFilterables(new ArrayList<>(filterables));
            object.setOrderables(new ArrayList<>(Orderables));
            object.setSearches(new ArrayList<>(searches));
            object.setRequires(new ArrayList<>(requires));
            object.setPrimaryKeys(new ArrayList<>(primaryKeys));
            object.setUniqueKeys(new ArrayList<>(uniqueKeys));
            AdminContainer.addAdminObject(object);
        }
        return bean;
    }

    /**
     * 返回去除空白后的注解值，空值时使用默认值。
     **/
    private String defaultIfBlank(String value, String defaultValue) {
        String normalized = emptyToNull(value);
        return normalized.isEmpty() ? defaultValue : normalized;
    }

    /**
     * 将 null 转为空串，并 trim。
     **/
    private String emptyToNull(String value) {
        return value == null ? "" : value.trim();
    }
}
