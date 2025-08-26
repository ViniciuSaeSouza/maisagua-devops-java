package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.LeituraDispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LeituraDispositivoRepository extends JpaRepository<LeituraDispositivo, Integer>, JpaSpecificationExecutor<LeituraDispositivo> {
    Optional<LeituraDispositivo> findTopByDispositivoIdDispositivoOrderByDataHoraDesc(Integer idDispositivo);

}
