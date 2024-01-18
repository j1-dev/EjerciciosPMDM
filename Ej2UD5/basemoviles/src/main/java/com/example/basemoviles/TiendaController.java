package com.example.basemoviles;

import com.example.basemoviles.entidades.GeoUtils;
import com.example.basemoviles.entidades.Persona;
import com.example.basemoviles.entidades.Tienda;
import com.example.basemoviles.repositorios.TiendaRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/tiendaapi")
public class TiendaController {
  private final TiendaRepositorio tiendaRepositorio;
  @Autowired
  public TiendaController(TiendaRepositorio tiendaRepositorio) {
    this.tiendaRepositorio = tiendaRepositorio;
  }

  @GetMapping("/listaTiendas")
  public List<Tienda> getTiendas(@RequestParam double userLatitude, @RequestParam double userLongitude){
    Iterable<Tienda> iterar=tiendaRepositorio.findAll();
    Iterator<Tienda> iterus=iterar.iterator();
    List<Tienda> tiendas = new ArrayList<>();
    while(iterus.hasNext()){
      Tienda tienda = iterus.next();
      double tiendaLatitude = tienda.getLatitud();
      double tiendaLongitude = tienda.getLongitud();
      double distance = GeoUtils.haversine(userLatitude, userLongitude, tiendaLatitude, tiendaLongitude);
      tienda.setDistancia(distance);
      tiendas.add(tienda);
    }

    tiendas.sort(Comparator.comparingDouble(Tienda::getDistancia));;

    return tiendas;
  }

  @GetMapping("/getTienda{id}")
  public ResponseEntity<Tienda> getTienda(@PathVariable("id") Long id) {
    Optional<Tienda> tiendaEncontrado = tiendaRepositorio.findById(id);
    if (tiendaEncontrado.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(tiendaEncontrado.get());
    }
  }

  @PutMapping("/actualizarTienda")
  public ResponseEntity<Long> modificar(@RequestBody Tienda tienda)
      throws URISyntaxException {
    System.out.println(tienda.toString());
    Tienda tiendaCreado = tiendaRepositorio.save(tienda);
    if (tiendaCreado == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(tiendaCreado.getIdTienda());
    }
  }

  @DeleteMapping("/eliminarTienda{id}")
  public ResponseEntity<Object> eliminar(@PathVariable Long id) {
    Optional<Tienda> tiendaEliminable = tiendaRepositorio.findById(id);
    if (tiendaEliminable.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      tiendaRepositorio.delete(tiendaEliminable.get());
      return ResponseEntity.noContent().build();
    }
  }

   @PostMapping("/nuevoTienda")
   public ResponseEntity<Long> nuevo(@RequestBody Tienda tienda)
            throws URISyntaxException {
     Tienda tiendaCreado = tiendaRepositorio.save(tienda);
     if (tiendaCreado == null) {
       return ResponseEntity.notFound().build();
     } else {
       return ResponseEntity.status(HttpStatus.CREATED).body(tiendaCreado.getIdTienda());
     }
   }

}
