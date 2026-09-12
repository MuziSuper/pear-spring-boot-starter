package cn.muzisheng.pear.admin.impl;

import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.mapper.AdminMapper;
import cn.muzisheng.pear.model.AdminField;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.params.AdminQueryResult;
import cn.muzisheng.pear.params.QueryForm;
import cn.muzisheng.pear.params.SqlQuery;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class AdminInputValidationTest {

    @Test
    void handleCreateRejectsNullEmptyAndMissingRequiredFields() {
        AdminServiceImpl service = service(new FakeAdminMapper());
        AdminObject adminObject = adminObject();
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(IllegalException.class, () -> service.handleCreate(request, "items", adminObject, null));
        assertThrows(IllegalException.class, () -> service.handleCreate(request, "items", adminObject, Map.of()));
        assertThrows(IllegalException.class, () -> service.handleCreate(request, "items", adminObject, map("name", "")));
    }

    @Test
    void handleCreateDoesNotRequireAutoNotColumnOrIgnoredFields() {
        FakeAdminMapper mapper = new FakeAdminMapper();
        AdminServiceImpl service = service(mapper);
        AdminObject adminObject = adminObject();

        ResponseEntity<Result<Map<String, Object>>> response = service.handleCreate(
                new MockHttpServletRequest(),
                "items",
                adminObject,
                map("name", "created")
        );

        assertEquals("created", response.getBody().getData().get("name"));
        assertEquals(1, mapper.createCalls);
    }

    @Test
    void handleUpdateRejectsNullOrEmptyDataWithoutNpe() {
        AdminServiceImpl service = service(new FakeAdminMapper());
        AdminObject adminObject = adminObject();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("id", "1");

        assertThrows(IllegalException.class, () -> service.handleUpdate(request, "items", adminObject, null));
        assertThrows(IllegalException.class, () -> service.handleUpdate(request, "items", adminObject, Map.of()));
    }

    @Test
    void unmarshalFromRejectsInvalidPrimitiveAndDateValuesAndConvertsLocalDate() {
        AdminServiceImpl service = service(new FakeAdminMapper());
        AdminObject adminObject = conversionAdminObject();

        assertThrows(IllegalException.class, () -> service.unmarshalFrom(adminObject, map("int_value", "nope"), Map.of(), false));
        assertThrows(IllegalException.class, () -> service.unmarshalFrom(adminObject, map("long_value", "nope"), Map.of(), false));
        assertThrows(IllegalException.class, () -> service.unmarshalFrom(adminObject, map("double_value", "nope"), Map.of(), false));
        assertThrows(IllegalException.class, () -> service.unmarshalFrom(adminObject, map("flag", "maybe"), Map.of(), false));
        assertThrows(IllegalException.class, () -> service.unmarshalFrom(adminObject, map("legacy_date", "not-a-date"), Map.of(), false));

        Map<String, Object> converted = service.unmarshalFrom(adminObject, map("local_date", "2026-05-28"), Map.of(), false);
        assertEquals(LocalDate.of(2026, 5, 28), converted.get("local_date"));
    }

    @Test
    void handleQueryWithoutBodyAndWithoutKeyRunsListQuery() {
        FakeAdminMapper mapper = new FakeAdminMapper();
        AdminServiceImpl service = service(mapper);
        AdminObject adminObject = adminObject();

        ResponseEntity<Result<Object>> response = service.handleQueryOrGetOne(
                new MockHttpServletRequest(),
                "items",
                adminObject,
                null
        );

        assertInstanceOf(AdminQueryResult.class, response.getBody().getData());
        assertEquals(1, mapper.queryCalls);
        assertEquals(0, mapper.selectFirstCalls);
    }

    @Test
    void handleQueryWithoutBodyAndWithKeyRunsGetOne() {
        FakeAdminMapper mapper = new FakeAdminMapper();
        mapper.selectFirstResult = List.of(map("id", "1", "name", "found"));
        AdminServiceImpl service = service(mapper);
        AdminObject adminObject = adminObject();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("id", "1");

        ResponseEntity<Result<Object>> response = service.handleQueryOrGetOne(request, "items", adminObject, new QueryForm());

        assertEquals(map("id", "1", "name", "found"), response.getBody().getData());
        assertEquals(0, mapper.queryCalls);
        assertEquals(1, mapper.selectFirstCalls);
    }

    private static AdminServiceImpl service(AdminMapper mapper) {
        return new AdminServiceImpl(
                mapper,
                mock(UserService.class),
                mock(ConfigService.class),
                mock(ResourcePatternResolver.class)
        );
    }

    private static AdminObject adminObject() {
        AdminObject adminObject = new AdminObject();
        adminObject.setTableName("items");
        adminObject.setShows(List.of("id", "name"));
        adminObject.setEdits(List.of("name"));
        adminObject.setPrimaryKeys(List.of("id"));
        adminObject.setRequires(List.of("id", "name", "created_at", "updated_at", "virtual_value", "ignored_value"));
        adminObject.setIgnores(Map.of("ignored_value", true));
        adminObject.setFields(List.of(
                field("id", Long.class, true, false, false, false),
                field("name", String.class, false, false, false, false),
                field("created_at", LocalDate.class, false, false, true, false),
                field("updated_at", LocalDate.class, false, false, false, true),
                field("virtual_value", String.class, false, true, false, false),
                field("ignored_value", String.class, false, false, false, false)
        ));
        return adminObject;
    }

    private static AdminObject conversionAdminObject() {
        AdminObject adminObject = new AdminObject();
        adminObject.setTableName("conversions");
        adminObject.setEdits(List.of("int_value", "long_value", "double_value", "flag", "legacy_date", "local_date"));
        adminObject.setFields(List.of(
                field("int_value", Integer.class, false, false, false, false),
                field("long_value", Long.class, false, false, false, false),
                field("double_value", Double.class, false, false, false, false),
                field("flag", Boolean.class, false, false, false, false),
                field("legacy_date", java.util.Date.class, false, false, false, false),
                field("local_date", LocalDate.class, false, false, false, false)
        ));
        return adminObject;
    }

    private static AdminField field(String name, Class<?> type, boolean autoId, boolean notColumn, boolean autoInsertTime, boolean autoUpdateTime) {
        AdminField field = new AdminField();
        field.setName(name);
        field.setFieldName(name);
        field.setType(type);
        field.setIsAutoId(autoId);
        field.setNotColumn(notColumn);
        field.setIsAutoInsertTime(autoInsertTime);
        field.setIsAutoUpdateTime(autoUpdateTime);
        return field;
    }

    private static Map<String, Object> map(Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put((String) values[i], values[i + 1]);
        }
        return map;
    }

    private static class FakeAdminMapper implements AdminMapper {
        private int createCalls;
        private int queryCalls;
        private int selectFirstCalls;
        private List<Map<String, Object>> selectFirstResult = new ArrayList<>();

        @Override
        public List<Map<String, Object>> selectFirst(String tableName, Map<String, Object> keys) {
            selectFirstCalls++;
            return selectFirstResult;
        }

        @Override
        public List<Map<String, Object>> query(SqlQuery query) {
            queryCalls++;
            return new ArrayList<>();
        }

        @Override
        public int create(String tableName, Map<String, Object> map) {
            createCalls++;
            return 1;
        }

        @Override
        public int update(String tableName, Map<String, Object> key, Map<String, Object> map) {
            return 1;
        }

        @Override
        public int delete(String tableName, Map<String, Object> keys) {
            return 1;
        }
    }
}
