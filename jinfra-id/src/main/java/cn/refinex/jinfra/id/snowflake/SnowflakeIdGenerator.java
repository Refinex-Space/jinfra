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

    private final SnowflakeConfig config;

    private final Clock clock;

    private final long maxTimestamp;

    private final long maxSequence;

    private final int timestampShift;

    private final int workerIdShift;

    private long lastTimestamp = -1L;

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

    private long currentRelativeTimestamp() {
        long timestamp = clock.millis() - config.epochMillis();
        if (timestamp < 0 || timestamp > maxTimestamp) {
            throw new IdGenerationException("snowflake timestamp is out of configured range");
        }
        return timestamp;
    }

    private void handleClockBackwards(long timestamp) {
        if (config.clockBackwardsStrategy() == ClockBackwardsStrategy.FAIL_FAST) {
            throw new IdGenerationException(
                    "snowflake clock moved backwards from " + lastTimestamp + " to " + timestamp);
        }
        throw new IdGenerationException("unsupported snowflake clock backwards strategy");
    }
}
