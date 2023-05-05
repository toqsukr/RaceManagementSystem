package database;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import race.system.Racer;

public class RacerDao {

    private SessionFactory sessionFactory;

    public RacerDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Racer> getAllRacers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT mydb FROM Racer", Racer.class).list();
        }
    }

    public void saveRacer(Racer racer) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(racer);
            tx.commit();
        }
    }
}
