package cloroapp.medicion.controller;

import cloroapp.medicion.model.Granja;
import cloroapp.medicion.repository.GranjaRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GranjaController {

    private final GranjaRepository granjas;

    public GranjaController(GranjaRepository granjas) {
        this.granjas = granjas;
    }

    @GetMapping("/granjas")
    public List<Granja> listar() {
        return granjas.findAll();
    }
}