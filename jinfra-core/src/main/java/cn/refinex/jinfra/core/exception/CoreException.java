package cn.refinex.jinfra.core.exception;

import cn.refinex.jinfra.core.error.ErrorCode;

import java.io.Serial;

/**
 * core 模块使用的基础异常。
 *
 * @author refinex
 * @since 0.1.0
 */
public class CoreException extends JInfraRuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 使用错误码和默认消息创建异常。
     *
     * @param errorCode 错误码，不能为 null
     */
    public CoreException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 使用错误码和自定义消息创建异常。
     *
     * @param errorCode 错误码，不能为 null
     * @param message 异常消息
     */
    public CoreException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 使用错误码、自定义消息和 cause 创建异常。
     *
     * @param errorCode 错误码，不能为 null
     * @param message 异常消息
     * @param cause 原始异常
     */
    public CoreException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 使用字符串错误码和消息创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     */
    public CoreException(String code, String message) {
        super(code, message);
    }

    /**
     * 使用字符串错误码、消息和 cause 创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public CoreException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
