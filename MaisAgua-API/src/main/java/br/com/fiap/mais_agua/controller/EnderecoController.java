package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.DTO.EnderecoDTO;
import br.com.fiap.mais_agua.model.Endereco;
import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.model.DTO.UnidadeReadDTO;
import br.com.fiap.mais_agua.model.DTO.UsuarioResponseDTO;
import br.com.fiap.mais_agua.repository.EnderecoRepository;
import br.com.fiap.mais_agua.repository.UnidadeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/endereco")
@Slf4j
@Tag(name = "Endereço")
public class EnderecoController {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @GetMapping
    @Operation(
            summary = "Listar endereços do usuário",
            description = "Retorna todos os endereços associados às unidades do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Endereços retornados com sucesso")
            }
    )
    @Cacheable("endereco")
    public ResponseEntity<List<EnderecoDTO>> index(@AuthenticationPrincipal Usuario usuario) {
        List<Endereco> enderecos = enderecoRepository.findByUnidadeUsuario(usuario);
        List<EnderecoDTO> enderecoDTOs = enderecos.stream()
                .map(this::mapToEnderecoDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(enderecoDTOs);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cadastrar endereço",
            description = "Cadastra um novo endereço vinculado a uma unidade do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso não autorizado à unidade"),
                    @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
            }
    )
    @CacheEvict(value = "endereco", allEntries = true)
    public EnderecoDTO create(@RequestBody @Valid Endereco endereco, @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando endereço: " + endereco.getLogradouro());

        Unidade unidade = unidadeRepository.findById(endereco.getUnidade().getIdUnidade())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        if (!unidade.getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar esta unidade");
        }

        endereco.setUnidade(unidade);
        Endereco savedEndereco = enderecoRepository.save(endereco);

        return mapToEnderecoDTO(savedEndereco);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Buscar endereço por ID",
            description = "Retorna os dados de um endereço específico, se o usuário for o dono.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado"),
                    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
            }
    )
    public ResponseEntity<EnderecoDTO> get(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        log.info("Buscando endereço " + id);
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));

        if (!endereco.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este endereço");
        }

        EnderecoDTO enderecoDTO = mapToEnderecoDTO(endereco);
        return ResponseEntity.ok(enderecoDTO);
    }


    @DeleteMapping("{id}")
    @Operation(
            summary = "Excluir endereço",
            description = "Exclui um endereço se ele pertencer a uma unidade do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Endereço excluído com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado"),
                    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
            }
    )
    @CacheEvict(value = "endereco", allEntries = true)
    public ResponseEntity<Object> destroy(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        log.info("Excluindo endereço " + id);
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));

        if (!endereco.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este endereço");
        }

        enderecoRepository.delete(endereco);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("{id}")
    @Operation(
            summary = "Atualizar endereço",
            description = "Atualiza um endereço se o usuário for dono da unidade relacionada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado"),
                    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
            }
    )
    @CacheEvict(value = "endereco", allEntries = true)
    public ResponseEntity<Object> update(@PathVariable Integer id,
                                         @RequestBody @Valid Endereco endereco,
                                         @AuthenticationPrincipal Usuario usuario) {
        log.info("Atualizando endereço " + id + " com " + endereco);

        Endereco oldEndereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));

        if (!oldEndereco.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para atualizar este endereço");
        }

        BeanUtils.copyProperties(endereco, oldEndereco, "id", "unidade");

        Endereco savedEndereco = enderecoRepository.save(oldEndereco);

        EnderecoDTO enderecoDTO = mapToEnderecoDTO(savedEndereco);
        return ResponseEntity.ok(enderecoDTO);
    }

    private EnderecoDTO mapToEnderecoDTO(Endereco endereco) {
        String pais = endereco.getCidade() != null && endereco.getCidade().getEstado() != null
                ? endereco.getCidade().getEstado().getPais().getNome()
                : null;

        String estado = endereco.getCidade() != null && endereco.getCidade().getEstado() != null
                ? endereco.getCidade().getEstado().getNome()
                : null;

        String cidade = endereco.getCidade() != null ? endereco.getCidade().getNome() : null;

        UnidadeReadDTO unidadeReadDTO = new UnidadeReadDTO(
                endereco.getUnidade().getIdUnidade(),
                endereco.getUnidade().getNome(),
                endereco.getUnidade().getCapacidadeTotalLitros(),
                endereco.getUnidade().getDataCadastro().toString(),
                new UsuarioResponseDTO(
                        endereco.getUnidade().getUsuario().getIdUsuario(),
                        endereco.getUnidade().getUsuario().getNome(),
                        endereco.getUnidade().getUsuario().getEmail()
                )
        );

        return new EnderecoDTO(
                endereco.getId(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getCep(),
                pais,
                estado,
                cidade,
                unidadeReadDTO
        );
    }
}
