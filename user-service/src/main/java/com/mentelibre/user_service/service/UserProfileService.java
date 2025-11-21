package com.mentelibre.user_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mentelibre.user_service.model.UserProfile;
import com.mentelibre.user_service.repository.UserProfileRepository;
import com.mentelibre.user_service.webclient.AuthClient;
import com.mentelibre.user_service.webclient.StorageClient;

import jakarta.transaction.Transactional;

@Service
@Transactional

public class UserProfileService {
    @Autowired
    private UserProfileRepository userProfileRepository; 

    @Autowired
    private GeneroService generoService;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private StorageClient storageClient;

    // Crear perfil
    public UserProfile crearPerfil(UserProfile perfil) {
        if (perfil.getUserId() == null) {
            throw new RuntimeException("El ID del usuario (userId) es obligatorio");
        }

        if (!authClient.existeUsuario(perfil.getUserId())) {
            throw new RuntimeException("El usuario con ID " + perfil.getUserId() + " no existe en el AuthService");
        }
        
        if (userProfileRepository.findByUserId(perfil.getUserId()).isPresent()) {
            throw new RuntimeException("El usuario ya tiene un perfil asociado");
        }

        if (perfil.getTelefono() == null || perfil.getTelefono().isBlank()) {
            throw new RuntimeException("El teléfono es obligatorio");
        }

        if (userProfileRepository.existsByTelefono(perfil.getTelefono())) {
            throw new RuntimeException("El teléfono ya se encuentra registrado");
        }

        if (perfil.getGenero() == null || !generoService.existeGenero(perfil.getGenero().getId())) {
            throw new RuntimeException("El género seleccionado no es válido");
        }

        return userProfileRepository.save(perfil);
    }

    // Obtener todos los perfiles
    public List<UserProfile> obtenerUserProfile(){
        return userProfileRepository.findAll();
    }

    // Obtener por id
    public UserProfile obtenerUserProfilePorId(Long id){
        return userProfileRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Perfil de usuario no encontrado Id: "+ id));
    }

    // Obtener perfil por userId (desde AuthService)
    public UserProfile obtenerUserProfilePorUserId(Long userId){
        return userProfileRepository.findByUserId(userId)
        .orElseThrow(()-> new RuntimeException("Perfil no encontrado para el usuario Id: "+ userId));
    }

    // Actualizar perfil
    public UserProfile actualizarUserProfile(Long id, UserProfile nuevosDatos) {
        UserProfile userProfile = userProfileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));

        if (nuevosDatos.getNombre() != null) {
            userProfile.setNombre(nuevosDatos.getNombre());
        }

        if (nuevosDatos.getApellido() != null) {
            userProfile.setApellido(nuevosDatos.getApellido());
        }

        if (nuevosDatos.getTelefono() != null && !nuevosDatos.getTelefono().equals(userProfile.getTelefono())) {
            if (userProfileRepository.existsByTelefono(nuevosDatos.getTelefono())) {
                throw new RuntimeException("El teléfono ya está en uso por otro perfil");
            }
            userProfile.setTelefono(nuevosDatos.getTelefono());
        }

        if (nuevosDatos.getBiografia() != null) {
            userProfile.setBiografia(nuevosDatos.getBiografia());
        }


        if (nuevosDatos.getFechaNacimiento() != null) {
            userProfile.setFechaNacimiento(nuevosDatos.getFechaNacimiento());
        }

        if (nuevosDatos.getDireccion() != null) {
            userProfile.setDireccion(nuevosDatos.getDireccion());
        }

        if (nuevosDatos.getNotificaciones() != null) {
            userProfile.setNotificaciones(nuevosDatos.getNotificaciones());
        }

        if (nuevosDatos.getGenero() != null && generoService.existeGenero(nuevosDatos.getGenero().getId())) {
            userProfile.setGenero(nuevosDatos.getGenero());
        }

        UserProfile actualizado = userProfileRepository.save(userProfile);

        return actualizado;
    }

    // Eliminar perfil
    public String eliminarUserProfile(Long id) {
        UserProfile userProfile = userProfileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado. ID: " + id));

        Long userId = userProfile.getUserId();
        userProfileRepository.delete(userProfile);

        try {
            authClient.eliminarUsuario(userId); // Llama a AuthService para eliminar también al usuario
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el usuario en AuthService: " + e.getMessage());
        }

        return "Perfil y usuario eliminados correctamente";
    }

    // Buscar perfiles por nombre (ignora mayúsculas y minúsculas)
    public List<UserProfile> buscarPorNombre(String nombre) {
        return userProfileRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Buscar perfiles por apellido
    public List<UserProfile> buscarPorApellido(String apellido) {
        return userProfileRepository.findByApellidoContainingIgnoreCase(apellido);
    }

    // Buscar perfiles por género
    public List<UserProfile> buscarPorGenero(Long generoId) {
        return userProfileRepository.findByGeneroId(generoId);
    }

    // Subir archivo para un usuario
    public Map<String, Object> uploadUserFile(MultipartFile file, Long userId, String category, String token) {
        return storageClient.uploadFile(file, userId, category, token);
    }

    // Listar archivos de un usuario
    public List<Map<String, Object>> getUserFiles(Long userId, String token) {
        return storageClient.getFilesByOwner(userId, token);
    }

    // Eliminar archivo de usuario
    public boolean deleteUserFile(Long fileId, String token) {
        return storageClient.deleteFile(fileId, token);
    }

    // Actualizar archivo de usuario
    public Map<String, Object> updateUserFile(Long fileId, MultipartFile file, String token) {
        return storageClient.updateFile(fileId, file, token);
    }
}