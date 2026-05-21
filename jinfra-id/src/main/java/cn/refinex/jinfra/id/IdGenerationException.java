package cn.refinex.jinfra.id;

import cn.refinex.jinfra.core.exception.CoreException;
import java.io.Serial;

/**
 * ID 生成异常。
 *
 * @author refinex
 * @since 0.1.0
 */
public class IdGenerationException extends CoreException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID 模块默认错误码。
     */
    public static final String CODE = "ID_GENERATION_ERROR";

    /**
     * 使用异常消息创建 ID 生成异常。
     *
     * @param message 异常消息
     */
    public IdGenerationException(String message) {
        super(CODE, message);
    }
}
