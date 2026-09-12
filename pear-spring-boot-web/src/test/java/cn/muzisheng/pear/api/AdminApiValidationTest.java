package cn.muzisheng.pear.api;

import cn.muzisheng.pear.exception.NotFoundException;
import cn.muzisheng.pear.dto.admin.AdminCreateRequestDTO;
import cn.muzisheng.pear.dto.admin.AdminQueryDTO;
import cn.muzisheng.pear.dto.admin.AdminUpdateRequestDTO;
import cn.muzisheng.pear.initialize.AdminContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AdminApiValidationTest {

    @AfterEach
    void cleanAdminContainer() {
        clearAdminContainer();
    }

    @Test
    void missingModelThrowsNotFoundExceptionForAdminEndpoints() {
        clearAdminContainer();
        AdminApi api = new AdminApi();
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(NotFoundException.class, () -> api.query(request, "missing_model", new AdminQueryDTO()));
        assertThrows(NotFoundException.class, () -> api.handleCreate(request, "missing_model", new AdminCreateRequestDTO()));
        assertThrows(NotFoundException.class, () -> api.handleUpdate(request, "missing_model", new AdminUpdateRequestDTO()));
        assertThrows(NotFoundException.class, () -> api.handleDelete(request, "missing_model"));
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
            ((List<?>) objects.get(null)).clear();
            ((Map<?, ?>) objectMap.get(null)).clear();
            ((Map<?, ?>) classMap.get(null)).clear();
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
