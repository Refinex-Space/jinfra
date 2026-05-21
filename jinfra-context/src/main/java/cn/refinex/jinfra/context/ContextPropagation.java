package cn.refinex.jinfra.context;

import cn.refinex.jinfra.core.util.AssertUtils;
import java.util.concurrent.Callable;

/**
 * 上下文传播工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class ContextPropagation {

    private ContextPropagation() {
        throw new AssertionError("No cn.refinex.jinfra.context.ContextPropagation instances");
    }

    /**
     * 捕获当前上下文并包装 Runnable。
     *
     * @param runnable 原始任务
     * @return 带上下文恢复能力的任务
     */
    public static Runnable wrap(Runnable runnable) {
        AssertUtils.notNull(runnable, "runnable must not be null");
        ContextSnapshot snapshot = JInfraContextHolder.capture();
        return () -> {
            try (ContextScope ignored = JInfraContextHolder.open(snapshot)) {
                runnable.run();
            }
        };
    }

    /**
     * 捕获当前上下文并包装 Callable。
     *
     * @param callable 原始任务
     * @param <T> 返回值类型
     * @return 带上下文恢复能力的任务
     */
    public static <T> Callable<T> wrap(Callable<T> callable) {
        AssertUtils.notNull(callable, "callable must not be null");
        ContextSnapshot snapshot = JInfraContextHolder.capture();
        return () -> {
            try (ContextScope ignored = JInfraContextHolder.open(snapshot)) {
                return callable.call();
            }
        };
    }
}
