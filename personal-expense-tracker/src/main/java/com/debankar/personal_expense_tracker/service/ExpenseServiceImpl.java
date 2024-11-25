package com.debankar.personal_expense_tracker.service;

import com.debankar.personal_expense_tracker.dto.ExpenseCreationDTO;
import com.debankar.personal_expense_tracker.dto.ExpenseDTO;
import com.debankar.personal_expense_tracker.entity.Expense;
import com.debankar.personal_expense_tracker.mapper.ExpenseMapper;
import com.debankar.personal_expense_tracker.repository.ExpenseRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    public Expense addExpense(ExpenseCreationDTO expenseDTO) {
        Expense expense = expenseMapper.toExpense(expenseDTO);
        expense.setDateTime(LocalDateTime.now());
        expenseRepository.save(expense);

        return expense;
    }

    @Override
    public List<Expense> getUserExpenses(String userId) {
        return expenseRepository.findByUserId(new ObjectId(userId));
    }

    @Override
    public Expense updateExpense(String expenseId, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(new ObjectId(expenseId))
                .orElseThrow(() -> new IllegalArgumentException("Expense with ID " + expenseId + " not found!"));

        if (expenseDTO.getCategory() != null) {
            expense.setCategory(expenseDTO.getCategory());
        }

        if (expenseDTO.getDescription() != null) {
            expense.setDescription(expenseDTO.getDescription());
        }

        if (expenseDTO.getAmount() != null) {
            expense.setAmount(expenseDTO.getAmount());
        }

        if (expenseDTO.getDateTime() != null) {
            expense.setDateTime(expenseDTO.getDateTime());
        }

        return expenseRepository.save(expense);
    }

    @Override
    public void deleteExpense(String expenseId) {
        if (!expenseRepository.existsById(new ObjectId(expenseId))) {
            throw new IllegalArgumentException("Expense with ID " + expenseId + " not found!");
        }

        expenseRepository.deleteById(new ObjectId(expenseId));
    }

    @Override
    public double calculateExpensesForPeriod(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Expense> expenses = expenseRepository.findByUserIdAndDateTimeBetween(
                new ObjectId(userId), startDate, endDate);

        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
