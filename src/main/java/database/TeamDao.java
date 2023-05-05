package database;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;

import jakarta.persistence.EntityManager;
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
            return em.createQuery("FROM Team", Team.class).getResultList();

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
}