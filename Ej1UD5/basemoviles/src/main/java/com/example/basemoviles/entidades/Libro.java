package com.example.basemoviles.entidades;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLibro;
    private String nombre;
    @ManyToOne
    @JoinColumn(name="idAlumno")
    private Alumno alumno;

    public Libro() {
    }

    public Libro(Long idLibro, String nombre, Alumno alumno) {
        this.idLibro = idLibro;
        this.nombre = nombre;
        this.alumno = alumno;
    }

    public Long getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Long idLibro) {
        this.idLibro = idLibro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }
}
