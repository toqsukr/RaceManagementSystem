package application.graphic;

import exception.DuplicateException;
import exception.EmptyAddInputException;
import exception.InvalidTrackLengthInputException;
import exception.InvalidTrackNameInputException;
import race.system.Racer;
import race.system.Track;

import java.util.List;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import util.Validation;

public class AddTrackGUI {

    public JFrame addTrackGUI = new JFrame("Добавление трассы");

    /**
     * This input is used to search for an entry in the table by the name of the
     * racer
     */
    private static final JTextField inputNameField = new JTextField("", 10);
    private static final JTextField inputLengthField = new JTextField("", 5);

    /**
     * This input is used to search for an entry in the table by the team of the
     * racer
     */

    private static JComboBox<String> comboRacer = new JComboBox<>();

    private static final JButton addBtn = new JButton("Добавить");

    private static final JButton cancelBtn = new JButton("Отмена");

    private static final JLabel nameLabel = new JLabel("Название трассы:");
    private static final JLabel lengthLabel = new JLabel("Длина трассы:");
    private static final JLabel winnerLabel = new JLabel("Лучший гонщик:");
    private MainTrackGUI parentWindow;

    /**
     * Constructor of GUI
     */
    public AddTrackGUI(MainTrackGUI parent) {
        parentWindow = parent;
        addTrackGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentWindow.setMainTrackEnable(true);
                addTrackGUI.dispose();
                clearInputs();
            }
        });
        addTrackGUI.setVisible(false);
        addTrackGUI.setBounds(200, 150, 410, 330);
        addTrackGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addTrackGUI.setResizable(false);
        URL addRacerIcon = this.getClass().getClassLoader().getResource("img/track.png");
        addTrackGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(addRacerIcon));

        updateComboRacer();
        comboRacer.setBackground(new Color(0xFFFFFF, false));
        comboRacer.setFocusable(false);
        addBtn.setBackground(new Color(0xDFD9D9D9, false));
        cancelBtn.setBackground(new Color(0xDFD9D9D9, false));

        Box leftBox = Box.createVerticalBox();
        Box centerBox = Box.createVerticalBox();
        Box rightBox = Box.createVerticalBox();

        Box toolBox = Box.createHorizontalBox();

        Box nameBox = Box.createHorizontalBox();
        Box lengthBox = Box.createHorizontalBox();
        Box winnerBox = Box.createHorizontalBox();

        addBtn.addActionListener(new AddEventListener());
        addBtn.setFocusable(false);

        cancelBtn.addActionListener(new CancelEventListener());
        cancelBtn.setFocusable(false);

        Container container = addTrackGUI.getContentPane();
        container.setLayout(new BorderLayout());

        toolBox.add(Box.createRigidArea(new Dimension(40, 0)));
        toolBox.add(addBtn);
        toolBox.add(Box.createRigidArea(new Dimension(20, 0)));
        toolBox.add(cancelBtn);

        centerBox.add(Box.createRigidArea(new Dimension(20, 60)));

        nameBox.add(Box.createRigidArea(new Dimension(35, 0)));
        nameBox.add(nameLabel);
        nameBox.add(Box.createRigidArea(new Dimension(16, 0)));
        nameBox.add(inputNameField);
        centerBox.add(nameBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 25)));

        lengthBox.add(Box.createRigidArea(new Dimension(35, 0)));
        lengthBox.add(lengthLabel);
        lengthBox.add(Box.createRigidArea(new Dimension(34, 0)));
        lengthBox.add(inputLengthField);
        centerBox.add(lengthBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 25)));

        winnerBox.add(Box.createRigidArea(new Dimension(15, 0)));
        Box winnerLabelBox = Box.createHorizontalBox();
        winnerLabelBox.add(Box.createRigidArea(new Dimension(20, 0)));
        winnerLabelBox.add(winnerLabel);

        Box teamInnerBox = Box.createVerticalBox();
        teamInnerBox.add(winnerLabelBox);
        winnerBox.add(teamInnerBox);
        winnerBox.add(Box.createRigidArea(new Dimension(16, 0)));

        winnerBox.add(Box.createRigidArea(new Dimension(10, 0)));
        winnerBox.add(comboRacer);
        centerBox.add(winnerBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 60)));

        centerBox.add(toolBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 40)));

        rightBox.add(Box.createRigidArea(new Dimension(35, 0)));

        container.add(leftBox, BorderLayout.WEST);
        container.add(centerBox, BorderLayout.CENTER);
        container.add(rightBox, BorderLayout.EAST);
    }

    private void clearInputs() {
        inputNameField.setText("");
        inputLengthField.setText("");
    }

    private void checkEmptyInputs() throws EmptyAddInputException {
        if (inputNameField.getText().equals("") | inputLengthField.getText().equals(""))
            throw new EmptyAddInputException();
    }

    private class AddEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                checkEmptyInputs();
                checkTrackInputDate();
                checkDuplicatTrack(inputNameField.getText().trim());
                Track track = new Track(inputNameField.getText().trim(), Integer.parseInt(inputLengthField.getText()));
                track.setTrackID(parentWindow.getParentWindow().getMainRacerGUI().getTrackDao().getFreeID());
                if (comboRacer.getSelectedIndex() != 0) {
                    String string = comboRacer.getSelectedItem().toString();
                    Racer racer = MainRacerGUI.isAtRacerList(
                            parentWindow.getParentWindow().getMainRacerGUI().getAllRacers(),
                            Integer.parseInt(
                                    string.substring(string.indexOf(':', 0) + 2, (string.indexOf(')', 0)))));
                    track.setWinner(racer);
                }
                parentWindow.addToAllTracks(track);
                parentWindow.getParentWindow().getMainRacerGUI().getTrackDao()
                        .updateFreeID(parentWindow.getAllTracks());
                clearInputs();
                parentWindow.getParentWindow().getMainGraphicGUI().getAddGraphicGUI().updateComboTrack();
                parentWindow.getParentWindow().getMainScoreGUI().getAddScoreWindow().updateComboTrack();
                parentWindow.setTrackTable();

            } catch (EmptyAddInputException exception) {
                JOptionPane.showMessageDialog(addTrackGUI, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTrackNameInputException exception) {
                JOptionPane.showMessageDialog(addTrackGUI, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTrackLengthInputException exception) {
                JOptionPane.showMessageDialog(addTrackGUI, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (DuplicateException exception) {
                JOptionPane.showMessageDialog(addTrackGUI, exception.getMessage(), "Ошибка добавления",
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
            clearInputs();
            parentWindow.setMainTrackEnable(true);
            addTrackGUI.setVisible(false);
        }
    }

    private void checkTrackInputDate() throws InvalidTrackNameInputException, InvalidTrackLengthInputException {
        if (!Validation.isValidTrackName(inputNameField.getText()))
            throw new InvalidTrackNameInputException();
        if (!Validation.isValidTrackLength(inputLengthField.getText()))
            throw new InvalidTrackLengthInputException();
    }

    public void setAddTrackVisibility(boolean value) {
        addTrackGUI.setVisible(value);
    }

    public void updateComboRacer() {
        comboRacer.removeAllItems();
        comboRacer.setSelectedItem(null);
        comboRacer.addItem("Нет");
        comboRacer.setSelectedIndex(0);
        List<Racer> allRacers = parentWindow.getParentWindow().getMainRacerGUI().getAllRacers();
        for (Racer racer : allRacers) {
            comboRacer.addItem(racer.getRacerName() + " (ID: " + racer.getRacerID() + ")");
        }
    }

    private void checkDuplicatTrack(String trackName) throws DuplicateException {
        Track track = MainTrackGUI.isAtTrackList(parentWindow.getAllTracks(), trackName);
        if (track != null)
            throw new DuplicateException("Трасса с таким названием уже существует!");
    }

}
