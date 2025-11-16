package com.rasberry.rasberry_api_management.scheduling;

import com.rasberry.rasberry_api_management.properties.RcloneConfigProperties;
import com.rasberry.rasberry_api_management.service.impl.RcloneOSActionImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulingRclone {

    private final RcloneOSActionImpl rcloneAction;
    private final RcloneConfigProperties rcloneConfigProperties;

    @PostConstruct
    public void start() {
        if (rcloneConfigProperties.isStartBackupAtStart()) {
            log.info("Запуск backup при старте программы");
            startBackupRclone();
        }
    }

    @Scheduled(cron = "${config.rclone.rcloneCron}")
    public void startBackupRclone() {
        long startTime = System.currentTimeMillis();
        log.info("Запуск backup rclone");

        rcloneAction.backup();

        long endTime = System.currentTimeMillis();
        log.info("Конец backup rclone. Затрачено время: {} секунд", ((endTime - startTime) / 1000));
    }
}