package database;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import race.system.Score;

public class ScoreDao {

    private EntityManager em;

    private boolean[] freeIDs;

    public ScoreDao(EntityManager em) {
        this.em = em;
        em.setFlushMode(FlushModeType.COMMIT);
        freeIDs = new boolean[2000];
        for (int i = 0; i < 2000; i++)
            freeIDs[i] = true;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public List<Score> getAllScores() throws HibernateException {
        try {
            em.getTransaction().begin();
            List<Score> scores = em.createQuery("FROM Score", Score.class)
                    .setHint("jakarta.persistence.cache.storeMode", "REFRESH").getResultList();
            em.clear();
            em.getTransaction().commit();
            return scores;

        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
            return null;
        }
    }

    public Score findScore(int id) {
        em.getTransaction().begin();
        Score score = em.find(Score.class, id);
        em.getTransaction().commit();
        return score;
    }

    public void saveScore(Score score) {
        try {
            em.getTransaction().begin();
            em.persist(score);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void updateScore(Score score) {
        try {
            em.getTransaction().begin();
            em.merge(score);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void deleteScore(Score score) {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Score r WHERE scoreID = :id", null).setParameter("id", score.getScoreID())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void clearScore() {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Score").executeUpdate();
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

    public void updateFreeID(List<Score> scores) {
        for (Score score : scores) {
            freeIDs[score.getScoreID()] = false;
        }
    }
}
