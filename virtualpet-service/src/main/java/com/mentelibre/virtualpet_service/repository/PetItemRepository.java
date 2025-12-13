package com.mentelibre.virtualpet_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.virtualpet_service.model.PetItem;

@Repository
public interface PetItemRepository extends JpaRepository<PetItem, Long> {

    // Listar todos los ítems de un tipo (ej: ropa, fondo, accesorio)
    List<PetItem> findByType(String type);

    // Listar solo items activos (disponibles en la tienda)
    List<PetItem> findByActive(boolean active);
}
