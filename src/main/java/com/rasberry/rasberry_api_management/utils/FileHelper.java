package com.rasberry.rasberry_api_management.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static List<String> getAllFolderName(String path) {
        List<String> result = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < (files != null ? files.length : 0); i++) {
            if (files[i].isDirectory()) {
                result.add(files[i].getName());
            }
        }
        return result;
    }
}
