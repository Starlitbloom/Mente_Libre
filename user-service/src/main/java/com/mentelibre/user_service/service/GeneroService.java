package com.mentelibre.user_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mentelibre.user_service.model.Genero;
import com.mentelibre.user_service.repository.GeneroRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class GeneroService {

    @Autowired
    private GeneroRepository generoRepository;

    // Obtener todos los géneros
    public List<Genero> obtenerTodos() {
        return generoRepository.findAll();
    }

    // Obtener género por ID
    public Genero obtenerPorId(Long id) {
        return generoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Género no encontrado. ID: " + id));
    }

    // Crear nuevo género
    public Genero crearGenero(Genero genero) {
        if (genero.getNombre() == null || genero.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del género es obligatorio");
        }

        if (generoRepository.existsByNombre(genero.getNombre())) {
            throw new RuntimeException("El género ya existe");
        }

        return generoRepository.save(genero);
    }

    // Actualizar género
    public Genero actualizarGenero(Long id, Genero nuevosDatos) {
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));

        if (nuevosDatos.getNombre() != null && !nuevosDatos.getNombre().equals(genero.getNombre())) {
            if (generoRepository.existsByNombre(nuevosDatos.getNombre())) {
                throw new RuntimeException("El nombre del género ya está en uso");
            }
            genero.setNombre(nuevosDatos.getNombre());
        }

        return generoRepository.save(genero);
    }

    // Eliminar género
    public String eliminarGenero(Long id) {
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Género no encontrado. ID: " + id));
        generoRepository.delete(genero);
        return "Género eliminado correctamente";
    }

    // Validar existencia de un género por ID
    public boolean existeGenero(Long id) {
        return generoRepository.existsById(id);
    }

    // Buscar géneros por nombre (parcial, ignorando mayúsculas/minúsculas)
    public List<Genero> buscarPorNombre(String nombre) {
        return generoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}