package com.mentelibre.storage_service.repository;

import com.mentelibre.storage_service.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    // Buscar todos los archivos de un usuario específico
    List<FileEntity> findByOwnerId(Long ownerId);

    // Buscar archivos por dueño y categoría (perfil o diario de gratitud)
    List<FileEntity> findByOwnerIdAndCategory(Long ownerId, com.mentelibre.storage_service.model.FileCategory category);
}
