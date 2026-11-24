package cloroapp.medicion.config;

import cloroapp.medicion.model.Granja;
import cloroapp.medicion.model.Punto;
import cloroapp.medicion.model.TipoPunto;
import cloroapp.medicion.repository.GranjaRepository;
import cloroapp.medicion.repository.PuntoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosSemilla implements CommandLineRunner {

    private final GranjaRepository granjas;
    private final PuntoRepository puntos;

    public DatosSemilla(GranjaRepository granjas, PuntoRepository puntos) {
        this.granjas = granjas;
        this.puntos = puntos;
    }

    @Override
    public void run(String... args) {
        if (granjas.count() == 0) {
            granjas.save(new Granja("g1", "Granja Norte"));
            granjas.save(new Granja("g2", "Granja Sur"));
            granjas.save(new Granja("g3", "Granja Melipilla"));
            System.out.println(">> Semilla: 3 granjas creadas");
        }
        if (puntos.count() == 0) {
            guardar("p1", "g1", "N-TQ-01", "Tanque principal", TipoPunto.TANQUE, "Sala de bombas");
            guardar("p2", "g1", "N-LI-01", "Línea galpón 1", TipoPunto.LINEA, "Galpón 1");
            guardar("p3", "g1", "N-BE-02", "Bebedero galpón 2", TipoPunto.BEBEDERO, "Galpón 2");
            guardar("p4", "g1", "N-BE-03", "Bebedero galpón 3", TipoPunto.BEBEDERO, "Galpón 3");
            guardar("p5", "g2", "S-TQ-01", "Tanque principal", TipoPunto.TANQUE, "Sala de bombas");
            guardar("p6", "g2", "S-LI-01", "Línea galpón 1", TipoPunto.LINEA, "Galpón 1");
            guardar("p7", "g2", "S-BE-02", "Bebedero galpón 2", TipoPunto.BEBEDERO, "Galpón 2");
            guardar("p8", "g2", "S-BE-03", "Bebedero galpón 3", TipoPunto.BEBEDERO, "Galpón 3");
            guardar("p9", "g3", "M-TQ-01", "Tanque principal", TipoPunto.TANQUE, "Sala de bombas");
            guardar("p10", "g3", "M-LI-01", "Línea galpón 1", TipoPunto.LINEA, "Galpón 1");
            guardar("p11", "g3", "M-BE-02", "Bebedero galpón 2", TipoPunto.BEBEDERO, "Galpón 2");
            guardar("p12", "g3", "M-BE-03", "Bebedero galpón 3", TipoPunto.BEBEDERO, "Galpón 3");
            System.out.println(">> Semilla: 12 puntos creados");
        }
    }

    private void guardar(String id, String idGranja, String codigo, String nombre,
                         TipoPunto tipo, String ubicacion) {
        Punto p = new Punto();
        p.setId(id);
        p.setIdGranja(idGranja);
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setTipo(tipo);
        p.setUbicacion(ubicacion);
        puntos.save(p);
    }
}