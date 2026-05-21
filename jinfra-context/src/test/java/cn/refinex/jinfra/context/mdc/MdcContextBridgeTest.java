package cn.refinex.jinfra.context.mdc;

import static org.assertj.core.api.Assertions.assertThat;

import cn.refinex.jinfra.context.ContextKey;
import cn.refinex.jinfra.context.ContextScope;
import cn.refinex.jinfra.context.ContextSnapshot;
import cn.refinex.jinfra.context.JInfraContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class MdcContextBridgeTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    @DisplayName("MDC bridge 应写入已知上下文 key")
    void shouldPutKnownKeysToMdc() {
        ContextSnapshot snapshot = ContextSnapshot.from(JInfraContext.empty()
                .put(ContextKey.TRACE_ID, "trace-1")
                .put(ContextKey.TENANT_ID, "tenant-a")
                .put(ContextKey.USER_ID, "user-1"));

        MdcContextBridge.put(snapshot);

        assertThat(MDC.get("traceId")).isEqualTo("trace-1");
        assertThat(MDC.get("tenantId")).isEqualTo("tenant-a");
        assertThat(MDC.get("userId")).isEqualTo("user-1");
    }

    @Test
    @DisplayName("withMdc 关闭后应清理已知 key")
    void shouldClearKnownKeysAfterScopeClose() {
        ContextSnapshot snapshot = ContextSnapshot.from(JInfraContext.of("traceId", "trace-1"));

        try (ContextScope ignored = MdcContextBridge.withMdc(snapshot)) {
            assertThat(MDC.get("traceId")).isEqualTo("trace-1");
        }

        assertThat(MDC.get("traceId")).isNull();
    }
}
