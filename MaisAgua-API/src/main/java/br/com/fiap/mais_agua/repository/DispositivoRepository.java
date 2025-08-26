package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Integer> {
    @Query("SELECT d FROM Dispositivo d " +
            "WHERE d.idDispositivo NOT IN " +
            "(SELECT rd.dispositivo.idDispositivo FROM ReservatorioDispositivo rd)")
    List<Dispositivo> findDispositivosSemReservatorio();

}
