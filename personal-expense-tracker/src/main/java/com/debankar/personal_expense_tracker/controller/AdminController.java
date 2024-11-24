package com.debankar.personal_expense_tracker.controller;

import com.debankar.personal_expense_tracker.dto.UserDTO;
import com.debankar.personal_expense_tracker.entity.User;
import com.debankar.personal_expense_tracker.mapper.UserMapper;
import com.debankar.personal_expense_tracker.security.JwtTokenProvider;
import com.debankar.personal_expense_tracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminController(UserService userService, UserMapper userMapper, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);    // removing "Bearer " from header
        String email = jwtTokenProvider.extractUsername(token);

        User authenticatedUser = userService.findByEmail(email);
        boolean isAdmin = authenticatedUser.getRole() == User.Role.ADMIN;

        if (!isAdmin) {
            throw new IllegalArgumentException("Users are not authorized to perform this action!");
        }

        List<UserDTO> allUserDTOList = userService.findAll()
                .stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(allUserDTOList);
    }
}
