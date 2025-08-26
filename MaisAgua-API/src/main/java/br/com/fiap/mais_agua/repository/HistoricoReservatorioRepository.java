package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.HistoricoReservatorio;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HistoricoReservatorioRepository extends JpaRepository<HistoricoReservatorio, Integer>, JpaSpecificationExecutor<HistoricoReservatorio> {
    Optional<HistoricoReservatorio> findTopByReservatorioIdReservatorioOrderByDataHoraDesc(Integer idReservatorio);
    boolean existsByReservatorio(Reservatorio reservatorio);

    @Query("SELECT h FROM HistoricoReservatorio h WHERE h.reservatorio.unidade.usuario = :usuario")
    List<HistoricoReservatorio> findByUsuario(Usuario usuario);

}
