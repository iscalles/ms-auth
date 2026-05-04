package ms_auth.authService.service;

import ms_auth.authService.model.CuentaAcceso;

import java.util.List;

public interface CuentaAccesoService {
    List<CuentaAcceso> listarCuentaAcceso();
    CuentaAcceso buscarCuentaAccesoPorId(Long id);
    CuentaAcceso crearCuentaAcceso(CuentaAcceso cuentaAcceso);
    CuentaAcceso actualizarCuentaAcceso(CuentaAcceso cuentaAcceso, Long id);
    void eliminarCuentaAcceso(Long id);
}
