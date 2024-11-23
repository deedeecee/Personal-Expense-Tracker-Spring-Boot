package com.debankar.personal_expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseDTO {

    private String id;

    private String category;

    private String description;

    private Double amount;

    private LocalDateTime dateTime;

    private String userId;
}