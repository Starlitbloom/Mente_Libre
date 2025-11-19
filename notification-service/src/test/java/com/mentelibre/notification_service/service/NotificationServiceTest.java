package com.mentelibre.notification_service.service;

import com.mentelibre.notification_service.model.Notification;
import com.mentelibre.notification_service.repository.NotificationRepository;
import com.mentelibre.notification_service.repository.NotificationRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private NotificationRuleRepository notificationRuleRepository;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        notificationRuleRepository = mock(NotificationRuleRepository.class);
        notificationService = new NotificationService(notificationRepository, notificationRuleRepository);
    }

    @Test
    void testGetAllNotifications() {
        Notification n1 = new Notification(1L, "RECORDATORIO", "Mensaje 1", 1L, false, null, null);
        Notification n2 = new Notification(2L, "ALERTA", "Mensaje 2", 2L, false, null, null);
        when(notificationRepository.findAll()).thenReturn(List.of(n1, n2));

        List<Notification> result = notificationService.getAllNotifications();
        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void testGetNotificationsByUser() {
        Notification n = new Notification(1L, "RECORDATORIO", "Mensaje usuario", 10L, false, null, null);
        when(notificationRepository.findByUserIdAndSentFalse(10L)).thenReturn(List.of(n));

        List<Notification> result = notificationService.getNotificationsByUser(10L);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getUserId());
        verify(notificationRepository, times(1)).findByUserIdAndSentFalse(10L);
    }

    @Test
    void testGetNotificationByIdFound() {
        Notification n = new Notification(1L, "ALERTA", "Mensaje", 5L, false, null, null);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        Optional<Notification> result = notificationService.getNotificationById(1L);
        assertTrue(result.isPresent());
        assertEquals("ALERTA", result.get().getType());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetNotificationByIdNotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Notification> result = notificationService.getNotificationById(1L);
        assertFalse(result.isPresent());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveNotificationNew() {
        Notification n = new Notification(null, "RECORDATORIO", "Nuevo mensaje", 3L, false, null, null);
        Notification saved = new Notification(1L, "RECORDATORIO", "Nuevo mensaje", 3L, false, null, null);
        when(notificationRepository.save(n)).thenReturn(saved);

        Notification result = notificationService.saveNotification(n);
        assertNotNull(result.getId());
        assertFalse(result.isSent());
        verify(notificationRepository, times(1)).save(n);
    }

    @Test
    void testMarkAsSent() {
        Notification n = new Notification(1L, "RECORDATORIO", "Mensaje", 2L, false, null, null);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));
        when(notificationRepository.save(n)).thenReturn(n);

        Optional<Notification> result = notificationService.markAsSent(1L);
        assertTrue(result.isPresent());
        assertTrue(result.get().isSent());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(n);
    }

    @Test
    void testDeleteNotification() {
        notificationService.deleteNotification(1L);
        verify(notificationRepository, times(1)).deleteById(1L);
    }
}
