package com.jz.miniapp.controller.api;

import com.jz.miniapp.common.Result;
import com.jz.miniapp.util.OssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 
 * @author jiazheng
 * @since 2026-03-26
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "文件上传接口")
public class FileUploadController {

    private final OssUtil ossUtil;

    /**
     * 单文件上传
     */
    @PostMapping("/single")
    @Operation(summary = "单文件上传")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = ossUtil.uploadFile(file);
            
            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("name", file.getOriginalFilename());
            data.put("size", String.valueOf(file.getSize()));
            
            return Result.success(data);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail("文件上传失败：" + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("参数错误：{}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 多文件上传
     */
    @PostMapping("/multiple")
    @Operation(summary = "多文件上传")
    public Result<Map<String, Object>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            Map<String, Object> data = new HashMap<>();
            java.util.List<Map<String, String>> fileList = new java.util.ArrayList<>();
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileUrl = ossUtil.uploadFile(file);
                    
                    Map<String, String> fileInfo = new HashMap<>();
                    fileInfo.put("url", fileUrl);
                    fileInfo.put("name", file.getOriginalFilename());
                    fileInfo.put("size", String.valueOf(file.getSize()));
                    fileList.add(fileInfo);
                }
            }
            
            data.put("files", fileList);
            data.put("count", String.valueOf(fileList.size()));
            
            return Result.success(data);
        } catch (IOException e) {
            log.error("批量文件上传失败", e);
            return Result.fail("批量文件上传失败：" + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("参数错误：{}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }
}
