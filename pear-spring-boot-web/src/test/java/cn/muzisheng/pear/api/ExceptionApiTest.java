package cn.muzisheng.pear.api;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.exception.AdminErrorException;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.exception.CacheException;
import cn.muzisheng.pear.exception.ForbiddenException;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.exception.HookException;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.exception.NotFoundException;
import cn.muzisheng.pear.exception.SqlStatementException;
import cn.muzisheng.pear.vo.common.ErrorVO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class ExceptionApiTest {
    private final ExceptionApi api = new ExceptionApi();

    @Test
    void mapsClientErrorsToExpectedStatuses() {
        assertResponse(api.handleNotFoundException(new NotFoundException("missing")), HttpStatus.NOT_FOUND, Constant.NOT_FOUND_EXCEPTION, "missing");
        assertResponse(api.handleIllegalException(new IllegalException("bad input")), HttpStatus.BAD_REQUEST, Constant.ILLEGAL_EXCEPTION, "bad input");
        assertResponse(api.handleAuthorizationException(new AuthorizationException("unauthorized")), HttpStatus.UNAUTHORIZED, Constant.UNAPPROVED_EXCEPTION, "unauthorized");
        assertResponse(api.handleForbiddenException(new ForbiddenException("forbidden")), HttpStatus.FORBIDDEN, Constant.FORBIDDEN_EXCEPTION, "forbidden");
        assertResponse(api.handleBadRequest(new HttpMessageNotReadableException("bad json", mock(HttpInputMessage.class))), HttpStatus.BAD_REQUEST, Constant.ILLEGAL_EXCEPTION, Constant.ILLEGAL_MESSAGE_EXCEPTION);
    }

    @Test
    void mapsServerErrorsToInternalServerError() {
        assertResponse(api.handleSqlStatementException(new SqlStatementException("sql")), HttpStatus.INTERNAL_SERVER_ERROR, Constant.SQL_EXCEPTION, "sql");
        assertResponse(api.handleHookException(new HookException("hook")), HttpStatus.INTERNAL_SERVER_ERROR, Constant.HOOK_EXCEPTION, "hook");
        assertResponse(api.handleTimeException(new AdminErrorException("admin")), HttpStatus.INTERNAL_SERVER_ERROR, Constant.ADMIN_EXCEPTION, "admin");
        assertResponse(api.handleCacheExecption(new CacheException("cache")), HttpStatus.INTERNAL_SERVER_ERROR, Constant.CACHE_EXCEPTION, "cache");
        assertResponse(api.handleGeneralException(new GeneralException("general")), HttpStatus.INTERNAL_SERVER_ERROR, Constant.GENERAL_EXCEPTION, "general");
    }

    @Test
    void mapsUnexpectedExceptionToGenericInternalServerErrorMessage() {
        assertResponse(api.handleException(new RuntimeException("secret detail")), HttpStatus.INTERNAL_SERVER_ERROR, Constant.GENERAL_EXCEPTION, "Internal server error");
    }

    private static void assertResponse(ResponseEntity<ErrorVO> response, HttpStatus httpStatus, int status, String message) {
        assertEquals(httpStatus, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(status, response.getBody().getStatus());
        assertEquals(message, response.getBody().getMessage());
        assertNotNull(response.getBody().getTime());
    }
}
