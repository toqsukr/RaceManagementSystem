package application.graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuGUI {
    private static JFrame mainMenuGUI = new JFrame("Главное меню");

    private final JButton racerBtn = new JButton("       Гонщики   ",
            new ImageIcon(new ImageIcon("etu/src/img/racer.png").getImage().getScaledInstance(25, 25, 4)));

    private final JButton trackBtn = new JButton("       Трассы     ",
            new ImageIcon(new ImageIcon("etu/src/img/track.png").getImage().getScaledInstance(25, 25, 4)));

    private final JButton graphicBtn = new JButton("   Расписание  ",
            new ImageIcon(new ImageIcon("etu/src/img/graphic.png").getImage().getScaledInstance(25, 25, 4)));

    private final JButton infoBtn = new JButton("  О программе  ",
            new ImageIcon(new ImageIcon("etu/src/img/info.png").getImage().getScaledInstance(25, 25, 4)));

    private final JButton exitBtn = new JButton("        Выход       ",
            new ImageIcon(new ImageIcon("etu/src/img/exit.png").getImage().getScaledInstance(25, 25, 4)));

    private static JLabel title = new JLabel("Система управления автогонками");

    private static MainRacerGUI mainRacerWindow = new MainRacerGUI();

    public MainMenuGUI() {
        mainMenuGUI.setBounds(650, 200, 300, 380);
        mainMenuGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuGUI.setResizable(false);
        mainMenuGUI.setIconImage(Toolkit.getDefaultToolkit().getImage("etu/src/img/favicon.png"));
        mainRacerWindow.show();

        racerBtn.setBackground(new Color(0xDFD9D9D9, false));
        racerBtn.addActionListener(new RacerEventListener());
        racerBtn.setFocusable(false);
        racerBtn.setMargin(new Insets(1, 2, 1, 10));

        trackBtn.setBackground(new Color(0xDFD9D9D9, false));
        trackBtn.setFocusable(false);
        trackBtn.setMargin(new Insets(1, 4, 1, 10));

        graphicBtn.setBackground(new Color(0xDFD9D9D9, false));
        graphicBtn.setFocusable(false);
        graphicBtn.setMargin(new Insets(1, 4, 1, 8));

        infoBtn.setBackground(new Color(0xDFD9D9D9, false));
        infoBtn.setFocusable(false);
        infoBtn.setMargin(new Insets(1, 4, 1, 6));

        exitBtn.setBackground(new Color(0xDFD9D9D9, false));
        exitBtn.setFocusable(false);
        exitBtn.setMargin(new Insets(1, 3, 1, 6));

        Box leftBox = Box.createVerticalBox();
        Box centerBox = Box.createVerticalBox();
        Box rightBox = Box.createVerticalBox();

        Box topBox = Box.createHorizontalBox();
        Box racerBox = Box.createHorizontalBox();
        Box trackBox = Box.createHorizontalBox();
        Box graphicBox = Box.createHorizontalBox();
        Box infoBox = Box.createHorizontalBox();
        Box exitBox = Box.createHorizontalBox();

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

        infoBox.add(infoBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(infoBox);

        exitBox.add(exitBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(exitBox);

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
            mainRacerWindow.setVisible(true);

        }
    }

    public void setVisible(boolean value) {
        mainMenuGUI.setVisible(value);
    }
}
