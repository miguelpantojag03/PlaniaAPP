package com.plania.controller;

import com.plania.dto.task.TaskRequest;
import com.plania.dto.task.TaskResponse;
import com.plania.dto.task.TaskUpdateRequest;
import com.plania.security.CustomUserDetails;
import com.plania.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(taskService.getAllTasks(currentUser.getId()));
    }

    @GetMapping("/today")
    public ResponseEntity<List<TaskResponse>> getTodayTasks(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(taskService.getTodayTasks(currentUser.getId()));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TaskResponse>> getPendingTasks(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(taskService.getPendingTasks(currentUser.getId()));
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TaskResponse>> getCompletedTasks(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(taskService.getCompletedTasks(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(taskService.getTaskById(id, currentUser.getId()));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request, currentUser.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(taskService.updateTask(id, request, currentUser.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        taskService.deleteTask(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(taskService.completeTask(id, currentUser.getId()));
    }

    @PatchMapping("/{id}/postpone")
    public ResponseEntity<TaskResponse> postponeTask(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(taskService.postponeTask(id, currentUser.getId()));
    }
}
