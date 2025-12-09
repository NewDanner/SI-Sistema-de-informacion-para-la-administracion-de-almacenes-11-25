
package com.salesiana.inventory_system.service;

import com.salesiana.inventory_system.entity.Rol;
import com.salesiana.inventory_system.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> obtenerRolesActivos() {
        return rolRepository.findByActivoTrue();
    }

    public Optional<Rol> obtenerRolPorId(Integer id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> obtenerRolPorCodigo(String codigo) {
        return rolRepository.findByCodigo(codigo);
    }

    public Rol guardarRol(Rol rol) {
        return rolRepository.save(rol);
    }
    
    // Método para inicializar roles si no existen
    public void inicializarRolesBasicos() {
        if (rolRepository.count() == 0) {
            Rol admin = new Rol();
            admin.setCodigo("ADMIN");
            admin.setNombre("Administrador");
            admin.setDescripcion("Acceso completo al sistema");
            admin.setNivelAcceso(4);
            rolRepository.save(admin);
            
            Rol gerente = new Rol();
            gerente.setCodigo("GERENTE");
            gerente.setNombre("Gerente");
            gerente.setDescripcion("Puede gestionar inventario y reportes");
            gerente.setNivelAcceso(3);
            rolRepository.save(gerente);
            
            Rol almacenero = new Rol();
            almacenero.setCodigo("ALMACENERO");
            almacenero.setNombre("Almacenero");
            almacenero.setDescripcion("Puede registrar movimientos");
            almacenero.setNivelAcceso(2);
            rolRepository.save(almacenero);
            
            Rol consultor = new Rol();
            consultor.setCodigo("CONSULTOR");
            consultor.setNombre("Consultor");
            consultor.setDescripcion("Solo consultas");
            consultor.setNivelAcceso(1);
            rolRepository.save(consultor);
        }
    }
}