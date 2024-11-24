package com.debankar.personal_expense_tracker.service;

import com.debankar.personal_expense_tracker.dto.UserCreationDTO;
import com.debankar.personal_expense_tracker.entity.User;
import com.debankar.personal_expense_tracker.mapper.UserMapper;
import com.debankar.personal_expense_tracker.repository.UserRepository;
import com.debankar.personal_expense_tracker.security.JwtTokenProvider;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User registerUser(UserCreationDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }

        User user = userMapper.toUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);

        return user;
    }

    @Override
    public String authenticate(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            throw new IllegalArgumentException("Invalid email or password!");
        }

        // Generate JWT token
        User user = userOptional.get();
        return jwtTokenProvider.generateToken(user.getEmail());
    }

    @Override
    public User findByUserId(String userId) {
        return userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
