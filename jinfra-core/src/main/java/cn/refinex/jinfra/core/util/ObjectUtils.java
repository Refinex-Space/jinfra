package cn.refinex.jinfra.core.util;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import cn.refinex.jinfra.core.error.ErrorCode;
import cn.refinex.jinfra.core.exception.CoreException;

/**
 * 对象工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class ObjectUtils {

    private ObjectUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.ObjectUtils instances");
    }

    /**
     * 当 value 为 null 时返回默认值。
     *
     * @param value 原值
     * @param defaultValue 默认值
     * @param <T> 值类型
     * @return 原值或默认值
     */
    public static <T> T defaultIfNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * null 安全对象相等比较。
     *
     * @param left 左值
     * @param right 右值
     * @return 如果两个对象相等则返回 true
     */
    public static boolean equals(Object left, Object right) {
        return java.util.Objects.equals(left, right);
    }

    /**
     * null 安全哈希值。
     *
     * @param value 待计算对象
     * @return 哈希值
     */
    public static int hashCode(Object value) {
        return java.util.Objects.hashCode(value);
    }

    /**
     * 要求对象不为 null。
     *
     * @param value 待检查对象
     * @param errorCode 失败错误码
     * @param <T> 值类型
     * @return 非空对象
     */
    public static <T> T requireNonNull(T value, ErrorCode errorCode) {
        if (value == null) {
            throw new CoreException(errorCode);
        }
        return value;
    }

    /**
     * 要求对象不为 null。
     *
     * @param value 待检查对象
     * @param message 失败消息
     * @param <T> 值类型
     * @return 非空对象
     */
    public static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new CoreException(CoreErrorCode.NULL_ARGUMENT, message);
        }
        return value;
    }
}
