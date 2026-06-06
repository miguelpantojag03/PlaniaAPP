package com.plania.controller;

import com.plania.dto.recommendation.RecommendationResponse;
import com.plania.security.CustomUserDetails;
import com.plania.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/today")
    public ResponseEntity<List<RecommendationResponse>> getTodayRecommendations(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(recommendationService.getTodayRecommendations(currentUser.getId()));
    }

    @GetMapping("/best-task")
    public ResponseEntity<RecommendationResponse> getBestTask(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(recommendationService.getBestTask(currentUser.getId()));
    }
}
