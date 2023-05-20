package database;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Query;
import race.system.Racer;

public class RacerDao {

    private EntityManager em;

    public RacerDao(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public List<Racer> getAllRacers() throws HibernateException {
        try {
            Query q = em.createQuery("FROM Racer", Racer.class);
            q.setFlushMode(FlushModeType.COMMIT);
            return q.getResultList();

        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
            return null;
        }
    }

    public void saveRacer(Racer racer) {
        try {
            em.persist(racer);
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void deleteRacer(Racer racer) {
        try {
            em.createQuery("DELETE FROM Racer r WHERE racerID = :id", null).setParameter("id", racer.getRacerID())
                    .executeUpdate();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void clearRacer() {
        try {
            em.createQuery("DELETE FROM Racer").executeUpdate();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }
}
