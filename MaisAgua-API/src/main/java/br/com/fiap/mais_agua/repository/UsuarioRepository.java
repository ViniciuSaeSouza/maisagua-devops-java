package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByIdUsuario(Integer idUsuario);
}
