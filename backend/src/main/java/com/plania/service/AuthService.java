package com.plania.service;

import com.plania.dto.auth.AuthResponse;
import com.plania.dto.auth.LoginRequest;
import com.plania.dto.auth.RegisterRequest;
import com.plania.exception.BadRequestException;
import com.plania.mapper.UserMapper;
import com.plania.model.Category;
import com.plania.model.User;
import com.plania.repository.CategoryRepository;
import com.plania.repository.UserRepository;
import com.plania.security.CustomUserDetails;
import com.plania.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthService(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User(
                request.name().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.password())
        );
        User savedUser = userRepository.save(user);
        createInitialCategories(savedUser);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, "Bearer", userMapper.toResponse(savedUser));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
            );
        } catch (BadCredentialsException exception) {
            throw new BadRequestException("Invalid email or password");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, "Bearer", userMapper.toResponse(user));
    }

    private void createInitialCategories(User user) {
        List<Category> categories = List.of(
                new Category("Universidad", "#6366F1", user),
                new Category("Trabajo", "#14B8A6", user),
                new Category("Personal", "#F59E0B", user),
                new Category("Salud", "#22C55E", user),
                new Category("Finanzas", "#0EA5E9", user),
                new Category("Hogar", "#A855F7", user),
                new Category("Otro", "#64748B", user)
        );
        categoryRepository.saveAll(categories);
    }
}
