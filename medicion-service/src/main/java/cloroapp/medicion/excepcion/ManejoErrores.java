package cloroapp.medicion.excepcion;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ManejoErrores {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> api(ApiException e) {
        return ResponseEntity.status(e.getStatus())
                .body(Map.of("detail", e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> ilegible(HttpMessageNotReadableException e) {
        return ResponseEntity.status(422)
                .body(Map.of("detail", "Datos inválidos o tipo de dato incorrecto"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> parametro(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(422)
                .body(Map.of("detail", "Parámetro inválido: " + e.getName()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, String>> sinHeader(MissingRequestHeaderException e) {
        return ResponseEntity.status(403)
                .body(Map.of("detail", "Falta el header " + e.getHeaderName()));
    }
}