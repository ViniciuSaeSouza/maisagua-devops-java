package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservatorioRepository extends JpaRepository<Reservatorio, Integer>  {

    List<Reservatorio> findByUnidadeUsuario(Usuario usuario);
    List<Reservatorio> findByUnidade(Unidade unidade);
    Reservatorio findByIdReservatorio(Integer idReservatorio);
    boolean existsByIdReservatorioAndUnidadeUsuario(Integer idReservatorio, Usuario usuario);

}
