package ms_auth.authService.service;

import ms_auth.authService.dto.CrearCuentaRequestDTO;
import ms_auth.authService.dto.LoginRequestDTO;
import ms_auth.authService.dto.LoginResponseDTO;
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
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final CuentaAccesoRepository cuentaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordService passwordService;

    public AuthService(CuentaAccesoRepository cuentaRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JwtService jwtService,
                       PasswordService passwordService) {
        this.cuentaRepository = cuentaRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        CuentaAcceso cuenta = cuentaRepository.findByRutUsuario(request.getRutUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordService.validarPassword(request.getPassword(), cuenta.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (!cuenta.getEstadoCuenta().equals("ACTIVO")) {
            throw new RuntimeException("Cuenta desactivada");
        }

        cuenta.setUltimoAcceso(LocalDateTime.now());
        cuentaRepository.save(cuenta);

        String accessToken = jwtService.generarAccessToken(cuenta.getRutUsuario(), "USER");
        String refreshToken = generarRefreshToken(cuenta.getRutUsuario());

        logger.info("Login exitoso para: {}", request.getRutUsuario());

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                cuenta.getRutUsuario(),
                "USER",
                jwtService.obtenerTiempoExpiracion()
        );
    }

    public LoginResponseDTO crearCuenta(CrearCuentaRequestDTO request) {
        if (cuentaRepository.findByRutUsuario(request.getRutUsuario()).isPresent()) {
            throw new RuntimeException("RUT de usuario ya existe");
        }

        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setRutUsuario(request.getRutUsuario());
        cuenta.setPasswordHash(passwordService.encriptarPassword(request.getPassword()));
        cuenta.setEstadoCuenta("ACTIVO");
        cuenta.setUltimoAcceso(LocalDateTime.now());

        cuentaRepository.save(cuenta);

        String accessToken = jwtService.generarAccessToken(cuenta.getRutUsuario(), "USER");
        String refreshToken = generarRefreshToken(cuenta.getRutUsuario());

        logger.info("Cuenta creada exitosamente para: {}", request.getRutUsuario());

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                cuenta.getRutUsuario(),
                "USER",
                jwtService.obtenerTiempoExpiracion()
        );
    }

    public LoginResponseDTO refrescarToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token no válido"));

        if (refreshToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expirado");
        }

        CuentaAcceso cuenta = cuentaRepository.findByRutUsuario(refreshToken.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String newAccessToken = jwtService.generarAccessToken(cuenta.getRutUsuario(), "USER");
        String newRefreshToken = generarRefreshToken(cuenta.getRutUsuario());

        refreshTokenRepository.delete(refreshToken);

        logger.info("Token refrescado para: {}", refreshToken.getIdUsuario());

        return new LoginResponseDTO(
                newAccessToken,
                newRefreshToken,
                cuenta.getRutUsuario(),
                "USER",
                jwtService.obtenerTiempoExpiracion()
        );
    }

    public boolean validarToken(String token) {
        return jwtService.validarToken(token);
    }

    private String generarRefreshToken(String rutUsuario) {
        String token = jwtService.generarRefreshToken(rutUsuario);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setIdUsuario(rutUsuario);
        refreshToken.setFechaExpiracion(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);
        return token;
    }
}