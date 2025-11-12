package com.rasberry.rasberry_api_management.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "config.rclone")
public class RcloneConfigProperties {

    private boolean enable;
    private String pathConfig;
    private String pathBackupFolder;
    private boolean saveTokenInApp;
}