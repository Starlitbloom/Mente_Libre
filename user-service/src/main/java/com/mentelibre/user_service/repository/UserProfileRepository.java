package com.mentelibre.user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.user_service.model.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
    Optional<UserProfile> findByUserId(Long userId); // Para buscar un perfil por ID de usuario
    boolean existsByTelefono(String telefono); // Para verificar si ya existe un telefono, devuelve true o false
    List<UserProfile> findByNombreContainingIgnoreCase(String nombre); // Para buscar perfiles por nombre, ignorando mayúsculas y minúsculas
    List<UserProfile> findByApellidoContainingIgnoreCase(String apellido); // Para buscar perfiles por
    List<UserProfile> findByGeneroId(Long generoId); // Buscar perfiles por género
}
