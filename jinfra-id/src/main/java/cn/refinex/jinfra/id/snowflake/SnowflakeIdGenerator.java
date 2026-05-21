package cn.refinex.jinfra.id.snowflake;

import cn.refinex.jinfra.core.util.AssertUtils;
import cn.refinex.jinfra.id.IdGenerationException;
import cn.refinex.jinfra.id.IdGenerator;
import java.time.Clock;

/**
 * Snowflake 风格 long ID 生成器。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class SnowflakeIdGenerator implements IdGenerator<Long> {

    /**
     * Snowflake 风格 ID 生成配置
     */
    private final SnowflakeConfig config;

    /**
     * 时钟
     */
    private final Clock clock;

    /**
     * 最大时间戳
     */
    private final long maxTimestamp;

    /**
     * 最大序列号
     */
    private final long maxSequence;

    /**
     * 时间戳左移位数
     */
    private final int timestampShift;

    /**
     * 工作机器 ID 左移位数
     */
    private final int workerIdShift;

    /**
     * 上次生成 ID 的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 序列号
     */
    private long sequence;

    /**
     * 创建 Snowflake 生成器。
     *
     * @param config Snowflake 配置
     * @param clock 时钟
     */
    public SnowflakeIdGenerator(SnowflakeConfig config, Clock clock) {
        AssertUtils.notNull(config, "snowflake config must not be null");
        AssertUtils.notNull(clock, "clock must not be null");

        this.config = config;
        this.clock = clock;
        this.maxTimestamp = SnowflakeConfig.maxValue(config.timestampBits());
        this.maxSequence = SnowflakeConfig.maxValue(config.sequenceBits());
        this.timestampShift = config.workerIdBits() + config.sequenceBits();
        this.workerIdShift = config.sequenceBits();
    }

    /**
     * 生成下一个 Snowflake ID。
     *
     * @return Snowflake ID
     */
    @Override
    public synchronized Long nextId() {
        long timestamp = currentRelativeTimestamp();
        if (timestamp < lastTimestamp) {
            handleClockBackwards(timestamp);
        }

        if (timestamp == lastTimestamp) {
            sequence++;
            if (sequence > maxSequence) {
                throw new IdGenerationException("snowflake sequence overflow in the same millisecond");
            }
        } else {
            sequence = 0L;
            lastTimestamp = timestamp;
        }

        return (timestamp << timestampShift)
                | (config.workerId() << workerIdShift)
                | sequence;
    }

    /**
     * 获取当前相对时间戳。
     *
     * @return 当前相对时间戳
     */
    private long currentRelativeTimestamp() {
        long timestamp = clock.millis() - config.epochMillis();
        if (timestamp < 0 || timestamp > maxTimestamp) {
            throw new IdGenerationException("snowflake timestamp is out of configured range");
        }
        return timestamp;
    }

    /**
     * 处理时钟倒退。
     *
     * @param timestamp 当前时间戳
     */
    private void handleClockBackwards(long timestamp) {
        if (config.clockBackwardsStrategy() == ClockBackwardsStrategy.FAIL_FAST) {
            throw new IdGenerationException("snowflake clock moved backwards from " + lastTimestamp + " to " + timestamp);
        }
        throw new IdGenerationException("unsupported snowflake clock backwards strategy");
    }
}
