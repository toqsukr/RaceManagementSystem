import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateError;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;

import application.graphic.MainMenuGUI;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import race.system.Racer;
import race.system.Team;

public class App {

    public static void main(String[] args) {

        MainMenuGUI mainMenuWindow = new MainMenuGUI();
        mainMenuWindow.setVisible(true);
        // try {
        // EntityManagerFactory emf =
        // Persistence.createEntityManagerFactory("rms_persistence");
        // EntityManager em = emf.createEntityManager();
        // System.out.println("Start hibernate");
        // em.getTransaction().begin();

        // List<Team> teamList = em.createQuery("FROM Team").getResultList();
        // Racer racer1 = new Racer();
        // racer1.setRacerName("Фернандо Алонсо");
        // racer1.setRacerAge(42);
        // racer1.setTeam(teamList.get(0));
        // racer1.setRacerPoints(4500);
        // em.persist(racer1);
        // em.getTransaction().commit();
        // } catch (HibernateError exception) {
        // JOptionPane.showMessageDialog(null, exception.getMessage(), "Hibernate
        // error", 0, null);
        // } catch (PersistenceException exception) {
        // exception.printStackTrace();
        // JOptionPane.showMessageDialog(null, exception.getMessage(), "Persistence
        // error", 0, null);
        // }

    }
}
