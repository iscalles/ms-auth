package ms_auth.authService.controller;

import ms_auth.authService.dto.*;
import ms_auth.authService.service.AuthService;
import ms_auth.authService.service.CuentaAccesoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CuentaAccesoService cuentaAccesoService;

    public AuthController(AuthService authService, CuentaAccesoService cuentaAccesoService) {
        this.authService = authService;
        this.cuentaAccesoService = cuentaAccesoService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        LoginResponseDTO response = authService.refrescarToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validarToken(@RequestHeader("Authorization") String token) {
        String tokenLimpio = token.replace("Bearer ", "");
        boolean valido = authService.validarToken(tokenLimpio);
        return ResponseEntity.ok(valido);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String tokenLimpio = token.replace("Bearer ", "");
        authService.logout(tokenLimpio);
        return ResponseEntity.ok("Logout exitoso");
    }

    // Público (sin JWT): el usuario todavía no puede autenticarse. Siempre responde igual,
    // exista o no el correo, para no revelar qué correos están registrados.
    @PostMapping("/recuperar-password")
    public ResponseEntity<Void> recuperarPassword(@Valid @RequestBody RecuperarPasswordRequestDTO request) {
        cuentaAccesoService.recuperarPassword(request.getCorreo());
        return ResponseEntity.ok().build();
    }
}