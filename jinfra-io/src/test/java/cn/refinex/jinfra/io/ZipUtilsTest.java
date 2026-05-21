package cn.refinex.jinfra.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ZipUtilsTest {

    @TempDir
    private Path tempDir;

    @Test
    @DisplayName("zipDirectory 与 unzip 应完成目录往返")
    void shouldZipAndUnzipDirectory() {
        Path sourceDir = tempDir.resolve("source");
        Path file = sourceDir.resolve("docs/readme.txt");
        FileUtils.writeString(file, "hello", StandardCharsets.UTF_8);
        Path zip = tempDir.resolve("archive.zip");
        Path targetDir = tempDir.resolve("target");

        ZipUtils.zipDirectory(sourceDir, zip);
        ZipUtils.unzip(zip, targetDir);

        assertThat(FileUtils.readString(targetDir.resolve("docs/readme.txt"), StandardCharsets.UTF_8))
                .isEqualTo("hello");
    }

    @Test
    @DisplayName("unzip 应拒绝 Zip Slip 条目")
    void shouldRejectZipSlipEntry() throws IOException {
        Path zip = tempDir.resolve("evil.zip");
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(zip))) {
            output.putNextEntry(new ZipEntry("../evil.txt"));
            output.write("evil".getBytes(StandardCharsets.UTF_8));
            output.closeEntry();
        }

        assertThatThrownBy(() -> ZipUtils.unzip(zip, tempDir.resolve("target")))
                .isInstanceOf(IoException.class);
    }
}
