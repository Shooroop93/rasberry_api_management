package com.rasberry.rasberry_api_management.service.impl;

import com.rasberry.rasberry_api_management.properties.RcloneConfigProperties;
import com.rasberry.rasberry_api_management.service.Rclone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class IniServiceImpl implements Rclone {

    private final RcloneConfigProperties rcloneConfigProperties;

    private Optional<INIConfiguration> readFile(String path) {
        Configurations CONFIGURATIONS = new Configurations();
        log.info("Чтение конфигурационного файла: {}", path);
        try {
            INIConfiguration ini = CONFIGURATIONS.ini(path);
            log.info("Конфигурационный файл найден и не пустой = {}", !ini.isEmpty());
            return Optional.of(ini);
        } catch (ConfigurationException ex) {
            log.error("Ошибка при чтение ini файла: {}", path, ex);
            return Optional.empty();
        }
    }

    public Map<String, Map<String, String>> getValueInMap() {
        Optional<INIConfiguration> iniConfiguration = readFile(rcloneConfigProperties.getPathConfig());

        if (iniConfiguration.isEmpty()) {
            String errorMessage = format("Ошибка при формирования структуры Map<String, Map<String, String>> из config file: %s", rcloneConfigProperties.getPathConfig());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        INIConfiguration iniFile = iniConfiguration.get();

        Map<String, Map<String, String>> iniFileContents = new HashMap<>();

        for (String section : iniFile.getSections()) {
            log.debug("Проверяем флаг '{}' сохранения токена и других данных для профиля: '{}'", rcloneConfigProperties.isSaveTokenInApp(), section);
            if (rcloneConfigProperties.isSaveTokenInApp()) {
                iniFileContents.put(section, getMetadataFromAnIniFile(iniFile, section));
            } else {
                iniFileContents.put(section, null);
            }
        }
        return iniFileContents;
    }

    private Map<String, String> getMetadataFromAnIniFile(INIConfiguration iniFile, String section) {
        log.info("Достаем метаданные для профиля: '{}'", section);
        Map<String, String> subSectionMap = new HashMap<>();
        SubnodeConfiguration confSection = iniFile.getSection(section);

        Iterator<String> confSectionKeys = confSection.getKeys();

        while (confSectionKeys.hasNext()) {
            try {
                String key = confSectionKeys.next();
                String value = confSection.getProperty(key).toString();
                subSectionMap.put(key, value);
            } catch (NullPointerException ignored) {
                log.error("Ошибка при взятие значений из ini file, для профиля: {}", section);
            }
        }
        return subSectionMap;
    }
}