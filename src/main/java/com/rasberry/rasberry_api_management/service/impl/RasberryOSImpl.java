package com.rasberry.rasberry_api_management.service.impl;

import com.rasberry.rasberry_api_management.properties.RcloneConfigProperties;
import com.rasberry.rasberry_api_management.properties.TelegramBotProperties;
import com.rasberry.rasberry_api_management.service.RcloneOSAction;
import com.rasberry.rasberry_api_management.utils.ApiHelper;
import com.rasberry.rasberry_api_management.utils.FileHelper;
import com.rasberry.rasberry_api_management.utils.LineHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        if (!isProcessBackup.get()) {
            log.info("Начинаем процесс backup");
            isProcessBackup.set(true);
            log.info("Блокируем возможность дополнительных backup: {}", isProcessBackup.get());

            ProcessBuilder processBuilder = createProcessBuilder(pathFolder, folderName, profile);
            Process process = null;
            try {
                process = processBuilder.start();
                if (Objects.nonNull(rcloneConfigProperties.getNotificationsUrl()) && !rcloneConfigProperties.getNotificationsUrl().isEmpty()) {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            String progressRclone = LineHelper.getProgressRclone(line);
//                            sendMessage(rcloneConfigProperties.getNotificationsUrl(), progressRclone);
                            ApiHelper.sendMessegeTelegram(progressRclone, telegramBotProperties.token());
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Ошибка при backup");
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
        List<String> theNamesOfAllFoldersForBackup = getTheNamesOfAllFoldersForBackup();
        if (theNamesOfAllFoldersForBackup != null && !theNamesOfAllFoldersForBackup.isEmpty()) {
            theNamesOfAllFoldersForBackup.forEach(
                    namefolder -> backup(rcloneConfigProperties.getPathBackupFolder(), namefolder, namefolder));
        } else {
            log.error("Отсутствует список папок для дебага");
        }
    }


    private void sendMessage(String url, String message) {
        webClient
                .post()
                .uri(url)
                .bodyValue(Map.of("message", message));
    }
}