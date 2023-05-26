package application.graphic.eventListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import application.graphic.MainMenuGUI;

/**
 * Сlass for implementing a toDataBase button listener
 */
public class ToDatabaseEventListener implements ActionListener {
    private MainMenuGUI window;

    public ToDatabaseEventListener(MainMenuGUI window) {
        this.window = window;
    }

    /***
     *
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        window.getMainRacerGUI().deployToDataBase();
    }
}
