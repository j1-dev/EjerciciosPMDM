package com.example.basemoviles;

import com.example.basemoviles.entidades.Alumno;
import com.example.basemoviles.repositorios.AlumnoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/alumnoapi")
public class AlumnoController {
  private final AlumnoRepositorio alumnoRepositorio;
  @Autowired
  public AlumnoController(AlumnoRepositorio alumnoRepositorio) {
    this.alumnoRepositorio = alumnoRepositorio;
  }

  @GetMapping("/listaAlumnos")
  public List<Alumno> getAlumnos(){
    Iterable<Alumno> iterar=alumnoRepositorio.findAll();
    Iterator<Alumno> iterus=iterar.iterator();
    List<Alumno> resultado=new ArrayList<>();
    while(iterus.hasNext()){
      resultado.add(iterus.next());
    }
    return resultado;
  }

  @GetMapping("/getAlumno{id}")
  public ResponseEntity<Alumno> getAlumno(@PathVariable("id") Long id) {
    Optional<Alumno> alumnoEncontrado = alumnoRepositorio.findById(id);
    if (alumnoEncontrado.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(alumnoEncontrado.get());
    }
  }

  @PutMapping("/actualizarAlumno")
  public ResponseEntity<Long> modificar(@RequestBody Alumno alumno)
      throws URISyntaxException {
    Alumno alumnoCreado = alumnoRepositorio.save(alumno);
    if (alumnoCreado == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(alumnoCreado.getIdAlumno());
    }
  }

  @DeleteMapping("/eliminarAlumno{id}")
  public ResponseEntity<Object> eliminar(@PathVariable Long id) {
    Optional<Alumno> alumnoEliminable = alumnoRepositorio.findById(id);
    if (alumnoEliminable.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      alumnoRepositorio.delete(alumnoEliminable.get());
      return ResponseEntity.noContent().build();
    }
  }

   @PostMapping("/nuevoAlumno")
   public ResponseEntity<Long> nuevo(@RequestBody Alumno alumno)
            throws URISyntaxException {
     Alumno alumnoCreado = alumnoRepositorio.save(alumno);
     System.out.println(alumno.toString());
     if (alumnoCreado == null) {
       return ResponseEntity.notFound().build();
     } else {
       return ResponseEntity.status(HttpStatus.CREATED).body(alumnoCreado.getIdAlumno());
     }
   }

}
