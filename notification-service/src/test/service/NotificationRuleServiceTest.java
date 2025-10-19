package com.mentelibre.notification_service.service;

import com.mentelibre.notification_service.model.NotificationRule;
import com.mentelibre.notification_service.repository.NotificationRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationRuleServiceTest {

    private NotificationRuleRepository ruleRepository;
    private NotificationRuleService ruleService;

    @BeforeEach
    void setUp() {
        ruleRepository = mock(NotificationRuleRepository.class);
        ruleService = new NotificationRuleService(ruleRepository);
    }

    @Test
    void testGetAllRules() {
        NotificationRule r1 = new NotificationRule(1L, "RECORDATORIO", "DIARIA", true, null, null);
        NotificationRule r2 = new NotificationRule(2L, "ALERTA", "INMEDIATA", true, null, null);
        when(ruleRepository.findAll()).thenReturn(List.of(r1, r2));

        List<NotificationRule> result = ruleService.getAllRules();
        assertEquals(2, result.size());
        verify(ruleRepository, times(1)).findAll();
    }

    @Test
    void testGetRuleByIdFound() {
        NotificationRule r = new NotificationRule(1L, "RECORDATORIO", "DIARIA", true, null, null);
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(r));

        Optional<NotificationRule> result = ruleService.getRuleById(1L);
        assertTrue(result.isPresent());
        assertEquals("RECORDATORIO", result.get().getType());
        verify(ruleRepository, times(1)).findById(1L);
    }

    @Test
    void testGetRuleByIdNotFound() {
        when(ruleRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<NotificationRule> result = ruleService.getRuleById(1L);
        assertFalse(result.isPresent());
        verify(ruleRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveRule() {
        NotificationRule r = new NotificationRule(null, "LOGRO", "SEMANAL", true, null, null);
        NotificationRule saved = new NotificationRule(1L, "LOGRO", "SEMANAL", true, null, null);
        when(ruleRepository.save(r)).thenReturn(saved);

        NotificationRule result = ruleService.saveRule(r);
        assertNotNull(result.getId());
        assertEquals("LOGRO", result.getType());
        verify(ruleRepository, times(1)).save(r);
    }

    @Test
    void testSetRuleActiveStatus() {
        NotificationRule r = new NotificationRule(1L, "ALERTA", "INMEDIATA", false, null, null);
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(r));
        when(ruleRepository.save(r)).thenReturn(r);

        Optional<NotificationRule> result = ruleService.setRuleActiveStatus(1L, true);
        assertTrue(result.isPresent());
        assertTrue(result.get().isActive());
        verify(ruleRepository, times(1)).findById(1L);
        verify(ruleRepository, times(1)).save(r);
    }

    @Test
    void testDeleteRule() {
        ruleService.deleteRule(1L);
        verify(ruleRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetActiveRules() {
        NotificationRule r1 = new NotificationRule(1L, "RECORDATORIO", "DIARIA", true, null, null);
        NotificationRule r2 = new NotificationRule(2L, "ALERTA", "SEMANAL", false, null, null);
        when(ruleRepository.findAll()).thenReturn(List.of(r1, r2));

        List<NotificationRule> result = ruleService.getActiveRules();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(ruleRepository, times(1)).findAll();
    }

    @Test
    void testGetRulesByType() {
        NotificationRule r1 = new NotificationRule(1L, "RECORDATORIO", "DIARIA", true, null, null);
        NotificationRule r2 = new NotificationRule(2L, "ALERTA", "SEMANAL", true, null, null);
        when(ruleRepository.findAll()).thenReturn(List.of(r1, r2));

        List<NotificationRule> result = ruleService.getRulesByType("RECORDATORIO");
        assertEquals(1, result.size());
        assertEquals("RECORDATORIO", result.get(0).getType());
        verify(ruleRepository, times(1)).findAll();
    }
}
