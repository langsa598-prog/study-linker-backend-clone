package com.study.service.notification.controller;

import com.study.service.notification.dto.NotificationRequest;
import com.study.service.notification.dto.NotificationResponse;
import com.study.service.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // GET /api/notifications - 알림 목록 조회
    @GetMapping
    public List<NotificationResponse> getAll() {
        return service.findAllResponses();
    }

    // GET /api/notifications/unread - 읽지 않은 알림 조회
    @GetMapping("/unread")
    public List<NotificationResponse> getUnread() {
        return service.findUnreadResponses();
    }

    // POST /api/notifications - 알림 생성
    @PostMapping
    public NotificationResponse create(@RequestBody NotificationRequest request) {
        return service.save(request);
    }

    // PATCH /api/notifications/{id}/read - 알림 읽음 처리
    @PatchMapping("/{id}/read")
    public NotificationResponse markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    // DELETE /api/notifications/{id} - 알림 단건 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    // DELETE /api/notifications/all - 알림 전체 삭제
    @DeleteMapping("/all")
    public void deleteAll() {
        service.deleteAll();
    }
}