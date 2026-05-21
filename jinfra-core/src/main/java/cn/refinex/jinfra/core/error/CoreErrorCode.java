package cn.refinex.jinfra.core.error;

/**
 * core 模块内置错误码。
 *
 * @author refinex
 * @since 0.1.0
 */
public enum CoreErrorCode implements ErrorCode {

    /**
     * 操作成功。
     */
    SUCCESS("SUCCESS", "Success"),

    /**
     * 参数不合法。
     */
    INVALID_ARGUMENT("INVALID_ARGUMENT", "Invalid argument"),

    /**
     * 参数为空。
     */
    NULL_ARGUMENT("NULL_ARGUMENT", "Null argument"),

    /**
     * 状态不合法。
     */
    ILLEGAL_STATE("ILLEGAL_STATE", "Illegal state"),

    /**
     * 内部错误。
     */
    INTERNAL_ERROR("INTERNAL_ERROR", "Internal error");

    private final String code;

    private final String message;

    CoreErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
