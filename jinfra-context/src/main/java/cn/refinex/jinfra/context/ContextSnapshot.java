package cn.refinex.jinfra.context;

import cn.refinex.jinfra.core.util.AssertUtils;

/**
 * 上下文快照。
 *
 * @param context 被捕获的上下文
 * @author refinex
 * @since 0.1.0
 */
public record ContextSnapshot(JInfraContext context) {

    /**
     * 创建上下文快照。
     *
     * @param context 被捕获的上下文
     */
    public ContextSnapshot {
        AssertUtils.notNull(context, "context must not be null");
    }

    /**
     * 从上下文创建快照。
     *
     * @param context 被捕获的上下文
     * @return 上下文快照
     */
    public static ContextSnapshot from(JInfraContext context) {
        return new ContextSnapshot(context);
    }
}
