package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.LeituraDispositivo;
import br.com.fiap.mais_agua.repository.DispositivoRepository;
import br.com.fiap.mais_agua.repository.HistoricoReservatorioRepository;
import br.com.fiap.mais_agua.repository.LeituraDispositivoRepository;
import br.com.fiap.mais_agua.repository.ReservatorioDispositivoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class LeituraDispositivoService {

    @Autowired
    private LeituraDispositivoRepository leituraRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Autowired
    private ReservatorioDispositivoRepository reservatorioDispositivoRepository;

    @Autowired
    private HistoricoReservatorioRepository historicoRepository;

    Random random = new Random();

    /**
     * Gera leituras dos dispositivos de acordo com o último nível do histórico do reservatório
     */
    @CacheEvict(value = "leituraDispositivo", allEntries = true)
    @Scheduled(cron = "0 10 6 * * *", zone = "America/Sao_Paulo") // Executa todos os dias às 6:10
    public void gerarLeitura() {
        System.out.println("Iniciando geração de leitura...");

        List<Dispositivo> dispositivos = dispositivoRepository.findAll();

        for (Dispositivo dispositivo : dispositivos) {
            var reservatorioOpt = reservatorioDispositivoRepository.findReservatorioByDispositivo(dispositivo.getIdDispositivo());

            if (reservatorioOpt.isEmpty()) {
                System.out.println("Dispositivo " + dispositivo.getIdDispositivo() + " não tem reservatório. Pulando...");
                continue;
            }

            var reservatorio = reservatorioOpt.get();

            var historicoOpt = historicoRepository
                    .findTopByReservatorioIdReservatorioOrderByDataHoraDesc(reservatorio.getIdReservatorio());

            if (historicoOpt.isEmpty()) {
                System.out.println("Reservatório " + reservatorio.getIdReservatorio() + " sem histórico. Pulando...");
                continue;
            }

            var historico = historicoOpt.get();

            // Cálculo do nível em %
            int capacidade = reservatorio.getCapacidadeTotalLitros();
            int nivelLitros = historico.getNivelLitros();
            int nivelPct = capacidade > 0 ? (int) ((nivelLitros * 100.0) / capacidade) : 0;

            // Gerar valores aleatórios
            int turbidez = random.nextInt(101); // 0 a 100
            double ph = 5 + (9 * random.nextDouble()); // entre 5 e 14

            // Criar a leitura
            var leitura = new LeituraDispositivo();
            leitura.setDispositivo(dispositivo);
            leitura.setDataHora(LocalDateTime.now());
            leitura.setNivelPct(nivelPct);
            leitura.setTurbidezNtu(turbidez);
            leitura.setPhInt(BigDecimal.valueOf(ph).setScale(2, RoundingMode.HALF_UP));

            // Salvar no banco
            leituraRepository.save(leitura);

            System.out.println("Leitura salva -> Dispositivo " + dispositivo.getIdDispositivo() +
                    " | Nível: " + nivelPct + "%" +
                    " | Turbidez: " + turbidez +
                    " | pH: " + leitura.getPhInt());
        }

        System.out.println("Finalizou geração de leituras.");
    }
}
