package com.mentelibre.notification_service.service;

import com.mentelibre.notification_service.model.Notification;
import com.mentelibre.notification_service.model.NotificationRule;
import com.mentelibre.notification_service.repository.NotificationRepository;
import com.mentelibre.notification_service.repository.NotificationRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRuleRepository notificationRuleRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationRuleRepository notificationRuleRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationRuleRepository = notificationRuleRepository;
    }

    // ------------------- Notificaciones -------------------

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdAndSentFalse(userId);
    }

    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public Notification saveNotification(Notification notification) {
        if (notification.getId() == null) {
            notification.setSent(false); // siempre se crea como no enviada
        }
        return notificationRepository.save(notification);
    }

    public Optional<Notification> markAsSent(Long id) {
        return notificationRepository.findById(id)
                .map(n -> {
                    n.setSent(true);
                    return notificationRepository.save(n);
                });
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    // ------------------- Reglas de Notificación -------------------

    public List<NotificationRule> getAllRules() {
        return notificationRuleRepository.findAll();
    }

    public Optional<NotificationRule> getRuleById(Long id) {
        return notificationRuleRepository.findById(id);
    }

    public NotificationRule saveRule(NotificationRule rule) {
        return notificationRuleRepository.save(rule);
    }

    public Optional<NotificationRule> setRuleActiveStatus(Long id, boolean active) {
        return notificationRuleRepository.findById(id)
                .map(r -> {
                    r.setActive(active);
                    return notificationRuleRepository.save(r);
                });
    }

    public void deleteRule(Long id) {
        notificationRuleRepository.deleteById(id);
    }
}
