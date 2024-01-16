package com.example.basemoviles.repositorios;

import com.example.basemoviles.entidades.Alumno;
import com.example.basemoviles.entidades.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface AlumnoRepositorio extends CrudRepository<Alumno,Long> {
}
