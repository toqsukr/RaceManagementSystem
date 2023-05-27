package application.graphic.eventListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import application.graphic.interfaces.CallbackInterface;

/**
 * Сlass for implementing a addBtn button listener
 */
public class AddEventListener implements ActionListener {
    private CallbackInterface callback;

    public AddEventListener(CallbackInterface callback) {
        this.callback = callback;
    }

    /***
     *
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        callback.onEvent();
    }
}
