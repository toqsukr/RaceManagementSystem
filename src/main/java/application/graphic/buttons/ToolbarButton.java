package application.graphic.buttons;

import application.graphic.eventListeners.EventListener;
import application.graphic.interfaces.CallbackInterface;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ToolbarButton extends JButton {

    public ToolbarButton(String tooltipText, String picPath, CallbackInterface callback) {
        this.setToolTipText(tooltipText);

        setBtnImage(loadResource(picPath));

        this.setBackground(new Color(0xDFD9D9D9, false));

        this.setFocusable(false);

        this.addActionListener(new EventListener(callback));
    }

    private void setBtnImage(URL resUrl) {
        this.setIcon(new ImageIcon(new ImageIcon(resUrl).getImage().getScaledInstance(50, 50, 4)));
    }

    private URL loadResource(String localPath) {
        return this.getClass().getClassLoader().getResource(localPath);
    }
}
