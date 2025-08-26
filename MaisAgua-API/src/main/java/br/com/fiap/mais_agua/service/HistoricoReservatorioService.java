package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.HistoricoReservatorio;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.StatusReservatorio;
import br.com.fiap.mais_agua.repository.HistoricoReservatorioRepository;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.repository.StatusReservatorioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoricoReservatorioService {

    @Autowired
    private ReservatorioRepository reservatorioRepository;
    @Autowired
    private HistoricoReservatorioRepository historicoRepository;
    @Autowired
    private StatusReservatorioRepository statusRepository;

    private final Random random = new Random();

    // Executa todo dia às 6h da manhã
    @CacheEvict(value = "historicoReservatorio", allEntries = true)
    @Scheduled(cron = "0 0 6 * * *", zone = "America/Sao_Paulo")
    public void gerarHistoricoDiario() {
        log.info("Iniciando geração de histórico dos reservatórios...");

        List<Reservatorio> reservatorios = reservatorioRepository.findAll();

        for (Reservatorio reservatorio : reservatorios) {
            int capacidadeMaxima = reservatorio.getCapacidadeTotalLitros();

            // Gerar nível aleatório entre 5% e 100% da capacidade
            int nivelLitros = gerarNivelAleatorio(capacidadeMaxima);

            // Definir status com base no nível
            StatusReservatorio status = definirStatus(nivelLitros, capacidadeMaxima);

            HistoricoReservatorio historico = new HistoricoReservatorio();
            historico.setReservatorio(reservatorio);
            historico.setNivelLitros(nivelLitros);
            historico.setDataHora(LocalDateTime.now());
            historico.setStatus(status);

            historicoRepository.save(historico);

            log.info("Histórico gerado para reservatório {}: {} litros, status {}",
                    reservatorio.getNome(), nivelLitros, status.getStatus());
        }

        log.info("Geração de históricos concluída.");
    }

    private int gerarNivelAleatorio(int capacidadeMaxima) {
        // Gera um valor aleatório entre 5% e 100% da capacidade
        double percentual = 0.05 + (0.95 * random.nextDouble());
        return Math.max(1, (int) (capacidadeMaxima * percentual));
    }

    private StatusReservatorio definirStatus(int nivel, int capacidade) {
        double percentual = (double) nivel / capacidade * 100;

        String nomeStatus;
        if (percentual >= 90) {
            nomeStatus = "Cheio";
        } else if (percentual >= 70) {
            nomeStatus = "Normal";
        } else if (percentual >= 40) {
            nomeStatus = "Baixo";
        } else if (percentual >= 10) {
            nomeStatus = "Crítico";
        } else {
            nomeStatus = "Esvaziado";
        }

        return statusRepository.findByStatus(nomeStatus)
                .orElseThrow(() -> new RuntimeException("Status não encontrado: " + nomeStatus));
    }
}
