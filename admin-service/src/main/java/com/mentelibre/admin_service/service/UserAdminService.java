package com.mentelibre.admin_service.service;

import com.mentelibre.admin_service.model.UserAdmin;
import com.mentelibre.admin_service.model.Rol;
import com.mentelibre.admin_service.repository.UserAdminRepository;
import com.mentelibre.admin_service.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAdminService {

    private final UserAdminRepository userAdminRepository;
    private final RolRepository rolRepository;

    @Autowired
    public UserAdminService(UserAdminRepository userAdminRepository,
                            RolRepository rolRepository) {
        this.userAdminRepository = userAdminRepository;
        this.rolRepository = rolRepository;
    }

    public List<UserAdmin> listarUsuarios() {
        return userAdminRepository.findAll();
    }

    public Optional<UserAdmin> buscarUsuarioPorId(Long id) {
        return userAdminRepository.findById(id);
    }

    public UserAdmin bloquearUsuario(Long id) {
        UserAdmin usuario = userAdminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setBloqueado(true);
        return userAdminRepository.save(usuario);
    }

    public UserAdmin desbloquearUsuario(Long id) {
        UserAdmin usuario = userAdminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setBloqueado(false);
        return userAdminRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        if (!userAdminRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userAdminRepository.deleteById(id);
    }

    public UserAdmin asignarRol(Long idUsuario, Long idRol) {
        UserAdmin usuario = userAdminRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);
        return userAdminRepository.save(usuario);
    }
}
