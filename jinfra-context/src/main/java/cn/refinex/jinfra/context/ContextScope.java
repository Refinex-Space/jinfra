package cn.refinex.jinfra.context;

import cn.refinex.jinfra.core.util.AssertUtils;

/**
 * 可关闭的上下文作用域。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class ContextScope implements AutoCloseable {

    /**
     * 关闭动作。
     */
    private final Runnable closeAction;

    /**
     * 是否已关闭。
     */
    private boolean closed;

    /**
     * 使用关闭动作创建上下文作用域。
     *
     * @param closeAction 关闭时执行的动作
     */
    private ContextScope(Runnable closeAction) {
        AssertUtils.notNull(closeAction, "context scope close action must not be null");
        this.closeAction = closeAction;
    }

    /**
     * 使用关闭动作创建上下文作用域。
     *
     * @param closeAction 关闭时执行的动作
     * @return 上下文作用域
     */
    public static ContextScope onClose(Runnable closeAction) {
        return new ContextScope(closeAction);
    }

    /**
     * 关闭作用域并执行恢复动作。
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        closeAction.run();
    }
}
