package com.rasberry.rasberry_api_management.constans;

import lombok.Getter;

@Getter
public enum RasberryOSCommand {

    RCLONE("rclone"),
    SYNC("sync"),
    PROGRESS("--progress"),
    STATS_ONE_LINE("--stats-one-line"),
    STATS("--stats"),
    CREATE_EMPTY_SRC_DIRS("--create-empty-src-dirs"),
    CHECKERS("--checkers"),
    TRANSFERS("--transfers"),
    LOG_LEVEL("--log-level"),
    USE_JSON_LOG("--use-json-log");

    private final String command;

    RasberryOSCommand(String command) {
        this.command = command;
    }
}
