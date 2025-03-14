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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
@Component
public class PearBeanInitialization implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if(bean.getClass().isAnnotationPresent(PearObject.class)){
            Class<?> clazz = bean.getClass();
            PearObject pearObjectAnnotation = clazz.getAnnotation(PearObject.class);
            AdminObject object=new AdminObject();
            object.setModel(clazz);
            object.setModelElem(bean);
            object.setDesc(pearObjectAnnotation.desc());
            object.setName(bean.getClass().getSimpleName());
            object.setGroup(pearObjectAnnotation.group());
            if(pearObjectAnnotation.path()==null|| pearObjectAnnotation.path().isEmpty()){
                object.setPath("/"+bean.getClass().getSimpleName());
            }else{
                object.setPath(pearObjectAnnotation.path());
            }
            if(pearObjectAnnotation.pluralName()==null|| pearObjectAnnotation.pluralName().isEmpty()) {
                object.setPluralName(PluralUtil.pluralize(bean.getClass().getSimpleName()));
            }else{
                object.setPluralName(pearObjectAnnotation.pluralName());
            }
            object.setTableName(pearObjectAnnotation.TableName().replaceAll("`",""));
            if(clazz.isAnnotationPresent(TableName.class)){
                TableName tablename=clazz.getAnnotation(TableName.class);
                object.setTableName(tablename.value().replaceAll("`",""));
            }else if(clazz.isAnnotationPresent(Entity.class)){
                Entity entity =clazz.getAnnotation(Entity.class);
                object.setTableName(entity.name().replaceAll("`",""));
            }else{
                object.setTableName(bean.getClass().getSimpleName().toLowerCase().replaceAll("`",""));
            }
            List<String> shows=new ArrayList<>();
            List<String> edits=new ArrayList<>();
            List<String> filterables=new ArrayList<>();
            List<String> Orderables=new ArrayList<>();
            List<String> searches=new ArrayList<>();
            List<String> requires=new ArrayList<>();
            List<String> primaryKeys=new ArrayList<>();
            List<String> uniqueKeys=new ArrayList<>();

            Field[] fields=clazz.getDeclaredFields();
            for (Field field:fields){
                if(field.isAnnotationPresent(PearField.class)) {
                    PearField pearFieldAnnotation = field.getAnnotation(PearField.class);
                    String name = "";
                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        if(column.name().length() >= 2 && column.name().endsWith("`")&& column.name().startsWith("`")){
                            name = column.name().substring(1, column.name().length() - 1);
                        }else{
                            name = column.name();
                        }
                    }
                    if (name.isEmpty()) {
                        name = field.getName();
                    }

                    if (pearFieldAnnotation.isEdit()) {
                        if(!edits.contains(name)){
                            edits.add(name);
                        }
                    }
                    if (pearFieldAnnotation.isShow()) {
                        if(!shows.contains(name)){
                            shows.add(name);
                        }
                    }
                    if (pearFieldAnnotation.isOrderable()) {
                        if(!Orderables.contains(name)){
                            Orderables.add(name);
                        }
                    }
                    if (pearFieldAnnotation.isFilterable()) {
                        if(!filterables.contains(name)){
                            filterables.add(name);
                        }
                    }
                    if (pearFieldAnnotation.isSearchable()) {
                        if(!searches.contains(name)){
                            searches.add(name);
                        }
                    }
                    if (pearFieldAnnotation.isRequire()) {
                        if(!requires.contains(name)){
                            requires.add(name);
                        }
                    }
                    if (pearFieldAnnotation.isUniqueKey()) {
                        if(!uniqueKeys.contains(name)){
                            uniqueKeys.add(name);
                        }
                    }
                    if (pearFieldAnnotation.isPrimaryKey()) {
                        if(!primaryKeys.contains(name)){
                            primaryKeys.add(name);
                        }
                    }
                }
            }
            object.setShows(shows);
            object.setEdits(edits);
            object.setFilterables(filterables);
            object.setOrderables(Orderables);
            object.setSearches(searches);
            object.setRequires(requires);
            object.setPrimaryKeys(primaryKeys);
            object.setUniqueKeys(uniqueKeys);
            if(!AdminContainer.existsAdminObject(object)){
                AdminContainer.addAdminObject(object);
            }
        }
        return bean;
    }
}
