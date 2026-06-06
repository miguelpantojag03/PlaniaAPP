package com.plania.repository;

import com.plania.model.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubTaskRepository extends JpaRepository<SubTask, Long> {

    List<SubTask> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    Optional<SubTask> findByIdAndTaskUserId(Long id, Long userId);
}
