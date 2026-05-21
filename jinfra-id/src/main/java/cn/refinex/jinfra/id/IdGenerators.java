package cn.refinex.jinfra.id;

import cn.refinex.jinfra.id.snowflake.SnowflakeConfig;
import cn.refinex.jinfra.id.snowflake.SnowflakeIdGenerator;
import cn.refinex.jinfra.id.ulid.UlidGenerator;
import cn.refinex.jinfra.id.uuid.UuidGenerator;
import java.security.SecureRandom;
import java.time.Clock;

/**
 * 内置 ID 生成器工厂。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class IdGenerators {

    private IdGenerators() {
        throw new AssertionError("No cn.refinex.jinfra.id.IdGenerators instances");
    }

    /**
     * 创建无连字符 UUID 生成器。
     *
     * @return UUID 生成器
     */
    public static IdGenerator<String> uuid() {
        return UuidGenerator.withoutHyphen();
    }

    /**
     * 创建标准 UUID 生成器。
     *
     * @return UUID 生成器
     */
    public static IdGenerator<String> uuidWithHyphen() {
        return UuidGenerator.withHyphen();
    }

    /**
     * 创建默认 ULID 生成器。
     *
     * @return ULID 生成器
     */
    public static IdGenerator<String> ulid() {
        return new UlidGenerator(Clock.systemUTC(), new SecureRandom());
    }

    /**
     * 使用指定时钟和随机源创建 ULID 生成器。
     *
     * @param clock 时钟
     * @param random 安全随机源
     * @return ULID 生成器
     */
    public static IdGenerator<String> ulid(Clock clock, SecureRandom random) {
        return new UlidGenerator(clock, random);
    }

    /**
     * 使用指定配置和时钟创建 Snowflake 生成器。
     *
     * @param config Snowflake 配置
     * @param clock 时钟
     * @return Snowflake 生成器
     */
    public static IdGenerator<Long> snowflake(SnowflakeConfig config, Clock clock) {
        return new SnowflakeIdGenerator(config, clock);
    }
}
