package ms_auth.authService.service.impl;

import ms_auth.authService.model.CuentaAcceso;
import ms_auth.authService.repository.CuentaAccesoRepository;
import ms_auth.authService.service.CuentaAccesoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaAccesoServiceImpl implements CuentaAccesoService {
    final private CuentaAccesoRepository cuentaAccesoRepository;

    public CuentaAccesoServiceImpl(CuentaAccesoRepository cuentaAccesoRepository) {
        this.cuentaAccesoRepository = cuentaAccesoRepository;
    }

    @Override
    public List<CuentaAcceso> listarCuentaAcceso() {
        return cuentaAccesoRepository.findAll();
    }

    @Override
    public CuentaAcceso buscarCuentaAccesoPorId(Long id) {
        return cuentaAccesoRepository.findById(id).orElse(null);
    }

    @Override
    public CuentaAcceso crearCuentaAcceso(CuentaAcceso cuentaAcceso) {
        return cuentaAccesoRepository.save(cuentaAcceso);
    }

    @Override
    public CuentaAcceso actualizarCuentaAcceso(CuentaAcceso cuentaAcceso, Long id) {
        CuentaAcceso cuentaAccesoExistente = cuentaAccesoRepository.findById(id).orElse(null);
        if (cuentaAccesoExistente != null) {
            cuentaAccesoExistente.setRutUsuario(cuentaAcceso.getRutUsuario());
            cuentaAccesoExistente.setPasswordHash(cuentaAcceso.getPasswordHash());
            cuentaAccesoExistente.setTokenRecuperacion(cuentaAcceso.getTokenRecuperacion());
            cuentaAccesoExistente.setEstadoCuenta(cuentaAcceso.getEstadoCuenta());
            cuentaAccesoExistente.setUltimoAcceso(cuentaAcceso.getUltimoAcceso());
            return cuentaAccesoRepository.save(cuentaAccesoExistente);
        } else {
            throw new RuntimeException("Cuenta de acceso no encontrada con id: " + id);
        }
    }

    @Override
    public void eliminarCuentaAcceso(Long id) {
        CuentaAcceso cuentaAccesoExistente = cuentaAccesoRepository.findById(id).orElse(null);
        if (cuentaAccesoExistente != null) {
            cuentaAccesoRepository.delete(cuentaAccesoExistente);
        } else {
            throw new RuntimeException("Cuenta de acceso no encontrada con id: " + id);
        }

    }
}
