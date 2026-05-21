package cn.refinex.jinfra.id;

/**
 * ID 生成器。
 *
 * @param <T> ID 类型
 * @author refinex
 * @since 0.1.0
 */
@FunctionalInterface
public interface IdGenerator<T> {

    /**
     * 生成下一个 ID。
     *
     * @return 新 ID
     */
    T nextId();
}
