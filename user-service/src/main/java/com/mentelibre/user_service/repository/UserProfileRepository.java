package com.mentelibre.user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.user_service.model.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
    Optional<UserProfile> findByUserId(Long userId); // Para buscar un perfil por ID de usuario
    List<UserProfile> findByGeneroId(Long generoId); // Buscar perfiles por género
}
