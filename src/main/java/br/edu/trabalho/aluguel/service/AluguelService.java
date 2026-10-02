package br.edu.trabalho.aluguel.service;

import br.edu.trabalho.aluguel.config.HibernateUtil;
import br.edu.trabalho.aluguel.model.Aluguel;
import br.edu.trabalho.aluguel.model.Bicicleta;
import br.edu.trabalho.aluguel.model.Cliente;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AluguelService {
    public Aluguel realizar(Long clienteId, Long bicicletaId, LocalDate inicio, LocalDate fimPrevista) {
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
                BigDecimal total = bicicleta.getValorDiaria().multiply(BigDecimal.valueOf(dias));
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

    public void finalizar(Long aluguelId, LocalDate dataDevolucao) {
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
                aluguel.getBicicleta().setDisponivel(true);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}
