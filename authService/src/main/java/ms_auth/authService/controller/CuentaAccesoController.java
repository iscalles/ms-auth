package ms_auth.authService.controller;

import ms_auth.authService.dto.CambiarContrasenaDTO;
import ms_auth.authService.dto.InicializarCuentaDTO;
import ms_auth.authService.model.CuentaAcceso;
import ms_auth.authService.service.CuentaAccesoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuenta-acceso")
public class CuentaAccesoController {
    private final CuentaAccesoService service;

    public CuentaAccesoController(CuentaAccesoService service) {
        this.service = service;
    }

    @GetMapping
    public List<CuentaAcceso> listarCuentaAcceso() {
        return service.listarCuentaAcceso();
    }

    @GetMapping("/{id}")
    public CuentaAcceso buscarCuentaAccesoPorId(@PathVariable Long id) {
        return service.buscarCuentaAccesoPorId(id);
    }

    @PostMapping
    public CuentaAcceso crearCuentaAcceso(@RequestBody CuentaAcceso cuentaAcceso) {
        return service.crearCuentaAcceso(cuentaAcceso);
    }

    @PutMapping("/{id}")
    public CuentaAcceso actualizarCuentaAcceso(@PathVariable Long id, @RequestBody CuentaAcceso cuentaAcceso) {
        return service.actualizarCuentaAcceso(cuentaAcceso, id);
    }

    @DeleteMapping("/{id}")
    public void eliminarCuentaAcceso(@PathVariable Long id) {
        service.eliminarCuentaAcceso(id);
    }

    // Recibe contraseña en texto plano, la hashea con BCrypt y crea la cuenta
    @PostMapping("/inicializar")
    public ResponseEntity<CuentaAcceso> inicializarCuenta(@Valid @RequestBody InicializarCuentaDTO dto) {
        CuentaAcceso cuenta = service.inicializarCuenta(dto.getIdUsuario(), dto.getPasswordPlano());
        return ResponseEntity.status(HttpStatus.CREATED).body(cuenta);
    }

    // Valida contraseña actual y actualiza con la nueva (el usuario ya está autenticado)
    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<Void> cambiarContrasena(@Valid @RequestBody CambiarContrasenaDTO dto) {
        service.cambiarContrasena(dto.getIdUsuario(), dto.getPasswordActual(), dto.getPasswordNuevo());
        return ResponseEntity.ok().build();
    }
}
