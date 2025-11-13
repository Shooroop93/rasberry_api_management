package com.rasberry.rasberry_api_management.service.impl;

import com.rasberry.rasberry_api_management.properties.RcloneConfigProperties;
import com.rasberry.rasberry_api_management.properties.TelegramBotProperties;
import com.rasberry.rasberry_api_management.service.RcloneOSAction;
import com.rasberry.rasberry_api_management.utils.ApiHelper;
import com.rasberry.rasberry_api_management.utils.FileHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rasberry.rasberry_api_management.utils.RcloneHelper.createProcessBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class RasberryOSImpl implements RcloneOSAction {

    private final AtomicBoolean isProcessBackup = new AtomicBoolean();
    private final RcloneConfigProperties rcloneConfigProperties;
    private final TelegramBotProperties telegramBotProperties;
    private final WebClient webClient;

    @Override
    public List<String> getTheNamesOfAllFoldersForBackup() {
        return FileHelper.getAllFolderName(rcloneConfigProperties.getPathBackupFolder());
    }

    @Override
    public void backup(String pathFolder, String folderName, String profile) {
        if (isProcessBackup.compareAndSet(false, true)) {
            log.info("Начинаем процесс backup");
            log.info("Блокируем возможность дополнительных backup: {}", isProcessBackup.get());

            ProcessBuilder processBuilder = createProcessBuilder(pathFolder, folderName, profile);
            processBuilder.redirectErrorStream(true);
            Process process = null;


            try {
                processBuilder.environment().put("RCLONE_CONFIG", "/home/admin/.config/rclone/rclone.conf");

                log.info("cmd: {}", String.join(" ", processBuilder.command()));

                process = processBuilder.start();

                log.info("start");
//                if (Objects.nonNull(rcloneConfigProperties.getNotificationsUrl()) && !rcloneConfigProperties.getNotificationsUrl().isEmpty()) {
                if (true) {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        log.info("bufferedReader");
                        String line;
                        Map<String, Integer> map = new HashMap<>();
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("Transferred")) {
                                ApiHelper.sendMessegeTelegram(line, telegramBotProperties.token());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Ошибка при backup", e);
            } finally {
                isProcessBackup.set(false);
                log.info("Разблокировали возможность дополнительных backup: {}", isProcessBackup);
            }
        } else {
            log.info("В данный момент происходит бэкап");
        }
    }

    @Override
    public void backup() {
        backup(rcloneConfigProperties.getPathBackupFolder(), "86c7de17-4be0-4759-abcc-c854c00d7c2b", "yandex_disk");

//        List<String> theNamesOfAllFoldersForBackup = getTheNamesOfAllFoldersForBackup();
//        if (theNamesOfAllFoldersForBackup != null && !theNamesOfAllFoldersForBackup.isEmpty()) {
//            theNamesOfAllFoldersForBackup.forEach(
//                    namefolder -> backup(rcloneConfigProperties.getPathBackupFolder(), namefolder, namefolder));
//        } else {
//            log.error("Отсутствует список папок для дебага");
//        }
    }


    private void sendMessage(String url, String message) {
        webClient
                .post()
                .uri(url)
                .bodyValue(Map.of("message", message));
    }
}