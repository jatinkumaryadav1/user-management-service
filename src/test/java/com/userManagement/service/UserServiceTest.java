package com.userManagement.service;

import com.userManagement.dtos.AuthRequest;
import com.userManagement.dtos.UserDto;
import com.userManagement.entity.User;
import com.userManagement.exception.InvalidCredentialsException;
import com.userManagement.exception.UserAlreadyExistsException;
import com.userManagement.exception.UserNotFoundException;
import com.userManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@test.com");
        user.setFullName("John Doe");
        user.setPassword("encodedPassword");

        userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setEmail("john@test.com");
        userDto.setFullName("John Doe");
        userDto.setPassword("password");
    }
    //  registerUser

    @Test
    void registerUser_success() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.registerUser(userDto);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_usernameExists() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(userDto));
    }

    @Test
    void registerUser_emailExists() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(userDto));
    }
    //  updateUser

    @Test
    void updateUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, userDto);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(1L, userDto));
    }
    //  deleteUser

    @Test
    void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(1L));
    }
    //  getUserById

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void getUserById_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1L));
    }
    //  getUsers

    @Test
    void getUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> users = userService.getUsers();

        assertEquals(1, users.size());
    }
    //  getUserByUserName

    @Test
    void getUserByUserName_success() {
        when(userRepository.findByUsername("john")).thenReturn(user);

        UserDto result = userService.getUserByUserName("john");

        assertEquals("john", result.getUsername());
    }

    @Test
    void getUserByUserName_userNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUserName("john"));
    }

    // verifyUser

    @Test
    void verifyUser_success() {
        AuthRequest authRequest = new AuthRequest("john", "password");

        when(userRepository.findByUsername("john")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        UserDto result = userService.verifyUser(authRequest);

        assertEquals("john", result.getUsername());
    }

    @Test
    void verifyUser_invalidPassword() {
        AuthRequest authRequest = new AuthRequest("john", "wrong");

        when(userRepository.findByUsername("john")).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.verifyUser(authRequest));
    }

    @Test
    void verifyUser_userNotFound() {
        AuthRequest authRequest = new AuthRequest("john", "password");

        when(userRepository.findByUsername("john")).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> userService.verifyUser(authRequest));
    }
}

