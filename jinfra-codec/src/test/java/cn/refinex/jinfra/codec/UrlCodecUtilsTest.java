package cn.refinex.jinfra.codec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlCodecUtilsTest {

    @Test
    @DisplayName("URL 编解码应支持 UTF-8 文本")
    void shouldEncodeAndDecodeUrl() {
        String encoded = UrlCodecUtils.encode("hello 你好", StandardCharsets.UTF_8);

        assertThat(encoded).contains("hello+");
        assertThat(UrlCodecUtils.decode(encoded, StandardCharsets.UTF_8)).isEqualTo("hello 你好");
    }

    @Test
    @DisplayName("非法 URL 编码应包装为 CodecException")
    void shouldWrapInvalidUrlEncoding() {
        assertThatThrownBy(() -> UrlCodecUtils.decode("%", StandardCharsets.UTF_8))
                .isInstanceOf(CodecException.class);
    }
}
