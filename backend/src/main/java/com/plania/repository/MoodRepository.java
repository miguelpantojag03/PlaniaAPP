package com.plania.repository;

import com.plania.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MoodRepository extends JpaRepository<Mood, Long> {

    Optional<Mood> findByUserIdAndDate(Long userId, LocalDate date);

    List<Mood> findByUserIdOrderByDateDesc(Long userId);
}
