package com.rasberry.rasberry_api_management.constans;

import lombok.Getter;

@Getter
public enum RasberryOSCommand {

    COLON(":"),
    RCLONE("rclone "),
    SYNC("sync "),
    USE_JSON_LOG(" --use-json-log"),
    LOG_LEVEL(" --log-level "),
    INFO("INFO"),
    NOTICE("NOTICE"),
    DEBUG("DEBUG"),
    CREATE_EMPTY_SRC_DIRS(" --create-empty-src-dirs"),
    CHECKERS(" --checkers="),
    TRANSFERS(" --transfers="),
    PROGRESS(" --progress"),
    STATS_ONE_LINE(" --stats-one-line"),
    STATS(" --stats=");

    private final String command;

    RasberryOSCommand(String command) {
        this.command = command;
    }
}
