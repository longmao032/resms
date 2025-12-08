package com.guang.resms.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtils {

    /**
     * 允许的图片格式
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    /**
     * 允许的图片扩展名
     */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "webp"
    );

    /**
     * 最大文件大小：5MB
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传目录的基础路径（使用项目根目录的绝对路径）
     */
    private static final String UPLOAD_BASE_PATH = getUploadBasePath();

    /**
     * 获取上传文件的基础路径
     * 使用项目根目录下的uploads文件夹
     */
    private static String getUploadBasePath() {
        // 获取项目根目录
        String projectPath = System.getProperty("user.dir");
        return projectPath + File.separator + "uploads";
    }

    /**
     * 验证文件是否为合法的图片文件
     * 
     * @param file 上传的文件
     * @return 是否合法
     */
    public static boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return false;
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String extension = getFileExtension(originalFilename);
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 保存上传的文件
     * 
     * @param file 上传的文件
     * @param subDir 子目录（如 "project"）
     * @param fileName 文件名（不含扩展名）
     * @return 相对路径
     * @throws IOException IO异常
     */
    public static String saveFile(MultipartFile file, String subDir, String fileName) throws IOException {
        // 获取文件扩展名
        String extension = getFileExtension(file.getOriginalFilename());
        
        // 构建完整的文件名
        String fullFileName = fileName + "." + extension;
        
        // 构建目录路径
        String dirPath = UPLOAD_BASE_PATH + File.separator + subDir;
        Path directory = Paths.get(dirPath);
        
        // 如果目录不存在，创建目录
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        
        // 构建文件完整路径
        Path filePath = directory.resolve(fullFileName);
        
        // 如果文件已存在，删除旧文件
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        // 保存文件
        file.transferTo(filePath.toFile());
        
        // 返回相对路径（用于存储到数据库）
        return "/" + subDir + "/" + fullFileName;
    }

    /**
     * 删除文件
     * 
     * @param relativePath 相对路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }

        try {
            // 移除开头的斜杠
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }

            Path filePath = Paths.get(UPLOAD_BASE_PATH, relativePath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 扩展名（小写）
     */
    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 格式化文件大小
     * 
     * @param size 文件大小（字节）
     * @return 格式化后的字符串
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 获取最大文件大小（字节）
     */
    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }

    /**
     * 获取最大文件大小（格式化字符串）
     */
    public static String getMaxFileSizeFormatted() {
        return formatFileSize(MAX_FILE_SIZE);
    }
}
