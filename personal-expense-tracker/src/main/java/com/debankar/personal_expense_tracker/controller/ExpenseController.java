package com.debankar.personal_expense_tracker.controller;

import com.debankar.personal_expense_tracker.dto.ExpenseCreationDTO;
import com.debankar.personal_expense_tracker.dto.ExpenseDTO;
import com.debankar.personal_expense_tracker.entity.Expense;
import com.debankar.personal_expense_tracker.mapper.ExpenseMapper;
import com.debankar.personal_expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    public ExpenseController(ExpenseService expenseService, ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
    }

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody ExpenseCreationDTO expenseDTO) {
        Expense createdExpense = expenseService.addExpense(expenseDTO);
        return new ResponseEntity<>(expenseMapper.toExpenseDTO(createdExpense), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByUser(@PathVariable String userId) {
        List<ExpenseDTO> expenseDTOList = expenseService.getUserExpenses(userId)
                .stream()
                .map(expenseMapper::toExpenseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(expenseDTOList);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseDTO> updateExpense(
            @PathVariable String expenseId,
            @Valid @RequestBody ExpenseDTO expenseDTO) {
        Expense updatedExpense = expenseService.updateExpense(expenseId, expenseDTO);
        return ResponseEntity.ok(expenseMapper.toExpenseDTO(updatedExpense));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
}
