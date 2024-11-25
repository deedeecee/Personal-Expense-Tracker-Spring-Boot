package com.debankar.personal_expense_tracker.controller;

import com.debankar.personal_expense_tracker.dto.ExpenseDTO;
import com.debankar.personal_expense_tracker.dto.UserDTO;
import com.debankar.personal_expense_tracker.entity.User;
import com.debankar.personal_expense_tracker.mapper.ExpenseMapper;
import com.debankar.personal_expense_tracker.mapper.UserMapper;
import com.debankar.personal_expense_tracker.security.JwtTokenProvider;
import com.debankar.personal_expense_tracker.service.ExpenseService;
import com.debankar.personal_expense_tracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final ExpenseService expenseService;
    private final UserMapper userMapper;
    private final ExpenseMapper expenseMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminController(UserService userService, ExpenseService expenseService, UserMapper userMapper, ExpenseMapper expenseMapper, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.userMapper = userMapper;
        this.expenseMapper = expenseMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/all-users")
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

    @GetMapping("/all-expenses/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authorizationHeader) throws AccessDeniedException {
        String token = authorizationHeader.substring(7);
        String email = jwtTokenProvider.extractUsername(token);

        User authenticatedUser = userService.findByEmail(email);

        if (authenticatedUser.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Access denied!");
        }

        List<ExpenseDTO> expenseDTOList = expenseService.getUserExpenses(userId)
                .stream()
                .map(expenseMapper::toExpenseDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(expenseDTOList, HttpStatus.OK);
    }
}
