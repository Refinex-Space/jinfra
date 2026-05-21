package cn.refinex.jinfra.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CollectionUtilsTest {

    @Test
    @DisplayName("isEmpty 与 isNotEmpty 应支持 null 安全判断")
    void shouldCheckEmptySafely() {
        assertThat(CollectionUtils.isEmpty(null)).isTrue();
        assertThat(CollectionUtils.isEmpty(List.of())).isTrue();
        assertThat(CollectionUtils.isNotEmpty(List.of("a"))).isTrue();
    }

    @Test
    @DisplayName("nullToEmpty 应返回不可变空集合")
    void shouldConvertNullToEmptyImmutableCollections() {
        List<String> list = CollectionUtils.nullToEmptyList(null);
        Set<String> set = CollectionUtils.nullToEmptySet(null);
        Map<String, String> map = CollectionUtils.nullToEmptyMap(null);

        assertThat(list).isEmpty();
        assertThat(set).isEmpty();
        assertThat(map).isEmpty();
        assertThatThrownBy(() -> list.add("x")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> set.add("x")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> map.put("k", "v")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("nullToEmpty 应保留非 null 集合实例")
    void shouldKeepNonNullCollections() {
        List<String> list = List.of("a");
        Set<String> set = Set.of("b");
        Map<String, String> map = Map.of("k", "v");

        assertThat(CollectionUtils.nullToEmptyList(list)).isSameAs(list);
        assertThat(CollectionUtils.nullToEmptySet(set)).isSameAs(set);
        assertThat(CollectionUtils.nullToEmptyMap(map)).isSameAs(map);
    }

    @Test
    @DisplayName("emptyToNull 应将空集合转为 null")
    void shouldConvertEmptyToNull() {
        List<String> nullList = null;

        assertThat(CollectionUtils.emptyToNull(nullList)).isNull();
        assertThat(CollectionUtils.emptyToNull(List.of())).isNull();
        Collection<String> collection = List.of("a");
        assertThat(CollectionUtils.emptyToNull(collection)).isSameAs(collection);
    }

    @Test
    @DisplayName("first 应返回集合第一个元素")
    void shouldReturnFirstElement() {
        assertThat(CollectionUtils.first(null)).isEmpty();
        assertThat(CollectionUtils.first(List.of())).isEmpty();
        assertThat(CollectionUtils.first(List.of("a", "b"))).contains("a");
    }

    @Test
    @DisplayName("工具类构造器不可实例化")
    void shouldNotInstantiateUtilityClass() throws Exception {
        var constructor = CollectionUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(AssertionError.class);
    }
}
