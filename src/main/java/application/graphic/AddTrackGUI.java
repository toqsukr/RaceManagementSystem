package application.graphic;

import java.util.List;

import exception.EmptyAddInputException;
import exception.InvalidAgeInputException;
import exception.InvalidNameInputException;
import exception.InvalidPointInputException;
import exception.InvalidTeamInputException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import race.system.Racer;
import race.system.Team;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import util.Validation;

public class AddTrackGUI {

    public JFrame AddTrackGUI = new JFrame("Добавление гонщика");

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
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("rms_persistence");
    private EntityManager em = emf.createEntityManager();
    private MainTrackGUI parentWindow;

    /**
     * Constructor of GUI
     */
    public AddTrackGUI(MainTrackGUI parent) {
        parentWindow = parent;
        AddTrackGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentWindow.setMainTrackEnable(true);
                AddTrackGUI.dispose();
                clearInputs();
            }
        });
        AddTrackGUI.setVisible(false);
        AddTrackGUI.setBounds(200, 150, 410, 330);
        AddTrackGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        AddTrackGUI.setResizable(false);
        URL addRacerIcon = this.getClass().getClassLoader().getResource("img/track.png");
        AddTrackGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(addRacerIcon));

        // setComboBox();
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

        Container container = AddTrackGUI.getContentPane();
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

        lengthBox.add(Box.createRigidArea(new Dimension(45, 0)));
        lengthBox.add(lengthLabel);
        lengthBox.add(Box.createRigidArea(new Dimension(51, 0)));
        lengthBox.add(inputLengthField);
        centerBox.add(lengthBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 15)));

        winnerBox.add(Box.createRigidArea(new Dimension(15, 0)));
        Box winnerLabelBox = Box.createHorizontalBox();
        winnerLabelBox.add(Box.createRigidArea(new Dimension(30, 0)));
        winnerLabelBox.add(winnerLabel);

        Box teamInnerBox = Box.createVerticalBox();
        teamInnerBox.add(winnerLabelBox);
        winnerBox.add(teamInnerBox);
        winnerBox.add(Box.createRigidArea(new Dimension(16, 0)));

        winnerBox.add(comboRacer);
        centerBox.add(winnerBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 15)));

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
        inputLengthField.setText("");
    }

    private void checkEmptyInputs() throws EmptyAddInputException {
        if (inputNameField.getText().equals("") | inputLengthField.getText().equals(""))
            throw new EmptyAddInputException();
    }

    private void checkRacerInputDate() throws InvalidNameInputException, InvalidAgeInputException,
            InvalidTeamInputException, InvalidPointInputException {
        if (!Validation.isValidName(inputNameField.getText()))
            throw new InvalidNameInputException();
        if (!Validation.isValidAge(inputLengthField.getText()))
            throw new InvalidAgeInputException();
    }

    private class AddEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            // try {
            // checkEmptyInputs();
            // checkRacerInputDate();

            // String teamName = teamCheckBox.isSelected() ? inputTeamField.getText()
            // : comboTeam.getSelectedItem().toString();
            // Team team = MainRacerGUI.isAtTeamList(parentWindow.getAllTeams(), teamName);
            // if (team == null) {
            // team = new Team(teamName);
            // parentWindow.addtoAllTeam(team);
            // parentWindow.updateComboTeam();
            // comboTeam.addItem(teamName);
            // } else
            // team.expandRacerNumber();

            // Racer racer = MainRacerGUI.isAtRacerList(parentWindow.getAllRacers(),
            // inputNameField.getText(),
            // inputAgeField.getText(), teamName,
            // inputPointField.getText());
            // if (racer == null) {
            // racer = new Racer(inputNameField.getText(),
            // Integer.parseInt(inputAgeField.getText()), team,
            // Integer.parseInt(inputPointField.getText()));
            // parentWindow.addToAllRacer(racer);
            // team.addPoints(Integer.parseInt(inputPointField.getText()));
            // parentWindow.getParentWindow().getMainTeamGUI().setTeamTable();
            // clearInputs();
            // parentWindow.addRacer(racer);
            // }
            // } catch (EmptyAddInputException exception) {
            // JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка
            // добавления",
            // JOptionPane.PLAIN_MESSAGE);
            // } catch (InvalidNameInputException exception) {
            // JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка
            // добавления",
            // JOptionPane.PLAIN_MESSAGE);
            // } catch (InvalidAgeInputException exception) {
            // JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка
            // добавления",
            // JOptionPane.PLAIN_MESSAGE);
            // } catch (InvalidTeamInputException exception) {
            // JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка
            // добавления",
            // JOptionPane.PLAIN_MESSAGE);
            // } catch (InvalidPointInputException exception) {
            // JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка
            // добавления",
            // JOptionPane.PLAIN_MESSAGE);
            // }
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
            AddTrackGUI.setVisible(false);
        }
    }

    public void setAddTrackVisibility(boolean value) {
        AddTrackGUI.setVisible(value);
    }

    // private void setComboBox() {
    // em.getTransaction().begin();
    // List<Team> allTeams = em.createQuery("FROM Team",
    // Team.class).getResultList();
    // String[] arr = new String[allTeams.size()];
    // for (int i = 0; i < allTeams.size(); i++) {
    // arr[i] = allTeams.get(i).getTeamName();
    // }
    // comboTeam = new JComboBox<String>(arr);
    // em.getTransaction().commit();
    // }

}
