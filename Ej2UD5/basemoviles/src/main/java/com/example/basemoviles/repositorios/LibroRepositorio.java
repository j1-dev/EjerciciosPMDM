package com.example.basemoviles.repositorios;

import com.example.basemoviles.entidades.Libro;
import com.example.basemoviles.entidades.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface LibroRepositorio extends CrudRepository<Libro,Long> {

}
