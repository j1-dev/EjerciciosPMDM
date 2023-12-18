package com.example.basemoviles;

import com.example.basemoviles.entidades.Alumno;
import com.example.basemoviles.entidades.Libro;
import com.example.basemoviles.repositorios.AlumnoRepositorio;
import com.example.basemoviles.repositorios.LibroRepositorio;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Ejemplo {

    public Ejemplo() {

    }

    public void pruebaInsercion(LibroRepositorio repositorioLibro, AlumnoRepositorio repositorioAlumno){
        Libro libro = new Libro();
        libro.setNombre("jeff");
        Optional<Alumno> a = repositorioAlumno.findById(3L);
        Alumno b = a.get();
        libro.setAlumno(b);
        repositorioLibro.save(libro);
        System.out.println(repositorioLibro.findAll());
    }

}
