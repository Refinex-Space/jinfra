package cn.refinex.jinfra.context;

import cn.refinex.jinfra.core.util.AssertUtils;

/**
 * 上下文键定义。
 *
 * @param name 上下文键名称
 * @param <T> 上下文值类型标记
 * @author refinex
 * @since 0.1.0
 */
public record ContextKey<T>(String name) {

    /**
     * 链路追踪 ID。
     */
    public static final ContextKey<String> TRACE_ID = new ContextKey<>("traceId");

    /**
     * 租户 ID。
     */
    public static final ContextKey<String> TENANT_ID = new ContextKey<>("tenantId");

    /**
     * 用户 ID。
     */
    public static final ContextKey<String> USER_ID = new ContextKey<>("userId");

    /**
     * 创建上下文键。
     *
     * @param name 上下文键名称
     */
    public ContextKey {
        AssertUtils.hasText(name, "context key name must not be blank");
    }
}
