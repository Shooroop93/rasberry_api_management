package com.rasberry.rasberry_api_management.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "config.rclone")
@NoArgsConstructor
@AllArgsConstructor
public class RcloneConfigProperties {

    private boolean enable;
    private boolean startBackupAtStart;
    private List<String> profile;
    private String pathBackupFolder;
    private boolean saveTokenInApp;
    private String notificationsUrl;
    private String pathRcloneConfig;
    private String rcloneCron;
    private String idChannelTelegram;
    private Settings settings = new Settings();

    @Getter
    @Setter
    public static class Settings {
        private String timeStats;
        private String checkers;
        private String transfers;
    }
}