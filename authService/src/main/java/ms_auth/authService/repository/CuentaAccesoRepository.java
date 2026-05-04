package ms_auth.authService.repository;

import ms_auth.authService.model.CuentaAcceso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaAccesoRepository  extends JpaRepository<CuentaAcceso, Long> {
}
