package cloroapp.medicion.cliente;

import cloroapp.medicion.excepcion.ApiException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UsuarioClient {

    @Value("${usuario-service.url}")
    private String baseUrl;

    private final RestTemplate rest = new RestTemplate();

    public void exigirJefatura(String idUsuario) {
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new ApiException(403, "Se requiere el header X-Usuario-Id");
        }
        try {
            Map respuesta = rest.getForObject(baseUrl + "/api/usuarios/{id}", Map.class, idUsuario);
            Object rol = respuesta == null ? null : respuesta.get("rol");
            if (!"JEFATURA".equals(rol)) {
                throw new ApiException(403, "Solo la jefatura puede administrar puntos");
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(403, "No se pudo verificar el usuario: " + e.getMessage());
        }
    }
}