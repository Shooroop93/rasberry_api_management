package com.rasberry.rasberry_api_management.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RcloneHelperTest {

    @Test
    void test() {
        String expected = "sudo rclone sync /home/test/test_folder test_profile:test_folder --progress --stats-one-line --stats=5s --create-empty-src-dirs --checkers=8 --transfers=4";
        ProcessBuilder processBuilder = RcloneHelper.createProcessBuilder("/home/test/", "test_folder", "test_profile");

        assertEquals(expected, String.join("", processBuilder.command()));
    }

}