package cn.refinex.jinfra.codec;

import cn.refinex.jinfra.core.util.AssertUtils;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * URL 编解码工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class UrlCodecUtils {

    private UrlCodecUtils() {
        throw new AssertionError("No cn.refinex.jinfra.codec.UrlCodecUtils instances");
    }

    /**
     * URL 编码。
     *
     * @param text 原始文本
     * @param charset 字符集
     * @return URL 编码文本
     */
    public static String encode(String text, Charset charset) {
        AssertUtils.notNull(text, "url text must not be null");
        AssertUtils.notNull(charset, "charset must not be null");
        return URLEncoder.encode(text, charset);
    }

    /**
     * URL 解码。
     *
     * @param text URL 编码文本
     * @param charset 字符集
     * @return 原始文本
     */
    public static String decode(String text, Charset charset) {
        AssertUtils.notNull(text, "url text must not be null");
        AssertUtils.notNull(charset, "charset must not be null");
        try {
            return URLDecoder.decode(text, charset);
        } catch (IllegalArgumentException ex) {
            throw new CodecException("failed to decode URL text", ex);
        }
    }
}
