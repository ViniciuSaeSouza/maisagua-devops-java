package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnidadeRepository extends JpaRepository<Unidade, Integer> {
    List<Unidade> findByUsuario(Usuario usuario);
}
