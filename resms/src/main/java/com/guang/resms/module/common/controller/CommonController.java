package com.guang.resms.module.common.controller;

import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.utils.FileUploadUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @PostMapping("/upload")
    public ResponseResult<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (!FileUploadUtils.isValidImage(file)) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "文件格式不正确或文件过大");
            }
            String relative = FileUploadUtils.saveFile(file, "avatars", UUID.randomUUID().toString().replace("-", ""));
            return ResponseResult.success("上传成功", "/uploads" + relative);
        } catch (IOException e) {
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "上传失败");
        }
    }
}
