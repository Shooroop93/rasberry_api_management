package com.rasberry.rasberry_api_management.service;

import java.io.IOException;
import java.util.List;

public interface RcloneOSAction {

    List<String> getTheNamesOfAllFoldersForBackup();

    void backup(String pathFolder, String folderName, String profile) throws IOException, InterruptedException;

    void backup();
}