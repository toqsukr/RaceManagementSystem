package application.graphic;

import java.net.URL;
import java.util.List;
import exception.EmptyAddInputException;
import exception.InvalidTimeException;
import race.system.Racer;
import race.system.Score;
import race.system.Track;
import util.Validation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AddScoreGUI {

    public JFrame addScoreGUI = new JFrame("Добавление рекорда");

    private static JComboBox<String> comboRacer = new JComboBox<>();

    private static final JTextField inputTimeField = new JTextField("", 5);

    private static JComboBox<String> comboTrack = new JComboBox<>();

    private static final JButton addBtn = new JButton("Добавить");

    private static final JButton cancelBtn = new JButton("Отмена");

    private static final JLabel racerLabel = new JLabel("Имя гонщика:");

    private static final JLabel trackLabel = new JLabel("Название трассы:");

    private static final JLabel timeLabel = new JLabel("Личный рекорд:");

    private MainScoreGUI parentWindow;

    /**
     * Constructor of GUI
     */
    public AddScoreGUI(MainScoreGUI parent) {
        parentWindow = parent;
        addScoreGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentWindow.setMainScoreEnable(true);
                addScoreGUI.dispose();
                clearInput();
            }
        });
        addScoreGUI.setVisible(false);
        addScoreGUI.setBounds(200, 150, 410, 330);
        addScoreGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addScoreGUI.setResizable(false);
        URL addRacerIcon = this.getClass().getClassLoader().getResource("img/score.png");
        addScoreGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(addRacerIcon));

        updateComboRacer();
        updateComboTrack();

        comboRacer.setBackground(new Color(0xFFFFFF, false));
        comboRacer.setFocusable(false);
        addBtn.setBackground(new Color(0xDFD9D9D9, false));
        cancelBtn.setBackground(new Color(0xDFD9D9D9, false));

        Box leftBox = Box.createVerticalBox();
        Box centerBox = Box.createVerticalBox();
        Box rightBox = Box.createVerticalBox();

        Box toolBox = Box.createHorizontalBox();

        Box racerBox = Box.createHorizontalBox();
        Box trackBox = Box.createHorizontalBox();
        Box timeBox = Box.createHorizontalBox();

        addBtn.addActionListener(new AddEventListener());
        addBtn.setFocusable(false);

        cancelBtn.addActionListener(new CancelEventListener());
        cancelBtn.setFocusable(false);

        Container container = addScoreGUI.getContentPane();
        container.setLayout(new BorderLayout());

        toolBox.add(Box.createRigidArea(new Dimension(40, 0)));
        toolBox.add(addBtn);
        toolBox.add(Box.createRigidArea(new Dimension(20, 0)));
        toolBox.add(cancelBtn);

        centerBox.add(Box.createRigidArea(new Dimension(20, 60)));

        racerBox.add(Box.createRigidArea(new Dimension(35, 0)));
        racerBox.add(racerLabel);
        racerBox.add(Box.createRigidArea(new Dimension(32, 0)));
        racerBox.add(comboRacer);
        centerBox.add(racerBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 25)));

        trackBox.add(Box.createRigidArea(new Dimension(35, 0)));
        trackBox.add(trackLabel);
        trackBox.add(Box.createRigidArea(new Dimension(28, 0)));
        trackBox.add(comboTrack);
        centerBox.add(trackBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 25)));

        timeBox.add(Box.createRigidArea(new Dimension(35, 0)));
        timeBox.add(timeLabel);
        timeBox.add(Box.createRigidArea(new Dimension(34, 0)));
        timeBox.add(inputTimeField);

        centerBox.add(timeBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 60)));

        centerBox.add(toolBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 40)));

        rightBox.add(Box.createRigidArea(new Dimension(35, 0)));

        container.add(leftBox, BorderLayout.WEST);
        container.add(centerBox, BorderLayout.CENTER);
        container.add(rightBox, BorderLayout.EAST);
    }

    private class AddEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                checkEmptyInputs();
                checkInputTime();

                String string = comboRacer.getSelectedItem().toString();
                Racer racer = MainRacerGUI.isAtRacerList(
                        parentWindow.getParentWindow().getMainRacerGUI().getAllRacers(),
                        Integer.parseInt(
                                string.substring(string.indexOf(':', 0) + 2, (string.indexOf(')', 0)))));
                Track track = MainTrackGUI.isAtTrackList(
                        parentWindow.getParentWindow().getMainTrackGUI().getAllTracks(),
                        comboTrack.getSelectedItem().toString());
                Score score = new Score(racer, track, Integer.parseInt(inputTimeField.getText()));
                score.setScoreID(parentWindow.getScoreDao().getFreeID());
                parentWindow.addToAllScores(score);
                parentWindow.getScoreDao().updateFreeID(parentWindow.getAllScores());
                clearInput();
                parentWindow.setScoreTable();

            } catch (EmptyAddInputException exception) {
                JOptionPane.showMessageDialog(addScoreGUI, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTimeException exception) {
                JOptionPane.showMessageDialog(addScoreGUI, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    private class CancelEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            clearInput();
            parentWindow.setMainScoreEnable(true);
            addScoreGUI.setVisible(false);
        }
    }

    public void updateComboRacer() {
        comboRacer.removeAllItems();
        comboRacer.setSelectedItem(null);
        List<Racer> allRacers = parentWindow.getParentWindow().getMainRacerGUI().getAllRacers();
        for (Racer racer : allRacers) {
            comboRacer.addItem(racer.getRacerName() + " (ID: " + racer.getRacerID() + ")");
        }
    }

    public void updateComboTrack() {
        comboTrack.removeAllItems();
        comboTrack.setSelectedItem(null);
        List<Track> allTracks = parentWindow.getParentWindow().getMainTrackGUI().getAllTracks();
        for (Track track : allTracks) {
            comboTrack.addItem(track.getTrackName());
        }
    }

    public void setAddScoreVisibility(boolean value) {
        addScoreGUI.setVisible(value);
    }

    private void checkEmptyInputs() throws EmptyAddInputException {
        if (inputTimeField.getText().equals(""))
            throw new EmptyAddInputException();
    }

    private void clearInput() {
        inputTimeField.setText("");
    }

    private void checkInputTime() throws InvalidTimeException {
        if (!Validation.isValidTime(inputTimeField.getText()))
            throw new InvalidTimeException();
    }

}
