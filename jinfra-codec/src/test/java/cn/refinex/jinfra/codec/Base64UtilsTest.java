package cn.refinex.jinfra.codec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Base64UtilsTest {

    @Test
    @DisplayName("Base64 应支持标准编码与解码")
    void shouldEncodeAndDecodeBase64() {
        String encoded = Base64Utils.encode("hello".getBytes(StandardCharsets.UTF_8));

        assertThat(encoded).isEqualTo("aGVsbG8=");
        assertThat(new String(Base64Utils.decode(encoded), StandardCharsets.UTF_8)).isEqualTo("hello");
    }

    @Test
    @DisplayName("URL Safe Base64 应不包含加号与斜杠")
    void shouldEncodeUrlSafeBase64() {
        String encoded = Base64Utils.encodeUrlSafe(new byte[] {(byte) 0xFB, (byte) 0xFF});

        assertThat(encoded).doesNotContain("+", "/");
        assertThat(Base64Utils.decodeUrlSafe(encoded)).containsExactly((byte) 0xFB, (byte) 0xFF);
    }

    @Test
    @DisplayName("非法 Base64 应包装为 CodecException")
    void shouldWrapInvalidBase64() {
        assertThatThrownBy(() -> Base64Utils.decode("%%%"))
                .isInstanceOf(CodecException.class);
    }
}
