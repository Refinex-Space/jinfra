package cn.refinex.jinfra.core.util;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import cn.refinex.jinfra.core.error.ErrorCode;
import cn.refinex.jinfra.core.exception.CoreException;
import java.util.Collection;

/**
 * 断言工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class AssertUtils {

    private AssertUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.AssertUtils instances");
    }

    /**
     * 断言对象不为 null。
     *
     * @param value 待检查对象
     * @param errorCode 断言失败错误码
     */
    public static void notNull(Object value, ErrorCode errorCode) {
        if (value == null) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言对象不为 null。
     *
     * @param value 待检查对象
     * @param message 断言失败消息
     */
    public static void notNull(Object value, String message) {
        if (value == null) {
            throw new CoreException(CoreErrorCode.NULL_ARGUMENT, message);
        }
    }

    /**
     * 断言字符串包含非空白字符。
     *
     * @param value 待检查字符串
     * @param errorCode 断言失败错误码
     */
    public static void hasText(String value, ErrorCode errorCode) {
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言字符串包含非空白字符。
     *
     * @param value 待检查字符串
     * @param message 断言失败消息
     */
    public static void hasText(String value, String message) {
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            throw new CoreException(CoreErrorCode.INVALID_ARGUMENT, message);
        }
    }

    /**
     * 断言表达式为 true。
     *
     * @param expression 表达式
     * @param errorCode 断言失败错误码
     */
    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言表达式为 true。
     *
     * @param expression 表达式
     * @param message 断言失败消息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new CoreException(CoreErrorCode.INVALID_ARGUMENT, message);
        }
    }

    /**
     * 断言状态表达式为 true。
     *
     * @param expression 状态表达式
     * @param errorCode 断言失败错误码
     */
    public static void state(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言状态表达式为 true。
     *
     * @param expression 状态表达式
     * @param message 断言失败消息
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new CoreException(CoreErrorCode.ILLEGAL_STATE, message);
        }
    }

    /**
     * 断言集合不为 null 且非空。
     *
     * @param collection 待检查集合
     * @param errorCode 断言失败错误码
     */
    public static void notEmpty(Collection<?> collection, ErrorCode errorCode) {
        if (collection == null || collection.isEmpty()) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言集合不为 null 且非空。
     *
     * @param collection 待检查集合
     * @param message 断言失败消息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new CoreException(CoreErrorCode.INVALID_ARGUMENT, message);
        }
    }
}
