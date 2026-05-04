package ms_auth.authService.service.impl;

import ms_auth.authService.model.RefreshToken;
import ms_auth.authService.repository.RefreshTokenRepository;
import ms_auth.authService.service.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    final private RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public List<RefreshToken> listarRefreshToken() {
        return refreshTokenRepository.findAll();
    }

    @Override
    public RefreshToken buscarRefreshTokenPorId(Long id) {
        return refreshTokenRepository.findById(id).orElse(null);
    }

    @Override
    public RefreshToken crearRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken actualizarRefreshToken(RefreshToken refreshToken, Long id) {
        RefreshToken refreshTokenExistente = refreshTokenRepository.findById(id).orElse(null);
        if (refreshTokenExistente != null) {
            refreshTokenExistente.setToken(refreshToken.getToken());
            refreshTokenExistente.setRutUsuario(refreshToken.getRutUsuario());
            refreshTokenExistente.setFechaExpiracion(refreshToken.getFechaExpiracion());
            return refreshTokenRepository.save(refreshTokenExistente);
        } else {
            throw new RuntimeException("Token inexistente");
        }
    }

    @Override
    public void eliminarRefreshToken(Long id) {
        RefreshToken refreshTokenExistente = refreshTokenRepository.findById(id).orElse(null);
        if (refreshTokenExistente != null) {
            refreshTokenRepository.delete(refreshTokenExistente);
        } else {
            throw new RuntimeException("Token no existente");
        }
    }




}
