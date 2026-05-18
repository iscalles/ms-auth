package ms_auth.authService.service.impl;

import ms_auth.authService.model.CuentaAcceso;
import ms_auth.authService.repository.CuentaAccesoRepository;
import ms_auth.authService.service.CuentaAccesoService;
import ms_auth.authService.service.PasswordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaAccesoServiceImpl implements CuentaAccesoService {
    final private CuentaAccesoRepository cuentaAccesoRepository;
    final private PasswordService passwordService;

    public CuentaAccesoServiceImpl(CuentaAccesoRepository cuentaAccesoRepository,
                                   PasswordService passwordService) {
        this.cuentaAccesoRepository = cuentaAccesoRepository;
        this.passwordService = passwordService;
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
            cuentaAccesoExistente.setIdUsuario(cuentaAcceso.getIdUsuario());
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
    public CuentaAcceso inicializarCuenta(Long idUsuario, String passwordPlano) {
        if (cuentaAccesoRepository.findByIdUsuario(idUsuario).isPresent()) {
            throw new RuntimeException("El usuario ya tiene una cuenta de acceso: " + idUsuario);
        }
        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setIdUsuario(idUsuario);
        cuenta.setPasswordHash(passwordService.encriptarPassword(passwordPlano));
        cuenta.setEstadoCuenta("ACTIVO");
        return cuentaAccesoRepository.save(cuenta);
    }

    @Override
    public void cambiarContrasena(Long idUsuario, String passwordActual, String passwordNuevo) {
        CuentaAcceso cuenta = cuentaAccesoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("No se encontró una cuenta para el usuario indicado"));
        if (!passwordService.validarPassword(passwordActual, cuenta.getPasswordHash())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        cuenta.setPasswordHash(passwordService.encriptarPassword(passwordNuevo));
        cuentaAccesoRepository.save(cuenta);
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
