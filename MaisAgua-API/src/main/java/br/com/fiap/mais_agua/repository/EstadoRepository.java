package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstadoRepository extends JpaRepository<Estado, Integer> {

    List<Estado> findByPaisId(Integer paisId);
}
