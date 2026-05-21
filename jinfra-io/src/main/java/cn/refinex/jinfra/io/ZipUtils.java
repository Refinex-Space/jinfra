package cn.refinex.jinfra.io;

import cn.refinex.jinfra.core.util.AssertUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZIP 压缩工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class ZipUtils {

    private ZipUtils() {
        throw new AssertionError("No cn.refinex.jinfra.io.ZipUtils instances");
    }

    /**
     * 压缩目录。
     *
     * @param sourceDir 源目录
     * @param zipFile ZIP 文件路径
     */
    public static void zipDirectory(Path sourceDir, Path zipFile) {
        AssertUtils.notNull(sourceDir, "source directory must not be null");
        AssertUtils.notNull(zipFile, "zip file must not be null");
        Path normalizedSourceDir = PathUtils.normalize(sourceDir);
        try {
            FileUtils.createParentDirectories(zipFile);
            try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(zipFile));
                    Stream<Path> paths = Files.walk(normalizedSourceDir)) {
                paths.filter(Files::isRegularFile).forEach(path -> writeZipEntry(normalizedSourceDir, path, output));
            }
        } catch (IOException ex) {
            throw new IoException("failed to zip directory: " + sourceDir, ex);
        }
    }

    /**
     * 解压 ZIP 文件。
     *
     * @param zipFile ZIP 文件路径
     * @param targetDir 目标目录
     */
    public static void unzip(Path zipFile, Path targetDir) {
        AssertUtils.notNull(zipFile, "zip file must not be null");
        AssertUtils.notNull(targetDir, "target directory must not be null");
        Path normalizedTargetDir = PathUtils.normalize(targetDir);
        try {
            Files.createDirectories(normalizedTargetDir);
            try (ZipInputStream input = new ZipInputStream(Files.newInputStream(zipFile))) {
                ZipEntry entry;
                while ((entry = input.getNextEntry()) != null) {
                    unzipEntry(input, entry, normalizedTargetDir);
                    input.closeEntry();
                }
            }
        } catch (IOException ex) {
            throw new IoException("failed to unzip file: " + zipFile, ex);
        }
    }

    /**
     * 写入 ZIP 条目。
     *
     * @param sourceDir 源目录
     * @param file 文件
     * @param output 输出流
     */
    private static void writeZipEntry(Path sourceDir, Path file, ZipOutputStream output) {
        String entryName = sourceDir.relativize(file).toString().replace('\\', '/');
        try (InputStream input = Files.newInputStream(file)) {
            output.putNextEntry(new ZipEntry(entryName));
            input.transferTo(output);
            output.closeEntry();
        } catch (IOException ex) {
            throw new IoException("failed to write zip entry: " + entryName, ex);
        }
    }

    /**
     * 解压 ZIP 条目。
     *
     * @param input 输入流
     * @param entry 条目
     * @param targetDir 目标目录
     * @throws IOException 如果解压失败
     */
    private static void unzipEntry(ZipInputStream input, ZipEntry entry, Path targetDir) throws IOException {
        Path target = PathUtils.ensureInside(targetDir, targetDir.resolve(entry.getName()));
        if (entry.isDirectory()) {
            Files.createDirectories(target);
            return;
        }
        FileUtils.createParentDirectories(target);
        Files.copy(input, target);
    }
}
