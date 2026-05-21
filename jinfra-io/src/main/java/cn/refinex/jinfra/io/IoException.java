package cn.refinex.jinfra.io;

import cn.refinex.jinfra.core.exception.CoreException;
import java.io.Serial;

/**
 * IO 模块异常。
 *
 * @author refinex
 * @since 0.1.0
 */
public class IoException extends CoreException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * IO 模块默认错误码。
     */
    public static final String CODE = "IO_ERROR";

    /**
     * 使用异常消息创建 IO 异常。
     *
     * @param message 异常消息
     */
    public IoException(String message) {
        super(CODE, message);
    }

    /**
     * 使用异常消息和原始异常创建 IO 异常。
     *
     * @param message 异常消息
     * @param cause 原始异常
     */
    public IoException(String message, Throwable cause) {
        super(CODE, message, cause);
    }
}
