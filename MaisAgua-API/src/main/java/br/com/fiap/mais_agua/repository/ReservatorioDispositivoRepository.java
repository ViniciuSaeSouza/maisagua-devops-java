package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.ReservatorioDispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservatorioDispositivoRepository extends JpaRepository<ReservatorioDispositivo, Integer> {
    List<ReservatorioDispositivo> findByDispositivo(Dispositivo dispositivo);
    List<ReservatorioDispositivo> findByReservatorio(Reservatorio reservatorio);
    Optional<ReservatorioDispositivo> findByReservatorioIdReservatorio(Integer idReservatorio);

    List<ReservatorioDispositivo> findByReservatorio_Unidade_Usuario_IdUsuario(Integer idUsuario);

    @Query("SELECT rd.reservatorio FROM ReservatorioDispositivo rd " +
            "WHERE rd.dispositivo.idDispositivo = :idDispositivo")
    Optional<Reservatorio> findReservatorioByDispositivo(@Param("idDispositivo") Integer idDispositivo);


}
