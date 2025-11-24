package com.study.service.notification.repository;

import com.study.service.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 읽지 않은 알림 전체 조회
    List<Notification> findByIsReadFalse();
}