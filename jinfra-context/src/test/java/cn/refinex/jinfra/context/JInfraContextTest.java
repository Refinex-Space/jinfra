package cn.refinex.jinfra.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.refinex.jinfra.core.exception.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JInfraContextTest {

    @Test
    @DisplayName("上下文应保持不可变")
    void shouldBeImmutable() {
        JInfraContext empty = JInfraContext.empty();
        JInfraContext updated = empty.put(ContextKey.TRACE_ID, "trace-1");

        assertThat(empty.get(ContextKey.TRACE_ID)).isNull();
        assertThat(updated.get(ContextKey.TRACE_ID)).isEqualTo("trace-1");
        assertThat(updated.asMap()).containsEntry("traceId", "trace-1");
    }

    @Test
    @DisplayName("删除 key 应返回新的上下文")
    void shouldRemoveKey() {
        JInfraContext context = JInfraContext.empty()
                .put(ContextKey.TENANT_ID, "tenant-a")
                .remove(ContextKey.TENANT_ID);

        assertThat(context.get(ContextKey.TENANT_ID)).isNull();
    }

    @Test
    @DisplayName("上下文 map 不允许外部修改")
    void shouldExposeUnmodifiableMap() {
        JInfraContext context = JInfraContext.of("traceId", "trace-1");

        assertThatThrownBy(() -> context.asMap().put("x", "y"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("blank key 应被拒绝")
    void shouldRejectBlankKey() {
        assertThatThrownBy(() -> new ContextKey<String>(" "))
                .isInstanceOf(CoreException.class);
    }
}
