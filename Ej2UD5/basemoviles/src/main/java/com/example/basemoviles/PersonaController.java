package com.example.basemoviles;

import com.example.basemoviles.entidades.Persona;
import com.example.basemoviles.repositorios.PersonaRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/personaapi")
public class PersonaController {
  private final PersonaRepositorio personaRepositorio;
  @Autowired
  public PersonaController(PersonaRepositorio personaRepositorio) {
    this.personaRepositorio = personaRepositorio;
  }

  @GetMapping("/listaPersonas")
  public List<Persona> getPersonas(){
    Iterable<Persona> iterar=personaRepositorio.findAll();
    Iterator<Persona> iterus=iterar.iterator();
    List<Persona> resultado=new ArrayList<>();
    while(iterus.hasNext()){
      resultado.add(iterus.next());
    }
    return resultado;
  }

  @GetMapping("/getPersona{id}")
  public ResponseEntity<Persona> getPersona(@PathVariable("id") Long id) {
    Optional<Persona> personaEncontrado = personaRepositorio.findById(id);
    if (personaEncontrado.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(personaEncontrado.get());
    }
  }

  @PutMapping("/actualizarPersona")
  public ResponseEntity<Long> modificar(@RequestBody Persona persona)
      throws URISyntaxException {
    Persona personaCreado = personaRepositorio.save(persona);
    if (personaCreado == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(personaCreado.getIdPersona());
    }
  }

  @DeleteMapping("/eliminarPersona{id}")
  public ResponseEntity<Object> eliminar(@PathVariable Long id) {
    Optional<Persona> personaEliminable = personaRepositorio.findById(id);
    if (personaEliminable.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      personaRepositorio.delete(personaEliminable.get());
      return ResponseEntity.noContent().build();
    }
  }

   @PostMapping("/nuevoPersona")
   public ResponseEntity<Long> nuevo(@RequestBody Persona persona)
            throws URISyntaxException {
     Persona personaCreado = personaRepositorio.save(persona);
     if (personaCreado == null) {
       return ResponseEntity.notFound().build();
     } else {
       return ResponseEntity.status(HttpStatus.CREATED).body(personaCreado.getIdPersona());
     }
   }

}
