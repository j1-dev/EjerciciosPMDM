package com.example.basemoviles.repositorios;

import com.example.basemoviles.entidades.Persona;

import org.springframework.data.repository.CrudRepository;

public interface PersonaRepositorio extends CrudRepository<Persona,Long> {

}
