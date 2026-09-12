package cn.muzisheng.pear.admin.impl;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.dao.AdminDAO;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.mapper.AdminMapper;
import cn.muzisheng.pear.model.AdminField;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.Order;
import cn.muzisheng.pear.params.AdminQueryResult;
import cn.muzisheng.pear.params.Filter;
import cn.muzisheng.pear.params.QueryForm;
import cn.muzisheng.pear.params.SqlQuery;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminSqlInjectionSafetyTest {
    private static final String INJECTION = "x' OR 1=1 --";

    @Test
    void crudSqlUsesMyBatisBindingsAndDoesNotInlineValues() {
        AdminDAO dao = new AdminDAO();
        Map<String, Object> keys = linkedMap("id", INJECTION);
        Map<String, Object> values = linkedMap("name", INJECTION);

        String selectSql = dao.selectFirst(Map.of("tableName", "users", "keys", keys));
        String createSql = dao.create(Map.of("tableName", "users", "map", values));
        String updateSql = dao.update(Map.of("tableName", "users", "key", keys, "map", values));
        String deleteSql = dao.delete(Map.of("tableName", "users", "keys", keys));

        assertTrue(selectSql.contains("`id` = #{keys['id']}"));
        assertTrue(createSql.contains("#{map['name']}"));
        assertTrue(updateSql.contains("`name` = #{map['name']}"));
        assertTrue(updateSql.contains("`id` = #{key['id']}"));
        assertTrue(deleteSql.contains("`id` = #{keys['id']}"));
        assertDoesNotContainRawValue(selectSql, createSql, updateSql, deleteSql);
    }

    @Test
    void querySqlUsesBindingsForKeywordAndFilterValues() {
        AdminServiceImpl service = newService(mock(AdminMapper.class));
        SqlQuery sqlQuery = buildSqlQuery(service, adminObject(), queryFormWithFiltersAndKeyword());

        String sql = new AdminDAO().query(sqlQuery);

        assertTrue(sql.contains("`users`.`status` = #{bindings.p0}"));
        assertTrue(sql.contains("`users`.`name` LIKE #{bindings.p1} OR `users`.`name` LIKE #{bindings.p2}"));
        assertTrue(sql.contains("`users`.`name` LIKE #{bindings.p3} OR `users`.`email` LIKE #{bindings.p3}"));
        assertEquals(INJECTION, sqlQuery.getBindings().get("p0"));
        assertEquals("%" + INJECTION + "%", sqlQuery.getBindings().get("p1"));
        assertEquals("%keyword' OR 1=1 --%", sqlQuery.getBindings().get("p3"));
        assertDoesNotContainRawValue(sql);
    }

    @Test
    void rejectsInvalidFilterOrderViewAndSearchFields() {
        AdminServiceImpl service = newService(mock(AdminMapper.class));
        AdminObject adminObject = adminObject();

        assertThrows(IllegalException.class, () -> buildSqlQuery(service, adminObject, formWithFilter("role", "=", "admin")));
        assertThrows(IllegalException.class, () -> buildSqlQuery(service, adminObject, formWithOrder("role", "asc")));
        assertThrows(IllegalException.class, () -> buildSqlQuery(service, adminObject, formWithOrder("id", "desc; drop table users")));
        assertThrows(IllegalException.class, () -> buildSqlQuery(service, adminObject, formWithViewFields("email")));
        assertThrows(IllegalException.class, () -> buildSqlQuery(service, adminObject, formWithSearchFields("status")));
    }

    @Test
    void combinesMultipleFiltersWithAndLikeValuesWithOrAndKeywordWithAnd() {
        AdminServiceImpl service = newService(mock(AdminMapper.class));

        SqlQuery sqlQuery = buildSqlQuery(service, adminObject(), queryFormWithFiltersAndKeyword());

        assertEquals(
                "`users`.`status` = #{bindings.p0} AND (`users`.`name` LIKE #{bindings.p1} OR `users`.`name` LIKE #{bindings.p2}) AND (`users`.`name` LIKE #{bindings.p3} OR `users`.`email` LIKE #{bindings.p3})",
                sqlQuery.getWhereClause()
        );
    }

    @Test
    void foreignModeQuerySetsLimitToZeroBeforeExecutingQuery() {
        AdminMapper adminMapper = mock(AdminMapper.class);
        when(adminMapper.query(org.mockito.ArgumentMatchers.any(SqlQuery.class))).thenReturn(List.of());
        AdminServiceImpl service = newService(adminMapper);
        QueryForm queryForm = new QueryForm();
        queryForm.setLimit(50);
        queryForm.setForeignMode(true);

        AdminQueryResult result = queryObjects(service, mock(HttpServletRequest.class), adminObject(), queryForm);

        ArgumentCaptor<SqlQuery> captor = ArgumentCaptor.forClass(SqlQuery.class);
        verify(adminMapper).query(captor.capture());
        assertEquals(0, captor.getValue().getLimit());
        assertEquals(0, queryForm.getLimit());
        assertEquals(0, result.getLimit());
    }

    private static QueryForm queryFormWithFiltersAndKeyword() {
        QueryForm queryForm = new QueryForm();
        queryForm.setLimit(25);
        queryForm.setViewFields(List.of("id", "name"));
        queryForm.setSearchFields(List.of("name", "email"));
        queryForm.setOrders(List.of(new Order("id", Constant.ORDER_OP_DESC)));
        queryForm.setKeyword("keyword' OR 1=1 --");
        queryForm.setFilters(List.of(
                filter("status", Constant.FILTER_OP_EQUAL, INJECTION),
                filter("name", Constant.FILTER_OP_LIKE, List.of(INJECTION, "bob"))
        ));
        return queryForm;
    }

    private static QueryForm formWithFilter(String name, String op, Object value) {
        QueryForm queryForm = new QueryForm();
        queryForm.setFilters(List.of(filter(name, op, value)));
        return queryForm;
    }

    private static QueryForm formWithOrder(String name, String op) {
        QueryForm queryForm = new QueryForm();
        queryForm.setOrders(List.of(new Order(name, op)));
        return queryForm;
    }

    private static QueryForm formWithViewFields(String... fields) {
        QueryForm queryForm = new QueryForm();
        queryForm.setViewFields(List.of(fields));
        return queryForm;
    }

    private static QueryForm formWithSearchFields(String... fields) {
        QueryForm queryForm = new QueryForm();
        queryForm.setSearchFields(List.of(fields));
        queryForm.setKeyword("keyword");
        return queryForm;
    }

    private static Filter filter(String name, String op, Object value) {
        Filter filter = new Filter();
        filter.setName(name);
        filter.setOp(op);
        filter.setValue(value);
        return filter;
    }

    private static AdminObject adminObject() {
        AdminObject adminObject = new AdminObject();
        adminObject.setTableName("users");
        adminObject.setFields(fields("id", "name", "status", "email"));
        adminObject.setShows(List.of("id", "name"));
        adminObject.setFilterables(List.of("status", "name"));
        adminObject.setOrderables(List.of("id", "name"));
        adminObject.setSearches(List.of("name", "email"));
        return adminObject;
    }

    private static List<AdminField> fields(String... names) {
        List<AdminField> fields = new ArrayList<>();
        for (String name : names) {
            AdminField field = new AdminField();
            field.setFieldName(name);
            field.setType(String.class);
            field.setNotColumn(false);
            fields.add(field);
        }
        return fields;
    }

    private static AdminServiceImpl newService(AdminMapper adminMapper) {
        return new AdminServiceImpl(
                adminMapper,
                mock(UserService.class),
                mock(ConfigService.class),
                mock(ResourcePatternResolver.class)
        );
    }

    private static SqlQuery buildSqlQuery(AdminServiceImpl service, AdminObject adminObject, QueryForm queryForm) {
        return invokePrivate(service, "buildSqlQuery", new Class<?>[]{AdminObject.class, QueryForm.class}, adminObject, queryForm);
    }

    private static AdminQueryResult queryObjects(AdminServiceImpl service, HttpServletRequest request, AdminObject adminObject, QueryForm queryForm) {
        return invokePrivate(
                service,
                "queryObjects",
                new Class<?>[]{HttpServletRequest.class, String.class, AdminObject.class, QueryForm.class},
                request,
                "users",
                adminObject,
                queryForm
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> T invokePrivate(Object target, String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return (T) method.invoke(target, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new AssertionError(cause);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static Map<String, Object> linkedMap(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

    private static void assertDoesNotContainRawValue(String... sqlStatements) {
        for (String sql : sqlStatements) {
            assertFalse(sql.contains(INJECTION), sql);
            assertFalse(sql.contains("keyword' OR 1=1 --"), sql);
        }
    }
}
