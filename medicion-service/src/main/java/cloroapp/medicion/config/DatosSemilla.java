package cloroapp.medicion.config;

import cloroapp.medicion.model.Granja;
import cloroapp.medicion.repository.GranjaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosSemilla implements CommandLineRunner {

    private final GranjaRepository granjas;

    public DatosSemilla(GranjaRepository granjas) {
        this.granjas = granjas;
    }

    @Override
    public void run(String... args) {
        if (granjas.count() == 0) {
            granjas.save(new Granja("g1", "Granja Norte"));
            granjas.save(new Granja("g2", "Granja Sur"));
            granjas.save(new Granja("g3", "Granja Melipilla"));
            System.out.println(">> Semilla: 3 granjas creadas");
        }
    }
}