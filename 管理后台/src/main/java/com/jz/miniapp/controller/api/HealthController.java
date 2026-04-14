package com.jz.miniapp.controller.api;

import com.jz.miniapp.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查 Controller
 */
@RestController
@RequestMapping("/mini/health")
@Tag(name = "健康检查", description = "服务健康检查接口")
public class HealthController {

    @GetMapping
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    public Result<String> health() {
        return Result.success("服务运行正常");
    }
}
