package cn.refinex.jinfra.context;

import cn.refinex.jinfra.core.exception.CoreException;
import java.io.Serial;

/**
 * 上下文模块异常。
 *
 * @author refinex
 * @since 0.1.0
 */
public class ContextException extends CoreException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 上下文模块默认错误码。
     */
    public static final String CODE = "CONTEXT_ERROR";

    /**
     * 使用异常消息创建上下文异常。
     *
     * @param message 异常消息
     */
    public ContextException(String message) {
        super(CODE, message);
    }

    /**
     * 使用异常消息和原始异常创建上下文异常。
     *
     * @param message 异常消息
     * @param cause 原始异常
     */
    public ContextException(String message, Throwable cause) {
        super(CODE, message, cause);
    }
}
