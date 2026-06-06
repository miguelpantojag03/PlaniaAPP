package com.plania.service;

import com.plania.dto.mood.MoodRequest;
import com.plania.dto.mood.MoodResponse;
import com.plania.exception.BadRequestException;
import com.plania.exception.ResourceNotFoundException;
import com.plania.mapper.MoodMapper;
import com.plania.model.Mood;
import com.plania.model.User;
import com.plania.repository.MoodRepository;
import com.plania.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MoodService {

    private final MoodRepository moodRepository;
    private final UserRepository userRepository;
    private final MoodMapper moodMapper;

    public MoodService(MoodRepository moodRepository, UserRepository userRepository, MoodMapper moodMapper) {
        this.moodRepository = moodRepository;
        this.userRepository = userRepository;
        this.moodMapper = moodMapper;
    }

    @Transactional
    public MoodResponse saveMood(MoodRequest request, Long userId) {
        User user = findUser(userId);
        LocalDate moodDate = request.date() != null ? request.date() : LocalDate.now();

        if (moodDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("Mood date cannot be in the future");
        }

        Mood mood = moodRepository.findByUserIdAndDate(userId, moodDate)
                .orElseGet(() -> new Mood(request.moodType(), moodDate, user));

        mood.setMoodType(request.moodType());
        mood.setNote(request.note());
        mood.setDate(moodDate);
        mood.setUser(user);

        return moodMapper.toResponse(moodRepository.save(mood));
    }

    @Transactional(readOnly = true)
    public MoodResponse getTodayMood(Long userId) {
        ensureUserExists(userId);
        return moodRepository.findByUserIdAndDate(userId, LocalDate.now())
                .map(moodMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Mood for today not found"));
    }

    @Transactional(readOnly = true)
    public List<MoodResponse> getMoodHistory(Long userId) {
        ensureUserExists(userId);
        return moodRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(moodMapper::toResponse)
                .toList();
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
}
