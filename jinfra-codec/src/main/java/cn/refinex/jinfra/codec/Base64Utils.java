package cn.refinex.jinfra.codec;

import cn.refinex.jinfra.core.util.AssertUtils;
import java.util.Base64;

/**
 * Base64 编解码工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class Base64Utils {

    private Base64Utils() {
        throw new AssertionError("No cn.refinex.jinfra.codec.Base64Utils instances");
    }

    /**
     * 标准 Base64 编码。
     *
     * @param bytes 原始字节
     * @return Base64 字符串
     */
    public static String encode(byte[] bytes) {
        AssertUtils.notNull(bytes, "bytes must not be null");
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 标准 Base64 解码。
     *
     * @param text Base64 字符串
     * @return 原始字节
     */
    public static byte[] decode(String text) {
        AssertUtils.notNull(text, "base64 text must not be null");
        try {
            return Base64.getDecoder().decode(text);
        } catch (IllegalArgumentException ex) {
            throw new CodecException("failed to decode Base64 text", ex);
        }
    }

    /**
     * URL Safe Base64 编码。
     *
     * @param bytes 原始字节
     * @return URL Safe Base64 字符串
     */
    public static String encodeUrlSafe(byte[] bytes) {
        AssertUtils.notNull(bytes, "bytes must not be null");
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    /**
     * URL Safe Base64 解码。
     *
     * @param text URL Safe Base64 字符串
     * @return 原始字节
     */
    public static byte[] decodeUrlSafe(String text) {
        AssertUtils.notNull(text, "base64 text must not be null");
        try {
            return Base64.getUrlDecoder().decode(text);
        } catch (IllegalArgumentException ex) {
            throw new CodecException("failed to decode URL safe Base64 text", ex);
        }
    }
}
