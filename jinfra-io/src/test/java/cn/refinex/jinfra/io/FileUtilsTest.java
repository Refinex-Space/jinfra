package cn.refinex.jinfra.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUtilsTest {

    @TempDir
    private Path tempDir;

    @Test
    @DisplayName("writeString 应自动创建父目录并可读回")
    void shouldWriteAndReadString() {
        Path file = tempDir.resolve("a/b/readme.txt");

        FileUtils.writeString(file, "hello", StandardCharsets.UTF_8);

        assertThat(FileUtils.readString(file, StandardCharsets.UTF_8)).isEqualTo("hello");
    }

    @Test
    @DisplayName("deleteIfExists 应删除文件且允许重复调用")
    void shouldDeleteIfExists() {
        Path file = tempDir.resolve("delete.txt");
        FileUtils.writeString(file, "x", StandardCharsets.UTF_8);

        FileUtils.deleteIfExists(file);
        FileUtils.deleteIfExists(file);

        assertThat(Files.exists(file)).isFalse();
    }
}
