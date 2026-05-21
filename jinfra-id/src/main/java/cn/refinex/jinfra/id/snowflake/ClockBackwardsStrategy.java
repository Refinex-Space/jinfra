package cn.refinex.jinfra.id.snowflake;

/**
 * 时钟回拨处理策略。
 *
 * @author refinex
 * @since 0.1.0
 */
public enum ClockBackwardsStrategy {

    /**
     * 检测到时钟回拨时立即失败。
     */
    FAIL_FAST
}
