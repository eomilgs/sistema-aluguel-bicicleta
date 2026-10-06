package br.edu.trabalho.aluguel.dao;

import br.edu.trabalho.aluguel.config.HibernateUtil;
import br.edu.trabalho.aluguel.model.Cliente;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ClienteDAO {
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
