package cn.refinex.jinfra.context;

import cn.refinex.jinfra.core.util.AssertUtils;

/**
 * 当前线程上下文持有器。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class JInfraContextHolder {

    private static final ThreadLocal<JInfraContext> HOLDER = ThreadLocal.withInitial(JInfraContext::empty);

    private JInfraContextHolder() {
        throw new AssertionError("No cn.refinex.jinfra.context.JInfraContextHolder instances");
    }

    /**
     * 获取当前线程上下文。
     *
     * @return 当前线程上下文
     */
    public static JInfraContext current() {
        return HOLDER.get();
    }

    /**
     * 设置当前线程上下文。
     *
     * @param context 上下文
     */
    public static void set(JInfraContext context) {
        AssertUtils.notNull(context, "context must not be null");
        HOLDER.set(context);
    }

    /**
     * 清理当前线程上下文。
     */
    public static void clear() {
        HOLDER.remove();
    }

    /**
     * 捕获当前线程上下文快照。
     *
     * @return 上下文快照
     */
    public static ContextSnapshot capture() {
        return ContextSnapshot.from(current());
    }

    /**
     * 打开快照上下文作用域。
     *
     * @param snapshot 上下文快照
     * @return 可关闭作用域，关闭时恢复旧上下文
     */
    public static ContextScope open(ContextSnapshot snapshot) {
        AssertUtils.notNull(snapshot, "context snapshot must not be null");

        JInfraContext previousContext = current();
        set(snapshot.context());
        return ContextScope.onClose(() -> set(previousContext));
    }
}
