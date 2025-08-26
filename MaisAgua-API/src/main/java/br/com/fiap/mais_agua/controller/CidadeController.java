package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Cidade;
import br.com.fiap.mais_agua.repository.CidadeRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/cidades")
@Tag(name = "Endereço")
public class CidadeController {

    @Autowired
    private CidadeRepository repository;

    @GetMapping
    @Operation(
            summary = "Listar todas as cidades",
            description = "Retorna todas as cidades cadastradas no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    @Cacheable("cidades")
    public List<Cidade> listar() {
        return repository.findAll();
    }

    @GetMapping("/por-estado/{idEstado}")
    @Operation(
            summary = "Listar cidades por estado",
            description = "Retorna as cidades pertencentes ao estado informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    public List<Cidade> listarPorEstado(@PathVariable Integer idEstado) {
        return repository.findByEstadoId(idEstado);
    }
}

