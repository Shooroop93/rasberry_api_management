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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        if (!isProcessBackup.compareAndSet(false, true)) {
            log.info("В данный момент происходит бэкап");
            return;
        }

        log.info("Начинаем процесс backup; блокировка установлена: {}", isProcessBackup.get());
        Process process = null;

        try {
            ProcessBuilder a = createProcessBuilder(pathFolder, folderName, profile)
                    .redirectErrorStream(true);

            log.info("cmd: {}", String.join(" ", a.command()));

//            process = pb.start();
            log.info("Процесс запущен (pid: {})", process.pid());

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                long lastSentNanos = 0L;
                final long MIN_INTERVAL_NANOS = java.util.concurrent.TimeUnit.SECONDS.toNanos(2);

                //
                ProcessBuilder pb = createProcessBuilder(pathFolder, folderName, profile)
                        .redirectErrorStream(true);
                pb.environment().put("RCLONE_CONFIG", "/etc/rclone/rclone.conf"); // <-- ваш реальный

                List<String> cmd = new ArrayList<>(pb.command());
                cmd.add("-vv");                    // или --log-level=DEBUG
                cmd.add("--retries=1");            // чтобы не ждать долго на диагностиках
// по желанию: структурированные логи
// cmd.add("--use-json-log");
                pb.command(cmd);

                StringBuilder output = new StringBuilder(4096);
                //

                while ((line = br.readLine()) != null) {
                    output.append(line).append('\n');
                    String progress = LineHelper.getProgressRclone(line);

                    if (progress != null && !progress.isBlank()) {
                        long now = System.nanoTime();

                        if (now - lastSentNanos >= MIN_INTERVAL_NANOS) {
                            ApiHelper.sendMessegeTelegram(progress, telegramBotProperties.token());
                            lastSentNanos = now;
                        }

                        log.trace("rclone: {}", progress);
                    }
                }
            }

            int exit = process.waitFor();
            if (exit != 0) {
                // выведите первые/последние 2000 символов для дебага
                String out = output.toString();
                int n = Math.min(out.length(), 2000);
                log.error("rclone stderr/stdout (tail):\n{}", out.substring(out.length() - n));
            }

        } catch (IOException e) {
            log.error("Ошибка запуска/чтения процесса backup", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Поток прерван во время ожидания завершения backup", e);
            if (process != null && process.isAlive()) {
                process.destroy();
                try {
                    if (!process.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)) {
                        process.destroyForcibly();
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    process.destroyForcibly();
                }
            }
        } finally {
            isProcessBackup.set(false);
            log.info("Разблокировали возможность дополнительных backup: {}", isProcessBackup.get());
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