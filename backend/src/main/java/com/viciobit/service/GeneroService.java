package com.viciobit.service;

import com.viciobit.dto.GeneroCrearDTO;
import com.viciobit.dto.GeneroDTO;
import com.viciobit.exception.ConflictoException;
import com.viciobit.exception.DatosInvalidosException;
import com.viciobit.exception.RecursoNotFoundException;
import com.viciobit.model.Genero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeneroService {

    private List<Genero> generos = new ArrayList<>(); // Lista de géneros. Al no contar con una BBDD los metemos en una lista
    private Long siguienteId = 1L; //Asigna IDs consecutivos a los géneros que vayamos creando

    //Dependencia para comprobar si hay videojuegos asociados antes de eliminar un género
    @Lazy
    @Autowired
    private VideojuegoService videojuegoService;

    public GeneroService() {
        cargarGenerosIniciales();
    }

    //Carga los géneros iniciales
    private void cargarGenerosIniciales() {
        agregarGeneroInicial("Aventura", "Juegos de aventura");
        agregarGeneroInicial("Acción", "Juegos de acción");
        agregarGeneroInicial("Deportes", "Juegos deportivos");
    }

    //Añade un género inicial a la lista
    private void agregarGeneroInicial(String nombre, String descripcion) {
        Genero genero = new Genero();
        genero.setId(siguienteId++);
        genero.setNombre(nombre);
        genero.setDescripcion(descripcion);
        generos.add(genero);
    }

    //Muestra los géneros
    public List<GeneroDTO> listarGeneros() {
        List<GeneroDTO> listaGeneros = new ArrayList<>();

        for (Genero genero : generos) {
            GeneroDTO generoDTO = new GeneroDTO(
                    genero.getId(),
                    genero.getNombre(),
                    genero.getDescripcion()
            );

            listaGeneros.add(generoDTO);
        }

        return listaGeneros;
    }

    //Busca por ID un género y devuelve su modelo para futuros usos
    public Genero buscarPorId(Long id) {
        for (Genero genero : generos) {
            if (genero.getId().equals(id)) {
                return genero;
            }
        }

        throw new RecursoNotFoundException("Genero no encontrado");
    }

    //Busca género y devuelve su DTO para que el controller no trabaje directamente con el modelo
    public GeneroDTO buscarGeneroId(Long id) {
        Genero genero = buscarPorId(id);

        return new GeneroDTO(
                genero.getId(),
                genero.getNombre(),
                genero.getDescripcion()
        );
    }

    //Crear un género
    public GeneroDTO crearGenero(GeneroCrearDTO generoCrearDTO) {

        validacionNombre(generoCrearDTO.getNombre());
        validacionDuplicado(generoCrearDTO.getNombre(), null);

        Genero genero = new Genero();
        genero.setId(siguienteId++);
        genero.setNombre(generoCrearDTO.getNombre());
        genero.setDescripcion(generoCrearDTO.getDescripcion());

        generos.add(genero);

        return new GeneroDTO(
                genero.getId(),
                genero.getNombre(),
                genero.getDescripcion()
        );
    }

    //Validación de nombre
    public void validacionNombre(String nombre) {

        //isBlank() reconoce espacios en blanco
        if (nombre == null || nombre.isBlank()) {
            throw new DatosInvalidosException("El nombre hay que ponerlo");
        }
    }

    //Evitar nombres duplicados
    //Género a excluir mientras se comprueba
    public void validacionDuplicado(String nombre, Long idExcluido) {
        String n = nombre.toLowerCase();

        for (Genero genero : generos) {
            if (genero.getNombre().toLowerCase().equals(n)
                    && !genero.getId().equals(idExcluido)) {
                throw new ConflictoException("El nombre ya existe");
            }
        }
    }

    //Modificar un género
    public GeneroDTO modificarGenero(Long id, GeneroCrearDTO generoCrearDTO) {

        Genero genero = buscarPorId(id);

        validacionNombre(generoCrearDTO.getNombre());
        validacionDuplicado(generoCrearDTO.getNombre(), id);

        genero.setNombre(generoCrearDTO.getNombre());
        genero.setDescripcion(generoCrearDTO.getDescripcion());

        return new GeneroDTO(
                genero.getId(),
                genero.getNombre(),
                genero.getDescripcion()
        );
    }

    //Eliminar un género
    public void eliminarGenero(Long id) {
        Genero genero = buscarPorId(id);

        if (videojuegoService.tieneVideojuegosAsociados(id)) {
            throw new ConflictoException(
                    "No se puede eliminar el género porque tiene videojuegos asociados"
            );
        }

        generos.remove(genero);
    }
}

