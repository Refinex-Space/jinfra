package cn.refinex.jinfra.id;

/**
 * 内置 ID 类型。
 *
 * @author refinex
 * @since 0.1.0
 */
public enum IdType {

    /**
     * UUID。
     */
    UUID,

    /**
     * ULID。
     */
    ULID,

    /**
     * Snowflake 风格 long ID。
     */
    SNOWFLAKE
}
