package com.viciobit.service;

import com.viciobit.dto.UsuarioActualizarDto;
import com.viciobit.dto.UsuarioDto;
import com.viciobit.exception.RecursoNotFoundException;
import com.viciobit.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioActualService {

    private final UsuarioService usuarioService;

    public UsuarioActualService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // el filtro de autenticación guarda el uid del token como nombre de la autenticación
    public Usuario obtenerUsuarioActual() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null) {
            throw new RecursoNotFoundException("No hay ningún usuario autenticado");
        }
        return usuarioService.buscarPorUid(autenticacion.getName());
    }

    public UsuarioDto obtenerPerfil() {
        return usuarioService.aDto(obtenerUsuarioActual());
    }

    public UsuarioDto actualizarPerfil(UsuarioActualizarDto entrada) {
        return usuarioService.actualizar(obtenerUsuarioActual(), entrada);
    }
}