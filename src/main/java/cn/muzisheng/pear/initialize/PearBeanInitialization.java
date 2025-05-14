package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.utils.PluralUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.HashSet;

/**
 * 在bean被加载前通过PearObject与PearField注解填充AdminObject对象
 **/
@Component
public class PearBeanInitialization implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, @Nullable String beanName) {
        // 捕获所有PearObject注解的Bean
        if (bean.getClass().isAnnotationPresent(PearObject.class)) {
            Class<?> clazz = bean.getClass();
            PearObject pearObjectAnnotation = clazz.getAnnotation(PearObject.class);
            AdminObject object = new AdminObject();
            object.setModel(clazz);
            object.setModelElem(bean);
            object.setName(clazz.getSimpleName());
            object.setDesc(pearObjectAnnotation.desc());
            object.setGroup(pearObjectAnnotation.group());
            if (pearObjectAnnotation.path() == null || pearObjectAnnotation.path().isEmpty()) {
                object.setPath("/" + clazz.getSimpleName());
            }
            if (pearObjectAnnotation.pluralName() == null || pearObjectAnnotation.pluralName().isEmpty()) {
                object.setPluralName(PluralUtil.pluralize(bean.getClass().getSimpleName()));
            }
            // tableName字段mybatisPlus的优先级最高,其次是pear,最后是默认
            if (clazz.isAnnotationPresent(TableName.class)) {
                TableName tablename = clazz.getAnnotation(TableName.class);
                object.setTableName(tablename.value().replaceAll("`", "").toLowerCase());
            } else if (clazz.isAnnotationPresent(Entity.class)) {
                Entity entity = clazz.getAnnotation(Entity.class);
                object.setTableName(entity.name().replaceAll("`", "").toLowerCase());
            } else if(pearObjectAnnotation.TableName() != null&&!pearObjectAnnotation.TableName().isEmpty()){
                object.setTableName(pearObjectAnnotation.TableName().replaceAll("`", "").toLowerCase());
            }else{
                object.setTableName(clazz.getSimpleName().toLowerCase());
            }
            if (object.getTableName() == null || object.getTableName().isEmpty()) {
                object.setTableName(clazz.getSimpleName().toLowerCase());
            }
            // 类信息填充完毕，以下对字段信息进行填充
            Set<String> shows = new HashSet<>();
            Set<String> edits = new HashSet<>();
            Set<String> filterables = new HashSet<>();
            Set<String> Orderables = new HashSet<>();
            Set<String> searches = new HashSet<>();
            Set<String> requires = new HashSet<>();
            Set<String> primaryKeys = new HashSet<>();
            Set<String> uniqueKeys = new HashSet<>();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(PearField.class)) {
                    PearField pearFieldAnnotation = field.getAnnotation(PearField.class);
                    String name = "";
                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        // 去除反引号，获取mybaitsPlus指定的字段名
                        name = column.name().replaceAll("`", "");
                    }
                    if (name.isEmpty()) {
                        name = field.getName();
                    }
                    if (pearFieldAnnotation.isEdit()) {
                        edits.add(name);
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
                    if (pearFieldAnnotation.isUniqueKey()) {
                        uniqueKeys.add(name);
                    }
                    if (pearFieldAnnotation.isPrimaryKey()) {
                        primaryKeys.add(name);
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
}
