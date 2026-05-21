package cn.refinex.jinfra.id.snowflake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.refinex.jinfra.id.IdGenerationException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SnowflakeIdGeneratorTest {

    @Test
    @DisplayName("Snowflake 应生成正数 long ID")
    void shouldGeneratePositiveId() {
        SnowflakeConfig config = new SnowflakeConfig(0L, 41, 10, 12, 1L, ClockBackwardsStrategy.FAIL_FAST);
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(
                config,
                Clock.fixed(Instant.ofEpochMilli(1_000L), ZoneOffset.UTC));

        assertThat(generator.nextId()).isPositive();
    }

    @Test
    @DisplayName("同一毫秒内 Snowflake 应递增序列")
    void shouldIncreaseSequenceInSameMillis() {
        SnowflakeConfig config = new SnowflakeConfig(0L, 41, 10, 12, 1L, ClockBackwardsStrategy.FAIL_FAST);
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(
                config,
                Clock.fixed(Instant.ofEpochMilli(1_000L), ZoneOffset.UTC));

        long first = generator.nextId();
        long second = generator.nextId();

        assertThat(second).isGreaterThan(first);
    }

    @Test
    @DisplayName("时钟回拨应按策略失败")
    void shouldFailWhenClockMovesBackwards() {
        SnowflakeConfig config = new SnowflakeConfig(0L, 41, 10, 12, 1L, ClockBackwardsStrategy.FAIL_FAST);
        MutableClock clock = new MutableClock(Instant.ofEpochMilli(1_000L), ZoneOffset.UTC);
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(config, clock);
        generator.nextId();

        clock.instant = Instant.ofEpochMilli(999L);

        assertThatThrownBy(generator::nextId).isInstanceOf(IdGenerationException.class);
    }

    private static final class MutableClock extends Clock {

        private Instant instant;

        private final ZoneId zone;

        private MutableClock(Instant instant, ZoneId zone) {
            this.instant = instant;
            this.zone = zone;
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
