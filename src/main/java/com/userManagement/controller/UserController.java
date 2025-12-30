package com.userManagement.controller;

import com.userManagement.RoleEnum;
import com.userManagement.dtos.InformationResponseBody;
import com.userManagement.dtos.UserDto;
import com.userManagement.exception.UserNotFoundException;
import com.userManagement.service.UserService;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getAuthenticatedUser(Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        UserDto userDto = userService.getUserByUserName(username);

        return ResponseEntity.ok(userDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserByUserId(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);

        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = userService.getUsers();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UserDto user) {
        userService.updateUser(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsers(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/adminInformation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InformationResponseBody> adminLevelInformation(){

        InformationResponseBody information = new InformationResponseBody();
        information.setStatus("SUCCESS");
        information.setMessage("ADMIN LEVEL API ENDPOINT ACCESSED SUCCESSFULLY");
        information.setApplicationName("USER MANAGEMENT SERVICE");
        information.setApplicationDescription("APPLICATION DESIGNED FOR MANAGING USERS");

        return ResponseEntity.ok(information);
    }

}
