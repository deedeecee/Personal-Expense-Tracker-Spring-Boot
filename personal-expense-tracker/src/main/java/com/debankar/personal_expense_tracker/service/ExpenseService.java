package com.debankar.personal_expense_tracker.service;

import com.debankar.personal_expense_tracker.dto.ExpenseCreationDTO;
import com.debankar.personal_expense_tracker.dto.ExpenseDTO;
import com.debankar.personal_expense_tracker.entity.Expense;

import java.util.List;

public interface ExpenseService {
    Expense addExpense(ExpenseCreationDTO expenseDTO);
    List<Expense> getUserExpenses(String userId);
    Expense updateExpense(String expenseId, ExpenseDTO expenseDTO);
    void deleteExpense(String expenseId);
}
