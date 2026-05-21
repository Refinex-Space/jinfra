package cn.refinex.jinfra.io;

import cn.refinex.jinfra.core.util.AssertUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 临时文件工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class TempFileUtils {

    private TempFileUtils() {
        throw new AssertionError("No cn.refinex.jinfra.io.TempFileUtils instances");
    }

    /**
     * 创建临时文件。
     *
     * @param prefix 文件名前缀
     * @param suffix 文件名后缀
     * @return 临时文件路径
     */
    public static Path createTempFile(String prefix, String suffix) {
        AssertUtils.hasText(prefix, "temp file prefix must not be blank");
        AssertUtils.notNull(suffix, "temp file suffix must not be null");
        try {
            return Files.createTempFile(prefix, suffix);
        } catch (IOException ex) {
            throw new IoException("failed to create temp file", ex);
        }
    }

    /**
     * 创建临时目录。
     *
     * @param prefix 目录名前缀
     * @return 临时目录路径
     */
    public static Path createTempDirectory(String prefix) {
        AssertUtils.hasText(prefix, "temp directory prefix must not be blank");
        try {
            return Files.createTempDirectory(prefix);
        } catch (IOException ex) {
            throw new IoException("failed to create temp directory", ex);
        }
    }
}
