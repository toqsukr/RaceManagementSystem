package application.graphic;

import java.util.List;

import exception.DuplicateException;
import exception.EmptyAddInputException;
import exception.InvalidNameInputException;
import exception.InvalidPointInputException;
import exception.InvalidTeamInputException;
import race.system.Racer;
import race.system.Team;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import application.App;
import database.TeamDao;
import util.Validation;

public class AddRacerGUI {

    private JFrame addRacerGUI = new JFrame("Добавление гонщика");

    /**
     * This input is used to search for an entry in the table by the name of the
     * racer
     */
    private static final JTextField inputNameField = new JTextField("", 10);
    private static final JTextField inputTeamField = new JTextField("", 10);

    /**
     * This input is used to search for an entry in the table by the team of the
     * racer
     */

    private static JComboBox<String> comboTeam = new JComboBox<>();
    private static JComboBox<String> comboAge = new JComboBox<>();

    private static final JTextField inputPointField = new JTextField("", 5);

    private static final JButton addBtn = new JButton("Добавить");

    private static final JButton cancelBtn = new JButton("Отмена");

    private static final JLabel nameLabel = new JLabel("Имя гонщика:");
    private static final JLabel teamLabel = new JLabel("Команда:");
    private static final JLabel ageLabel = new JLabel("Возраст:");
    private static final JLabel pointLabel = new JLabel("Очки:");
    private static final JCheckBox teamCheckBox = new JCheckBox("Другое", null, false);
    private MainRacerGUI parentWindow;

