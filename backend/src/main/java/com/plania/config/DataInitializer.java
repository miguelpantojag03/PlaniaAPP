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
            User demoUser = userRepository.findByEmail("demo@plania.com")
                    .orElseGet(() -> new User("Usuario Demo", "demo@plania.com", ""));

            demoUser.setName("Usuario Demo");
            demoUser.setPassword(passwordEncoder.encode("demo12345"));
            User savedUser = userRepository.save(demoUser);

            createCategoryIfMissing(categoryRepository, savedUser, "Universidad", "#6366F1");
            createCategoryIfMissing(categoryRepository, savedUser, "Trabajo", "#14B8A6");
            createCategoryIfMissing(categoryRepository, savedUser, "Personal", "#F59E0B");
            createCategoryIfMissing(categoryRepository, savedUser, "Salud", "#22C55E");
            createCategoryIfMissing(categoryRepository, savedUser, "Finanzas", "#0EA5E9");
            createCategoryIfMissing(categoryRepository, savedUser, "Hogar", "#A855F7");
            createCategoryIfMissing(categoryRepository, savedUser, "Otro", "#64748B");
        };
    }

    private void createCategoryIfMissing(CategoryRepository categoryRepository, User user, String name, String color) {
        if (!categoryRepository.existsByNameIgnoreCaseAndUserId(name, user.getId())) {
            categoryRepository.save(new Category(name, color, user));
        }
    }
}
