package ms_auth.authService.service;

import ms_auth.authService.model.CuentaAcceso;

import java.util.List;

public interface CuentaAccesoService {
    List<CuentaAcceso> listarCuentaAcceso();
    CuentaAcceso buscarCuentaAccesoPorId(Long id);
    CuentaAcceso crearCuentaAcceso(CuentaAcceso cuentaAcceso);
    CuentaAcceso actualizarCuentaAcceso(CuentaAcceso cuentaAcceso, Long id);
    void eliminarCuentaAcceso(Long id);
    // Crea la cuenta hasheando la contraseña en texto plano con BCrypt
    CuentaAcceso inicializarCuenta(Long idUsuario, String passwordPlano);
    // Valida la contraseña actual y actualiza con la nueva (ya hasheada)
    void cambiarContrasena(Long idUsuario, String passwordActual, String passwordNuevo);
}
