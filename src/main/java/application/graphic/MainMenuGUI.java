package application.graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuGUI {
    private static JFrame mainMenuGUI = new JFrame("Главное меню");

    private final JButton racerBtn = new JButton("Гонщики");

    private final JButton trackBtn = new JButton("Трассы");

    private final JButton graphicBtn = new JButton("Расписание");

    private static JLabel title = new JLabel("Система управления автогонками");

    public void show() {
        mainMenuGUI.setBounds(650, 200, 300, 400);
        mainMenuGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuGUI.setResizable(false);
        mainMenuGUI.setIconImage(Toolkit.getDefaultToolkit().getImage("etu/src/img/favicon.png"));

        racerBtn.setBackground(new Color(0xDFD9D9D9, false));
        racerBtn.addActionListener(new RacerEventListener());
        trackBtn.setBackground(new Color(0xDFD9D9D9, false));
        graphicBtn.setBackground(new Color(0xDFD9D9D9, false));
        mainMenuGUI.setVisible(true);

        Box leftBox = Box.createVerticalBox();
        Box centerBox = Box.createVerticalBox();
        Box rightBox = Box.createVerticalBox();

        Box topBox = Box.createHorizontalBox();
        Box racerBox = Box.createHorizontalBox();
        Box trackBox = Box.createHorizontalBox();
        Box graphicBox = Box.createHorizontalBox();

        Container container = mainMenuGUI.getContentPane();
        container.setLayout(new BorderLayout());

        topBox.add(Box.createRigidArea(new Dimension(0, 40)));
        topBox.add(title);
        topBox.add(Box.createRigidArea(new Dimension(0, 40)));
        centerBox.add(topBox);

        racerBox.add(racerBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(racerBox);

        trackBox.add(trackBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(trackBox);

        graphicBox.add(graphicBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(graphicBox);

        container.add(leftBox, BorderLayout.WEST);
        container.add(centerBox, BorderLayout.CENTER);
        container.add(rightBox, BorderLayout.EAST);
    }

    private static class RacerEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            MainRacerGUI racerWindow = new MainRacerGUI();
            racerWindow.show();
        }
    }
}
