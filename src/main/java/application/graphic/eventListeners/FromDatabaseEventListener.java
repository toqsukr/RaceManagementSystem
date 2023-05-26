package application.graphic.eventListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import application.graphic.MainMenuGUI;

/**
 * Сlass for implementing a fromDataBase button listener
 */
public class FromDatabaseEventListener implements ActionListener {
    private MainMenuGUI window;

    public FromDatabaseEventListener(MainMenuGUI window) {
        this.window = window;
    }

    /***
     *
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        window.getMainRacerGUI().downloadFromDataBase();
    }
}
