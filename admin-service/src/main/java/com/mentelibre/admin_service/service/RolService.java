package com.mentelibre.admin_service.service;

import com.mentelibre.admin_service.model.Rol;
import com.mentelibre.admin_service.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    // Listar todos los roles
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    // Buscar rol por ID
    public Optional<Rol> buscarRolPorId(Long id) {
        return rolRepository.findById(id);
    }

    // Buscar rol por nombre
    public Optional<Rol> buscarRolPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    // Crear nuevo rol
    public Rol crearRol(Rol rol) {
        return rolRepository.save(rol);
    }

    // Eliminar rol
    public void eliminarRol(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado");
        }
        rolRepository.deleteById(id);
    }
}

