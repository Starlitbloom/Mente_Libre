package com.mentelibre.admin_service.service;

import com.mentelibre.admin_service.model.Rol;
import com.mentelibre.admin_service.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private final RolRepository rolRepository;

    @Autowired
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarRolPorId(Long id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> buscarRolPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    public Rol crearRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public void eliminarRol(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado");
        }
        rolRepository.deleteById(id);
    }
}
