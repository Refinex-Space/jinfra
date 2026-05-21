package cn.refinex.jinfra.core.error;

/**
 * 错误码抽象。
 *
 * <p>下游模块可以通过枚举或普通类实现该接口，异常和结果模型只对外暴露字符串形式的错误码。</p>
 *
 * @author refinex
 * @since 0.1.0
 */
public interface ErrorCode {

    /**
     * 返回稳定的错误码。
     *
     * @return 错误码
     */
    String code();

    /**
     * 返回默认开发者消息。
     *
     * @return 默认消息
     */
    String message();
}
