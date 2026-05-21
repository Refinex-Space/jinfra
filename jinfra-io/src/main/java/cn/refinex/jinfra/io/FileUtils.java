package cn.refinex.jinfra.io;

import cn.refinex.jinfra.core.util.AssertUtils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * 文件工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class FileUtils {

    private FileUtils() {
        throw new AssertionError("No cn.refinex.jinfra.io.FileUtils instances");
    }

    /**
     * 读取文本文件。
     *
     * @param path 文件路径
     * @param charset 字符集
     * @return 文件内容
     */
    public static String readString(Path path, Charset charset) {
        AssertUtils.notNull(path, "path must not be null");
        AssertUtils.notNull(charset, "charset must not be null");
        try {
            return Files.readString(path, charset);
        } catch (IOException ex) {
            throw new IoException("failed to read file: " + path, ex);
        }
    }

    /**
     * 写入文本文件，必要时创建父目录。
     *
     * @param path 文件路径
     * @param content 文件内容
     * @param charset 字符集
     */
    public static void writeString(Path path, String content, Charset charset) {
        AssertUtils.notNull(path, "path must not be null");
        AssertUtils.notNull(content, "content must not be null");
        AssertUtils.notNull(charset, "charset must not be null");
        try {
            createParentDirectories(path);
            Files.writeString(path, content, charset);
        } catch (IOException ex) {
            throw new IoException("failed to write file: " + path, ex);
        }
    }

    /**
     * 删除文件或目录；路径不存在时不报错。
     *
     * @param path 文件或目录路径
     */
    public static void deleteIfExists(Path path) {
        AssertUtils.notNull(path, "path must not be null");
        try {
            if (!Files.exists(path)) {
                return;
            }
            if (Files.isDirectory(path)) {
                try (Stream<Path> paths = Files.walk(path)) {
                    paths.sorted(Comparator.reverseOrder()).forEach(FileUtils::deleteSinglePath);
                }
            } else {
                Files.deleteIfExists(path);
            }
        } catch (IOException ex) {
            throw new IoException("failed to delete path: " + path, ex);
        }
    }

    /**
     * 创建父目录。
     *
     * @param path 文件路径
     */
    public static void createParentDirectories(Path path) {
        AssertUtils.notNull(path, "path must not be null");
        Path parent = path.toAbsolutePath().normalize().getParent();
        if (parent == null) {
            return;
        }
        try {
            Files.createDirectories(parent);
        } catch (IOException ex) {
            throw new IoException("failed to create parent directories for: " + path, ex);
        }
    }

    /**
     * 删除单个路径。
     *
     * @param path 路径
     */
    private static void deleteSinglePath(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new IoException("failed to delete path: " + path, ex);
        }
    }
}
