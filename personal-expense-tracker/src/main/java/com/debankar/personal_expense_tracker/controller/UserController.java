package com.debankar.personal_expense_tracker.controller;

import com.debankar.personal_expense_tracker.dto.ExpenseCreationDTO;
import com.debankar.personal_expense_tracker.dto.ExpenseDTO;
import com.debankar.personal_expense_tracker.dto.UserCreationDTO;
import com.debankar.personal_expense_tracker.dto.UserDTO;
import com.debankar.personal_expense_tracker.entity.Expense;
import com.debankar.personal_expense_tracker.entity.User;
import com.debankar.personal_expense_tracker.mapper.ExpenseMapper;
import com.debankar.personal_expense_tracker.mapper.UserMapper;
import com.debankar.personal_expense_tracker.security.JwtTokenProvider;
import com.debankar.personal_expense_tracker.service.ExpenseService;
import com.debankar.personal_expense_tracker.service.UserService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ExpenseService expenseService;
    private final UserMapper userMapper;
    private final ExpenseMapper expenseMapper;
    private final JwtTokenProvider jwtTokenProvider;


    public UserController(
            UserService userService,
            ExpenseService expenseService,
            UserMapper userMapper,
            ExpenseMapper expenseMapper,
            JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.userMapper = userMapper;
        this.expenseMapper = expenseMapper;
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

    @PostMapping("/add-expense")
    public ResponseEntity<ExpenseDTO> addExpense(
            @RequestBody ExpenseCreationDTO expenseDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String email = jwtTokenProvider.extractUsername(token);

        User authenticatedUser = userService.findByEmail(email);
        boolean isUser = authenticatedUser.getRole() == User.Role.USER;

        if (!isUser) {
            throw new IllegalArgumentException("Only valid users can create expenses!");
        }

        expenseDTO.setUserId(authenticatedUser.getId().toString());
        Expense expense = expenseService.addExpense(expenseDTO);

        return new ResponseEntity<>(expenseMapper.toExpenseDTO(expense), HttpStatus.CREATED);
    }

    @GetMapping("/all-expenses")
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String email = jwtTokenProvider.extractUsername(token);

        User authenticatedUser = userService.findByEmail(email);
        String authenticatedUserId = authenticatedUser.getId().toString();

        List<ExpenseDTO> expenseDTOList = expenseService.getUserExpenses(authenticatedUserId)
                .stream()
                .map(expenseMapper::toExpenseDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(expenseDTOList, HttpStatus.OK);
    }

    @DeleteMapping("/delete-expense/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable String expenseId,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String email = jwtTokenProvider.extractUsername(token);

        User authenticatedUser = userService.findByEmail(email);
        String authenticatedUserId = authenticatedUser.getId().toString();
        boolean foundExpense = false;

        for (Expense expense : expenseService.getUserExpenses(authenticatedUserId)) {
            if (expense.getId().toString().equals(expenseId)) {
                foundExpense = true;
                break;
            }
        }

        if (foundExpense) {
            expenseService.deleteExpense(expenseId);
        } else {
            throw new IllegalArgumentException("No such expense exists for this user!");
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/weekly-expenses")
    public ResponseEntity<Double> getWeeklyExpenses(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String email = jwtTokenProvider.extractUsername(token);

        User authenticatedUser = userService.findByEmail(email);
        String authenticatedUserId = authenticatedUser.getId().toString();

        LocalDateTime startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).with(LocalTime.MAX);

        double weeklyExpenses = expenseService.calculateExpensesForPeriod(authenticatedUserId, startOfWeek, endOfWeek);

        return ResponseEntity.ok(weeklyExpenses);
    }

    @GetMapping("/monthly-expenses")
    public ResponseEntity<Double> getMonthlyExpenses(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String email = jwtTokenProvider.extractUsername(token);

        User authenticatedUser = userService.findByEmail(email);
        String authenticatedUserId = authenticatedUser.getId().toString();

        LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        double monthlyExpenses = expenseService.calculateExpensesForPeriod(authenticatedUserId, startOfMonth, endOfMonth);

        return ResponseEntity.ok(monthlyExpenses);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authorizationHeader) throws AccessDeniedException {
        String token = authorizationHeader.substring(7);    // removing "Bearer " from header
        String email = jwtTokenProvider.extractUsername(token);

        User authenticatedUser = userService.findByEmail(email);
        boolean isAdmin = authenticatedUser.getRole() == User.Role.ADMIN;

        if (!isAdmin && !authenticatedUser.getId().toString().equals(userId)) {
            throw new AccessDeniedException("Access denied: You can only view your own details.");
        }

        User requestedUser = userService.findByUserId(userId);
        return ResponseEntity.ok(userMapper.toUserDTO(requestedUser));
    }
}
