package com.example.basemoviles.repositorios;

import com.example.basemoviles.entidades.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepositorio extends CrudRepository<Usuario,Long> {
}
