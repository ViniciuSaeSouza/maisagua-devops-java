package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Endereco;
import br.com.fiap.mais_agua.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    List<Endereco> findByUnidadeUsuario(Usuario usuario);

    @Query("SELECT e FROM Endereco e " +
            "JOIN FETCH e.cidade c " +
            "JOIN FETCH c.estado est " +
            "JOIN FETCH est.pais p " +
            "WHERE e.id = :id")
    Endereco findEnderecoComRelacionamentos(@Param("id") Integer id);

}
