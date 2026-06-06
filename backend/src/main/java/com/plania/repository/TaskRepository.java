package com.plania.repository;

import com.plania.model.Task;
import com.plania.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdOrderByDueDateAscDueTimeAsc(Long userId);

    List<Task> findByUserIdAndDueDateOrderByDueTimeAsc(Long userId, LocalDate dueDate);

    List<Task> findByUserIdAndStatusOrderByDueDateAscDueTimeAsc(Long userId, TaskStatus status);

    List<Task> findByUserIdAndStatusInOrderByDueDateAscDueTimeAsc(Long userId, List<TaskStatus> statuses);

    List<Task> findByUserIdAndStatusAndDueDateOrderByDueTimeAsc(Long userId, TaskStatus status, LocalDate dueDate);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndStatus(Long userId, TaskStatus status);

    long countByUserIdAndStatusAndDueDate(Long userId, TaskStatus status, LocalDate dueDate);

    long countByUserIdAndStatusAndCompletedAtBetween(
            Long userId,
            TaskStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
}
