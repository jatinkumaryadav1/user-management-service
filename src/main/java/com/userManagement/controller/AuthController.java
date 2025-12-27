package com.userManagement.controller;

import com.userManagement.dtos.AuthRequest;
import com.userManagement.dtos.LoginResponse;
import com.userManagement.dtos.UserDto;
import com.userManagement.exception.InvalidCredentialsException;
import com.userManagement.exception.UserNotFoundException;
import com.userManagement.mappers.UserMapper;
import com.userManagement.security.JwtUtil;
import com.userManagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private JwtUtil jwtUtil;
    private UserService userService;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody UserDto users) {
        UserDto user = userService.registerUser(users);

        if (user == null) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setStatus("error");
            loginResponse.setMessage("Unable to register, please try after some time!");

            return ResponseEntity.ok(loginResponse);
        }

        Map<String, Object > claims = Map.of("role", "USER");
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setStatus("success");
        loginResponse.setMessage("Register successful");
        loginResponse.setToken(token);
        loginResponse.setUserDto(user);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest authRequest) {

        try {
            UserDto userDto = userService.verifyUser(authRequest);

            Map<String, Object> claims = Map.of("role", "USER");
            String token = jwtUtil.generateToken(userDto.getUsername(), claims);

            LoginResponse response = new LoginResponse();
            response.setStatus("success");
            response.setMessage("Login successful");
            response.setToken(token);
            response.setUserDto(userDto);

            return ResponseEntity.ok(response);

        } catch (UserNotFoundException | InvalidCredentialsException e) {

            LoginResponse response = new LoginResponse();
            response.setStatus("error");
            response.setMessage("Invalid username or password");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


}
