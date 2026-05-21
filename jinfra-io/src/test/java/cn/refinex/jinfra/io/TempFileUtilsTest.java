package cn.refinex.jinfra.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TempFileUtilsTest {

    @Test
    @DisplayName("createTempFile 应创建临时文件")
    void shouldCreateTempFile() {
        Path file = TempFileUtils.createTempFile("jinfra-", ".tmp");
        try {
            assertThat(Files.isRegularFile(file)).isTrue();
        } finally {
            FileUtils.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("createTempDirectory 应创建临时目录")
    void shouldCreateTempDirectory() {
        Path dir = TempFileUtils.createTempDirectory("jinfra-");
        try {
            assertThat(Files.isDirectory(dir)).isTrue();
        } finally {
            FileUtils.deleteIfExists(dir);
        }
    }
}
