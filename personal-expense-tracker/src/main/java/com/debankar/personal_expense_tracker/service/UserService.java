package com.debankar.personal_expense_tracker.service;

import com.debankar.personal_expense_tracker.dto.UserCreationDTO;
import com.debankar.personal_expense_tracker.entity.User;

public interface UserService {
    User registerUser(UserCreationDTO userDTO);
    User authenticate(String email, String password);
    User findByUserId(String userId);
}