package cn.refinex.jinfra.codec;

import cn.refinex.jinfra.core.exception.CoreException;
import java.io.Serial;

/**
 * 编解码模块异常。
 *
 * @author refinex
 * @since 0.1.0
 */
public class CodecException extends CoreException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编解码模块默认错误码。
     */
    public static final String CODE = "CODEC_ERROR";

    /**
     * 使用异常消息创建编解码异常。
     *
     * @param message 异常消息
     */
    public CodecException(String message) {
        super(CODE, message);
    }

    /**
     * 使用异常消息和原始异常创建编解码异常。
     *
     * @param message 异常消息
     * @param cause 原始异常
     */
    public CodecException(String message, Throwable cause) {
        super(CODE, message, cause);
    }
}