    /**
     * Constructor of GUI
     */
    public AddRacerGUI(MainRacerGUI parent) {
        parentWindow = parent;
        addRacerGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentWindow.setMainRacerEnable(true);
                addRacerGUI.dispose();
                clearInputs();
            }
        });
        addRacerGUI.setVisible(false);
        addRacerGUI.setBounds(200, 150, 410, 330);
        addRacerGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addRacerGUI.setResizable(false);
        URL addRacerIcon = this.getClass().getClassLoader().getResource("img/racer.png");
        addRacerGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(addRacerIcon));

        setComboTeam();
        setComboAge();

        comboTeam.setBackground(new Color(0xFFFFFF, false));
        comboTeam.setFocusable(false);
        teamCheckBox.setBackground(addRacerGUI.getBackground());
        teamCheckBox.setFocusable(false);
        addBtn.setBackground(new Color(0xDFD9D9D9, false));
        cancelBtn.setBackground(new Color(0xDFD9D9D9, false));
        setInputTeamVisibility(false);

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

        teamCheckBox.addItemListener(new TeamCheckboxItemListener());

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
        centerBox.add(Box.createRigidArea(new Dimension(0, 25)));

        ageBox.add(Box.createRigidArea(new Dimension(45, 0)));
        ageBox.add(ageLabel);
        ageBox.add(Box.createRigidArea(new Dimension(51, 0)));
        ageBox.add(comboAge);
        centerBox.add(ageBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 15)));

        teamBox.add(Box.createRigidArea(new Dimension(15, 0)));
        Box teamLabelBox = Box.createHorizontalBox();
        teamLabelBox.add(Box.createRigidArea(new Dimension(30, 0)));
        teamLabelBox.add(teamLabel);
        Box teamCheckboxBox = Box.createHorizontalBox();
        teamCheckboxBox.add(teamCheckBox);
        teamLabelBox.add(Box.createRigidArea(new Dimension(30, 0)));

        Box teamInnerBox = Box.createVerticalBox();
        teamInnerBox.add(teamLabelBox);
        teamInnerBox.add(teamCheckboxBox);
        teamBox.add(teamInnerBox);
        teamBox.add(Box.createRigidArea(new Dimension(16, 0)));

        Box teamInputBox = Box.createVerticalBox();

        teamInputBox.add(Box.createRigidArea(new Dimension(0, 10)));
        teamInputBox.add(inputTeamField);
        teamInputBox.add(comboTeam);
        teamInputBox.add(Box.createRigidArea(new Dimension(0, 10)));
        teamBox.add(teamInputBox);
        centerBox.add(teamBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 15)));

        pointBox.add(Box.createRigidArea(new Dimension(45, 0)));
        pointBox.add(pointLabel);
        pointBox.add(Box.createRigidArea(new Dimension(67, 0)));
        pointBox.add(inputPointField);

        centerBox.add(pointBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 20)));
        centerBox.add(toolBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 50)));

        rightBox.add(Box.createRigidArea(new Dimension(45, 0)));

        container.add(leftBox, BorderLayout.WEST);
        container.add(centerBox, BorderLayout.CENTER);
        container.add(rightBox, BorderLayout.EAST);
    }

    private void clearInputs() {
        inputNameField.setText("");
        inputTeamField.setText("");
        inputPointField.setText("");
    }

    private void checkEmptyInputs() throws EmptyAddInputException {
        if (inputNameField.getText().equals("") | inputPointField.getText().equals("")
                | (teamCheckBox.isSelected() && inputTeamField.getText().equals("")))
            throw new EmptyAddInputException();
    }

    private void checkRacerInputDate() throws InvalidNameInputException,
            InvalidTeamInputException, InvalidPointInputException {
        if (!Validation.isValidName(inputNameField.getText()))
            throw new InvalidNameInputException();
        if (!Validation.isValidPoint(inputPointField.getText()))
            throw new InvalidPointInputException();
        if (teamCheckBox.isSelected() && !Validation.isValidTeam(inputTeamField.getText()))
            throw new InvalidTeamInputException();
    }

    private class TeamCheckboxItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            setInputTeamVisibility(!inputTeamField.isVisible());
            setComboTeamVisibility(!comboTeam.isVisible());
        }

    }

    private class AddEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                checkEmptyInputs();
                checkRacerInputDate();
                parentWindow.getRacerDao().updateFreeID(parentWindow.getAllRacers());
                String teamName;
                if (!comboTeam.isVisible() && !teamCheckBox.isVisible()) {
                    setComboTeamVisibility(true);
                    setTeamCheckBoxVisibility(true);
                    setInputTeamVisibility(false);
                    teamName = inputTeamField.getText().trim();
                } else {
                    teamName = teamCheckBox.isSelected() ? inputTeamField.getText()
                            : comboTeam.getSelectedItem().toString();
                }
                if (teamCheckBox.isSelected())
                    checkDuplicatTeam(inputTeamField.getText().trim());
                Team team = MainRacerGUI.isAtTeamList(parentWindow.getAllTeams(), teamName);
                if (team == null) {
                    team = new Team(teamName);
                    team.setTeamID(parentWindow.getTeamDao().getFreeID());
                    parentWindow.addtoAllTeam(team);
                    parentWindow.updateComboTeam();
                    comboTeam.addItem(teamName);
                } else
                    team.setRacerNumber(team.getRacerNumber() + 1);
                Racer racer = MainRacerGUI.isAtRacerList(parentWindow.getAllRacers(), inputNameField.getText().trim(),
                        teamName);
                int result = 0;
                if (racer != null) {
                    result = JOptionPane.showConfirmDialog(addRacerGUI,
                            "Гонщик с таким же именем уже существует в данной команде.\nПродолжить?",
                            "Подтверждение действия",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                }

                if (result == JOptionPane.YES_OPTION) {
                    racer = new Racer(inputNameField.getText().trim(),
                            Integer.parseInt(comboAge.getSelectedItem().toString()),
                            team,
                            Integer.parseInt(inputPointField.getText()));
                    racer.setRacerID(parentWindow.getRacerDao().getFreeID());
                    parentWindow.addToAllRacer(racer);
                    team.addPoints(Integer.parseInt(inputPointField.getText()));
                    parentWindow.getParentWindow().getMainTeamGUI().setTeamTable();
                    parentWindow.getParentWindow().getMainTrackGUI().getAddTrackWindow().updateComboRacer();
                    parentWindow.getParentWindow().getMainTrackGUI().updateComboRacer();
                    parentWindow.getParentWindow().getMainScoreGUI().updateComboRacer();
                    parentWindow.getParentWindow().getMainScoreGUI().getAddScoreWindow().updateComboRacer();
                    clearInputs();
                    parentWindow.addRacer(racer);
                }
            } catch (EmptyAddInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidNameInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTeamInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidPointInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (DuplicateException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка добавления",
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
            parentWindow.setMainRacerEnable(true);
            setAddRacerVisibility(false);
        }
    }

    private void setComboTeam() {
        TeamDao teamDao = new TeamDao(App.getEntityManager());
        List<Team> allTeams = teamDao.getAllTeams();
        String[] arr = new String[allTeams.size()];
        for (int i = 0; i < allTeams.size(); i++) {
            arr[i] = allTeams.get(i).getTeamName();
        }
        comboTeam = new JComboBox<String>(arr);
    }

    public void addItemComboTeam(String item) {
        comboTeam.addItem(item);
    }

    public void clearComboTeam() {
        comboTeam.removeAllItems();
        comboTeam.setSelectedItem(null);
    }

    public void setTeamCheckBoxVisibility(boolean value) {
        teamCheckBox.setVisible(value);
    }

    public void setComboTeamVisibility(boolean value) {
        comboTeam.setVisible(value);
    }

    public void updateComboTeam() {
        comboTeam.removeAllItems();
        comboTeam.setSelectedItem(null);
        for (Team team : parentWindow.getAllTeams()) {
            addItemComboTeam(team.getTeamName());
        }
        if (comboTeam.getItemAt(0) == null) {
            setTeamCheckBoxVisibility(false);
            setComboTeamVisibility(false);
            setInputTeamVisibility(true);
        } else {
            setTeamCheckBoxVisibility(true);
            setComboTeamVisibility(true);
            setInputTeamVisibility(false);
        }
    }

    public JComboBox<String> getComboTeam() {
        return comboTeam;
    }

    public void setAddRacerVisibility(boolean value) {
        addRacerGUI.setVisible(value);
    }

    public void setInputTeamVisibility(boolean value) {
        inputTeamField.setVisible(value);
    }

    public void setCheckBoxValue(boolean value) {
        teamCheckBox.setSelected(value);
    }

    private void checkDuplicatTeam(String teamName) throws DuplicateException {
        Team team = MainRacerGUI.isAtTeamList(parentWindow.getAllTeams(), teamName);
        if (team != null)
            throw new DuplicateException("Команда с таким названием уже существует!");
    }

    private void setComboAge() {
        for (int i = 18; i < 100; i++) {
            comboAge.addItem(Integer.valueOf(i).toString());
        }
    }
}
