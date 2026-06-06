package com.plania.controller;

import com.plania.dto.dashboard.DashboardResponse;
import com.plania.security.CustomUserDetails;
import com.plania.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(dashboardService.getTodayDashboard(currentUser.getId()));
    }

    @GetMapping("/today")
    public ResponseEntity<DashboardResponse> getTodayDashboard(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(dashboardService.getTodayDashboard(currentUser.getId()));
    }
}
