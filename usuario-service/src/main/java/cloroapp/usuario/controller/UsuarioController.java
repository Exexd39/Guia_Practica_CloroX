package cloroapp.usuario.controller;

import cloroapp.usuario.dto.LoginRequest;
import cloroapp.usuario.dto.UsuarioDto;
import cloroapp.usuario.service.UsuarioService;
import jakarta.validation.Valid;
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
}