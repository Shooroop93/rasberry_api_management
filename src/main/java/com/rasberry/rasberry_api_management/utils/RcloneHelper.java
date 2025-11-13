package com.rasberry.rasberry_api_management.utils;

import static com.rasberry.rasberry_api_management.constans.RasberryOSCommand.CHECKERS;
import static com.rasberry.rasberry_api_management.constans.RasberryOSCommand.CREATE_EMPTY_SRC_DIRS;
import static com.rasberry.rasberry_api_management.constans.RasberryOSCommand.LOG_LEVEL;
import static com.rasberry.rasberry_api_management.constans.RasberryOSCommand.RCLONE;
import static com.rasberry.rasberry_api_management.constans.RasberryOSCommand.STATS;
import static com.rasberry.rasberry_api_management.constans.RasberryOSCommand.SYNC;
import static com.rasberry.rasberry_api_management.constans.RasberryOSCommand.TRANSFERS;
import static com.rasberry.rasberry_api_management.constans.RasberryOSCommand.USE_JSON_LOG;

public class RcloneHelper {

    public static ProcessBuilder createProcessBuilder(String pathFolder, String folderName, String profile) {

        return new ProcessBuilder(
                RCLONE.getCommand(),
                SYNC.getCommand(),
                pathFolder + folderName,
                profile + ":" + folderName,
                USE_JSON_LOG.getCommand(),
                LOG_LEVEL.getCommand(),
                "INFO",
                STATS.getCommand() + "=5s",
                CREATE_EMPTY_SRC_DIRS.getCommand(),
                CHECKERS.getCommand() + "=8",
                TRANSFERS.getCommand() + "=4"
        );
    }
}