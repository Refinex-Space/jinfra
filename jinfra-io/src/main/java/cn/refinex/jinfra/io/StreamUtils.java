package cn.refinex.jinfra.io;

import cn.refinex.jinfra.core.util.AssertUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 流工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class StreamUtils {

    private StreamUtils() {
        throw new AssertionError("No cn.refinex.jinfra.io.StreamUtils instances");
    }

    /**
     * 复制输入流到输出流，不关闭传入的流。
     *
     * @param input 输入流
     * @param output 输出流
     * @return 复制字节数
     */
    public static long copy(InputStream input, OutputStream output) {
        AssertUtils.notNull(input, "input stream must not be null");
        AssertUtils.notNull(output, "output stream must not be null");
        try {
            return input.transferTo(output);
        } catch (IOException ex) {
            throw new IoException("failed to copy stream", ex);
        }
    }

    /**
     * 读取输入流全部字节，不关闭传入的流。
     *
     * @param input 输入流
     * @return 字节数组
     */
    public static byte[] toByteArray(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }
}
