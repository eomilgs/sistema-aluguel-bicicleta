package br.edu.exemplo.aluguel.dao;

import br.edu.exemplo.aluguel.config.HibernateUtil;
import br.edu.exemplo.aluguel.model.Cliente;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

/** Operações CRUD de Cliente usando Session e Transaction do Hibernate diretamente. */
public class ClienteDao {
    public Cliente salvar(Cliente cliente) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(cliente);
            tx.commit();
            return cliente;
        }
    }

    public Cliente buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Cliente.class, id);
        }
    }

    public List<Cliente> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Cliente", Cliente.class).getResultList();
        }
    }

    public void atualizar(Cliente cliente) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(cliente);
            tx.commit();
        }
    }

    public void excluir(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Cliente cliente = session.get(Cliente.class, id);
            if (cliente != null) session.remove(cliente);
            tx.commit();
        }
    }
}
