package com.plania.mapper;

import com.plania.dto.task.TaskRequest;
import com.plania.dto.task.TaskResponse;
import com.plania.dto.task.TaskUpdateRequest;
import com.plania.model.Category;
import com.plania.model.Task;
import com.plania.model.User;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request, User user, Category category) {
        Task task = new Task(
                request.title(),
                request.dueDate(),
                request.priority(),
                request.energyRequired(),
                request.estimatedMinutes(),
                user
        );
        task.setDescription(request.description());
        task.setDueTime(request.dueTime());
        task.setCategory(category);
        return task;
    }

    public void updateEntity(Task task, TaskUpdateRequest request, Category category) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setDueTime(request.dueTime());
        task.setPriority(request.priority());
        task.setEnergyRequired(request.energyRequired());
        task.setEstimatedMinutes(request.estimatedMinutes());
        task.setStatus(request.status());
        task.setCategory(category);
    }

    public TaskResponse toResponse(Task task) {
        Category category = task.getCategory();

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getDueTime(),
                task.getPriority(),
                task.getEnergyRequired(),
                task.getEstimatedMinutes(),
                task.getStatus(),
                category != null ? category.getName() : null,
                category != null ? category.getColor() : null,
                task.getPostponedCount(),
                task.getSmartScore(),
                task.getPostponedCount() >= 3,
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getCompletedAt()
        );
    }
}
