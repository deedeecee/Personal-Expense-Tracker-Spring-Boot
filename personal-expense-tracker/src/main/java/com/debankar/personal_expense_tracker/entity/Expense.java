package com.debankar.personal_expense_tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "expenses")
public class Expense {
    @Id
    private ObjectId id;

    private String category;

    private String description;

    private Double amount;

    private LocalDateTime dateTime;

    private ObjectId userId;

    public Expense(String category, String description, Double amount, LocalDateTime dateTime, ObjectId userId) {
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.dateTime = dateTime;
        this.userId = userId;
    }
}
