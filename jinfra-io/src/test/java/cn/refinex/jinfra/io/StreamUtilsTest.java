package cn.refinex.jinfra.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StreamUtilsTest {

    @Test
    @DisplayName("copy 应复制输入流到输出流")
    void shouldCopyStream() {
        ByteArrayInputStream input = new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        long copied = StreamUtils.copy(input, output);

        assertThat(copied).isEqualTo(5);
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo("hello");
    }

    @Test
    @DisplayName("toByteArray 应读取完整字节")
    void shouldReadBytes() {
        ByteArrayInputStream input = new ByteArrayInputStream(new byte[] {1, 2, 3});

        assertThat(StreamUtils.toByteArray(input)).containsExactly(1, 2, 3);
    }
}
