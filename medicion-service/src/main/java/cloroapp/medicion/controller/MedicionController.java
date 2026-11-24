package cloroapp.medicion.controller;

import cloroapp.medicion.dto.MedicionRequest;
import cloroapp.medicion.dto.RespuestaMedicion;
import cloroapp.medicion.model.Medicion;
import cloroapp.medicion.service.MedicionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MedicionController {

    private final MedicionService service;

    public MedicionController(MedicionService service) {
        this.service = service;
    }

    @PostMapping("/mediciones")
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaMedicion crear(@RequestBody MedicionRequest req) {
        return service.crear(req);
    }

    @GetMapping("/puntos/{id}/mediciones")
    public List<Medicion> listar(@PathVariable String id,
                                 @RequestParam(required = false) String periodo) {
        return service.listar(id, periodo);
    }
}