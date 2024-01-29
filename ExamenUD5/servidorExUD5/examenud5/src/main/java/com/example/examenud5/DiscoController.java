package com.example.examenud5;

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
@RequestMapping("/discoapi")
public class DiscoController {

    private final DiscoRepositorio discoRepositorio;

    @Autowired
    public DiscoController(DiscoRepositorio discoRepositorio) {
        this.discoRepositorio = discoRepositorio;
    }

    @GetMapping("/listaDiscos")
    public List<Disco> getEventos(){
        Iterable<Disco> iterar= discoRepositorio.findAll();
        Iterator<Disco> iterus=iterar.iterator();
        List<Disco> resultado=new ArrayList<>();
        while(iterus.hasNext()){
            resultado.add(iterus.next());
        }
        return resultado;
    }

    @PostMapping("/nuevoDisco")
    public ResponseEntity<Long> nuevo(@RequestBody Disco Disco)
            throws URISyntaxException {
        System.out.println(Disco.getNombre());
        Disco discoCreado = discoRepositorio.save(Disco);
        if (discoCreado == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(discoCreado.getIdDisco());
        }
    }

    @GetMapping("/getDisco{id}")
    public ResponseEntity<Disco> getDisco(@PathVariable("id") Long id) {
        Optional<Disco> discoEncontrado = discoRepositorio.findById(id);
        if (discoEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(discoEncontrado.get());
        }
    }

    @PutMapping("/actualizarDisco")
    public ResponseEntity<Long> modificar(@RequestBody Disco Disco)
            throws URISyntaxException {
        Disco discoModificado = discoRepositorio.save(Disco);
        if (discoModificado == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(discoModificado.getIdDisco());
        }
    }

    @DeleteMapping("/eliminarDisco{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        Optional<Disco> discoEliminable = discoRepositorio.findById(id);
        if (discoEliminable.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            discoRepositorio.delete(discoEliminable.get());
            return ResponseEntity.noContent().build();
        }
    }

}
