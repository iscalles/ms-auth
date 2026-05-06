package ms_auth.authService.repository;

import ms_auth.authService.model.CuentaAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaAccesoRepository extends JpaRepository<CuentaAcceso, Long> {
    Optional<CuentaAcceso> findByRutUsuario(String rutUsuario);
}
