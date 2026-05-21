package cn.refinex.jinfra.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContextPropagationTest {

    @AfterEach
    void clearContext() {
        JInfraContextHolder.clear();
    }

    @Test
    @DisplayName("wrap Runnable 应传播捕获时上下文")
    void shouldWrapRunnableWithCapturedContext() {
        JInfraContextHolder.set(JInfraContext.of("traceId", "trace-1"));
        AtomicReference<String> value = new AtomicReference<>();

        Runnable wrapped = ContextPropagation.wrap(() ->
                value.set(JInfraContextHolder.current().get(ContextKey.TRACE_ID)));
        JInfraContextHolder.clear();
        wrapped.run();

        assertThat(value).hasValue("trace-1");
    }

    @Test
    @DisplayName("wrap Callable 应传播捕获时上下文并返回结果")
    void shouldWrapCallableWithCapturedContext() throws Exception {
        JInfraContextHolder.set(JInfraContext.of("tenantId", "tenant-a"));

        Callable<String> callable = ContextPropagation.wrap(() ->
                JInfraContextHolder.current().get(ContextKey.TENANT_ID));
        JInfraContextHolder.clear();

        assertThat(callable.call()).isEqualTo("tenant-a");
    }
}
