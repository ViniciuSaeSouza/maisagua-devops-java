package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.StatusReservatorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusReservatorioRepository extends JpaRepository<StatusReservatorio, Integer> {

    Optional<StatusReservatorio> findByStatus(String nome);

}
