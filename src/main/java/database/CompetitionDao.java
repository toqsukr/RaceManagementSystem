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

    public void updateFreeID(List<Competition> competitions) {
        for (Competition competition : competitions) {
            freeIDs[competition.getCompetitionId()] = false;
        }
    }
}
