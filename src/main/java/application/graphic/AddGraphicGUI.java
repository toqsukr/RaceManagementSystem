package application.graphic;

import java.net.URL;
import java.util.List;

import race.system.Competition;
import race.system.MyDate;
import race.system.Track;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AddGraphicGUI {

    public JFrame addGraphicGUI = new JFrame("Добавление соревнования");

    private static final JButton addBtn = new JButton("Добавить");

    private static final JButton cancelBtn = new JButton("Отмена");

    private static final JLabel trackLabel = new JLabel("Название трассы:");

    private static final JLabel dayLabel = new JLabel("День проведения соревнования:");

    private static final JLabel monthLabel = new JLabel("Месяц проведения соревнования:");

    private static final JLabel yearLabel = new JLabel("Год проведения соревнования:");

    private static JComboBox<String> comboTrack = new JComboBox<>();

    private static JComboBox<String> comboDay = new JComboBox<>();

    private static JComboBox<String> comboMonth = new JComboBox<>();

    private static JComboBox<String> comboYear = new JComboBox<>();

    private MainGraphicGUI parentWindow;

    /**
     * Constructor of GUI
     */
    public AddGraphicGUI(MainGraphicGUI parent) {
        parentWindow = parent;
        addGraphicGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentWindow.setMainGraphicEnable(true);
                addGraphicGUI.dispose();
            }
        });
        addGraphicGUI.setVisible(false);
        addGraphicGUI.setBounds(200, 150, 410, 330);
        addGraphicGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addGraphicGUI.setResizable(false);
        URL addRacerIcon = this.getClass().getClassLoader().getResource("img/score.png");
        addGraphicGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(addRacerIcon));

        updateComboYear();
        updateComboMonth();
        updateComboDay();
        updateComboTrack();

        comboTrack.setBackground(new Color(0xFFFFFF, false));
        comboTrack.setFocusable(false);

        comboDay.setBackground(new Color(0xFFFFFF, false));
        comboDay.setFocusable(false);

        comboMonth.setBackground(new Color(0xFFFFFF, false));
        comboMonth.addActionListener(new SelectEventListener());
        comboMonth.setFocusable(false);

        comboYear.setBackground(new Color(0xFFFFFF, false));
        comboYear.addActionListener(new SelectEventListener());
        comboYear.setFocusable(false);

        addBtn.setBackground(new Color(0xDFD9D9D9, false));
        cancelBtn.setBackground(new Color(0xDFD9D9D9, false));

        Box leftBox = Box.createVerticalBox();
        Box centerBox = Box.createVerticalBox();
        Box rightBox = Box.createVerticalBox();

        Box toolBox = Box.createHorizontalBox();

        Box trackBox = Box.createHorizontalBox();
        Box dayBox = Box.createHorizontalBox();
        Box monthBox = Box.createHorizontalBox();
        Box yearBox = Box.createHorizontalBox();

        addBtn.addActionListener(new AddEventListener());
        addBtn.setFocusable(false);

        cancelBtn.addActionListener(new CancelEventListener());
        cancelBtn.setFocusable(false);

        Container container = addGraphicGUI.getContentPane();
        container.setLayout(new BorderLayout());

        toolBox.add(Box.createRigidArea(new Dimension(40, 0)));
        toolBox.add(addBtn);
        toolBox.add(Box.createRigidArea(new Dimension(20, 0)));
        toolBox.add(cancelBtn);

        centerBox.add(Box.createRigidArea(new Dimension(20, 40)));

        trackBox.add(Box.createRigidArea(new Dimension(35, 0)));
        trackBox.add(trackLabel);
        trackBox.add(Box.createRigidArea(new Dimension(50, 0)));
        trackBox.add(comboTrack);
        centerBox.add(trackBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 25)));

        yearBox.add(Box.createRigidArea(new Dimension(34, 0)));
        yearBox.add(yearLabel);
        yearBox.add(Box.createRigidArea(new Dimension(42, 0)));
        yearBox.add(comboYear);

        centerBox.add(yearBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 25)));

        monthBox.add(Box.createRigidArea(new Dimension(35, 0)));
        monthBox.add(monthLabel);
        monthBox.add(Box.createRigidArea(new Dimension(26, 0)));
        monthBox.add(comboMonth);

        centerBox.add(monthBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 25)));

        dayBox.add(Box.createRigidArea(new Dimension(30, 0)));
        dayBox.add(dayLabel);
        dayBox.add(Box.createRigidArea(new Dimension(38, 0)));
        dayBox.add(comboDay);

        centerBox.add(dayBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 35)));

        centerBox.add(toolBox);
        centerBox.add(Box.createRigidArea(new Dimension(0, 25)));

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
            MyDate date = MainGraphicGUI.isAtDateList(parentWindow.getAllDates(),
                    Integer.parseInt(comboDay.getSelectedItem().toString()),
                    Integer.parseInt(comboMonth.getSelectedItem().toString()),
                    Integer.parseInt(comboYear.getSelectedItem().toString()));
            if (date == null) {
                date = new MyDate(Integer.parseInt(comboDay.getSelectedItem().toString()),
                        Integer.parseInt(comboMonth.getSelectedItem().toString()),
                        Integer.parseInt(comboYear.getSelectedItem().toString()));
                date.setDateID(parentWindow.getMyDateDao().getFreeID());
                parentWindow.addToAllDates(date);
                parentWindow.getMyDateDao().updateFreeID(parentWindow.getAllDates());
                Track track = MainTrackGUI.isAtTrackList(
                        parentWindow.getParentWindow().getMainTrackGUI().getAllTracks(),
                        comboTrack.getSelectedItem().toString());

                Competition competition = new Competition(date, track);
                competition.setCompetitionID(parentWindow.getCompetitionDao().getFreeID());
                parentWindow.addToAllCompetitions(competition);
                parentWindow.getCompetitionDao().updateFreeID(parentWindow.getAllCompetitions());
                parentWindow.setCompetitionsTable();
            } else {
                JOptionPane.showMessageDialog(addGraphicGUI, "В эту дату уже проводится соревнование!", "Сообщение",
                        JOptionPane.PLAIN_MESSAGE);
            }

        }
    }

    private class SelectEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            comboDay.removeAllItems();
            comboDay.setSelectedItem(null);
            updateComboDay();
        }
    }

    private class CancelEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            parentWindow.setMainGraphicEnable(true);
            addGraphicGUI.setVisible(false);
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

    public void updateComboDay() {
        int last;
        if (comboMonth.getSelectedIndex() != 0 && comboYear.getSelectedIndex() != 0) {
            if (Integer.parseInt(comboMonth.getSelectedItem().toString()) != 2) {
                last = " 1 3 5 7 8 10 12 ".contains(comboMonth.getSelectedItem().toString()) ? 32 : 31;
            } else
                last = Integer.parseInt(comboYear.getSelectedItem().toString()) % 4 == 0 ? 30 : 29;
            for (int i = 1; i < last; i++) {
                comboDay.addItem(Integer.valueOf(i).toString());
            }
        }
        comboDay.setEnabled(comboMonth.getSelectedIndex() != 0 && comboYear.getSelectedIndex() != 0);
    }

    public void updateComboMonth() {
        comboMonth.addItem("Не выбран");
        comboMonth.setSelectedIndex(0);
        for (int i = 1; i < 13; i++) {
            comboMonth.addItem(Integer.valueOf(i).toString());
        }
    }

    public void updateComboYear() {
        comboYear.addItem("Не выбран");
        comboYear.setSelectedIndex(0);
        for (int i = 2024; i < 2100; i++) {
            comboYear.addItem(Integer.valueOf(i).toString());
        }
    }

    public void setAddGraphicVisibility(boolean value) {
        addGraphicGUI.setVisible(value);
    }

}
