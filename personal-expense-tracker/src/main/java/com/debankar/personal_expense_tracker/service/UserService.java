package com.debankar.personal_expense_tracker.service;

import com.debankar.personal_expense_tracker.dto.UserCreationDTO;
import com.debankar.personal_expense_tracker.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(UserCreationDTO userDTO);
    String authenticate(String email, String password);
    User findByUserId(String userId);
    User findByEmail(String email);
    List<User> findAll();
}
