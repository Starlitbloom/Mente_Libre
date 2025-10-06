package com.mentelibre.user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.user_service.model.Genero;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {
    Optional<Genero> findByNombre(String nombre); // Buscar un género exacto por nombre
    boolean existsByNombre(String nombre); // Verificar si ya existe un género con ese nombre
    List<Genero> findByNombreContainingIgnoreCase(String nombre); // Búsqueda parcial, ignorando mayúsculas/minúsculas
}