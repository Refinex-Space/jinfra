package cn.refinex.jinfra.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 集合工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class CollectionUtils {

    private CollectionUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.CollectionUtils instances");
    }

    /**
     * 判断集合是否为 null 或空集合。
     *
     * @param collection 待检查集合
     * @return 如果集合为空则返回 true
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否非空。
     *
     * @param collection 待检查集合
     * @return 如果集合非空则返回 true
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * null 列表转为空列表。
     *
     * @param list 列表
     * @param <T> 元素类型
     * @return 原列表或不可变空列表
     */
    public static <T> List<T> nullToEmptyList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * null Set 转为空 Set。
     *
     * @param set Set
     * @param <T> 元素类型
     * @return 原 Set 或不可变空 Set
     */
    public static <T> Set<T> nullToEmptySet(Set<T> set) {
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * null Map 转为空 Map。
     *
     * @param map Map
     * @param <K> key 类型
     * @param <V> value 类型
     * @return 原 Map 或不可变空 Map
     */
    public static <K, V> Map<K, V> nullToEmptyMap(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    /**
     * 空集合转为 null。
     *
     * @param collection 集合
     * @param <T> 集合类型
     * @return 原集合或 null
     */
    public static <T extends Collection<?>> T emptyToNull(T collection) {
        return isEmpty(collection) ? null : collection;
    }

    /**
     * 返回集合第一个元素。
     *
     * @param collection 集合
     * @param <T> 元素类型
     * @return 第一个元素；集合为空时返回 {@link Optional#empty()}
     */
    public static <T> Optional<T> first(Collection<T> collection) {
        if (isEmpty(collection)) {
            return Optional.empty();
        }
        return Optional.ofNullable(collection.iterator().next());
    }
}
