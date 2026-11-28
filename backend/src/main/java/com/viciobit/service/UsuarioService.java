package com.viciobit.service;

import com.viciobit.dto.CambiarRolDto;
import com.viciobit.dto.UsuarioActualizarDto;
import com.viciobit.dto.UsuarioDto;
import com.viciobit.exception.DatosInvalidosException;
import com.viciobit.exception.RecursoNotFoundException;
import com.viciobit.model.Rol;
import com.viciobit.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();
    private Long siguienteId = 1L;
    private final String administradores;

    // viciobit.admins contiene los correos de los administradores separados por comas
    public UsuarioService(@Value("${viciobit.admins:}") String administradores) {
        this.administradores = administradores;
    }

    // lo usa el filtro de autenticación: si es la primera vez que entra, crea el usuario
    public Usuario obtenerOCrear(String uid, String email, String nombre) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUid().equals(uid)) {
                return usuario;
            }
        }

        Usuario usuario = new Usuario();
        usuario.setId(siguienteId);
        siguienteId++;
        usuario.setUid(uid);
        usuario.setEmail(email);
        if (nombre == null || nombre.isBlank()) {
            usuario.setNombre(email);
        } else {
            usuario.setNombre(nombre);
        }
        usuario.setFechaAlta(LocalDate.now());
        if (esAdministrador(email)) {
            usuario.setRol(Rol.ADMIN);
        } else {
            usuario.setRol(Rol.CLIENTE);
        }

        usuarios.add(usuario);
        return usuario;
    }

    public Usuario buscarPorUid(String uid) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUid().equals(uid)) {
                return usuario;
            }
        }
        throw new RecursoNotFoundException("No existe el usuario");
    }

    public Usuario buscarPorId(Long id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        throw new RecursoNotFoundException("No existe el usuario " + id);
    }

    public List<UsuarioDto> listar() {
        List<UsuarioDto> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            resultado.add(aDto(usuario));
        }
        return resultado;
    }

    public UsuarioDto actualizar(Usuario usuario, UsuarioActualizarDto entrada) {
        if (entrada.getNombre() == null || entrada.getNombre().isBlank()) {
            throw new DatosInvalidosException("El nombre es obligatorio");
        }
        usuario.setNombre(entrada.getNombre().trim());
        usuario.setDireccionEnvio(entrada.getDireccionEnvio());
        return aDto(usuario);
    }

    public UsuarioDto cambiarRol(Long id, CambiarRolDto entrada) {
        Usuario usuario = buscarPorId(id);
        if (entrada.getRol() == null) {
            throw new DatosInvalidosException("Hay que indicar el rol");
        }
        usuario.setRol(entrada.getRol());
        return aDto(usuario);
    }

    public UsuarioDto aDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setDireccionEnvio(usuario.getDireccionEnvio());
        dto.setRol(usuario.getRol());
        dto.setFechaAlta(usuario.getFechaAlta());
        return dto;
    }

    private boolean esAdministrador(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String[] lista = administradores.split(",");
        for (String admin : lista) {
            if (admin.trim().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }
        return false;
    }
}
