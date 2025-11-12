package com.rasberry.rasberry_api_management.contoller;

import com.rasberry.rasberry_api_management.properties.RcloneConfigProperties;
import com.rasberry.rasberry_api_management.service.RcloneOSAction;
import com.rasberry.rasberry_api_management.service.impl.IniServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final IniServiceImpl iniService;
    private final RcloneOSAction rcloneOSAction;
    private final RcloneConfigProperties rcloneConfigProperties;

    @GetMapping("/test")
    public ResponseEntity<?> testController() {
        return ResponseEntity.ok(iniService.getValueInMap());
    }

    @GetMapping("/api/backup")
    public void testBackup() {
        rcloneOSAction.backup();
    }
}