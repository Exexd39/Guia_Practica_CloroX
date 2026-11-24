package cloroapp.usuario.service;

import cloroapp.usuario.dto.UsuarioRequest;
import cloroapp.usuario.model.Rol;
import cloroapp.usuario.model.Usuario;
import cloroapp.usuario.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario login(String correo, String clave) {
        return repo.findByCorreoIgnoreCase(correo.trim())
                .filter(u -> u.getClave().equals(clave))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Correo o clave incorrectos"));
    }

    public Usuario obtener(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario crear(UsuarioRequest datos) {
        if (datos.clave() == null || datos.clave().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La clave es obligatoria");
        }
        validarCorreoLibre(datos.correo(), null);
        Usuario u = new Usuario(
                UUID.randomUUID().toString(),
                datos.nombre().trim(),
                datos.correo().trim(),
                datos.clave(),
                datos.rol(),
                datos.granjasAsignadas());
        return repo.save(u);
    }

    public Usuario actualizar(String id, UsuarioRequest datos) {
        Usuario u = obtener(id);
        validarCorreoLibre(datos.correo(), id);
        u.setNombre(datos.nombre().trim());
        u.setCorreo(datos.correo().trim());
        if (datos.clave() != null && !datos.clave().isBlank()) {
            u.setClave(datos.clave());
        }
        u.setRol(datos.rol());
        u.setGranjasAsignadas(new ArrayList<>(datos.granjasAsignadas()));
        return repo.save(u);
    }

    public void eliminar(String id) {
        obtener(id);
        repo.deleteById(id);
    }

    // Verifica que quien hace la petición exista y sea JEFATURA
    public void exigirJefatura(String idSolicitante) {
        Usuario solicitante = repo.findById(idSolicitante)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));
        if (solicitante.getRol() != Rol.JEFATURA) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo jefatura puede gestionar usuarios");
        }
    }

    private void validarCorreoLibre(String correo, String idActual) {
        repo.findByCorreoIgnoreCase(correo.trim()).ifPresent(otro -> {
            if (!otro.getId().equals(idActual)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
            }
        });
    }
}