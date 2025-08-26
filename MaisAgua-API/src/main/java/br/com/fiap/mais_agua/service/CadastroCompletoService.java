package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.*;
import br.com.fiap.mais_agua.model.DTO.CadastroCompletoDTO;
import br.com.fiap.mais_agua.model.DTO.CadastroCompletoResponseDTO;
import br.com.fiap.mais_agua.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CadastroCompletoService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UnidadeRepository unidadeRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public CadastroCompletoResponseDTO cadastrar(CadastroCompletoDTO dto) {
        // 1 - Verificar se o e-mail já está cadastrado
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "E-mail já cadastrado");
        }

        // 2 - Cadastrar Usuário
        Usuario usuario = Usuario.builder()
                .nome(dto.nomeUsuario())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .build();
        usuario = usuarioRepository.save(usuario);

        // 3 - Cadastrar Unidade
        Unidade unidade = Unidade.builder()
                .nome(dto.nomeUnidade())
                .capacidadeTotalLitros(dto.capacidadeTotalLitros())
                .usuario(usuario)
                .dataCadastro(LocalDateTime.now())
                .build();
        unidade = unidadeRepository.save(unidade);

        // 4 - Buscar cidade
        Cidade cidade = cidadeRepository.findById(dto.idCidade())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cidade não encontrada"));

        // 5 - Cadastrar Endereço
        Endereco endereco = Endereco.builder()
                .logradouro(dto.logradouro())
                .numero(dto.numero())
                .complemento(dto.complemento())
                .cep(dto.cep())
                .cidade(cidade)
                .unidade(unidade)
                .build();
        enderecoRepository.save(endereco);

        return CadastroCompletoResponseDTO.builder()
                .nomeUsuario(usuario.getNome())
                .email(usuario.getEmail())
                .nomeUnidade(unidade.getNome())
                .capacidadeTotalLitros(unidade.getCapacidadeTotalLitros())
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .cep(endereco.getCep())
                .idCidade(cidade.getId())
                .idUsuario(usuario.getIdUsuario())
                .build();
    }
}
