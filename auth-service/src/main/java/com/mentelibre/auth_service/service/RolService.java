package com.mentelibre.auth_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mentelibre.auth_service.model.Rol;
import com.mentelibre.auth_service.repository.RolRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional

public class RolService {
    @Autowired
    private RolRepository rolRepository;
    
    public List<Rol> obtenerRoles(){
        return rolRepository.findAll();
    }

    public List<Rol> obtenerRolOrdenPorId() {
        return rolRepository.findAllByOrderByIdAsc();
    }

    public Rol obtenerRolPorId(Long id){
        return rolRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Rol no encontrado Id: "+ id));
    }

    public Rol crearRol(Rol rol){ // Metodo para crear un rol
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del rol es obligatorio");
        }

        if (rolRepository.existsByNombre(rol.getNombre())) {
            throw new RuntimeException("Ya existe un rol con este nombre");
        }
        return rolRepository.save(rol);

    }

    public String eliminarRol(Long id){
        Rol rol = rolRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Rol no encontrado Id: "+ id));
        
        if (id == 1 || id == 2 || id == 3) {
            throw new RuntimeException("No se puede eliminar este rol base del sistema");    
        }

        rolRepository.delete(rol);
        return "Rol eliminado";
    }

    public Rol actualizarRol(Long id, String nombre) {
        Rol rol = rolRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("Rol no encontrado ID: "+ id));

        if (nombre != null && !nombre.trim().isEmpty()) {
            if (rolRepository.existsByNombre(nombre)) {
                throw new RuntimeException("Ya existe un rol con ese nombre");
            }
            rol.setNombre(nombre);
        }
        return rolRepository.save(rol);
    }


}
