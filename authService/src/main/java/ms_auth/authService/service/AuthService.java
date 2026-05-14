package ms_auth.authService.service;

import ms_auth.authService.client.UsuarioClient;
import ms_auth.authService.client.UsuarioDTOInternal;
import ms_auth.authService.dto.LoginRequestDTO;
import ms_auth.authService.dto.LoginResponseDTO;
import ms_auth.authService.dto.RefreshTokenRequestDTO;
import ms_auth.authService.model.CuentaAcceso;
import ms_auth.authService.model.RefreshToken;
import ms_auth.authService.repository.CuentaAccesoRepository;
import ms_auth.authService.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final CuentaAccesoRepository cuentaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordService passwordService;
    private final UsuarioClient usuarioClient;

    public AuthService(CuentaAccesoRepository cuentaRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JwtService jwtService,
                       PasswordService passwordService,
                       UsuarioClient usuarioClient) {
        this.cuentaRepository = cuentaRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
        this.usuarioClient = usuarioClient;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        //Consultar  MS_USUARIO para obtener idUsuario usando RUT
        UsuarioDTOInternal usuarioInternal;
        try {
            usuarioInternal = usuarioClient.obtenerUsuarioPorRut(request.getRutUsuario());
            logger.info("Usuario obtenido de ms-usuario: {}", usuarioInternal.getIdUsuario());
        } catch (Exception e) {
            logger.error("Error en Feign - Error completo: ", e);
            throw new RuntimeException("Usuario no encontrado");
        }

        // Buscar cuenta por ID_USUARIO en BD local
        CuentaAcceso cuenta = cuentaRepository.findByIdUsuario(usuarioInternal.getIdUsuario())
                .orElseThrow(() -> {
                    logger.error("NO ENCONTRADO: No existe CuentaAcceso para ID_USUARIO: {}", usuarioInternal.getIdUsuario());
                    return new RuntimeException("Cuenta de acceso no configurada");
                });
        logger.info("CuentaAcceso encontrada: ID_CUENTA={}", cuenta.getIdCuenta());

        // Validar contraseña
        if (!passwordService.validarPassword(request.getPassword(), cuenta.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Verificar estado de cuenta
        if (!cuenta.getEstadoCuenta().equals("ACTIVO")) {
            throw new RuntimeException("Cuenta desactivada");
        }

        // Actualizar último acceso
        cuenta.setUltimoAcceso(LocalDateTime.now());
        cuentaRepository.save(cuenta);

        // Generar JWT con ID_USUARIO + ROLES
        String accessToken = jwtService.generarAccessToken(
                usuarioInternal.getIdUsuario(),
                usuarioInternal.getRoles()
        );

        // Generar RefreshToken
        String refreshToken = generarRefreshToken(usuarioInternal.getIdUsuario());

        logger.info("Login exitoso para usuario ID: {}", usuarioInternal.getIdUsuario());

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                usuarioInternal.getIdUsuario(),
                usuarioInternal.getNombre() + " " + usuarioInternal.getApellidos(),
                usuarioInternal.getCorreo(),
                usuarioInternal.getRoles(),
                jwtService.obtenerTiempoExpiracion()
        );
    }

    public LoginResponseDTO refrescarToken(String refreshTokenStr) {
        //Buscar RefreshToken en BD
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token no válido"));

        //Validar expiración
        if (refreshToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expirado");
        }

        //Buscar cuenta por ID_USUARIO
        CuentaAcceso cuenta = cuentaRepository.findById(refreshToken.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //Consultar MS_USUARIO para obtener roles actualizados
        UsuarioDTOInternal usuarioInternal;
        try {
            usuarioInternal = usuarioClient.obtenerUsuarioInterno(cuenta.getIdUsuario());
        } catch (Exception e) {
            logger.error("Error consultando MS_USUARIO en refresh", e);
            throw new RuntimeException("Error consultando servicio de usuario");
        }

        //Generar nuevo AccessToken
        String newAccessToken = jwtService.generarAccessToken(
                cuenta.getIdUsuario(),
                usuarioInternal.getRoles()
        );

        //Generar nuevo RefreshToken
        refreshTokenRepository.delete(refreshToken);
        String newRefreshToken = generarRefreshToken(cuenta.getIdUsuario());

        logger.info("Token refrescado para usuario ID: {}", cuenta.getIdUsuario());

        return new LoginResponseDTO(
                newAccessToken,
                newRefreshToken,
                cuenta.getIdUsuario(),
                usuarioInternal.getNombre() + " " + usuarioInternal.getApellidos(),
                usuarioInternal.getCorreo(),
                usuarioInternal.getRoles(),
                jwtService.obtenerTiempoExpiracion()
        );
    }

    public boolean validarToken(String token) {
        return jwtService.validarToken(token);
    }

    public void logout(String token) {
        if (!jwtService.validarToken(token)) {
            throw new RuntimeException("Token inválido");
        }

        Long idUsuario = jwtService.extraerIdUsuario(token);

        // Eliminar todos los RefreshTokens del usuario
        refreshTokenRepository.deleteByIdUsuario(idUsuario);

        logger.info("Logout exitoso para usuario ID: {}", idUsuario);
    }

    private String generarRefreshToken(Long idUsuario) {
        String token = jwtService.generarRefreshToken(idUsuario);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setIdUsuario(idUsuario);
        refreshToken.setFechaExpiracion(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);
        return token;
    }
}