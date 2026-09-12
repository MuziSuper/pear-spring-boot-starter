package cn.muzisheng.pear.util;

import cn.muzisheng.pear.exception.BaseException;
import cn.muzisheng.pear.exception.BusinessException;
import cn.muzisheng.pear.exception.ErrorCode;

import java.util.Collection;
import java.util.Objects;

/**
 * Lightweight precondition assertions for application code.
 * Prefer the {@link BaseException} overloads when the caller needs an exact exception type.
 */
public final class AssertUtil {
    private AssertUtil() {
    }

    /** Throws the supplied exception when {@code expression} is false. */
    public static void isTrue(boolean expression, BaseException exception) {
        if (!expression) {
            throw exception;
        }
    }

    /** Throws a {@link BusinessException} with the given error code when {@code expression} is false. */
    public static void isTrue(boolean expression, ErrorCode errorCode, Object... messageArgs) {
        if (!expression) {
            throw businessException(errorCode, messageArgs);
        }
    }

    public static void isFalse(boolean expression, BaseException exception) {
        isTrue(!expression, exception);
    }

    public static void isFalse(boolean expression, ErrorCode errorCode, Object... messageArgs) {
        isTrue(!expression, errorCode, messageArgs);
    }

    /** Verifies a value is present and returns it for fluent assignment. */
    public static <T> T notNull(T value, BaseException exception) {
        isTrue(value != null, exception);
        return value;
    }

    public static <T> T notNull(T value, ErrorCode errorCode, Object... messageArgs) {
        isTrue(value != null, errorCode, messageArgs);
        return value;
    }

    /** Verifies that a string contains at least one non-whitespace character. */
    public static String hasText(String value, BaseException exception) {
        isTrue(value != null && !value.isBlank(), exception);
        return value;
    }

    public static String hasText(String value, ErrorCode errorCode, Object... messageArgs) {
        isTrue(value != null && !value.isBlank(), errorCode, messageArgs);
        return value;
    }

    public static <T extends Collection<?>> T notEmpty(T value, BaseException exception) {
        isTrue(value != null && !value.isEmpty(), exception);
        return value;
    }

    public static void equals(Object expected, Object actual, BaseException exception) {
        isTrue(Objects.equals(expected, actual), exception);
    }

    public static void notEquals(Object left, Object right, BaseException exception) {
        isTrue(!Objects.equals(left, right), exception);
    }

    public static void state(boolean expression, BaseException exception) {
        isTrue(expression, exception);
    }

    private static BusinessException businessException(ErrorCode errorCode, Object... messageArgs) {
        String message = messageArgs == null || messageArgs.length == 0 ? null : String.valueOf(messageArgs[0]);
        return new BusinessException(errorCode, message);
    }
}
