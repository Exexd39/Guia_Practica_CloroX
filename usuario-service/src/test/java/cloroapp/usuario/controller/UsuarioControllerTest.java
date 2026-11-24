package cloroapp.usuario.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cloroapp.usuario.model.Rol;
import cloroapp.usuario.model.Usuario;
import cloroapp.usuario.service.UsuarioService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UsuarioService service;

    private static final String LOGIN_OK = """
            {"correo":"operario@cloro.cl","clave":"1234"}""";

    private static final String LOGIN_MALO = """
            {"correo":"operario@cloro.cl","clave":"mala"}""";

    @Test
    void loginCorrectoDevuelveUsuarioSinClave() throws Exception {
        Usuario u = new Usuario("u1", "Ana Pérez", "operario@cloro.cl", "1234", Rol.OPERARIO, List.of("g1"));
        when(service.login("operario@cloro.cl", "1234")).thenReturn(u);

        mvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u1"))
                .andExpect(jsonPath("$.rol").value("OPERARIO"))
                .andExpect(jsonPath("$.clave").doesNotExist());
    }

    @Test
    void loginIncorrectoDevuelve401ConDetalle() throws Exception {
        when(service.login("operario@cloro.cl", "mala"))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o clave incorrectos"));

        mvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_MALO))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Correo o clave incorrectos"));
    }

    @Test
    void listarSinEncabezadoDevuelve401() throws Exception {
        mvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Falta el encabezado X-Usuario-Id"));
    }

    @Test
    void listarComoOperarioDevuelve403() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo jefatura puede gestionar usuarios"))
                .when(service).exigirJefatura("u1");

        mvc.perform(get("/api/usuarios").header("X-Usuario-Id", "u1"))
                .andExpect(status().isForbidden());
    }
}