package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "qwerty");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .addProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        Session session = sessionFactory.openSession();
        try(session) {
            NativeQuery<Player> query = session.createNativeQuery("select * from player", Player.class);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    @Override
    public int getAllCount() {
        Session session = sessionFactory.openSession();
        try(session) {
            Query<Long> query = session.createNamedQuery("getAllCount", Long.class);
            Long result = query.getSingleResult();
            return Math.toIntExact(result);
        }
    }

    @Override
    public Player save(Player player) {
        Session session = sessionFactory.openSession();
        try (session){
            session.beginTransaction();
            session.save(player);
            session.getTransaction().commit();
            return player;
        }
    }

    @Override
    public Player update(Player player) {
        Session session = sessionFactory.openSession();
        try(session) {
            session.beginTransaction();
            session.update(player);
            session.getTransaction().commit();
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        Session session = sessionFactory.openSession();
        try(session) {
            Player player = session.find(Player.class, id);
            return Optional.of(player);
        }
    }

    @Override
    public void delete(Player player) {
        Session session = sessionFactory.openSession();
        try(session) {
            session.beginTransaction();
            session.remove(player);
            session.getTransaction().commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
    sessionFactory.close();
    }
}