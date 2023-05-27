package application.graphic.eventListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import application.graphic.interfaces.CallbackInterface;

public class EventListener implements ActionListener {

    private CallbackInterface callback;

    public EventListener(CallbackInterface callback) {
        this.callback = callback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        callback.onEvent();
    }

}
