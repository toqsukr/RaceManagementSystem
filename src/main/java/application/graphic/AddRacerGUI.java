package application.graphic;

import exception.EmptyAddInputException;
import exception.InvalidAgeInputException;
import exception.InvalidNameInputException;
import exception.InvalidPointInputException;
import exception.InvalidTeamInputException;
import race.system.Racer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import util.Validation;

public class AddRacerGUI {

    public JFrame addRacerGUI = new JFrame("Добавление гонщика");

    /**
     * This input is used to search for an entry in the table by the name of the
     * racer
     */
    private static final JTextField inputNameField = new JTextField("Имя гонщика", 10);
    private static final JTextField inputAgeField = new JTextField("Возраст", 5);

    /**
     * This input is used to search for an entry in the table by the team of the
     * racer
     */
    private static final JTextField inputTeamField = new JTextField("Команда", 10);
    private static final JTextField inputPointField = new JTextField("Очки", 5);

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
        addRacerGUI.setIconImage(Toolkit.getDefaultToolkit().getImage("etu/src/img/favicon.png"));

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
        cancelBtn.addActionListener(new CancelEventListener());

        Container container = addRacerGUI.getContentPane();
        container.setLayout(new BorderLayout());

        inputNameField.addFocusListener(new RacerInputFocusListener());
        inputAgeField.addFocusListener(new AgeInputFocusListener());
        inputTeamField.addFocusListener(new TeamInputFocusListener());
        inputPointField.addFocusListener(new PointInputFocusListener());

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
        inputNameField.setText("Имя гонщика");
        inputAgeField.setText("Возраст");
        inputTeamField.setText("Команда");
        inputPointField.setText("Очки");
    }

    private static void checkEmptyInputs() throws EmptyAddInputException {
        if (inputNameField.getText().equals("Имя гонщика") | inputAgeField.getText().equals("Возраст")
                | inputTeamField.getText().equals("Команда") | inputPointField.getText().equals("Очки"))
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

                Racer racer = new Racer(inputNameField.getText(), Integer.parseInt(inputAgeField.getText()),
                        inputTeamField.getText(), Integer.parseInt(inputPointField.getText()));
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

    public static class RacerInputFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            if (inputNameField.getText().equals("Имя гонщика"))
                MainRacerGUI.setInput(inputNameField, "");
        }

        public void focusLost(FocusEvent e) {
            if (inputNameField.getText().equals(""))
                MainRacerGUI.setInput(inputNameField, "Имя гонщика");
        }

    }

    public static class TeamInputFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            if (inputTeamField.getText().equals("Команда"))
                MainRacerGUI.setInput(inputTeamField, "");
        }

        public void focusLost(FocusEvent e) {
            if (inputTeamField.getText().equals(""))
                MainRacerGUI.setInput(inputTeamField, "Команда");
        }

    }

    private static class AgeInputFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            if (inputAgeField.getText().equals("Возраст"))
                MainRacerGUI.setInput(inputAgeField, "");
        }

        public void focusLost(FocusEvent e) {
            if (inputAgeField.getText().equals(""))
                MainRacerGUI.setInput(inputAgeField, "Возраст");
        }
    }

    private static class PointInputFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            if (inputPointField.getText().equals("Очки"))
                MainRacerGUI.setInput(inputPointField, "");
        }

        public void focusLost(FocusEvent e) {
            if (inputPointField.getText().equals(""))
                MainRacerGUI.setInput(inputPointField, "Очки");
        }
    }
}
