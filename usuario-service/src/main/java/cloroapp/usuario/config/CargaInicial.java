package cloroapp.usuario.config;

import cloroapp.usuario.model.Rol;
import cloroapp.usuario.model.Usuario;
import cloroapp.usuario.repository.UsuarioRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {

    private final UsuarioRepository repo;

    public CargaInicial(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        repo.save(new Usuario("u1", "Ana Pérez", "operario@cloro.cl", "1234", Rol.OPERARIO, List.of("g1")));
        repo.save(new Usuario("u2", "Luis Soto", "supervisor@cloro.cl", "1234", Rol.SUPERVISOR, List.of("g1", "g2")));
        repo.save(new Usuario("u3", "Carla Mora", "jefatura@cloro.cl", "1234", Rol.JEFATURA, List.of("g1", "g2", "g3")));
    }
}