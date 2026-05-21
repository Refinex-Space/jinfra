package cn.refinex.jinfra.core.lang;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import cn.refinex.jinfra.core.error.ErrorCode;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * 不可变结果模型。
 *
 * @param success 是否成功
 * @param code 错误码
 * @param message 消息
 * @param data 数据
 * @param <T> 数据类型
 * @author refinex
 * @since 0.1.0
 */
public record Result<T>(boolean success, String code, String message, T data) {

    /**
     * 创建结果模型。
     *
     * @param success 是否成功
     * @param code 错误码
     * @param message 消息
     * @param data 数据
     */
    public Result {
        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException(success ? "success code must not be blank" : "failure code must not be blank");
        }
        if (StringUtils.isBlank(message)) {
            throw new IllegalArgumentException(success ? "success message must not be blank" : "failure message must not be blank");
        }
        if (!success && CoreErrorCode.SUCCESS.code().equals(code)) {
            throw new IllegalArgumentException("failure code must not be SUCCESS");
        }
    }

    /**
     * 创建无数据成功结果。
     *
     * @return 成功结果
     */
    public static Result<Void> ok() {
        return new Result<>(true, CoreErrorCode.SUCCESS.code(), CoreErrorCode.SUCCESS.message(), null);
    }

    /**
     * 创建带数据成功结果。
     *
     * @param data 数据
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(true, CoreErrorCode.SUCCESS.code(), CoreErrorCode.SUCCESS.message(), data);
    }

    /**
     * 使用字符串错误码和消息创建失败结果。
     *
     * @param code 错误码，不能为 blank，且不能为 SUCCESS
     * @param message 消息，不能为 blank
     * @return 失败结果
     */
    public static Result<Void> failure(String code, String message) {
        return new Result<>(false, code, message, null);
    }

    /**
     * 使用错误码默认消息创建失败结果。
     *
     * @param errorCode 错误码，不能为 null
     * @return 失败结果
     */
    public static Result<Void> failure(ErrorCode errorCode) {
        Objects.requireNonNull(errorCode, "errorCode must not be null");
        return failure(errorCode.code(), errorCode.message());
    }

    /**
     * 使用错误码和自定义消息创建失败结果。
     *
     * @param errorCode 错误码，不能为 null
     * @param message 消息，不能为 blank
     * @return 失败结果
     */
    public static Result<Void> failure(ErrorCode errorCode, String message) {
        Objects.requireNonNull(errorCode, "errorCode must not be null");
        return failure(errorCode.code(), message);
    }
}
