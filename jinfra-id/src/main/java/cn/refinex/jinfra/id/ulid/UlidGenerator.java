package cn.refinex.jinfra.id.ulid;

import cn.refinex.jinfra.core.util.AssertUtils;
import cn.refinex.jinfra.id.IdGenerationException;
import cn.refinex.jinfra.id.IdGenerator;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Arrays;

/**
 * 单调 ULID 生成器。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class UlidGenerator implements IdGenerator<String> {

    private static final int RANDOMNESS_BYTES = 10;

    private final Clock clock;

    private final SecureRandom random;

    private long lastTimestamp = -1L;

    private byte[] lastRandomness = new byte[RANDOMNESS_BYTES];

    /**
     * 创建 ULID 生成器。
     *
     * @param clock 时钟
     * @param random 安全随机源
     */
    public UlidGenerator(Clock clock, SecureRandom random) {
        AssertUtils.notNull(clock, "clock must not be null");
        AssertUtils.notNull(random, "random must not be null");
        this.clock = clock;
        this.random = random;
    }

    /**
     * 生成下一个 ULID。
     *
     * @return ULID 字符串
     */
    @Override
    public synchronized String nextId() {
        long timestamp = clock.millis();
        if (timestamp < 0 || timestamp > Ulid.MAX_TIMESTAMP) {
            throw new IdGenerationException("ULID timestamp is out of 48-bit range");
        }

        byte[] randomness;
        if (timestamp == lastTimestamp) {
            randomness = Arrays.copyOf(lastRandomness, RANDOMNESS_BYTES);
            increment(randomness);
        } else {
            randomness = new byte[RANDOMNESS_BYTES];
            random.nextBytes(randomness);
            lastTimestamp = timestamp;
        }
        lastRandomness = Arrays.copyOf(randomness, RANDOMNESS_BYTES);
        return Ulid.encode(timestamp, randomness);
    }

    private static void increment(byte[] randomness) {
        for (int index = randomness.length - 1; index >= 0; index--) {
            int value = randomness[index] & 0xFF;
            if (value != 0xFF) {
                randomness[index] = (byte) (value + 1);
                return;
            }
            randomness[index] = 0;
        }
        throw new IdGenerationException("ULID randomness overflow in the same millisecond");
    }
}
