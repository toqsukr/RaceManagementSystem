import java.util.List;

import application.graphic.MainMenuGUI;
import database.RacerDao;
import race.system.Racer;
import race.system.Team;

public class App {

    public static void main(String[] args) {

        // RacerDao racerDao = new RacerDao();
        // System.out.println("Start hibernate");
        // racerDao.getEntityManager().getTransaction().begin();
        // Team team1 = new Team("Alpha Romeo");
        // Racer racer1 = new Racer("Ник Вуйчич", 50, team1, 900);
        // racerDao.saveRacer(racer1);
        // List<Racer> racers = racerDao.getAllRacers();
        // for (int i = 0; i < racers.size(); i++) {
        // racers.get(i).showRacerInfo();
        // }
        // racerDao.getEntityManager().getTransaction().commit();

        MainMenuGUI mainMenuWindow = new MainMenuGUI();
        mainMenuWindow.setVisible(true);

    }
}
