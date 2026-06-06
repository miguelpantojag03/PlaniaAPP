package com.plania.service;

import com.plania.dto.dashboard.DashboardResponse;
import com.plania.dto.recommendation.RecommendationResponse;
import com.plania.dto.task.TaskResponse;
import com.plania.exception.ResourceNotFoundException;
import com.plania.mapper.TaskMapper;
import com.plania.model.Mood;
import com.plania.model.User;
import com.plania.model.enums.MoodType;
import com.plania.model.enums.TaskStatus;
import com.plania.repository.MoodRepository;
import com.plania.repository.TaskRepository;
import com.plania.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final MoodRepository moodRepository;
    private final RecommendationService recommendationService;
    private final TaskMapper taskMapper;

    public DashboardService(
            UserRepository userRepository,
            TaskRepository taskRepository,
            MoodRepository moodRepository,
            RecommendationService recommendationService,
            TaskMapper taskMapper
    ) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.moodRepository = moodRepository;
        this.recommendationService = recommendationService;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public DashboardResponse getTodayDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate today = LocalDate.now();
        MoodType todayMood = moodRepository.findByUserIdAndDate(userId, today)
                .map(Mood::getMoodType)
                .orElse(MoodType.NORMAL);

        long pendingTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING)
                + taskRepository.countByUserIdAndStatus(userId, TaskStatus.POSTPONED);
        LocalDateTime startOfDay = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(today, LocalTime.MAX);
        long completedTasksToday = taskRepository.countByUserIdAndStatusAndCompletedAtBetween(
                userId,
                TaskStatus.COMPLETED,
                startOfDay,
                endOfDay
        );
        long completedTasksTotal = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);

        List<TaskResponse> todayTasks = taskRepository.findByUserIdAndDueDateOrderByDueTimeAsc(userId, today)
                .stream()
                .map(taskMapper::toResponse)
                .toList();

        RecommendationResponse recommendedTask = recommendationService.getTodayRecommendations(userId)
                .stream()
                .findFirst()
                .orElse(null);

        String greeting = "Hola, " + user.getName() + ". Hoy tienes " + pendingTasks + " tareas pendientes.";

        return new DashboardResponse(
                greeting,
                today,
                todayMood,
                pendingTasks,
                completedTasksToday,
                completedTasksTotal,
                user.getTotalPoints(),
                user.getCurrentStreak(),
                recommendedTask,
                todayTasks,
                motivationalMessage(pendingTasks, completedTasksToday, todayMood)
        );
    }

    private String motivationalMessage(long pendingTasks, long completedTasksToday, MoodType moodType) {
        if (pendingTasks == 0) {
            return "Todo despejado por hoy. Buen trabajo.";
        }

        if (completedTasksToday > 0) {
            return "Ya empezaste el dia con progreso. Sigue con una tarea manejable.";
        }

        return switch (moodType) {
            case ENERGETIC -> "Aprovecha tu energia: empieza por una tarea importante.";
            case TIRED -> "Ve paso a paso. Una tarea pequena tambien cuenta.";
            case STRESSED -> "Respira, elige una prioridad y avanza sin intentar hacerlo todo a la vez.";
            case UNMOTIVATED -> "Empieza con algo corto. El impulso llega despues del primer paso.";
            case NORMAL -> "Elige una tarea y dale un inicio sencillo.";
        };
    }
}
