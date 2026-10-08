package br.edu.exemplo.aluguel.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** Centraliza a criação da SessionFactory do Hibernate. */
public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = new Configuration()
            .configure()
            .buildSessionFactory();

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        SESSION_FACTORY.close();
    }
}
