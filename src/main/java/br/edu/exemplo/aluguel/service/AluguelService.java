package br.edu.exemplo.aluguel.service;

import br.edu.exemplo.aluguel.config.HibernateUtil;
import br.edu.exemplo.aluguel.model.Aluguel;
import br.edu.exemplo.aluguel.model.Bicicleta;
import br.edu.exemplo.aluguel.model.Cliente;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Regras que alteram aluguel e bicicleta na mesma transação do Hibernate. */
public class AluguelService {
    private static final BigDecimal TAXA_JUROS_DIARIA = new BigDecimal("0.005");

    public Aluguel realizar(Long clienteId, Long bicicletaId, LocalDate inicio, LocalDate fimPrevista) {
        if (clienteId == null) throw new IllegalArgumentException("Informe o cliente.");
        if (bicicletaId == null) throw new IllegalArgumentException("Informe a bicicleta.");
        if (inicio == null || fimPrevista == null) throw new IllegalArgumentException("Informe as datas do aluguel.");
        if (!fimPrevista.isAfter(inicio)) {
            throw new IllegalArgumentException("A data prevista deve ser posterior à data de início.");
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Cliente cliente = session.get(Cliente.class, clienteId);
                if (cliente == null) throw new IllegalArgumentException("Cliente não encontrado.");
                Bicicleta bicicleta = session.get(Bicicleta.class, bicicletaId);
                if (bicicleta == null) throw new IllegalArgumentException("Bicicleta não encontrada.");
                if (!bicicleta.isDisponivel()) throw new IllegalStateException("A bicicleta já está alugada.");

                long dias = ChronoUnit.DAYS.between(inicio, fimPrevista);
                BigDecimal total = bicicleta.getValorDiaria().multiply(BigDecimal.valueOf(dias)).setScale(2, RoundingMode.HALF_UP);
                Aluguel aluguel = new Aluguel(inicio, fimPrevista, total, cliente, bicicleta);
                session.persist(aluguel);
                bicicleta.setDisponivel(false);
                tx.commit();
                return aluguel;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    public Aluguel finalizar(Long aluguelId, LocalDate dataDevolucao) {
        if (aluguelId == null) throw new IllegalArgumentException("Informe o aluguel.");
        if (dataDevolucao == null) throw new IllegalArgumentException("Informe a data de devolução.");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Aluguel aluguel = session.get(Aluguel.class, aluguelId);
                if (aluguel == null) throw new IllegalArgumentException("Aluguel não encontrado.");
                if (aluguel.getDataDevolucao() != null) throw new IllegalStateException("Este aluguel já foi finalizado.");
                if (dataDevolucao.isBefore(aluguel.getDataInicio())) {
                    throw new IllegalArgumentException("A devolução não pode ser anterior ao início.");
                }
                aluguel.setDataDevolucao(dataDevolucao);
                aluguel.setValorTotal(aluguel.getValorTotal().add(calcularJurosAtraso(aluguel, dataDevolucao)));
                aluguel.getBicicleta().setDisponivel(true);
                tx.commit();
                return aluguel;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    private BigDecimal calcularJurosAtraso(Aluguel aluguel, LocalDate dataDevolucao) {
        long diasAtraso = ChronoUnit.DAYS.between(aluguel.getDataFimPrevista(), dataDevolucao);
        if (diasAtraso <= 0) return BigDecimal.ZERO;
        return aluguel.getBicicleta().getValorDiaria()
                .multiply(TAXA_JUROS_DIARIA)
                .multiply(BigDecimal.valueOf(diasAtraso))
                .setScale(2, RoundingMode.HALF_UP);
    }
}

