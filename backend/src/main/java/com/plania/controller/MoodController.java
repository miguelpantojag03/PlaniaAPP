package com.plania.controller;

import com.plania.dto.mood.MoodRequest;
import com.plania.dto.mood.MoodResponse;
import com.plania.security.CustomUserDetails;
import com.plania.service.MoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/moods")
public class MoodController {

    private final MoodService moodService;

    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }

    @PostMapping
    public ResponseEntity<MoodResponse> saveMood(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody MoodRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(moodService.saveMood(request, currentUser.getId()));
    }

    @GetMapping("/today")
    public ResponseEntity<MoodResponse> getTodayMood(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(moodService.getTodayMood(currentUser.getId()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<MoodResponse>> getMoodHistory(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(moodService.getMoodHistory(currentUser.getId()));
    }
}
