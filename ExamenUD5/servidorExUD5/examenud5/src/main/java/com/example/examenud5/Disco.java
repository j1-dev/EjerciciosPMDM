package com.example.examenud5;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;

@Entity
public class Disco implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDisco;
    private String nombre;
    private Integer tipo;
    private Boolean escuchado;

    public Disco() {

    }

    public Disco(Long idDisco, String nombre, Integer tipo, Boolean escuchado) {
        this.idDisco = idDisco;
        this.nombre = nombre;
        this.tipo = tipo;
        this.escuchado = escuchado;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdDisco() {
        return idDisco;
    }

    public void setIdDisco(Long idDisco) {
        this.idDisco = idDisco;
    }

    public Boolean getEscuchado() {
        return escuchado;
    }

    public void setEscuchado(Boolean escuchado) {
        this.escuchado = escuchado;
    }
}
