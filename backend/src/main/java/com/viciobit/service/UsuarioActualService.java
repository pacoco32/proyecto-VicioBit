package com.viciobit.service;

import com.google.firebase.auth.FirebaseToken;
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

    // el filtro guarda el token de Firebase ya verificado; si es su primer acceso se crea el usuario
    public Usuario obtenerUsuarioActual() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof FirebaseToken)) {
            throw new RecursoNotFoundException("No hay ningún usuario autenticado");
        }
        FirebaseToken token = (FirebaseToken) autenticacion.getPrincipal();
        return usuarioService.obtenerOCrear(token.getUid(), token.getEmail(), token.getName());
    }

    public UsuarioDto obtenerPerfil() {
        return usuarioService.aDto(obtenerUsuarioActual());
    }

    public UsuarioDto actualizarPerfil(UsuarioActualizarDto entrada) {
        return usuarioService.actualizar(obtenerUsuarioActual(), entrada);
    }
}