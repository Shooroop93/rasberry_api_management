package com.rasberry.rasberry_api_management.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "config.rclone")
public class RcloneConfigProperties {

    private boolean enable;
    private List<String> profile;
    private String pathBackupFolder;
    private boolean saveTokenInApp;
    private String notificationsUrl;
    private String pathRcloneConfig;
    private String rcloneCron;
    private String idChannelTelegram;
}