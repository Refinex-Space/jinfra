package cn.refinex.jinfra.core.exception;

import cn.refinex.jinfra.core.error.ErrorCode;

import java.io.Serial;

/**
 * JInfra 统一根异常。
 *
 * @author refinex
 * @since 0.1.0
 */
public class JInfraException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String code;

    /**
     * 使用错误码和默认消息创建异常。
     *
     * @param errorCode 错误码，不能为 null
     */
    public JInfraException(ErrorCode errorCode) {
        this(errorCode.code(), errorCode.message());
    }

    /**
     * 使用错误码和自定义消息创建异常。
     *
     * @param errorCode 错误码，不能为 null
     * @param message 异常消息
     */
    public JInfraException(ErrorCode errorCode, String message) {
        this(errorCode.code(), message);
    }

    /**
     * 使用错误码、自定义消息和 cause 创建异常。
     *
     * @param errorCode 错误码，不能为 null
     * @param message 异常消息
     * @param cause 原始异常
     */
    public JInfraException(ErrorCode errorCode, String message, Throwable cause) {
        this(errorCode.code(), message, cause);
    }

    /**
     * 使用字符串错误码和消息创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     */
    public JInfraException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用字符串错误码、消息和 cause 创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public JInfraException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 返回错误码。
     *
     * @return 错误码
     */
    public String code() {
        return code;
    }
}
