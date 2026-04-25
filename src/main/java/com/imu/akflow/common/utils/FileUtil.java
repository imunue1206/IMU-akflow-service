package com.imu.akflow.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    /**
     * 验证MD文件
     */
    public static File getMdFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        File file = new File(filePath);

        // 检查文件是否存在
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + filePath);
        }

        // 检查是否是文件
        if (!file.isFile()) {
            throw new IllegalArgumentException("路径不是文件: " + filePath);
        }

        // 检查文件扩展名
        String filename = file.getName().toLowerCase();
        if (!filename.endsWith(".md")) {
            throw new IllegalArgumentException("只支持MarkDown格式文件: " + filename);
        }

        // 检查文件大小（限制10MB）
        if (file.length() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("文件过大，最大支持10MB,过长的文件将污染AI上下文,应精简或拆分");
        }

        return file;
    }

    /**
     * 读取MD文件内容
     */
    public static String readMdFileContent(File file) throws Exception {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

    /**
     * 从文件名提取标题
     */
    public static String extractTitleFromFilename(String filename) {
        // 移除.md扩展名
        String title = filename;
        if (title.toLowerCase().endsWith(".md")) {
            title = title.substring(0, title.length() - 3);
        }

        // 清理文件名（移除特殊字符）
        title = title.replaceAll("_", " ")
                .replaceAll("-", " ")
                .trim();

        // 如果标题为空，使用默认标题
        if (StringUtils.isBlank(title)) {
            title = "未命名文档";
        }

        return title;
    }
}
