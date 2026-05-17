package ms_auth.authService.controller;

import ms_auth.authService.model.RefreshToken;
import ms_auth.authService.repository.RefreshTokenRepository;
import ms_auth.authService.service.RefreshTokenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refresh-token")
public class RefreshTokenController {
    private final RefreshTokenService service;

    public RefreshTokenController(RefreshTokenService service) {
        this.service = service;
    }

    @GetMapping
    public List<RefreshToken> listarRefreshToken() {
        return service.listarRefreshToken();
    }

    @GetMapping("/{id}")
    public RefreshToken buscarRefreshTokenPorId(@PathVariable Long id) {
        return service.buscarRefreshTokenPorId(id);
    }
    @PostMapping
    public RefreshToken crearRefreshToken(@RequestBody RefreshToken refreshToken) {
        return service.crearRefreshToken(refreshToken);
    }
    @PutMapping("/{id}")
    public RefreshToken actualizarRefreshToken(@PathVariable Long id, @RequestBody RefreshToken refreshToken) {
        return service.actualizarRefreshToken(refreshToken, id);
    }
    @DeleteMapping("/{id}")
    public void eliminarRefreshToken(@PathVariable Long id) {
        service.eliminarRefreshToken(id);
    }
}
