package com.example.basemoviles;

import com.example.basemoviles.repositorios.AlumnoRepositorio;
import com.example.basemoviles.repositorios.LibroRepositorio;
import com.example.basemoviles.repositorios.UsuarioRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BasemovilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasemovilesApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(LibroRepositorio repositorioLibro, AlumnoRepositorio repositorioAlumno) {
        return (args) -> {
            Ejemplo ejemplo=new Ejemplo();
            ejemplo.pruebaInsercion(repositorioLibro, repositorioAlumno);
        };
    }

}
