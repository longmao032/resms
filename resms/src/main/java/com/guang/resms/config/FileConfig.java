package com.guang.resms.config;

import com.guang.resms.common.utils.FileUploadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文件配置类
 * 用于将配置文件中的配置注入到静态工具类中
 */
@Component
public class FileConfig implements InitializingBean {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Override
    public void afterPropertiesSet() {
        FileUploadUtils.setUploadBasePath(uploadPath);
    }
}
