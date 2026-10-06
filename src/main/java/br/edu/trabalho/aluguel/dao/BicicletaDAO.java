package br.edu.trabalho.aluguel.dao;

import br.edu.trabalho.aluguel.config.HibernateUtil;
import br.edu.trabalho.aluguel.model.Bicicleta;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BicicletaDAO {
    public Bicicleta salvar(Bicicleta bicicleta) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(bicicleta);
            tx.commit();
            return bicicleta;
        }
    }

    public Bicicleta buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Bicicleta.class, id);
        }
    }

    public List<Bicicleta> listarDisponiveis() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Bicicleta where disponivel = true", Bicicleta.class)
                    .getResultList();
        }
    }

    public List<Bicicleta> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Bicicleta order by id", Bicicleta.class).getResultList();
        }
    }

    public void atualizar(Bicicleta bicicleta) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(bicicleta);
            tx.commit();
        }
    }

    public void excluir(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Bicicleta bicicleta = session.get(Bicicleta.class, id);
            if (bicicleta != null) session.remove(bicicleta);
            tx.commit();
        }
    }
}
