package com.rasberry.rasberry_api_management.service;

import java.util.List;

public interface RcloneOSAction {

    List<String> getTheNamesOfAllFoldersForBackup();
    void backup();
}