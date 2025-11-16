package com.rasberry.rasberry_api_management.constans;

import lombok.Getter;

@Getter
public enum RcloneOSCommand {

    RCLONE("rclone"),
    SYNC("sync"),
    PROGRESS("--progress"),
    STATS_ONE_LINE("--stats-one-line-date"),
    STATS("--stats"),
    CREATE_EMPTY_SRC_DIRS("--create-empty-src-dirs"),
    CHECKERS("--checkers"),
    TRANSFERS("--transfers"),
    LOG_LEVEL("--log-level"),
    USE_JSON_LOG("--use-json-log"),
    INFO("INFO");

    private final String command;

    RcloneOSCommand(String command) {
        this.command = command;
    }
}
