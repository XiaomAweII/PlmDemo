package com.example.controller;

import com.example.model.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    @PostMapping("/submit")
    public Result<String> submit() {
        return Result.success("提交成功");
    }

    @PostMapping("/upload/{fileName}")
    public Result<String> upload(@PathVariable String fileName) {
        return Result.success("上传成功：" + fileName);
    }
}