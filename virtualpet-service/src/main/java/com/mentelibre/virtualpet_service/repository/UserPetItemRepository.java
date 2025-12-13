package com.mentelibre.virtualpet_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.virtualpet_service.model.UserPetItem;

@Repository
public interface UserPetItemRepository extends JpaRepository<UserPetItem, Long> {

    // Todos los ítems que posee un usuario
    List<UserPetItem> findByUserId(Long userId);

    // Buscar si el usuario ya compró un ítem específico
    boolean existsByUserIdAndItemId(Long userId, Long itemId);

    // Listar ítems equipados del usuario
    List<UserPetItem> findByUserIdAndEquipped(Long userId, boolean equipped);
}