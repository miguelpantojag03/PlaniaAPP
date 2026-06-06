package com.plania.service;

import com.plania.dto.recommendation.RecommendationResponse;
import com.plania.exception.ResourceNotFoundException;
import com.plania.mapper.TaskMapper;
import com.plania.model.Mood;
import com.plania.model.Task;
import com.plania.model.enums.EnergyRequired;
import com.plania.model.enums.MoodType;
import com.plania.model.enums.Priority;
import com.plania.model.enums.TaskStatus;
import com.plania.repository.MoodRepository;
import com.plania.repository.TaskRepository;
import com.plania.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {

    private final TaskRepository taskRepository;
    private final MoodRepository moodRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public RecommendationService(
            TaskRepository taskRepository,
            MoodRepository moodRepository,
            UserRepository userRepository,
            TaskMapper taskMapper
    ) {
        this.taskRepository = taskRepository;
        this.moodRepository = moodRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public List<RecommendationResponse> getTodayRecommendations(Long userId) {
        ensureUserExists(userId);
        MoodType moodType = getTodayMoodType(userId);

        return findActiveTasks(userId)
                .stream()
                .map(task -> scoreTask(task, moodType))
                .sorted(Comparator.comparing(RecommendationResponse::smartScore).reversed())
                .toList();
    }

    @Transactional
    public RecommendationResponse getBestTask(Long userId) {
        return getTodayRecommendations(userId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No pending tasks available for recommendation"));
    }

    private List<Task> findActiveTasks(Long userId) {
        return taskRepository.findByUserIdAndStatusInOrderByDueDateAscDueTimeAsc(
                userId,
                List.of(TaskStatus.PENDING, TaskStatus.POSTPONED)
        );
    }

    private RecommendationResponse scoreTask(Task task, MoodType moodType) {
        ScoreResult scoreResult = calculateSmartScore(task, moodType);
        task.setSmartScore(scoreResult.score());
        Task savedTask = taskRepository.save(task);

        return new RecommendationResponse(
                taskMapper.toResponse(savedTask),
                scoreResult.score(),
                moodType,
                scoreResult.reason(),
                LocalDateTime.now()
        );
    }

    private ScoreResult calculateSmartScore(Task task, MoodType moodType) {
        int score = 0;
        StringBuilder reason = new StringBuilder("Te recomendamos esta tarea porque ");
        boolean hasReason = false;

        LocalDate today = LocalDate.now();
        long daysUntilDue = ChronoUnit.DAYS.between(today, task.getDueDate());

        if (daysUntilDue < 0) {
            score += 50;
            reason.append("esta vencida");
            hasReason = true;
        } else if (daysUntilDue == 0) {
            score += 40;
            reason.append("vence hoy");
            hasReason = true;
        } else if (daysUntilDue == 1) {
            score += 30;
            reason.append("vence manana");
            hasReason = true;
        } else if (daysUntilDue < 3) {
            score += 20;
            reason.append("vence en menos de 3 dias");
            hasReason = true;
        }

        int priorityPoints = priorityPoints(task.getPriority());
        score += priorityPoints;
        reason = appendReason(reason, hasReason, "tiene prioridad " + task.getPriority().name().toLowerCase());
        hasReason = true;

        if (task.getPostponedCount() > 2) {
            score += 15;
            reason = appendReason(reason, hasReason, "ha sido aplazada varias veces");
            hasReason = true;
        }

        int moodEnergyPoints = moodEnergyPoints(moodType, task.getEnergyRequired());
        score += moodEnergyPoints;
        if (moodEnergyPoints > 0) {
            reason = appendReason(reason, hasReason, "su energia requerida encaja con tu estado de animo");
            hasReason = true;
        } else if (moodEnergyPoints < 0) {
            reason = appendReason(reason, hasReason, "requiere mas energia de la ideal para tu estado actual");
            hasReason = true;
        }

        if (task.getEstimatedMinutes() < 30) {
            score += 5;
            reason = appendReason(reason, hasReason, "puede completarse rapido");
            hasReason = true;
        }

        if (task.getEstimatedMinutes() > 120 && moodType == MoodType.TIRED) {
            score -= 10;
            reason = appendReason(reason, hasReason, "es larga para un dia de baja energia");
        }

        reason.append(".");
        return new ScoreResult(score, reason.toString());
    }

    private int priorityPoints(Priority priority) {
        return switch (priority) {
            case URGENT -> 40;
            case HIGH -> 30;
            case MEDIUM -> 15;
            case LOW -> 5;
        };
    }

    private int moodEnergyPoints(MoodType moodType, EnergyRequired energyRequired) {
        if ((moodType == MoodType.TIRED || moodType == MoodType.STRESSED || moodType == MoodType.UNMOTIVATED)
                && energyRequired == EnergyRequired.LOW) {
            return 10;
        }

        if ((moodType == MoodType.TIRED || moodType == MoodType.STRESSED)
                && energyRequired == EnergyRequired.HIGH) {
            return -10;
        }

        if (moodType == MoodType.ENERGETIC && energyRequired == EnergyRequired.HIGH) {
            return 10;
        }

        return 0;
    }

    private MoodType getTodayMoodType(Long userId) {
        return moodRepository.findByUserIdAndDate(userId, LocalDate.now())
                .map(Mood::getMoodType)
                .orElse(MoodType.NORMAL);
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
    }

    private StringBuilder appendReason(StringBuilder reason, boolean hasReason, String text) {
        if (hasReason) {
            reason.append(", ");
        }
        reason.append(text);
        return reason;
    }

    private record ScoreResult(Integer score, String reason) {
    }
}
