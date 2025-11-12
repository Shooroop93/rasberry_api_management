//package com.rasberry.rasberry_api_management.utils;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class LineHelperTest {
//
//    @Test
//    void checkValid_1() {
//        String test = "172.345 KiB / 309.233 MiB, 0%, 34.469 KiB/s, ETA 2h33m1s";
//        assertEquals("Progress: 0% | ETA: 2h33m1s", LineHelper.getProgressRclone(test));
//    }
//
//    @Test
//    void checkValid_2() {
//        String test = "309.233 MiB, 51%, 34.469 KiB/s, ETA 3m";
//        assertEquals("Progress: 51% | ETA: 3m", LineHelper.getProgressRclone(test));
//    }
//
//    @Test
//    void checkValid_3() {
//        String test = "ETA 1s, 100%, 34.469 KiB/s";
//        assertEquals("Progress: 100% | ETA: 1s", LineHelper.getProgressRclone(test));
//    }
//
//    @Test
//    void checkNoValid_1() {
//        String test = "100%, 34.469 KiB/s";
//        assertEquals("Progress: 100% | ETA: N/A", LineHelper.getProgressRclone(test));
//    }
//
//    @Test
//    void checkNoValid_2() {
//        String test = "172.345 KiB / 309.233 MiB, 34.469 KiB/s, ETA 2h33m1s";
//        assertEquals("Progress: N/A% | ETA: 2h33m1s", LineHelper.getProgressRclone(test));
//    }
//
//    @Test
//    void checkNoValid_3() {
//        String test = "172.345 KiB / 309.233 MiB, 34.469 KiB/s";
//        assertEquals("Progress: N/A% | ETA: N/A", LineHelper.getProgressRclone(test));
//    }
//}