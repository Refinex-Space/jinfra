package cn.refinex.jinfra.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JInfraContextHolderTest {

    @AfterEach
    void clearContext() {
        JInfraContextHolder.clear();
    }

    @Test
    @DisplayName("当前线程上下文应支持 set/current/clear")
    void shouldSetCurrentAndClear() {
        JInfraContextHolder.set(JInfraContext.of("traceId", "trace-1"));

        assertThat(JInfraContextHolder.current().get(ContextKey.TRACE_ID)).isEqualTo("trace-1");

        JInfraContextHolder.clear();

        assertThat(JInfraContextHolder.current().asMap()).isEmpty();
    }

    @Test
    @DisplayName("scope 关闭后应恢复旧上下文")
    void shouldRestorePreviousContextAfterScopeClose() {
        JInfraContextHolder.set(JInfraContext.of("traceId", "old"));
        ContextSnapshot snapshot = ContextSnapshot.from(JInfraContext.of("traceId", "new"));

        try (ContextScope ignored = JInfraContextHolder.open(snapshot)) {
            assertThat(JInfraContextHolder.current().get(ContextKey.TRACE_ID)).isEqualTo("new");
        }

        assertThat(JInfraContextHolder.current().get(ContextKey.TRACE_ID)).isEqualTo("old");
    }
}
