package com.debankar.personal_expense_tracker.mapper;

import com.debankar.personal_expense_tracker.dto.UserCreationDTO;
import com.debankar.personal_expense_tracker.dto.UserDTO;
import com.debankar.personal_expense_tracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toUserDTO(User user) {
        String id = user.getId().toString();
        String name = user.getName();
        String email = user.getEmail();

        return new UserDTO(id, name, email);
    }

    public User toUser(UserCreationDTO userDTO) {
        return new User(
                userDTO.getName(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getRole()
        );
    }
}
