package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.*;
import br.com.fiap.mais_agua.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservatorioService {
    @Autowired
    private ReservatorioRepository reservatorioRepository;
    @Autowired
    private UnidadeRepository unidadeRepository;
    @Autowired

    private DispositivoRepository dispositivoRepository;
    @Autowired

    private ReservatorioDispositivoRepository reservatorioDispositivoRepository;
    @Autowired
    private HistoricoReservatorioRepository historicoReservatorioRepository;

    // executar um conjunto de operações no bd em uma unica transação
    @Transactional
    public Reservatorio criarReservatorio(Reservatorio reservatorio, Usuario usuario) {
        log.info("Cadastrando reservatório: {}", reservatorio.getNome());

        Unidade unidade = unidadeRepository.findById(reservatorio.getUnidade().getIdUnidade())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        if (!unidade.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar essa unidade");
        }

        if (reservatorio.getCapacidadeTotalLitros() > unidade.getCapacidadeTotalLitros()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O reservátorio não pode ultrapassar a capacidade total de litros da unidade, que é "
                            + unidade.getCapacidadeTotalLitros());
        }

        validarCapacidadeReservatorios(unidade, reservatorio.getCapacidadeTotalLitros());

        reservatorio.setUnidade(unidade);
        Reservatorio novoReservatorio = reservatorioRepository.save(reservatorio);

        List<Dispositivo> dispositivosSemVinculo = dispositivoRepository.findDispositivosSemReservatorio();

        Dispositivo dispositivo;
        if (!dispositivosSemVinculo.isEmpty()) {
            dispositivo = dispositivosSemVinculo.get(0); // Pega o primeiro disponível
            log.info("Associando dispositivo existente: {}", dispositivo.getIdDispositivo());
        } else {
            dispositivo = Dispositivo.builder()
                    .dataInstalacao(LocalDateTime.now())
                    .build();
            dispositivo = dispositivoRepository.save(dispositivo);
            log.info("Criando novo dispositivo: {}", dispositivo.getIdDispositivo());
        }

        ReservatorioDispositivo vinculo = ReservatorioDispositivo.builder()
                .dataInstalacao(LocalDateTime.now())
                .reservatorio(novoReservatorio)
                .dispositivo(dispositivo)
                .build();
        reservatorioDispositivoRepository.save(vinculo);

        return novoReservatorio;
    }


    private void validarCapacidadeReservatorios(Unidade unidade, Integer capacidadeNovoReservatorio) {
        // Soma dos reservatórios já existentes
        Integer capacidadeTotalReservatorios = reservatorioRepository.findByUnidade(unidade)
                .stream()
                .mapToInt(Reservatorio::getCapacidadeTotalLitros)
                .sum();

        // Verifica se a soma atual + novo reservatório ultrapassa a capacidade da unidade
        if ((capacidadeTotalReservatorios + capacidadeNovoReservatorio) > unidade.getCapacidadeTotalLitros()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A capacidade total dos reservatórios excede a capacidade da unidade. " +
                            "Capacidade da unidade: " + unidade.getCapacidadeTotalLitros() + " litros. " +
                            "Capacidade já utilizada: " + capacidadeTotalReservatorios + " litros. " +
                            "Tentando adicionar mais: " + capacidadeNovoReservatorio + " litros.");
        }
    }

    @Transactional
    public void deletarReservatorio(Integer id, Usuario usuario) {
        Reservatorio reservatorio = reservatorioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservatório não encontrado"));

        if (!reservatorio.getUnidade().getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este reservatório");
        }
        if (historicoReservatorioRepository.existsByReservatorio(reservatorio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível apagar o reservatório, pois há histórico vinculado.");
        }

        var vinculos = reservatorioDispositivoRepository.findByReservatorio(reservatorio);
        reservatorioDispositivoRepository.deleteAll(vinculos);

        reservatorioRepository.delete(reservatorio);
    }
}
