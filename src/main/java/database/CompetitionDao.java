package database;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import race.system.Competition;

public class CompetitionDao {
    private EntityManager em;

    private boolean[] freeIDs;

    public CompetitionDao(EntityManager em) {
        this.em = em;
        em.setFlushMode(FlushModeType.COMMIT);
        freeIDs = new boolean[2000];
        for (int i = 0; i < 2000; i++)
            freeIDs[i] = true;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public List<Competition> getAllCompetitions() throws HibernateException {
        try {
            em.getTransaction().begin();
            List<Competition> competitons = em.createQuery("FROM Competition", Competition.class)
                    .setHint("jakarta.persistence.cache.storeMode", "REFRESH").getResultList();
            em.clear();
            em.getTransaction().commit();
            return competitons;

        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
            return null;
        }
    }

    public Competition findCompetition(int id) {
        em.getTransaction().begin();
        Competition competition = em.find(Competition.class, id);
        em.clear();
        em.getTransaction().commit();
        return competition;
    }

    public void saveCompetition(Competition competition) {
        try {
            em.getTransaction().begin();
            em.persist(competition);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void updateCompetition(Competition competition) {
        try {
            em.getTransaction().begin();
            em.merge(competition);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void deleteCompetition(Competition competition) {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Competition c WHERE competitionID = :id", null)
                    .setParameter("id", competition.getCompetitionID())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void clearCompetition() {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Competition").executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public int getFreeID() {
        int id = 0;
        em.getTransaction().begin();
        for (int i = 0; i < freeIDs.length; i++) {
            if (freeIDs[i]) {
                id = i;
                break;
            }
        }
        em.getTransaction().commit();
        return id;
    }

    public void addFreeID(int id) {
        freeIDs[id] = true;
    }

    public void updateFreeID(List<Competition> competitions) {
        for (Competition competition : competitions) {
            freeIDs[competition.getCompetitionID()] = false;
        }
    }
}
