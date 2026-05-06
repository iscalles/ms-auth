package ms_auth.authService.controller;

import ms_auth.authService.dto.*;
import ms_auth.authService.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/crear-cuenta")
    public ResponseEntity<LoginResponseDTO> crearCuenta(@Valid @RequestBody CrearCuentaRequestDTO request) {
        LoginResponseDTO response = authService.crearCuenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
}