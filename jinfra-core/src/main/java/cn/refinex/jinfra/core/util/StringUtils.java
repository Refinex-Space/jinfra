package cn.refinex.jinfra.core.util;

/**
 * 字符串工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class StringUtils {

    private StringUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.StringUtils instances");
    }

    /**
     * 判断字符串是否为 null、空字符串或空白字符串。
     *
     * @param value 待检查字符串
     * @return 如果字符串为空白则返回 true
     */
    public static boolean isBlank(String value) {
        return org.apache.commons.lang3.StringUtils.isBlank(value);
    }

    /**
     * 判断字符串是否包含非空白字符。
     *
     * @param value 待检查字符串
     * @return 如果字符串包含非空白字符则返回 true
     */
    public static boolean isNotBlank(String value) {
        return org.apache.commons.lang3.StringUtils.isNotBlank(value);
    }

    /**
     * 去除首尾空白，null 返回空字符串。
     *
     * @param value 待处理字符串
     * @return 处理后的字符串
     */
    public static String trimToEmpty(String value) {
        return org.apache.commons.lang3.StringUtils.trimToEmpty(value);
    }

    /**
     * 当字符串为空白时返回默认值。
     *
     * @param value 待检查字符串
     * @param defaultValue 默认值
     * @return 原字符串或默认值
     */
    public static String defaultIfBlank(String value, String defaultValue) {
        return org.apache.commons.lang3.StringUtils.defaultIfBlank(value, defaultValue);
    }

    /**
     * null 安全字符串相等比较。
     *
     * @param left 左值
     * @param right 右值
     * @return 如果两个字符串相等则返回 true
     */
    public static boolean equals(String left, String right) {
        return java.util.Objects.equals(left, right);
    }
}
