package com.plania.service;

import com.plania.dto.user.UserResponse;
import com.plania.dto.user.UserUpdateRequest;
import com.plania.exception.BadRequestException;
import com.plania.exception.ResourceNotFoundException;
import com.plania.mapper.UserMapper;
import com.plania.model.User;
import com.plania.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        return userMapper.toResponse(findUser(userId));
    }

    @Transactional
    public UserResponse updateCurrentUser(Long userId, UserUpdateRequest request) {
        User user = findUser(userId);
        String normalizedEmail = request.email().trim().toLowerCase();

        userRepository.findByEmail(normalizedEmail)
                .filter(existingUser -> !existingUser.getId().equals(userId))
                .ifPresent(existingUser -> {
                    throw new BadRequestException("Email is already registered");
                });

        user.setName(request.name().trim());
        user.setEmail(normalizedEmail);
        return userMapper.toResponse(userRepository.save(user));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
