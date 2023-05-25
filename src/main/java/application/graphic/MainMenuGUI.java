package application.graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;

public class MainMenuGUI {
    private static JFrame mainMenuGUI = new JFrame("Главное меню");

    private final JButton racerBtn = new JButton("       Гонщики   ");

    private final JButton teamBtn = new JButton("      Команды   ");

    private final JButton trackBtn = new JButton("       Трассы     ");

    private final JButton graphicBtn = new JButton("   Расписание  ");

    private final JButton scoreBtn = new JButton("      Рекорды     ");

    private final JButton exitBtn = new JButton("       Выход       ");

    private JLabel title = new JLabel("Система управления автогонками");

    private MainRacerGUI mainRacerWindow;

    private MainTeamGUI mainTeamWindow;

    private MainTrackGUI mainTrackWindow;

    private MainScoreGUI mainScoreWindow;

    public MainMenuGUI() {

        mainMenuGUI.addWindowListener((WindowListener) new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeOperation();
            }
        });

        mainMenuGUI.setBounds(650, 200, 300, 380);
        mainMenuGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainMenuGUI.setResizable(false);
        URL mainMenuIcon = this.getClass().getClassLoader().getResource("img/favicon.png");
        mainMenuGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(mainMenuIcon));
        mainRacerWindow = new MainRacerGUI(this);
        mainTeamWindow = new MainTeamGUI(this);
        mainTrackWindow = new MainTrackGUI(this);
        mainScoreWindow = new MainScoreGUI(this);

        URL racerIcon = this.getClass().getClassLoader().getResource("img/racer.png");
        racerBtn.setIcon(new ImageIcon(new ImageIcon(racerIcon).getImage().getScaledInstance(22, 22, 4)));
        racerBtn.setBackground(new Color(0xDFD9D9D9, false));
        racerBtn.addActionListener(new RacerEventListener());
        racerBtn.setFocusable(false);
        racerBtn.setMargin(new Insets(1, 2, 1, 10));

        URL teamIcon = this.getClass().getClassLoader().getResource("img/team.png");
        teamBtn.setIcon(new ImageIcon(new ImageIcon(teamIcon).getImage().getScaledInstance(22, 22, 4)));
        teamBtn.setBackground(new Color(0xDFD9D9D9, false));
        teamBtn.addActionListener(new TeamEventListener());
        teamBtn.setFocusable(false);
        teamBtn.setMargin(new Insets(1, 2, 1, 10));

        URL trackIcon = this.getClass().getClassLoader().getResource("img/track.png");
        trackBtn.setIcon(new ImageIcon(new ImageIcon(trackIcon).getImage().getScaledInstance(23, 22, 4)));
        trackBtn.setBackground(new Color(0xDFD9D9D9, false));
        trackBtn.addActionListener(new TrackEventListener());
        trackBtn.setFocusable(false);
        trackBtn.setMargin(new Insets(1, 4, 1, 10));

        URL graphicIcon = this.getClass().getClassLoader().getResource("img/graphic.png");
        graphicBtn.setIcon(new ImageIcon(new ImageIcon(graphicIcon).getImage().getScaledInstance(22, 22, 4)));
        graphicBtn.setBackground(new Color(0xDFD9D9D9, false));
        graphicBtn.setFocusable(false);
        graphicBtn.setMargin(new Insets(1, 4, 1, 8));

        URL scoreIcon = this.getClass().getClassLoader().getResource("img/score.png");
        scoreBtn.setIcon(new ImageIcon(new ImageIcon(scoreIcon).getImage().getScaledInstance(21, 22, 4)));
        scoreBtn.setBackground(new Color(0xDFD9D9D9, false));
        scoreBtn.addActionListener(new ScoreEventListener());
        scoreBtn.setFocusable(false);
        scoreBtn.setMargin(new Insets(1, 4, 1, 6));

        URL exitIcon = this.getClass().getClassLoader().getResource("img/exit.png");
        exitBtn.setIcon(new ImageIcon(new ImageIcon(exitIcon).getImage().getScaledInstance(22, 22, 4)));
        exitBtn.setBackground(new Color(0xDFD9D9D9, false));
        exitBtn.addActionListener(new ExitEventListener());
        exitBtn.setFocusable(false);
        exitBtn.setMargin(new Insets(1, 6, 1, 7));

        Box leftBox = Box.createVerticalBox();
        Box centerBox = Box.createVerticalBox();
        Box rightBox = Box.createVerticalBox();

        Box topBox = Box.createHorizontalBox();
        Box racerBox = Box.createHorizontalBox();
        Box teamBox = Box.createHorizontalBox();
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

        teamBox.add(teamBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(teamBox);

        trackBox.add(trackBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(trackBox);

        graphicBox.add(graphicBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(graphicBox);

        infoBox.add(scoreBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(infoBox);

        exitBox.add(exitBtn);
        centerBox.add(Box.createRigidArea(new Dimension(0, 10)));
        centerBox.add(exitBox);

        container.add(leftBox, BorderLayout.WEST);
        container.add(centerBox, BorderLayout.CENTER);
        container.add(rightBox, BorderLayout.EAST);
    }

    private void closeOperation() {
        try {
            MainRacerGUI.stopEditCell(mainRacerWindow.getRacerTable());
            mainRacerWindow.checkEditedData();
            int result = mainRacerWindow.saveBeforeClose(
                    "Сохранить изменения в списке гонщиков?\nПосле закрытия окна несохраненные данные будут утеряны!");
            if (result != -1) {
                System.exit(1);
            }
        } catch (Exception exception) {
            int confirm = JOptionPane.showConfirmDialog(mainMenuGUI,
                    "Данные содержат ошибку и не могут быть сохранены!\nЗакрыть окно?",
                    "Предупреждение",
                    JOptionPane.OK_CANCEL_OPTION);
            if (confirm == JOptionPane.OK_OPTION) {
                System.exit(1);
            }
        }
    }

    public void setMainMenuEnable(boolean value) {
        mainMenuGUI.setEnabled(value);
    }

    private class RacerEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            mainRacerWindow.setVisible(true);
        }
    }

    private class TeamEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            mainTeamWindow.setVisible(true);
        }
    }

    private class TrackEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            mainTrackWindow.setVisible(true);
        }
    }

    private class ScoreEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            mainScoreWindow.setVisible(true);
        }
    }

    private class ExitEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            closeOperation();
        }
    }

    public void setVisible(boolean value) {
        mainMenuGUI.setVisible(value);
    }

    public MainRacerGUI getMainRacerGUI() {
        return mainRacerWindow;
    }

    public MainTeamGUI getMainTeamGUI() {
        return mainTeamWindow;
    }

    public MainTrackGUI getMainTrackGUI() {
        return mainTrackWindow;
    }
}
