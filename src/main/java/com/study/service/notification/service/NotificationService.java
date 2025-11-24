package com.study.service.notification.service;

import com.study.service.notification.domain.Notification;
import com.study.service.notification.dto.NotificationRequest;
import com.study.service.notification.dto.NotificationResponse;
import com.study.service.notification.repository.NotificationRepository;
import com.study.service.user.domain.User;
import com.study.service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // 알림 전체 조회
    public List<NotificationResponse> findAllResponses() {
        return notificationRepository.findAll().stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    // 읽지 않은 알림 조회
    public List<NotificationResponse> findUnreadResponses() {
        return notificationRepository.findByIsReadFalse().stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    // 알림 생성
    @Transactional
    public NotificationResponse save(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + request.getUserId()));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(request.getMessage());
        notification.setType(
                Notification.Type.valueOf(request.getType().toUpperCase())
        );
        notification.setIsRead(false); // 생성 시 기본값: 읽지 않음

        Notification saved = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(saved);
    }

    // 알림 읽음 처리 (단건)
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("알림을 찾을 수 없습니다. ID: " + id));

        notification.setIsRead(true);

        return NotificationResponse.fromEntity(notification);
    }

    // 알림 단건 삭제
    @Transactional
    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }

    // 알림 전체 삭제
    @Transactional
    public void deleteAll() {
        notificationRepository.deleteAll();
    }
}