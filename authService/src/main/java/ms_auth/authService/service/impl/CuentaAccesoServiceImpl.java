package ms_auth.authService.service.impl;

import ms_auth.authService.client.UsuarioClient;
import ms_auth.authService.client.UsuarioDTOInternal;
import ms_auth.authService.model.CuentaAcceso;
import ms_auth.authService.repository.CuentaAccesoRepository;
import ms_auth.authService.repository.RefreshTokenRepository;
import ms_auth.authService.service.CuentaAccesoService;
import ms_auth.authService.service.EmailService;
import ms_auth.authService.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CuentaAccesoServiceImpl implements CuentaAccesoService {

    private static final Logger logger = LoggerFactory.getLogger(CuentaAccesoServiceImpl.class);

    final private CuentaAccesoRepository cuentaAccesoRepository;
    final private RefreshTokenRepository refreshTokenRepository;
    final private PasswordService passwordService;
    final private UsuarioClient usuarioClient;
    final private EmailService emailService;

    public CuentaAccesoServiceImpl(CuentaAccesoRepository cuentaAccesoRepository,
                                   RefreshTokenRepository refreshTokenRepository,
                                   PasswordService passwordService,
                                   UsuarioClient usuarioClient,
                                   EmailService emailService) {
        this.cuentaAccesoRepository = cuentaAccesoRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordService = passwordService;
        this.usuarioClient = usuarioClient;
        this.emailService = emailService;
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
    @Transactional
    public CuentaAcceso inicializarCuenta(Long idUsuario, String passwordPlano) {
        if (cuentaAccesoRepository.findByIdUsuario(idUsuario).isPresent()) {
            throw new RuntimeException("El usuario ya tiene una cuenta de acceso: " + idUsuario);
        }
        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setIdUsuario(idUsuario);
        cuenta.setPasswordHash(passwordService.encriptarPassword(passwordPlano));
        cuenta.setEstadoCuenta("ACTIVO");
        cuenta.setDebeCambiarPassword(true);
        CuentaAcceso creada = cuentaAccesoRepository.save(cuenta);
        logger.info("Cuenta de acceso creada para idUsuario={}", idUsuario);

        // Envía la contraseña temporal por correo en vez de que el administrador la entregue a mano.
        // Si la consulta a ms-usuario o el envío fallan, no debe impedir que la cuenta quede creada.
        try {
            UsuarioDTOInternal usuario = usuarioClient.obtenerUsuarioInterno(idUsuario);
            emailService.enviarContrasenaInicial(usuario.getCorreo(), usuario.getNombre(), usuario.getRutUsuario(), passwordPlano);
        } catch (Exception e) {
            // El EmailService ya registra sus propios errores; aquí solo se evita romper la creación
            // de la cuenta si no se pudo obtener el correo del usuario desde ms-usuario.
        }

        return creada;
    }

    @Override
    public void cambiarContrasena(Long idUsuario, String passwordActual, String passwordNuevo) {
        CuentaAcceso cuenta = cuentaAccesoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("No se encontró una cuenta para el usuario indicado"));
        if (!passwordService.validarPassword(passwordActual, cuenta.getPasswordHash())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        cuenta.setPasswordHash(passwordService.encriptarPassword(passwordNuevo));
        cuenta.setDebeCambiarPassword(false);
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

    @Override
    @Transactional
    public void desactivarCuentaPorUsuario(Long idUsuario) {
        CuentaAcceso cuenta = cuentaAccesoRepository.findByIdUsuario(idUsuario).orElse(null);
        if (cuenta == null) {
            // El usuario nunca tuvo cuenta de acceso (ej: se creó sin login habilitado); no hay nada que desactivar.
            return;
        }

        cuenta.setEstadoCuenta("ELIMINADO");
        cuentaAccesoRepository.save(cuenta);
        logger.info("Cuenta de acceso desactivada para idUsuario={}", idUsuario);

        // Revoca cualquier sesión activa para que no pueda seguir refrescando el access token
        refreshTokenRepository.deleteByIdUsuario(idUsuario);
    }

    @Override
    @Transactional
    public void recuperarPassword(String correo) {
        logger.info("Solicitud de recuperación de password para correo: {}", correo);
        UsuarioDTOInternal usuario;
        try {
            usuario = usuarioClient.obtenerUsuarioPorCorreo(correo);
        } catch (Exception e) {
            // No se revela al usuario si el correo existe o no en el sistema, pero
            // el detalle queda en el log del servidor para poder diagnosticarlo.
            logger.warn("recuperarPassword: no se encontró usuario para el correo '{}' en ms-usuario ({})", correo, e.getMessage());
            return;
        }
        if (usuario == null) {
            logger.warn("recuperarPassword: ms-usuario devolvió null para el correo '{}'", correo);
            return;
        }
        logger.info("recuperarPassword: usuario encontrado idUsuario={} para correo '{}'", usuario.getIdUsuario(), correo);

        CuentaAcceso cuenta = cuentaAccesoRepository.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
        if (cuenta == null) {
            logger.warn("recuperarPassword: el usuario idUsuario={} no tiene CuentaAcceso", usuario.getIdUsuario());
            return;
        }

        String passwordTemporal = generarPasswordTemporal();
        cuenta.setPasswordHash(passwordService.encriptarPassword(passwordTemporal));
        cuenta.setDebeCambiarPassword(true);
        cuentaAccesoRepository.save(cuenta);

        // Por seguridad, cierra cualquier sesión activa que hubiera quedado abierta
        refreshTokenRepository.deleteByIdUsuario(usuario.getIdUsuario());

        emailService.enviarRecuperacionPassword(usuario.getCorreo(), usuario.getNombre(), usuario.getRutUsuario(), passwordTemporal);
        logger.info("recuperarPassword: completado para idUsuario={}", usuario.getIdUsuario());
    }

    // Genera una contraseña de 12 caracteres con al menos una mayúscula, una minúscula y un
    // número (mismo criterio que usa el frontend al crear usuarios desde el mantenedor).
    private String generarPasswordTemporal() {
        String mayusculas = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String minusculas = "abcdefghjkmnpqrstuvwxyz";
        String numeros = "23456789";
        String todos = mayusculas + minusculas + numeros;
        SecureRandom random = new SecureRandom();

        List<Character> caracteres = new ArrayList<>();
        caracteres.add(mayusculas.charAt(random.nextInt(mayusculas.length())));
        caracteres.add(minusculas.charAt(random.nextInt(minusculas.length())));
        caracteres.add(numeros.charAt(random.nextInt(numeros.length())));
        for (int i = 3; i < 12; i++) {
            caracteres.add(todos.charAt(random.nextInt(todos.length())));
        }
        Collections.shuffle(caracteres, random);

        StringBuilder resultado = new StringBuilder();
        caracteres.forEach(resultado::append);
        return resultado.toString();
    }
}
