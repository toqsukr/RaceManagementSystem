package database;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import race.system.Team;

public class TeamDao {

    private EntityManager em;

    public TeamDao(EntityManager em) {
        this.em = em;
        em.setFlushMode(FlushModeType.COMMIT);
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public List<Team> getAllTeams() throws HibernateException {
        try {
            em.getTransaction().begin();
            List<Team> teams = em.createQuery("FROM Team", Team.class).getResultList();
            em.getTransaction().commit();
            return teams;

        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
            return null;
        }
    }

    public Team findTeam(int id) {
        em.getTransaction().begin();
        Team team = em.find(Team.class, id);
        em.getTransaction().commit();
        return team;
    }

    public void saveTeam(Team team) {
        try {
            em.getTransaction().begin();
            em.persist(team);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }

    }

    public void updateTeam(Team team) {
        try {
            em.getTransaction().begin();
            em.merge(team);
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void updateTeamID(Team team, Integer value) {
        em.getTransaction().begin();
        team.setTeamID(value);
        em.getTransaction().commit();
    }

    public void updateTeamName(Team team, String value) {
        em.getTransaction().begin();
        team.setTeamName(value);
        em.getTransaction().commit();
    }

    public void deleteTeam(Team team) {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Team t WHERE teamID = :id", null).setParameter("id", team.getTeamID())
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public void clearTeam() {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.getTransaction().commit();
        } catch (HibernateException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }
}
