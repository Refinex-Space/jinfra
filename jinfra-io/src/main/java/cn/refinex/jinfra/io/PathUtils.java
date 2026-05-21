package cn.refinex.jinfra.io;

import cn.refinex.jinfra.core.util.AssertUtils;
import java.nio.file.Path;

/**
 * 路径工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class PathUtils {

    private PathUtils() {
        throw new AssertionError("No cn.refinex.jinfra.io.PathUtils instances");
    }

    /**
     * 返回绝对规范路径。
     *
     * @param path 原始路径
     * @return 绝对规范路径
     */
    public static Path normalize(Path path) {
        AssertUtils.notNull(path, "path must not be null");
        return path.toAbsolutePath().normalize();
    }

    /**
     * 确保目标路径位于基础目录内。
     *
     * @param baseDir 基础目录
     * @param target 目标路径
     * @return 规范后的目标路径
     */
    public static Path ensureInside(Path baseDir, Path target) {
        Path normalizedBaseDir = normalize(baseDir);
        Path normalizedTarget = normalize(target);
        if (!normalizedTarget.startsWith(normalizedBaseDir)) {
            throw new IoException("target path escapes base directory");
        }
        return normalizedTarget;
    }

    /**
     * 返回路径扩展名。
     *
     * @param path 路径
     * @return 扩展名；无扩展名时返回空字符串
     */
    public static String extension(Path path) {
        String fileName = fileName(path);
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    /**
     * 返回文件名。
     *
     * @param path 路径
     * @return 文件名
     */
    public static String fileName(Path path) {
        AssertUtils.notNull(path, "path must not be null");
        Path fileName = path.getFileName();
        return fileName == null ? "" : fileName.toString();
    }
}
