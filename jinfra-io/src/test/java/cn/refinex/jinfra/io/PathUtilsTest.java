package cn.refinex.jinfra.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathUtilsTest {

    @Test
    @DisplayName("normalize 应返回绝对规范路径")
    void shouldNormalizePath() {
        Path path = PathUtils.normalize(Path.of("a", "..", "b.txt"));

        assertThat(path).isAbsolute();
        assertThat(path.getFileName().toString()).isEqualTo("b.txt");
    }

    @Test
    @DisplayName("ensureInside 应拒绝目录逃逸")
    void shouldRejectPathTraversal() {
        Path baseDir = Path.of("/tmp/app");
        Path target = Path.of("/tmp/app/../secret.txt");

        assertThatThrownBy(() -> PathUtils.ensureInside(baseDir, target))
                .isInstanceOf(IoException.class);
    }

    @Test
    @DisplayName("extension 与 fileName 应处理常见文件名")
    void shouldResolveExtensionAndFileName() {
        Path path = Path.of("/tmp/archive.tar.gz");

        assertThat(PathUtils.fileName(path)).isEqualTo("archive.tar.gz");
        assertThat(PathUtils.extension(path)).isEqualTo("gz");
    }
}
