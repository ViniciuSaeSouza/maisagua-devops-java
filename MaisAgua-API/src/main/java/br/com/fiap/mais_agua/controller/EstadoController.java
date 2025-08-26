package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Estado;
import br.com.fiap.mais_agua.repository.EstadoRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/estados")
@Tag(name = "Endereço")
public class EstadoController {

    @Autowired
    private EstadoRepository repository;

    @GetMapping
    @Operation(
            summary = "Listar todos os estados",
            description = "Retorna todos os estados cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    @Cacheable("estados")
    public List<Estado> listar() {
        return repository.findAll();
    }

    @GetMapping("/por-pais/{idPais}")
    @Operation(
            summary = "Listar estados por país",
            description = "Retorna os estados pertencentes ao país informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            }
    )
    public List<Estado> listarPorPais(@PathVariable Integer idPais) {
        return repository.findByPaisId(idPais);
    }
}

