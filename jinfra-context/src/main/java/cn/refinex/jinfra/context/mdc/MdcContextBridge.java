package cn.refinex.jinfra.context.mdc;

import cn.refinex.jinfra.context.ContextException;
import cn.refinex.jinfra.context.ContextKey;
import cn.refinex.jinfra.context.ContextScope;
import cn.refinex.jinfra.context.ContextSnapshot;
import cn.refinex.jinfra.core.util.AssertUtils;
import java.util.List;
import org.slf4j.MDC;

/**
 * JInfra 上下文与 SLF4J MDC 的桥接工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class MdcContextBridge {

    private static final List<ContextKey<String>> KNOWN_KEYS = List.of(
            ContextKey.TRACE_ID,
            ContextKey.TENANT_ID,
            ContextKey.USER_ID);

    private MdcContextBridge() {
        throw new AssertionError("No cn.refinex.jinfra.context.mdc.MdcContextBridge instances");
    }

    /**
     * 将快照中的已知上下文键写入 MDC。
     *
     * @param snapshot 上下文快照
     */
    public static void put(ContextSnapshot snapshot) {
        AssertUtils.notNull(snapshot, "context snapshot must not be null");
        try {
            for (ContextKey<String> key : KNOWN_KEYS) {
                String value = snapshot.context().get(key);
                if (value == null) {
                    MDC.remove(key.name());
                } else {
                    MDC.put(key.name(), value);
                }
            }
        } catch (RuntimeException ex) {
            throw new ContextException("failed to put context into MDC", ex);
        }
    }

    /**
     * 打开 MDC 作用域，关闭时清理已知上下文键。
     *
     * @param snapshot 上下文快照
     * @return 可关闭作用域
     */
    public static ContextScope withMdc(ContextSnapshot snapshot) {
        put(snapshot);
        return ContextScope.onClose(MdcContextBridge::clearKnownKeys);
    }

    /**
     * 清理 MDC 中的已知上下文键。
     */
    public static void clearKnownKeys() {
        try {
            for (ContextKey<String> key : KNOWN_KEYS) {
                MDC.remove(key.name());
            }
        } catch (RuntimeException ex) {
            throw new ContextException("failed to clear context from MDC", ex);
        }
    }
}
