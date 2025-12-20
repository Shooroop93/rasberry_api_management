package com.rasberry.rasberry_api_management.utils;

import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.CHECKERS;
import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.COPY;
import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.CREATE_EMPTY_SRC_DIRS;
import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.INFO;
import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.LOG_LEVEL;
import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.RCLONE;
import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.STATS;
import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.SYNC;
import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.TRANSFERS;
import static com.rasberry.rasberry_api_management.constans.RcloneOSCommand.USE_JSON_LOG;

public class RcloneHelper {

    public static ProcessBuilder createProcessBuilderSync(String pathFolder,
                                                          String folderName,
                                                          String profile,
                                                          String timeStats,
                                                          String checkers,
                                                          String transfers) {

        return new ProcessBuilder(
                RCLONE.getCommand(),
                SYNC.getCommand(),
                pathFolder + folderName,
                profile + ":" + folderName,
                USE_JSON_LOG.getCommand(),
                LOG_LEVEL.getCommand(),
                INFO.getCommand(),
                STATS.getCommand() + "=" + timeStats,
                CREATE_EMPTY_SRC_DIRS.getCommand(),
                CHECKERS.getCommand() + "=" + checkers,
                TRANSFERS.getCommand() + "=" + transfers
        );
    }

    public static ProcessBuilder createProcessBuilderCopy(String pathFolder,
                                                          String folderName,
                                                          String profile,
                                                          String timeStats,
                                                          String checkers,
                                                          String transfers) {

        return new ProcessBuilder(
                RCLONE.getCommand(),
                COPY.getCommand(),
                pathFolder + folderName,
                profile + ":" + folderName,
                USE_JSON_LOG.getCommand(),
                LOG_LEVEL.getCommand(),
                INFO.getCommand(),
                STATS.getCommand() + "=" + timeStats,
                CREATE_EMPTY_SRC_DIRS.getCommand(),
                CHECKERS.getCommand() + "=" + checkers,
                TRANSFERS.getCommand() + "=" + transfers
        );
    }
}