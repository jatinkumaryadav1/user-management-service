package com.userManagement.controller;

import com.userManagement.dtos.InformationResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/adminInformation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminLevelInformation() {

        InformationResponseBody information = new InformationResponseBody();
        information.setStatus("SUCCESS");
        information.setMessage("ADMIN LEVEL API ENDPOINT ACCESSED SUCCESSFULLY");
        information.setApplicationName("USER MANAGEMENT SERVICE");
        information.setApplicationDescription("APPLICATION DESIGNED FOR MANAGING USERS");

        return ResponseEntity.ok(information);
    }

}
