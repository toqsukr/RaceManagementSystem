package database;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Query;
import race.system.Team;

public class TeamDao {

    private EntityManager em;

    public TeamDao(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public List<Team> getAllTeams() throws HibernateException {
        try {
            Query q = em.createQuery("FROM Team", Team.class);
            q.setFlushMode(FlushModeType.COMMIT);
            return q.getResultList();

        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
            return null;
        }
    }

    public void saveTeam(Team team) {
        try {
            em.persist(team);
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }

    }

    public void deleteTeam(Team team) {
        try {
            em.createQuery("DELETE FROM Team t WHERE teamID = :id", null).setParameter("id", team.getTeamID())
                    .executeUpdate();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void clearTeam() {
        try {
            em.createQuery("DELETE FROM Team").executeUpdate();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }
}
