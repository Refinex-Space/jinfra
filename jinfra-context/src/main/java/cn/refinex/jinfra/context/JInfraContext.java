package cn.refinex.jinfra.context;

import cn.refinex.jinfra.core.util.AssertUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 不可变 JInfra 上下文。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class JInfraContext {

    private static final JInfraContext EMPTY = new JInfraContext(Map.of());

    private final Map<String, String> values;

    private JInfraContext(Map<String, String> values) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    /**
     * 返回空上下文。
     *
     * @return 空上下文
     */
    public static JInfraContext empty() {
        return EMPTY;
    }

    /**
     * 使用单个键值对创建上下文。
     *
     * @param key 上下文键名称
     * @param value 上下文值
     * @return 包含指定键值的上下文
     */
    public static JInfraContext of(String key, String value) {
        return empty().put(new ContextKey<>(key), value);
    }

    /**
     * 获取上下文值。
     *
     * @param key 上下文键
     * @return 上下文值；不存在时返回 null
     */
    public String get(ContextKey<String> key) {
        AssertUtils.notNull(key, "context key must not be null");
        return values.get(key.name());
    }

    /**
     * 返回包含新键值的上下文。
     *
     * @param key 上下文键
     * @param value 上下文值，不能为 null
     * @return 新上下文
     */
    public JInfraContext put(ContextKey<String> key, String value) {
        AssertUtils.notNull(key, "context key must not be null");
        AssertUtils.notNull(value, "context value must not be null");
        Map<String, String> copiedValues = new LinkedHashMap<>(values);
        copiedValues.put(key.name(), value);
        return new JInfraContext(copiedValues);
    }

    /**
     * 返回移除指定键后的上下文。
     *
     * @param key 上下文键
     * @return 新上下文
     */
    public JInfraContext remove(ContextKey<String> key) {
        AssertUtils.notNull(key, "context key must not be null");
        if (!values.containsKey(key.name())) {
            return this;
        }
        Map<String, String> copiedValues = new LinkedHashMap<>(values);
        copiedValues.remove(key.name());
        if (copiedValues.isEmpty()) {
            return EMPTY;
        }
        return new JInfraContext(copiedValues);
    }

    /**
     * 返回只读上下文 Map。
     *
     * @return 只读上下文键值
     */
    public Map<String, String> asMap() {
        return values;
    }
}
