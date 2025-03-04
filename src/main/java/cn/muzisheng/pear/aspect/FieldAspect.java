package cn.muzisheng.pear.aspect;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import cn.muzisheng.pear.handler.*;
import cn.muzisheng.pear.initialize.AdminContainer;
import cn.muzisheng.pear.model.*;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Aspect
@Component
public class FieldAspect {
    @Pointcut("@annotation(cn.muzisheng.pear.annotation.PearObject)")
    public void adminPoint(){}
    @Around("adminPoint()")
    public Object adminPointFunc(ProceedingJoinPoint jp) throws Throwable{
        Class<?> clazz=jp.getTarget().getClass();
        if(clazz.isAnnotationPresent(PearObject.class)){
            PearObject pearObjectAnnotation = clazz.getAnnotation(PearObject.class);
            AdminObject object=new AdminObject();
            object.setModel(clazz);
            object.setModelElem(jp.getTarget());
            object.setDesc(pearObjectAnnotation.desc());
            object.setName(jp.getSignature().getName());
            object.setGroup(pearObjectAnnotation.group());
            object.setPath(pearObjectAnnotation.path());
            object.setPluralName(pearObjectAnnotation.pluralName());
            if(clazz.isAnnotationPresent(TableName.class)){
                TableName tablename=clazz.getAnnotation(TableName.class);
                object.setTableName(tablename.value());
            }else {
                object.setTableName(pearObjectAnnotation.TableName());
            }
            List<String> shows=new ArrayList<>();
            List<String> edits=new ArrayList<>();
            List<String> filterables=new ArrayList<>();
            List<String> Orderables=new ArrayList<>();
            List<String> searches=new ArrayList<>();
            List<String> requires=new ArrayList<>();
            List<String> primaryKeys=new ArrayList<>();
            List<String> uniqueKeys=new ArrayList<>();
//            List<Order> Orders=new ArrayList<>();
//            List<String> styles=new ArrayList<>();
//            List<AdminScript> AdminScripts=new ArrayList<>();
//            Map<String, Boolean> permissions=new HashMap<>();
//            AdminIcon icon=new AdminIcon();
//            Map<String, AdminAttribute> Attributes=new HashMap<>();
//            Map<String, Boolean> ignores=new HashMap<>();
//            Map<String, String> primaryKeyMap=new HashMap<>();
            Field[] fields=clazz.getDeclaredFields();
            for (Field field:fields){
                if(field.isAnnotationPresent(PearField.class)) {
                    PearField pearFieldAnnotation = field.getAnnotation(PearField.class);
                    String name = "";
                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        name = column.name().replaceAll("`", "");
                    }
                    if ("".equals(name)) {
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
                    if (pearFieldAnnotation.isPrimaryKey()) {
                        primaryKeys.add(name);
                    }
                    if (pearFieldAnnotation.isUniqueKey()) {
                        uniqueKeys.add(name);
                    }
                    if (pearFieldAnnotation.isUniqueKey()) {
                        uniqueKeys.add(name);
                    }
                    if (pearFieldAnnotation.isPrimaryKey()) {
                        primaryKeys.add(name);
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
            object.setTableName(pearObjectAnnotation.TableName());
            AdminContainer.addAdminObject(object);
        }
        return jp.proceed();
    }
}
