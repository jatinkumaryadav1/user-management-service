package com.userManagement.service;

import com.userManagement.dtos.AuthRequest;
import com.userManagement.dtos.UserDto;
import com.userManagement.entity.User;
import com.userManagement.exception.InvalidCredentialsException;
import com.userManagement.exception.UserAlreadyExistsException;
import com.userManagement.exception.UserNotFoundException;
import com.userManagement.mappers.UserMapper;
import com.userManagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = encoder;
    }

    @Transactional
    public UserDto registerUser(UserDto userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }


        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setRoles(userDto.getRoles());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = userRepository.save(user);

        return UserMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        User existingUser = optionalUser.get();
        existingUser.setFullName(userDto.getFullName());
        existingUser.setEmail(userDto.getEmail());

        // we can write more setter methods to update more fields
        User savedUser = userRepository.save(existingUser);

        return UserMapper.toDto(savedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        userRepository.deleteById(userId);
    }

    public UserDto getUserById(Long userId) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        return UserMapper.toDto(optionalUser.get());
    }
    public List<UserDto> getUsers() {
        List<User> userList = userRepository.findAll();
        List<UserDto> dtoList = new ArrayList<>();

        for (User user : userList) {
            dtoList.add(UserMapper.toDto(user));
        }

        return dtoList;
    }

    public UserDto getUserByUserName(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null){
            throw new UserNotFoundException("User not found: " + username);
        }

        return UserMapper.toDto(user);
    }

    // method to check password and username are match or not
    public UserDto verifyUser(AuthRequest authRequest) {

        User user = userRepository.findByUsername(authRequest.getUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found: " + authRequest.getUsername());
        }

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return UserMapper.toDto(user);
    }

}
