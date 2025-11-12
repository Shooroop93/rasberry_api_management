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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rasberry.rasberry_api_management.utils.RcloneHelper.createProcessBuilder;
import static java.lang.String.format;

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
            Process process = null;

            try {
                processBuilder.environment().put("RCLONE_CONFIG", "/home/admin/.config/rclone/rclone.conf");

                // Добавим -vv для диагностики на время
                List<String> cmd = new ArrayList<>(processBuilder.command());
                cmd.add("-vv");
                processBuilder.command(cmd);

                log.info("cmd: {}", String.join(" ", processBuilder.command()));

                process = processBuilder.start();

                log.info("start");
//                if (Objects.nonNull(rcloneConfigProperties.getNotificationsUrl()) && !rcloneConfigProperties.getNotificationsUrl().isEmpty()) {
                if (true) {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        log.info("bufferedReader");
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {

                            Pattern percentPattern = Pattern.compile("(\\d+)%");
                            Pattern etaPattern = Pattern.compile("ETA\\s+([0-9hms]+)");

                            Matcher percentMatcher = percentPattern.matcher(line);
                            Matcher etaMatcher = etaPattern.matcher(line);

                            if (percentMatcher.find() || etaMatcher.find()) {
                                String percent = percentMatcher.find() ? percentMatcher.group(1) : "N/A";
                                String eta = etaMatcher.find() ? etaMatcher.group(1) : "N/A";

                                String progressRclone = format("Производится процесс бекапа программой rclone, процент выполнения программы: '%s', оставшиеся время выполнения: '%s'", percent, eta);

                                sendMessage(rcloneConfigProperties.getNotificationsUrl(), progressRclone);
                                ApiHelper.sendMessegeTelegram(progressRclone, telegramBotProperties.token());
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