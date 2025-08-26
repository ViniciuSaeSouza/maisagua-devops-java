package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.DTO.UnidadeReadDTO;
import br.com.fiap.mais_agua.model.DTO.UnidadeResponseDTO;
import br.com.fiap.mais_agua.model.DTO.UsuarioResponseDTO;
import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.UnidadeRepository;
import br.com.fiap.mais_agua.service.UnidadeService;
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

@RestController
@RequestMapping("/unidade")
@Slf4j
@Tag(name = "Unidade", description = "Operações relacionadas as unidades do usuário")
public class UnidadeController {
    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private UnidadeService unidadeService;

    @GetMapping
    @Operation(
            summary = "Listar unidades",
            description = "Lista todas as unidades cadastradas pelo usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de unidades retornada com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
            }
    )
    @Cacheable("unidades")
    public List<UnidadeReadDTO> index(@AuthenticationPrincipal Usuario usuario) {
        return unidadeRepository.findByUsuario(usuario)
                .stream()
                .map(unidade -> {
                    UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                            unidade.getUsuario().getIdUsuario(),
                            unidade.getUsuario().getNome(),
                            unidade.getUsuario().getEmail()
                    );

                    return new UnidadeReadDTO(
                            unidade.getIdUnidade(),
                            unidade.getNome(),
                            unidade.getCapacidadeTotalLitros(),
                            unidade.getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            usuarioDTO
                    );
                })
                .toList();
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(
            summary = "Cadastrar nova unidade",
            description = "Cria uma nova unidade vinculada ao usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Unidade cadastrada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos")
            }
    )
    @CacheEvict(value = "unidades", allEntries = true)
    public UnidadeResponseDTO create(@RequestBody @Valid Unidade unidade, @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando unidade " + unidade.getNome());

        unidade.setUsuario(usuario);

        Unidade unidadeSalva = unidadeRepository.save(unidade);

        UnidadeResponseDTO responseDTO = new UnidadeResponseDTO(
                unidadeSalva.getNome(),
                unidadeSalva.getCapacidadeTotalLitros(),
                unidadeSalva.getUsuario().getIdUsuario());

        return responseDTO;
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Buscar unidade por ID",
            description = "Retorna os dados de uma unidade específica pertencente ao usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unidade encontrada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado"),
                    @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
            }
    )
    public ResponseEntity<UnidadeReadDTO> get(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        log.info("Buscando unidade " + id);

        Unidade unidade = getUnidade(id, usuario);

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                unidade.getUsuario().getIdUsuario(),
                unidade.getUsuario().getNome(),
                unidade.getUsuario().getEmail()
        );

        UnidadeReadDTO dto = new UnidadeReadDTO(
                unidade.getIdUnidade(),
                unidade.getNome(),
                unidade.getCapacidadeTotalLitros(),
                unidade.getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                usuarioDTO
        );

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Excluir unidade",
            description = "Remove uma unidade e todos os reservatórios associados, se forem do usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Unidade excluída com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado"),
                    @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
                    @ApiResponse(responseCode = "409", description = "Não é possível excluir a unidade, porque um reservatório vinculado possui histórico.")
            }
    )
    @CacheEvict(value = "unidades", allEntries = true)
    public ResponseEntity<Void> destroy(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        log.info("Excluindo unidade {}", id);
        unidadeService.deletarUnidade(id, usuario);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("{id}")
    @Operation(
            summary = "Atualizar unidade",
            description = "Atualiza os dados de uma unidade existente vinculada ao usuário autenticado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unidade atualizada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado"),
                    @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
            }
    )
    @CacheEvict(value = "unidades", allEntries = true)
    public ResponseEntity<UnidadeReadDTO> update(@PathVariable Integer id, @RequestBody @Valid Unidade unidade, @AuthenticationPrincipal Usuario usuario
    ) {
        log.info("Atualizando unidade " + id + " com " + unidade);

        var oldUnidade = getUnidade(id, usuario);

        BeanUtils.copyProperties(unidade, oldUnidade, "idUnidade", "usuario", "dataCadastro");
        unidadeRepository.save(oldUnidade);

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                oldUnidade.getUsuario().getIdUsuario(),
                oldUnidade.getUsuario().getNome(),
                oldUnidade.getUsuario().getEmail()
        );

        UnidadeReadDTO dto = new UnidadeReadDTO(
                oldUnidade.getIdUnidade(),
                oldUnidade.getNome(),
                oldUnidade.getCapacidadeTotalLitros(),
                oldUnidade.getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                usuarioDTO
        );

        return ResponseEntity.ok(dto);
    }


    private Unidade getUnidade(Integer id, Usuario usuario){
        var unidadeFind = unidadeRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada")
                );

        if(!unidadeFind.getUsuario().equals(usuario)){
            throw new ResponseStatusException((HttpStatus.FORBIDDEN));
        }
        return unidadeFind;
    }
}
