package cloroapp.medicion.controller;

import cloroapp.medicion.dto.PuntoRequest;
import cloroapp.medicion.model.Punto;
import cloroapp.medicion.service.PuntoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PuntoController {

    private final PuntoService service;

    public PuntoController(PuntoService service) {
        this.service = service;
    }

    @GetMapping("/puntos")
    public List<Punto> listar(@RequestParam(required = false) String idGranja) {
        return service.listar(idGranja);
    }

    @GetMapping("/puntos/{id}")
    public Punto obtener(@PathVariable String id) {
        return service.obtener(id);
    }

    @PostMapping("/puntos")
    @ResponseStatus(HttpStatus.CREATED)
    public Punto crear(@RequestBody PuntoRequest req,
                       @RequestHeader(value = "X-Usuario-Id", required = false) String idUsuario) {
        return service.crear(req, idUsuario);
    }

    @PutMapping("/puntos/{id}")
    public Punto actualizar(@PathVariable String id, @RequestBody PuntoRequest req,
                            @RequestHeader(value = "X-Usuario-Id", required = false) String idUsuario) {
        return service.actualizar(id, req, idUsuario);
    }

    @DeleteMapping("/puntos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id,
                         @RequestHeader(value = "X-Usuario-Id", required = false) String idUsuario) {
        service.eliminar(id, idUsuario);
    }
}