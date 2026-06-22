package ms_auth.authService.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Violación de restricción única en base de datos (ORA-00001, UK duplicado)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Ya existe un registro con esos datos.");
        body.put("status", 409);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // Cualquier otro error de acceso a datos (SQL inválido, columna NOT NULL, conexión, etc.):
    // nunca se expone el detalle crudo de Oracle/JDBC al usuario, solo se loguea en el server.
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Ocurrió un error interno al guardar los datos. Intenta nuevamente.");
        body.put("status", 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // Errores de validación de campos (@Valid, @NotBlank, @Size, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(e -> "Campo '" + e.getField() + "': " + e.getDefaultMessage())
                .orElse("Error de validación en los datos enviados.");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", mensaje);
        body.put("status", 400);
        return ResponseEntity.badRequest().body(body);
    }

    // Errores de negocio lanzados desde los servicios (cuenta no encontrada, contraseña
    // incorrecta, etc.). Se deja al final porque es el más genérico de todos.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        body.put("status", 400);
        return ResponseEntity.badRequest().body(body);
    }
}
