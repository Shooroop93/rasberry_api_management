package com.rasberry.rasberry_api_management.service.impl;

import com.rasberry.rasberry_api_management.service.RcloneOSAction;

import java.util.List;

public class RasberryOSImpl implements RcloneOSAction {


    @Override
    public List<String> getTheNamesOfAllFoldersForBackup() {

        return List.of();
    }

    @Override
    public void backup() {

    }
}