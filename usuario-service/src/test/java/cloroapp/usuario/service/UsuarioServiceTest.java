package cloroapp.usuario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cloroapp.usuario.dto.UsuarioRequest;
import cloroapp.usuario.model.Rol;
import cloroapp.usuario.model.Usuario;
import cloroapp.usuario.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class UsuarioServiceTest {

    private UsuarioRepository repo;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        repo = mock(UsuarioRepository.class);
        service = new UsuarioService(repo);
    }

    private Usuario operario() {
        return new Usuario("u1", "Ana Pérez", "operario@cloro.cl", "1234", Rol.OPERARIO, List.of("g1"));
    }

    @Test
    void loginConClaveCorrectaDevuelveUsuario() {
        when(repo.findByCorreoIgnoreCase("operario@cloro.cl")).thenReturn(Optional.of(operario()));

        Usuario u = service.login("operario@cloro.cl", "1234");

        assertEquals("u1", u.getId());
    }

    @Test
    void loginConClaveIncorrectaDa401() {
        when(repo.findByCorreoIgnoreCase("operario@cloro.cl")).thenReturn(Optional.of(operario()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.login("operario@cloro.cl", "mala"));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void exigirJefaturaRechazaOperarioCon403() {
        when(repo.findById("u1")).thenReturn(Optional.of(operario()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.exigirJefatura("u1"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void crearConCorreoRepetidoDa409SinGuardar() {
        when(repo.findByCorreoIgnoreCase("operario@cloro.cl")).thenReturn(Optional.of(operario()));
        UsuarioRequest datos = new UsuarioRequest("Otro", "operario@cloro.cl", "1234", Rol.OPERARIO, List.of("g1"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.crear(datos));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repo, never()).save(any());
    }
}