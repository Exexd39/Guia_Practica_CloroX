package cloroapp.usuario.controller;

import cloroapp.usuario.dto.LoginRequest;
import cloroapp.usuario.dto.UsuarioDto;
import cloroapp.usuario.dto.UsuarioRequest;
import cloroapp.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public UsuarioDto login(@Valid @RequestBody LoginRequest datos) {
        return UsuarioDto.desde(service.login(datos.correo(), datos.clave()));
    }

    @GetMapping("/{id}")
    public UsuarioDto obtener(@PathVariable String id) {
        return UsuarioDto.desde(service.obtener(id));
    }

    @GetMapping
    public List<UsuarioDto> listar(@RequestHeader("X-Usuario-Id") String idSolicitante) {
        service.exigirJefatura(idSolicitante);
        return service.listar().stream().map(UsuarioDto::desde).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto crear(@RequestHeader("X-Usuario-Id") String idSolicitante,
                            @Valid @RequestBody UsuarioRequest datos) {
        service.exigirJefatura(idSolicitante);
        return UsuarioDto.desde(service.crear(datos));
    }

    @PutMapping("/{id}")
    public UsuarioDto actualizar(@RequestHeader("X-Usuario-Id") String idSolicitante,
                                 @PathVariable String id,
                                 @Valid @RequestBody UsuarioRequest datos) {
        service.exigirJefatura(idSolicitante);
        return UsuarioDto.desde(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@RequestHeader("X-Usuario-Id") String idSolicitante,
                         @PathVariable String id) {
        service.exigirJefatura(idSolicitante);
        service.eliminar(id);
    }
}