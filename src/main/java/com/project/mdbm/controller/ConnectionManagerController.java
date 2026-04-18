package com.project.mdbm.controller;

import com.project.mdbm.dto.GenericAPIResponse;
import com.project.mdbm.service.ConnectionManagerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/db")
public class ConnectionManagerController {

    @Autowired
    private ConnectionManagerService connectionManagerService;

    @GetMapping("/connect")
    public ResponseEntity<GenericAPIResponse> connectToDatabase(
            @RequestParam String connectionName,
            HttpSession session) {
        return ResponseEntity.ok(connectionManagerService.connectToDatabase(connectionName, session));
    }
}