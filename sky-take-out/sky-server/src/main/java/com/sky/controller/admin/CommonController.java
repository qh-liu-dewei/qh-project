package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file.getOriginalFilename());
        try {
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            String url = aliOssUtil.upload(bytes, originalFilename);
            return Result.success(url);
        }catch (Exception e){
            log.error("上传失败",e);
            return Result.error("文件上传失败");
        }
    }
}
