package cn.refinex.jinfra.codec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HexUtilsTest {

    @Test
    @DisplayName("Hex 应支持小写编码与解码")
    void shouldEncodeAndDecodeHex() {
        String encoded = HexUtils.encode(new byte[] {0x0A, 0x1B, 0x2C});

        assertThat(encoded).isEqualTo("0a1b2c");
        assertThat(HexUtils.decode(encoded)).containsExactly(0x0A, 0x1B, 0x2C);
    }

    @Test
    @DisplayName("非法 Hex 应包装为 CodecException")
    void shouldWrapInvalidHex() {
        assertThatThrownBy(() -> HexUtils.decode("abc"))
                .isInstanceOf(CodecException.class);
    }
}
