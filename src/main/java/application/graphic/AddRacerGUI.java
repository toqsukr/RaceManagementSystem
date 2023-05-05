package application.graphic;

import exception.EmptyAddInputException;
import exception.InvalidAgeInputException;
import exception.InvalidNameInputException;
import exception.InvalidPointInputException;
import exception.InvalidTeamInputException;
import race.system.Racer;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;
import util.Validation;

public class AddRacerGUI {

    public JFrame addRacerGUI = new JFrame("Добавление гонщика");

    /**
     * This input is used to search for an entry in the table by the name of the
     * racer
     */
    private static final JTextField inputNameField = new JTextField("", 10);
    private static final JTextField inputAgeField = new JTextField("", 5);

    /**
     * This input is used to search for an entry in the table by the team of the
     * racer
     */
    private static final JTextField inputTeamField = new JTextField("", 10);
    private static final JTextField inputPointField = new JTextField("", 5);

    private static final JButton addBtn = new JButton("Добавить");

    private static final JButton cancelBtn = new JButton("Отмена");

    private static final JLabel nameLabel = new JLabel("Имя гонщика:");
    private static final JLabel teamLabel = new JLabel("Команда:");
    private static final JLabel ageLabel = new JLabel("Возраст:");
    private static final JLabel pointLabel = new JLabel("Очки:");

    /**
     * Constructor of GUI
     */
    public void show() {
        addRacerGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainRacerGUI.setMainRacerEnable(true);
                addRacerGUI.dispose();
                clearInputs();
            }
        });
        addRacerGUI.setVisible(false);
        addRacerGUI.setBounds(200, 150, 410, 300);
        addRacerGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addRacerGUI.setResizable(false);
        URL addRacerIcon = this.getClass().getClassLoader().getResource("img/racer.png");
        addRacerGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(addRacerIcon));

        addBtn.setBackground(new Color(0xDFD9D9D9, false));
        cancelBtn.setBackground(new Color(0xDFD9D9D9, false));

        Box leftBox = Box.createVerticalBox();
        Box centerBox = Box.createVerticalBox();
        Box rightBox = Box.createVerticalBox();

        Box toolBox = Box.createHorizontalBox();

        Box nameBox = Box.createHorizontalBox();
        Box ageBox = Box.createHorizontalBox();
        Box teamBox = Box.createHorizontalBox();
        Box pointBox = Box.createHorizontalBox();

        addBtn.addActionListener(new AddEventListener());
        addBtn.setFocusable(false);

        cancelBtn.addActionListener(new CancelEventListener());
        cancelBtn.setFocusable(false);

        Container container = addRacerGUI.getContentPane();
        container.setLayout(new BorderLayout());

        toolBox.add(Box.createRigidArea(new Dimension(40, 0)));
        toolBox.add(addBtn);
        toolBox.add(Box.createRigidArea(new Dimension(20, 0)));
        toolBox.add(cancelBtn);

        centerBox.add(Box.createRigidArea(new Dimension(20, 45)));

        nameBox.add(Box.createRigidArea(new Dimension(45, 0)));
        nameBox.add(nameLabel);
        nameBox.add(Box.createRigidArea(new Dimension(20, 0)));
        nameBox.add(inputNameField);
        centerBox.add(nameBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 15)));

        ageBox.add(Box.createRigidArea(new Dimension(45, 0)));
        ageBox.add(ageLabel);
        ageBox.add(Box.createRigidArea(new Dimension(51, 0)));
        ageBox.add(inputAgeField);
        centerBox.add(ageBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 15)));

        teamBox.add(Box.createRigidArea(new Dimension(45, 0)));
        teamBox.add(teamLabel);
        teamBox.add(Box.createRigidArea(new Dimension(46, 0)));
        teamBox.add(inputTeamField);
        centerBox.add(teamBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 15)));

        pointBox.add(Box.createRigidArea(new Dimension(45, 0)));
        pointBox.add(pointLabel);
        pointBox.add(Box.createRigidArea(new Dimension(67, 0)));
        pointBox.add(inputPointField);

        centerBox.add(pointBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 45)));
        centerBox.add(toolBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 50)));

        rightBox.add(Box.createRigidArea(new Dimension(45, 0)));

        container.add(leftBox, BorderLayout.WEST);
        container.add(centerBox, BorderLayout.CENTER);
        container.add(rightBox, BorderLayout.EAST);
    }

    private static void clearInputs() {
        inputNameField.setText("");
        inputAgeField.setText("");
        inputTeamField.setText("");
        inputPointField.setText("");
    }

    private static void checkEmptyInputs() throws EmptyAddInputException {
        if (inputNameField.getText().equals("") | inputAgeField.getText().equals("")
                | inputTeamField.getText().equals("") | inputPointField.getText().equals(""))
            throw new EmptyAddInputException();
    }

    private static void checkRacerInputDate() throws InvalidNameInputException, InvalidAgeInputException,
            InvalidTeamInputException, InvalidPointInputException {
        if (!Validation.isValidName(inputNameField.getText()))
            throw new InvalidNameInputException();
        if (!Validation.isValidAge(inputAgeField.getText()))
            throw new InvalidAgeInputException();
        if (!Validation.isValidTeam(inputTeamField.getText()))
            throw new InvalidTeamInputException();
        if (!Validation.isValidPoint(inputPointField.getText()))
            throw new InvalidPointInputException();
    }

    private static class AddEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                checkEmptyInputs();
                checkRacerInputDate();

                Racer racer = new Racer(inputNameField.getText(), Integer.parseInt(inputAgeField.getText()), null,
                        Integer.parseInt(inputPointField.getText()));
                clearInputs();
                MainRacerGUI.setMainRacerEnable(true);
                MainRacerGUI.addRacer(racer);
            } catch (EmptyAddInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidNameInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidAgeInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTeamInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidPointInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    private static class CancelEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            clearInputs();
            MainRacerGUI.setMainRacerEnable(true);
            MainRacerGUI.setAddRacerVisible(false);
        }
    }

}
