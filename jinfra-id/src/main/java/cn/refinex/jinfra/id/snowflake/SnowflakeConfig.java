package cn.refinex.jinfra.id.snowflake;

import cn.refinex.jinfra.core.util.AssertUtils;

/**
 * Snowflake 风格 ID 生成配置。
 *
 * @param epochMillis 业务纪元毫秒
 * @param timestampBits 时间戳位宽
 * @param workerIdBits 工作节点位宽
 * @param sequenceBits 毫秒内序列位宽
 * @param workerId 工作节点 ID
 * @param clockBackwardsStrategy 时钟回拨策略
 * @author refinex
 * @since 0.1.0
 */
public record SnowflakeConfig(
        long epochMillis,
        int timestampBits,
        int workerIdBits,
        int sequenceBits,
        long workerId,
        ClockBackwardsStrategy clockBackwardsStrategy) {

    /**
     * 创建 Snowflake 配置。
     *
     * @param epochMillis 业务纪元毫秒
     * @param timestampBits 时间戳位宽
     * @param workerIdBits 工作节点位宽
     * @param sequenceBits 毫秒内序列位宽
     * @param workerId 工作节点 ID
     * @param clockBackwardsStrategy 时钟回拨策略
     */
    public SnowflakeConfig {
        AssertUtils.isTrue(epochMillis >= 0, "snowflake epochMillis must be non-negative");
        AssertUtils.isTrue(timestampBits > 0, "snowflake timestampBits must be positive");
        AssertUtils.isTrue(workerIdBits > 0, "snowflake workerIdBits must be positive");
        AssertUtils.isTrue(sequenceBits > 0, "snowflake sequenceBits must be positive");
        AssertUtils.isTrue(timestampBits + workerIdBits + sequenceBits <= Long.SIZE - 1,
                "snowflake total bits must not exceed 63");
        AssertUtils.notNull(clockBackwardsStrategy, "snowflake clockBackwardsStrategy must not be null");
        AssertUtils.isTrue(workerId >= 0 && workerId <= maxValue(workerIdBits),
                "snowflake workerId is out of range");
    }

    /**
     * 计算指定位宽可表示的最大值。
     *
     * @param bits 位宽
     * @return 最大值
     */
    public static long maxValue(int bits) {
        AssertUtils.isTrue(bits > 0 && bits < Long.SIZE, "bits must be between 1 and 63");
        return (1L << bits) - 1L;
    }
}
