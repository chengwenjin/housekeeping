package com.jz.miniapp.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.jz.miniapp.config.OssConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云 OSS 文件上传工具类
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssUtil {

    private final OssConfig ossConfig;

    /**
     * 上传文件到 OSS
     * 
     * @param file 文件
     * @return 文件访问 URL
     * @throws IOException IO 异常
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = generateFileName(extension);
        
        // 按日期分目录存储
        String dirName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"));
        String objectName = dirName + fileName;

        // 创建 OSSClient 实例
        OSS ossClient = createOSSClient();
        
        try {
            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucketName(), 
                objectName, 
                file.getInputStream()
            );
            ossClient.putObject(putObjectRequest);
            
            // 返回文件访问 URL
            String fileUrl = getFileUrl(objectName);
            log.info("文件上传成功 - originalName: {}, objectName: {}, url: {}", 
                    originalFilename, objectName, fileUrl);
            
            return fileUrl;
        } finally {
            // 关闭 OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 删除 OSS 文件
     * 
     * @param fileUrl 文件 URL
     */
    public void deleteFile(String fileUrl) {
        String objectName = extractObjectName(fileUrl);
        if (objectName == null) {
            log.warn("无法从 URL 提取对象名：{}", fileUrl);
            return;
        }

        OSS ossClient = createOSSClient();
        try {
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
            log.info("文件删除成功 - objectName: {}", objectName);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 创建 OSSClient
     */
    private OSS createOSSClient() {
        return new OSSClientBuilder().build(
            ossConfig.getEndpoint(),
            ossConfig.getAccessKeyId(),
            ossConfig.getAccessKeySecret()
        );
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 获取文件访问 URL
     */
    private String getFileUrl(String objectName) {
        // 如果配置了 CDN 域名，使用 CDN 域名
        if (ossConfig.getUrlPrefix() != null && !ossConfig.getUrlPrefix().isEmpty()) {
            return ossConfig.getUrlPrefix() + "/" + objectName;
        }
        // 否则使用 OSS 默认域名
        return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + objectName;
    }

    /**
     * 从 URL 中提取对象名
     */
    private String extractObjectName(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        
        // 从 URL 末尾提取对象名（去掉域名部分）
        int index = fileUrl.indexOf(ossConfig.getBucketName());
        if (index != -1) {
            int start = index + ossConfig.getBucketName().length() + 1; // 跳过 bucket 名和点号
            if (start < fileUrl.length()) {
                return fileUrl.substring(start);
            }
        }
        return null;
    }
}
