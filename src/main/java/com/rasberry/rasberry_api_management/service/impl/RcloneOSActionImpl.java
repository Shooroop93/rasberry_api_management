package com.rasberry.rasberry_api_management.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasberry.rasberry_api_management.properties.RcloneConfigProperties;
import com.rasberry.rasberry_api_management.properties.TelegramBotProperties;
import com.rasberry.rasberry_api_management.service.RcloneOSAction;
import com.rasberry.rasberry_api_management.utils.ApiHelper;
import com.rasberry.rasberry_api_management.utils.FileHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rasberry.rasberry_api_management.utils.RcloneHelper.createProcessBuilderCopy;
import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class RcloneOSActionImpl implements RcloneOSAction {

    private final AtomicBoolean isProcessBackup = new AtomicBoolean();
    private final RcloneConfigProperties rcloneConfigProperties;
    private final TelegramBotProperties telegramBotProperties;
    private final ApiHelper apiHelper;
    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public List<String> getTheNamesOfAllFoldersForBackup() {
        return FileHelper.getAllFolderName(rcloneConfigProperties.getPathBackupFolder());
    }

    @Override
    public void backup(String pathFolder, String folderName, String profile) {
        if (rcloneConfigProperties.isEnable()) {
            if (isProcessBackup.compareAndSet(false, true)) {
                log.info("Начинаем процесс backup");
                log.info("Блокируем возможность дополнительных backup: {}", isProcessBackup.get());

                ProcessBuilder processBuilder = createProcessBuilderCopy(pathFolder,
                        folderName,
                        profile,
                        rcloneConfigProperties.getSettings().getTimeStats(),
                        rcloneConfigProperties.getSettings().getCheckers(),
                        rcloneConfigProperties.getSettings().getTransfers());

                processBuilder.redirectErrorStream(true);
                Process process = null;

                try {
                    processBuilder.environment().put("RCLONE_CONFIG", rcloneConfigProperties.getPathRcloneConfig());

                    log.info("cmd: {}", String.join(" ", processBuilder.command()));

                    process = processBuilder.start();

                    log.info("start backup");
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        String messageId = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("Transferred")) {
                                StringBuilder message = new StringBuilder();
                                message.append(format("Происходит backup для пользователя: %s. По профилю rclone: %s.", folderName, profile));
                                message.append("\n");
                                ObjectMapper objectMapper = new ObjectMapper();
                                Map<String, Object> mapJson = objectMapper.readValue(line, Map.class);
                                String msg = (String) mapJson.get("msg");

                                String[] linesplit = msg.split("\\R");

                                for (String split : linesplit) {
                                    if (split.trim().startsWith("Transferred:")) {
                                        message.append(split.trim());
                                        break;
                                    }
                                }

                                if (Objects.isNull(messageId)) {
                                    String responseTelegram = apiHelper.sendMessageTelegram(
                                            message.toString(),
                                            rcloneConfigProperties.getIdChannelTelegram(),
                                            telegramBotProperties.token(),
                                            null);

                                    Map<String, Object> readValue = mapper.readValue(responseTelegram, Map.class);
                                    Map<String, Object> result = (Map<String, Object>) readValue.get("result");

                                    messageId = String.valueOf(result.get("message_id"));
                                } else {
                                    apiHelper.sendMessageTelegram(message.toString(), rcloneConfigProperties.getIdChannelTelegram(), telegramBotProperties.token(), messageId);
                                }
                            }
                        }
                    }

                    process.waitFor();

                } catch (IOException e) {
                    log.error("Ошибка при backup", e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Backup прерван (Interrupted) для {}", folderName, e);
                } finally {
                    isProcessBackup.set(false);
                    log.info("Разблокировали возможность дополнительных backup: {}", isProcessBackup);
                }
            } else {
                log.info("В данный момент происходит бэкап");
            }
        }
    }

    @Override
    public void backup() {
        if (rcloneConfigProperties.isEnable()) {
            List<String> theNamesOfAllFoldersForBackup = getTheNamesOfAllFoldersForBackup();
            if (theNamesOfAllFoldersForBackup != null && !theNamesOfAllFoldersForBackup.isEmpty()) {
                for (String folderName : theNamesOfAllFoldersForBackup) {
                    rcloneConfigProperties.getProfile().forEach(profile ->
                            backup(rcloneConfigProperties.getPathBackupFolder(), folderName, profile));
                }
            } else {
                log.error("Отсутствует список папок для backup");
            }
        } else {
            log.warn("В настройках backup для rclone: {}", rcloneConfigProperties.isEnable());
        }
    }
}