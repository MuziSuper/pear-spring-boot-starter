package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import cn.muzisheng.pear.exception.AdminErrorException;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.model.AdminField;
import cn.muzisheng.pear.model.AdminForeign;
import cn.muzisheng.pear.model.AdminObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminMetadataInitializationTest {

    @AfterEach
    void cleanAdminContainer() {
        clearAdminContainer();
    }

    @Test
    void pearObjectMappingUsesConfiguredValuesAndDefaultsWhenBlank() {
        AdminObject configured = initialize(ConfiguredPearEntity.class);
        AdminObject defaults = initialize(DefaultPearEntity.class);

        assertEquals("/configured-path", configured.getPath());
        assertEquals("configured-items", configured.getPluralName());
        assertEquals("/configured/edit", configured.getEditPage());
        assertEquals("/configured/list", configured.getListPage());
        assertTrue(configured.isInvisible());
        assertNotNull(configured.getIcon());
        assertEquals("/icon.svg", configured.getIcon().getUrl());

        assertEquals("/DefaultPearEntity", defaults.getPath());
        assertEquals("defaultpearentities", defaults.getPluralName());
    }

    @Test
    void tableNameResolutionUsesExpectedPriorityAndNormalizesIdentifiers() {
        assertEquals("jpa_table_name", initialize(JpaTableNameEntity.class).getTableName());
        assertEquals("entity_table_name", initialize(EntityNameEntity.class).getTableName());
        assertEquals("mybatis_table_name", initialize(MybatisTableNameEntity.class).getTableName());
        assertEquals("pear_table_name", initialize(PearTableNameEntity.class).getTableName());
        assertEquals("class_name_table_entity", initialize(ClassNameTableEntity.class).getTableName());
    }

    @Test
    void inheritedFieldsAreScannedChildFieldsOverrideParentAndOrderIsStable() {
        AdminObject adminObject = build(ChildFieldEntity.class);

        assertEquals(List.of("parent_id", "parent_visible", "child_override", "child_field"), fieldNames(adminObject));
        assertEquals(List.of("parent_id", "parent_visible", "child_override", "child_field"), adminObject.getShows());
        assertEquals(List.of("parent_visible", "child_override", "child_field"), adminObject.getEdits());
        assertTrue(adminObject.getFilterables().containsAll(List.of("parent_visible", "child_override", "child_field")));
        assertTrue(adminObject.getOrderables().containsAll(List.of("parent_visible", "child_override", "child_field")));
        assertTrue(adminObject.getSearches().containsAll(List.of("parent_visible", "child_override", "child_field")));
    }

    @Test
    void columnNameResolutionUsesNonBlankColumnAndTableFieldValuesOnly() {
        AdminObject adminObject = build(ColumnResolutionEntity.class);

        assertEquals(List.of("id", "jpa_column", "mp_column", "empty_column_name", "empty_table_field_value"), fieldNames(adminObject));
        assertTrue(adminObject.getShows().contains("jpa_column"));
        assertTrue(adminObject.getShows().contains("mp_column"));
        assertTrue(adminObject.getShows().contains("empty_column_name"));
        assertTrue(adminObject.getShows().contains("empty_table_field_value"));
    }

    @Test
    void transientAndNonExistingTableFieldsAreNotColumnsAndDoNotEnterSqlWhitelistsOrKeys() {
        AdminObject adminObject = build(NonColumnEntity.class);

        AdminField transientField = field(adminObject, "computed");
        AdminField tableField = field(adminObject, "virtualValue");
        assertTrue(transientField.getNotColumn());
        assertTrue(tableField.getNotColumn());
        assertFalse(adminObject.getShows().contains("computed"));
        assertFalse(adminObject.getShows().contains("virtual_value"));
        assertFalse(adminObject.getPrimaryKeys().contains("computed"));
        assertFalse(adminObject.getUniqueKeys().contains("virtual_value"));
    }

    @Test
    void keySourcesPopulatePrimaryAndUniqueKeysAndGeneratedIdIsNotEditable() {
        AdminObject adminObject = build(KeySourceEntity.class);

        assertTrue(adminObject.getPrimaryKeys().contains("id"));
        assertTrue(adminObject.getPrimaryKeys().contains("pear_primary"));
        assertTrue(adminObject.getUniqueKeys().contains("email"));
        assertTrue(adminObject.getUniqueKeys().contains("external_code"));
        assertFalse(adminObject.getEdits().contains("id"));
        assertTrue(field(adminObject, "id").getIsAutoId());
    }

    @Test
    void adminContainerFailsFastForDuplicateTablesAndObjectsWithoutKeysAndFactoryFindsByClass() {
        initialize(DuplicateTableOne.class);

        assertThrows(AdminErrorException.class, () -> initialize(DuplicateTableTwo.class));

        AdminObject noKey = initialize(NoKeyEntity.class);
        assertThrows(GeneralException.class, () -> AdminContainer.buildAdminObjects(List.of(noKey)));

        clearAdminContainer();
        AdminObject customTable = build(FactoryLookupEntity.class);
        assertEquals("factory_lookup_custom", customTable.getTableName());
        AdminContainer.AdminFactory factory = new AdminContainer.AdminFactory(FactoryLookupEntity.class);
        assertEquals(customTable, factory.getAdminObject());
    }

    @Test
    void foreignIdCanResolveInheritedForeignObjectField() {
        AdminObject adminObject = build(ChildForeignEntity.class);

        AdminField accountId = field(adminObject, "accountId");
        AdminForeign foreign = accountId.getForeign();
        assertNotNull(foreign);
        assertEquals("accountId", foreign.getField());
        assertEquals("account_id", foreign.getFieldName());
        assertEquals("account", foreign.getForeignField());
        assertEquals("/account", foreign.getPath());
        assertTrue(adminObject.getIgnores().containsKey("account"));
    }

    private static AdminObject initialize(Class<?> model) {
        try {
            Object bean = model.getDeclaredConstructor().newInstance();
            new PearBeanInitialization().postProcessBeforeInitialization(bean, model.getSimpleName());
            return AdminContainer.getAllAdminObjects().get(AdminContainer.getAllAdminObjects().size() - 1);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static AdminObject build(Class<?> model) {
        AdminObject adminObject = initialize(model);
        AdminContainer.buildAdminObjects(List.of(adminObject));
        return adminObject;
    }

    private static List<String> fieldNames(AdminObject adminObject) {
        List<String> names = new ArrayList<>();
        for (AdminField adminField : adminObject.getFields()) {
            names.add(adminField.getFieldName());
        }
        return names;
    }

    private static AdminField field(AdminObject adminObject, String fieldName) {
        for (AdminField adminField : adminObject.getFields()) {
            if (adminField.getName().equals(fieldName) || adminField.getFieldName().equals(fieldName)) {
                return adminField;
            }
        }
        throw new AssertionError("field not found: " + fieldName);
    }

    @SuppressWarnings("unchecked")
    private static void clearAdminContainer() {
        try {
            Field objects = AdminContainer.class.getDeclaredField("adminObjects");
            Field objectMap = AdminContainer.class.getDeclaredField("adminObjectMap");
            Field classMap = AdminContainer.class.getDeclaredField("adminObjectClassMap");
            objects.setAccessible(true);
            objectMap.setAccessible(true);
            classMap.setAccessible(true);
            ((List<AdminObject>) objects.get(null)).clear();
            ((Map<String, AdminObject>) objectMap.get(null)).clear();
            ((Map<Class<?>, AdminObject>) classMap.get(null)).clear();
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @PearObject(
            TableName = "configured_pear_entity",
            path = "/configured-path",
            pluralName = "configured-items",
            editPage = "/configured/edit",
            listPage = "/configured/list",
            iconUrl = "/icon.svg",
            isInvisible = true
    )
    static class ConfiguredPearEntity {
        @Id
        @PearField
        Long id;
    }

    @PearObject
    static class DefaultPearEntity {
        @Id
        @PearField
        Long id;
    }

    @PearObject(TableName = "pear_should_not_win")
    @TableName("mybatis_should_not_win")
    @Entity(name = "entity_should_not_win")
    @Table(name = "jpa_table_name")
    static class JpaTableNameEntity {
        @Id
        @PearField
        Long id;
    }

    @PearObject(TableName = "pear_should_not_win")
    @TableName("mybatis_should_not_win")
    @Entity(name = "entity_table_name")
    static class EntityNameEntity {
        @Id
        @PearField
        Long id;
    }

    @PearObject(TableName = "pear_should_not_win")
    @TableName("`mybatis_table_name`")
    @Entity
    static class MybatisTableNameEntity {
        @Id
        @PearField
        Long id;
    }

    @PearObject(TableName = "pear_table_name")
    static class PearTableNameEntity {
        @Id
        @PearField
        Long id;
    }

    @PearObject
    @Entity
    static class ClassNameTableEntity {
        @Id
        @PearField
        Long id;
    }

    static class ParentFieldEntity {
        @Id
        @GeneratedValue
        @PearField
        Long parentId;

        @PearField
        String parentVisible;

        @PearField
        String overrideMe;
    }

    @PearObject(TableName = "child_field_entity")
    static class ChildFieldEntity extends ParentFieldEntity {
        @Column(name = "child_override")
        @PearField
        String overrideMe;

        @PearField
        String childField;
    }

    @PearObject(TableName = "column_resolution_entity")
    static class ColumnResolutionEntity {
        @Id
        @PearField
        Long id;

        @Column(name = "jpa_column")
        @PearField
        String columnField;

        @TableField(value = "mp_column")
        @PearField
        String tableField;

        @Column(name = "")
        @PearField
        String emptyColumnName;

        @TableField(value = "")
        @PearField
        String emptyTableFieldValue;
    }

    @PearObject(TableName = "non_column_entity")
    static class NonColumnEntity {
        @Id
        @PearField
        Long id;

        @Transient
        @PearField(isPrimaryKey = true)
        String computed;

        @TableField(exist = false)
        @PearField(isUniqueKey = true)
        String virtualValue;
    }

    @PearObject(TableName = "key_source_entity")
    static class KeySourceEntity {
        @Id
        @GeneratedValue
        @PearField
        Long id;

        @Column(unique = true)
        @PearField
        String email;

        @Column(name = "pear_primary")
        @PearField(isPrimaryKey = true)
        String pearPrimary;

        @Column(name = "external_code")
        @PearField(isUniqueKey = true)
        String externalCode;
    }

    @PearObject(TableName = "duplicate_table")
    static class DuplicateTableOne {
        @Id
        @PearField
        Long id;
    }

    @PearObject(TableName = "duplicate_table")
    static class DuplicateTableTwo {
        @Id
        @PearField
        Long id;
    }

    @PearObject(TableName = "no_key_entity")
    static class NoKeyEntity {
        @PearField
        String name;
    }

    @PearObject(TableName = "factory_lookup_custom")
    static class FactoryLookupEntity {
        @Id
        @PearField
        Long id;
    }

    static class Account {
    }

    static class ParentForeignEntity {
        @PearField
        Account account;
    }

    @PearObject(TableName = "child_foreign_entity")
    static class ChildForeignEntity extends ParentForeignEntity {
        @Id
        @PearField
        Long id;

        @PearField
        Long accountId;
    }
}
