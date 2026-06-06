package com.plania.service;

import com.plania.dto.task.TaskRequest;
import com.plania.dto.task.TaskResponse;
import com.plania.dto.task.TaskUpdateRequest;
import com.plania.exception.BadRequestException;
import com.plania.exception.ResourceNotFoundException;
import com.plania.mapper.TaskMapper;
import com.plania.model.Category;
import com.plania.model.Task;
import com.plania.model.User;
import com.plania.model.enums.Priority;
import com.plania.model.enums.TaskStatus;
import com.plania.repository.CategoryRepository;
import com.plania.repository.TaskRepository;
import com.plania.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaskMapper taskMapper;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            TaskMapper taskMapper
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks(Long userId) {
        ensureUserExists(userId);
        return taskRepository.findByUserIdOrderByDueDateAscDueTimeAsc(userId)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTodayTasks(Long userId) {
        ensureUserExists(userId);
        return taskRepository.findByUserIdAndDueDateOrderByDueTimeAsc(userId, LocalDate.now())
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getPendingTasks(Long userId) {
        ensureUserExists(userId);
        return taskRepository.findByUserIdAndStatusOrderByDueDateAscDueTimeAsc(userId, TaskStatus.PENDING)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getCompletedTasks(Long userId) {
        ensureUserExists(userId);
        return taskRepository.findByUserIdAndStatusOrderByDueDateAscDueTimeAsc(userId, TaskStatus.COMPLETED)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id, Long userId) {
        return taskMapper.toResponse(findTaskForUser(id, userId));
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request, Long userId) {
        User user = findUser(userId);
        Category category = findCategoryForUser(request.categoryId(), userId);
        Task task = taskMapper.toEntity(request, user, category);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request, Long userId) {
        Task task = findTaskForUser(id, userId);
        Category category = findCategoryForUser(request.categoryId(), userId);
        boolean wasCompleted = task.getStatus() == TaskStatus.COMPLETED;
        taskMapper.updateEntity(task, request, category);

        if (request.status() == TaskStatus.COMPLETED && !wasCompleted) {
            task.setCompletedAt(LocalDateTime.now());
            addPointsForCompletedTask(task.getUser(), task.getPriority());
        } else if (request.status() == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        }

        if (request.status() != TaskStatus.COMPLETED) {
            task.setCompletedAt(null);
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id, Long userId) {
        Task task = findTaskForUser(id, userId);
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse completeTask(Long id, Long userId) {
        Task task = findTaskForUser(id, userId);

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new BadRequestException("Task is already completed");
        }

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        addPointsForCompletedTask(task.getUser(), task.getPriority());

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse postponeTask(Long id, Long userId) {
        Task task = findTaskForUser(id, userId);

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new BadRequestException("Completed tasks cannot be postponed");
        }

        task.setStatus(TaskStatus.POSTPONED);
        task.setDueDate(task.getDueDate().plusDays(1));
        task.setPostponedCount(task.getPostponedCount() + 1);

        return taskMapper.toResponse(taskRepository.save(task));
    }

    private Task findTaskForUser(Long id, Long userId) {
        ensureUserExists(userId);
        return taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
    }

    private Category findCategoryForUser(Long categoryId, Long userId) {
        if (categoryId == null) {
            return null;
        }

        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private void addPointsForCompletedTask(User user, Priority priority) {
        int points = switch (priority) {
            case LOW -> 5;
            case MEDIUM -> 10;
            case HIGH -> 20;
            case URGENT -> 30;
        };

        user.setTotalPoints(user.getTotalPoints() + points);
        updateUserStreak(user);
        userRepository.save(user);
    }

    private void updateUserStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastActivityDate = user.getLastActivityDate();

        if (lastActivityDate == null) {
            user.setCurrentStreak(1);
        } else if (lastActivityDate.isEqual(today)) {
            user.setCurrentStreak(Math.max(user.getCurrentStreak(), 1));
        } else if (lastActivityDate.isEqual(today.minusDays(1))) {
            user.setCurrentStreak(user.getCurrentStreak() + 1);
            user.setTotalPoints(user.getTotalPoints() + 10);
        } else {
            user.setCurrentStreak(1);
        }

        user.setLastActivityDate(today);
    }
}
