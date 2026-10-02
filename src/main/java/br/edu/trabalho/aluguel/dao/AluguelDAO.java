package br.edu.trabalho.aluguel.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AluguelDAO {
    public Aluguel salvar(Aluguel aluguel) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(aluguel);
            tx.commit();
            return aluguel;
        }
    }

    public List<Aluguel> listarTodosComRelacionamentos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select a from Aluguel a join fetch a.cliente join fetch a.bicicleta", Aluguel.class)
                    .getResultList();
        }
    }

    public boolean existeParaCliente(Long clienteId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long quantidade = session.createQuery(
                            "select count(a) from Aluguel a where a.cliente.id = :clienteId", Long.class)
                    .setParameter("clienteId", clienteId).getSingleResult();
            return quantidade > 0;
        }
    }

    public boolean existeParaBicicleta(Long bicicletaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long quantidade = session.createQuery(
                            "select count(a) from Aluguel a where a.bicicleta.id = :bicicletaId", Long.class)
                    .setParameter("bicicletaId", bicicletaId).getSingleResult();
            return quantidade > 0;
        }
    }

    public void atualizar(Aluguel aluguel) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(aluguel);
            tx.commit();
        }
    }

    public void excluir(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Aluguel aluguel = session.get(Aluguel.class, id);
            if (aluguel != null) session.remove(aluguel);
            tx.commit();
        }
    }
}
