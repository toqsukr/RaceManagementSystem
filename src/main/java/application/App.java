package application;

import application.graphic.MainMenuGUI;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class App {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("rms_persistence");

    private static EntityManager em = emf.createEntityManager();

    public static void main(String[] args) {
        MainMenuGUI mainMenuWindow = new MainMenuGUI();
        mainMenuWindow.setVisible(true);
    }

    public static EntityManager getEntityManager() {
        return em;
    }
}
