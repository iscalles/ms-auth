package ms_auth.authService.controller;

import ms_auth.authService.model.CuentaAcceso;
import ms_auth.authService.service.CuentaAccesoService;
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
}
