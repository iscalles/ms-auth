package ms_auth.authService.service;

import ms_auth.authService.model.RefreshToken;

import java.util.List;

public interface RefreshTokenService  {
    List<RefreshToken> listarRefreshToken();
    RefreshToken buscarRefreshTokenPorId(Long id);
    RefreshToken crearRefreshToken(RefreshToken refreshToken);
    RefreshToken actualizarRefreshToken(RefreshToken refreshToken, Long id);
    void eliminarRefreshToken(Long id);
}
