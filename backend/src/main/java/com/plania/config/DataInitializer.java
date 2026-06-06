package com.plania.config;

import com.plania.model.Category;
import com.plania.model.User;
import com.plania.repository.CategoryRepository;
import com.plania.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("dev")
    CommandLineRunner createDemoUser(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.existsByEmail("demo@plania.com")) {
                return;
            }

            User demoUser = new User(
                    "Usuario Demo",
                    "demo@plania.com",
                    passwordEncoder.encode("demo12345")
            );
            User savedUser = userRepository.save(demoUser);

            List<Category> categories = List.of(
                    new Category("Universidad", "#6366F1", savedUser),
                    new Category("Trabajo", "#14B8A6", savedUser),
                    new Category("Personal", "#F59E0B", savedUser),
                    new Category("Salud", "#22C55E", savedUser),
                    new Category("Finanzas", "#0EA5E9", savedUser),
                    new Category("Hogar", "#A855F7", savedUser),
                    new Category("Otro", "#64748B", savedUser)
            );

            categoryRepository.saveAll(categories);
        };
    }
}
