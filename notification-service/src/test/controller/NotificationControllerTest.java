package com.mentelibre.notification_service.controller;

import com.mentelibre.notification_service.model.Notification;
import com.mentelibre.notification_service.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    void setUp() {
        notification1 = new Notification(1L, "RECORDATORIO", "Mensaje 1", 5L, false, null, null);
        notification2 = new Notification(2L, "ALERTA", "Mensaje 2", 6L, true, null, null);
    }

    @Test
    void testGetAllNotifications() throws Exception {
        when(notificationService.getAllNotifications()).thenReturn(List.of(notification1, notification2));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(notificationService, times(1)).getAllNotifications();
    }

    @Test
    void testGetNotificationsByUser() throws Exception {
        when(notificationService.getNotificationsByUser(5L)).thenReturn(List.of(notification1));

        mockMvc.perform(get("/api/notifications/user/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(5));

        verify(notificationService, times(1)).getNotificationsByUser(5L);
    }

    @Test
    void testGetNotificationByIdFound() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(Optional.of(notification1));

        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("RECORDATORIO"));

        verify(notificationService, times(1)).getNotificationById(1L);
    }

    @Test
    void testGetNotificationByIdNotFound() throws Exception {
        when(notificationService.getNotificationById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notifications/99"))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getNotificationById(99L);
    }

    @Test
    void testCreateNotification() throws Exception {
        Notification toCreate = new Notification(null, "LOGRO", "Nuevo mensaje", 7L, false, null, null);
        Notification created = new Notification(3L, "LOGRO", "Nuevo mensaje", 7L, false, null, null);

        when(notificationService.saveNotification(any(Notification.class))).thenReturn(created);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.type").value("LOGRO"));

        verify(notificationService, times(1)).saveNotification(any(Notification.class));
    }

    @Test
    void testUpdateNotificationFound() throws Exception {
        Notification updated = new Notification(1L, "ALERTA", "Mensaje actualizado", 5L, true, null, null);
        when(notificationService.getNotificationById(1L)).thenReturn(Optional.of(notification1));
        when(notificationService.saveNotification(any(Notification.class))).thenReturn(updated);

        mockMvc.perform(put("/api/notifications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mensaje actualizado"));

        verify(notificationService, times(1)).getNotificationById(1L);
        verify(notificationService, times(1)).saveNotification(any(Notification.class));
    }

    @Test
    void testUpdateNotificationNotFound() throws Exception {
        Notification updated = new Notification(99L, "ALERTA", "Mensaje actualizado", 5L, true, null, null);
        when(notificationService.getNotificationById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/notifications/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getNotificationById(99L);
        verify(notificationService, never()).saveNotification(any(Notification.class));
    }

    @Test
    void testMarkAsSentFound() throws Exception {
        Notification sent = new Notification(1L, "RECORDATORIO", "Mensaje 1", 5L, true, null, null);
        when(notificationService.markAsSent(1L)).thenReturn(Optional.of(sent));

        mockMvc.perform(patch("/api/notifications/1/sent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sent").value(true));

        verify(notificationService, times(1)).markAsSent(1L);
    }

    @Test
    void testMarkAsSentNotFound() throws Exception {
        when(notificationService.markAsSent(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/notifications/99/sent"))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).markAsSent(99L);
    }

    @Test
    void testDeleteNotification() throws Exception {
        doNothing().when(notificationService).deleteNotification(1L);

        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).deleteNotification(1L);
    }
}
