package cn.refinex.jinfra.codec;

import cn.refinex.jinfra.core.util.AssertUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Hex 编解码工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class HexUtils {

    private HexUtils() {
        throw new AssertionError("No cn.refinex.jinfra.codec.HexUtils instances");
    }

    /**
     * 使用小写 Hex 编码。
     *
     * @param bytes 原始字节
     * @return 小写 Hex 字符串
     */
    public static String encode(byte[] bytes) {
        AssertUtils.notNull(bytes, "bytes must not be null");
        return Hex.encodeHexString(bytes);
    }

    /**
     * 解码 Hex 字符串。
     *
     * @param text Hex 字符串
     * @return 原始字节
     */
    public static byte[] decode(String text) {
        AssertUtils.notNull(text, "hex text must not be null");
        try {
            return Hex.decodeHex(text);
        } catch (DecoderException ex) {
            throw new CodecException("failed to decode Hex text", ex);
        }
    }
}
