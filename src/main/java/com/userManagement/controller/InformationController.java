package com.userManagement.controller;


import com.userManagement.dtos.InformationResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class InformationController {

    @GetMapping("/information")
    public ResponseEntity<InformationResponseBody> getInformation(){
        InformationResponseBody information = new InformationResponseBody();
        information.setStatus("SUCCESS");
        information.setMessage("PUBLIC API ENDPOINT ACCESSED SUCCESSFULLY");
        information.setApplicationName("USER MANAGEMENT SERVICE");
        information.setApplicationDescription("APPLICATION DESIGNED FOR MANAGING USERS");

        return ResponseEntity.ok(information);
    }
}
