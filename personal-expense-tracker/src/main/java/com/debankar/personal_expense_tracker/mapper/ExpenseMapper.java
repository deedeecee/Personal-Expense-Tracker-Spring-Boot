package com.debankar.personal_expense_tracker.mapper;

import com.debankar.personal_expense_tracker.dto.ExpenseCreationDTO;
import com.debankar.personal_expense_tracker.dto.ExpenseDTO;
import com.debankar.personal_expense_tracker.entity.Expense;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExpenseMapper {
    public ExpenseDTO toExpenseDTO(Expense expense) {
        String id = expense.getId().toString();
        String category = expense.getCategory();
        String description = expense.getDescription();
        Double amount = expense.getAmount();
        LocalDateTime dateTime = expense.getDateTime();
        String userId = expense.getUserId().toString();

        return new ExpenseDTO(id, category, description, amount, dateTime, userId);
    }

    public Expense toExpense(ExpenseCreationDTO expenseDTO) {
        return new Expense(
                expenseDTO.getCategory(),
                expenseDTO.getDescription(),
                expenseDTO.getAmount(),
                expenseDTO.getDateTime(),
                new ObjectId(expenseDTO.getUserId())
        );
    }
}
