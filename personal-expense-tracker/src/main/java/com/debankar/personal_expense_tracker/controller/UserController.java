package com.debankar.personal_expense_tracker.controller;

import com.debankar.personal_expense_tracker.dto.UserCreationDTO;
import com.debankar.personal_expense_tracker.dto.UserDTO;
import com.debankar.personal_expense_tracker.entity.User;
import com.debankar.personal_expense_tracker.mapper.UserMapper;
import com.debankar.personal_expense_tracker.security.JwtTokenProvider;
import com.debankar.personal_expense_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;


    public UserController(UserService userService, UserMapper userMapper, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserCreationDTO userDTO) {
        User createdUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(userMapper.toUserDTO(createdUser), HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestParam String email, @RequestParam String password) {
        String jwtToken = userService.authenticate(email, password);
        return ResponseEntity.ok("Authentication successful for email: " + email + "\nJWT Token: " + jwtToken);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);    // removing "Bearer " from header
        String email = jwtTokenProvider.extractUsername(token);

        User authenticatedUser = userService.findByEmail(email);
        boolean isAdmin = authenticatedUser.getRole() == User.Role.ADMIN;

        if (!isAdmin && !authenticatedUser.getId().toString().equals(userId)) {
            throw new IllegalArgumentException("Access denied: You can only view your own details.");
        }

        User requestedUser = userService.findByUserId(userId);
        return ResponseEntity.ok(userMapper.toUserDTO(requestedUser));
    }
}
