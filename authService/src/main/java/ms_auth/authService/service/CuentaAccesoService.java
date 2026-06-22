package ms_auth.authService.service;

import ms_auth.authService.model.CuentaAcceso;

import java.util.List;

public interface CuentaAccesoService {
    List<CuentaAcceso> listarCuentaAcceso();
    CuentaAcceso buscarCuentaAccesoPorId(Long id);
    CuentaAcceso crearCuentaAcceso(CuentaAcceso cuentaAcceso);
    CuentaAcceso actualizarCuentaAcceso(CuentaAcceso cuentaAcceso, Long id);
    void eliminarCuentaAcceso(Long id);
    // Marca la cuenta como ELIMINADO (no la borra), registra el estado previo en el
    // historial y revoca sus refresh tokens. No falla si el usuario nunca tuvo cuenta.
    void desactivarCuentaPorUsuario(Long idUsuario);
    // Crea la cuenta hasheando la contraseña en texto plano con BCrypt
    CuentaAcceso inicializarCuenta(Long idUsuario, String passwordPlano);
    // Valida la contraseña actual y actualiza con la nueva (ya hasheada)
    void cambiarContrasena(Long idUsuario, String passwordActual, String passwordNuevo);
    // Genera una contraseña temporal nueva y la envía por correo. No revela si el
    // correo está registrado o no (ni falla si no lo está): siempre responde igual.
    void recuperarPassword(String correo);
}
