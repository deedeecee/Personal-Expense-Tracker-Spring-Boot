package com.debankar.personal_expense_tracker.repository;

import com.debankar.personal_expense_tracker.entity.Expense;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends MongoRepository<Expense, ObjectId> {
    List<Expense> findByUserId(ObjectId userId);
//    long countByUserId(ObjectId userId);
    List<Expense> findByUserIdAndDateTimeBetween(ObjectId userId, LocalDateTime startDate, LocalDateTime endDate);
}
