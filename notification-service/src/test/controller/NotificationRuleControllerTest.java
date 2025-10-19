package com.mentelibre.notification_service.controller;

import com.mentelibre.notification_service.model.NotificationRule;
import com.mentelibre.notification_service.service.NotificationRuleService;
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

@WebMvcTest(NotificationRuleController.class)
class NotificationRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationRuleService ruleService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationRule rule1;
    private NotificationRule rule2;

    @BeforeEach
    void setUp() {
        rule1 = new NotificationRule(1L, "RECORDATORIO", "DIARIA", true, null, null);
        rule2 = new NotificationRule(2L, "ALERTA", "INMEDIATA", false, null, null);
    }

    @Test
    void testGetAllRules() throws Exception {
        when(ruleService.getAllRules()).thenReturn(List.of(rule1, rule2));

        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(ruleService, times(1)).getAllRules();
    }

    @Test
    void testGetRuleByIdFound() throws Exception {
        when(ruleService.getRuleById(1L)).thenReturn(Optional.of(rule1));

        mockMvc.perform(get("/api/rules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("RECORDATORIO"));

        verify(ruleService, times(1)).getRuleById(1L);
    }

    @Test
    void testGetRuleByIdNotFound() throws Exception {
        when(ruleService.getRuleById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rules/99"))
                .andExpect(status().isNotFound());

        verify(ruleService, times(1)).getRuleById(99L);
    }

    @Test
    void testCreateRule() throws Exception {
        NotificationRule toCreate = new NotificationRule(null, "LOGRO", "SEMANAL", true, null, null);
        NotificationRule created = new NotificationRule(3L, "LOGRO", "SEMANAL", true, null, null);

        when(ruleService.saveRule(any(NotificationRule.class))).thenReturn(created);

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.type").value("LOGRO"));

        verify(ruleService, times(1)).saveRule(any(NotificationRule.class));
    }

    @Test
    void testUpdateRuleFound() throws Exception {
        NotificationRule updated = new NotificationRule(1L, "ALERTA", "INMEDIATA", false, null, null);

        when(ruleService.getRuleById(1L)).thenReturn(Optional.of(rule1));
        when(ruleService.saveRule(any(NotificationRule.class))).thenReturn(updated);

        mockMvc.perform(put("/api/rules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("ALERTA"));

        verify(ruleService, times(1)).getRuleById(1L);
        verify(ruleService, times(1)).saveRule(any(NotificationRule.class));
    }

    @Test
    void testUpdateRuleNotFound() throws Exception {
        NotificationRule updated = new NotificationRule(99L, "ALERTA", "INMEDIATA", false, null, null);

        when(ruleService.getRuleById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/rules/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());

        verify(ruleService, times(1)).getRuleById(99L);
        verify(ruleService, never()).saveRule(any(NotificationRule.class));
    }

    @Test
    void testSetRuleActiveStatusFound() throws Exception {
        NotificationRule updated = new NotificationRule(1L, "RECORDATORIO", "DIARIA", false, null, null);

        when(ruleService.setRuleActiveStatus(1L, false)).thenReturn(Optional.of(updated));

        mockMvc.perform(patch("/api/rules/1/active")
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        verify(ruleService, times(1)).setRuleActiveStatus(1L, false);
    }

    @Test
    void testSetRuleActiveStatusNotFound() throws Exception {
        when(ruleService.setRuleActiveStatus(99L, true)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/rules/99/active")
                        .param("active", "true"))
                .andExpect(status().isNotFound());

        verify(ruleService, times(1)).setRuleActiveStatus(99L, true);
    }

    @Test
    void testDeleteRule() throws Exception {
        doNothing().when(ruleService).deleteRule(1L);

        mockMvc.perform(delete("/api/rules/1"))
                .andExpect(status().isNoContent());

        verify(ruleService, times(1)).deleteRule(1L);
    }
}
