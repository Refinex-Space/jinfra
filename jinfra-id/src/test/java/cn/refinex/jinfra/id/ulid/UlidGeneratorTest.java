package cn.refinex.jinfra.id.ulid;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UlidGeneratorTest {

    @Test
    @DisplayName("ULID 应生成 26 位 Crockford Base32 字符串")
    void shouldGenerateCanonicalUlid() {
        UlidGenerator generator = new UlidGenerator(
                Clock.fixed(Instant.ofEpochMilli(1_700_000_000_000L), ZoneOffset.UTC),
                new SecureRandom(new byte[] {1, 2, 3, 4}));

        String id = generator.nextId();

        assertThat(id).hasSize(26).matches("[0-9A-HJKMNP-TV-Z]{26}");
    }

    @Test
    @DisplayName("同一毫秒内 ULID 应保持字典序递增")
    void shouldGenerateMonotonicUlidInSameMillis() {
        UlidGenerator generator = new UlidGenerator(
                Clock.fixed(Instant.ofEpochMilli(1_700_000_000_000L), ZoneOffset.UTC),
                new SecureRandom(new byte[] {1, 2, 3, 4}));

        String first = generator.nextId();
        String second = generator.nextId();

        assertThat(second).isGreaterThan(first);
    }
}
