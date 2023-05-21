package database;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import race.system.Racer;

public class RacerDao {

    private EntityManager em;

    public RacerDao(EntityManager em) {
        this.em = em;
        em.setFlushMode(FlushModeType.COMMIT);

    }

    public EntityManager getEntityManager() {
        return em;
    }

    public List<Racer> getAllRacers() throws HibernateException {
        try {
            em.getTransaction().begin();
            List<Racer> racers = em.createQuery("FROM Racer", Racer.class)
                    .setHint("jakarta.persistence.cache.storeMode", "REFRESH").getResultList();
            em.clear();
            em.getTransaction().commit();
            return racers;

        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
            return null;
        }
    }

    public Racer findRacer(int id) {
        em.getTransaction().begin();
        Racer racer = em.find(Racer.class, id);
        em.getTransaction().commit();
        return racer;
    }

    public void saveRacer(Racer racer) {
        try {
            em.getTransaction().begin();
            em.persist(racer);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void updateRacer(Racer racer) {
        try {
            em.getTransaction().begin();
            em.merge(racer);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void deleteRacer(Racer racer) {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Racer r WHERE racerID = :id", null).setParameter("id", racer.getRacerID())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void clearRacer() {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Racer").executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }
}
