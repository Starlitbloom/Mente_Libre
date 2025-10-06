package com.mentelibre.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.mentelibre.user_service.model.Genero;
import com.mentelibre.user_service.repository.GeneroRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeneroServiceTest {

    @Mock
    private GeneroRepository generoRepository;

    @InjectMocks
    private GeneroService generoService;

    @Test // Caso: verificar si existe género por ID
    void existeGenero_idExiste_devuelveTrue() {
        when(generoRepository.existsById(1L)).thenReturn(true);

        boolean resultado = generoService.existeGenero(1L);

        assertTrue(resultado);
        verify(generoRepository, times(1)).existsById(1L);
    }

    @Test // Caso: verificar si género no existe
    void existeGenero_idNoExiste_devuelveFalse() {
        when(generoRepository.existsById(99L)).thenReturn(false);

        boolean resultado = generoService.existeGenero(99L);

        assertFalse(resultado);
        verify(generoRepository, times(1)).existsById(99L);
    }

    @Test // Caso: crear género válido
    void crearGenero_valido_guardaGenero() {
        Genero genero = new Genero();
        genero.setNombre("No binario");

        when(generoRepository.save(genero)).thenReturn(genero);

        Genero resultado = generoService.crearGenero(genero);

        assertEquals(genero, resultado);
        verify(generoRepository, times(1)).save(genero);
    }

    @Test // Caso: crear género con nombre vacío
    void crearGenero_nombreVacio_lanzaExcepcion() {
        Genero genero = new Genero();
        genero.setNombre("");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            generoService.crearGenero(genero);
        });

        assertEquals("El nombre del género es obligatorio", ex.getMessage());
        verify(generoRepository, never()).save(any());
    }

    @Test // Caso: obtener todos los géneros
    void obtenerTodos_devuelveLista() {
        List<Genero> generos = Arrays.asList(
            new Genero(1L, "Femenino"),
            new Genero(2L, "Masculino")
        );

        when(generoRepository.findAll()).thenReturn(generos);

        List<Genero> resultado = generoService.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("Femenino", resultado.get(0).getNombre());
        verify(generoRepository, times(1)).findAll();
    }
}
