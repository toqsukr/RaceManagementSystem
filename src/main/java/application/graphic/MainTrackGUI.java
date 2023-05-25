package application.graphic;

import util.CreateReport;
import util.Validation;
import race.system.Competition;
import race.system.Racer;
import race.system.Score;
import race.system.Track;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import database.TrackDao;
import exception.InvalidDataException;
import exception.InvalidTrackLengthInputException;
import exception.InvalidTrackNameInputException;
import exception.NothingDataException;
import exception.UnselectedDeleteException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

/**
 * GUI of Race Management System
 */
public class MainTrackGUI extends JFrame {
    private static JFrame mainTrackGUI = new JFrame("Список трасс");

    /**
     * This button adds new field into table
     */
    private static final JButton addBtn = new JButton();

    /**
     * This button deletes selected field
     */
    private static final JButton deleteBtn = new JButton();

    /**
     * This button allows you to edit selected field
     */
    private static final JButton editBtn = new JButton();

    /**
     * This button forms team data report
     */
    private static final JButton reportBtn = new JButton();

    /**
     * This button confirms changes made
     */
    private static final JButton confirmBtn = new JButton();

    /**
     * This button cancles changes made
     */
    private static final JButton cancelBtn = new JButton();

    private static final JButton toDataBaseBtn = new JButton();

    private static final JButton fromDataBaseBtn = new JButton();

    private JComboBox<String> comboRacer = new JComboBox<>();

    /**
     * This bar is used to store buttons
     */
    private static final JToolBar toolBar = new JToolBar();

    /**
     * Table column names
     */
    private static final String[] columns = { "Название трассы", "Длина трассы", "Призер" };

    /**
     * Fields of the table
     */
    private static String[][] data = {};

    /**
     * The table model storing displaying data
     */
    private static DefaultTableModel trackTable = new DefaultTableModel(data, columns);

    /**
     * The table model storing the version of the table before editing
     */
    private static DefaultTableModel previousTrackTable = new DefaultTableModel(data, columns);

    /**
     * Create the table
     */
    private final JTable tracks = new JTable(trackTable) {
        @Override
        public boolean isCellEditable(int i, int j) {
            return getEditingPermit();
        }
    };

    /**
     * Creation of the scroll panel
     */
    private final JScrollPane scroll = new JScrollPane(tracks);

    /***
     * Variable storing table edit status
     */
    private boolean editingPermit = false;

    /***
     * The logger variable
     */
    private static Logger logger;

    private MainMenuGUI parentWindow;

    private AddTrackGUI addTrackWindow;

    private List<Track> allTracks;

    /***
     * The function creating mainTrackGUI
     */
    public MainTrackGUI(MainMenuGUI parent) {
        parentWindow = parent;

        try {

            ConfigurationFactory factory = XmlConfigurationFactory.getInstance();
            URL configUrl = this.getClass().getClassLoader().getResource("configuration.xml");
            InputStream inputStream = configUrl.openStream();
            ConfigurationSource configurationSource = new ConfigurationSource(inputStream);

            Configuration configuration = factory.getConfiguration(null, configurationSource);

            ConsoleAppender appender = ConsoleAppender
                    .createDefaultAppenderForLayout(PatternLayout.createDefaultLayout());

            configuration.addAppender(appender);

            LoggerContext context = new LoggerContext("JournalDevLoggerContext");
            startLogging(context, configuration);

            mainTrackGUI.addWindowListener((WindowListener) new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        MainRacerGUI.stopEditCell(tracks);
                        int result = 0;
                        checkEditedData();
                        if (editingPermit) {
                            if (tracks.getSelectedRow() != -1)
                                tracks.getCellEditor(tracks.getSelectedRow(), tracks.getSelectedColumn())
                                        .stopCellEditing();
                            if (!MainRacerGUI.isEqualTable(trackTable, previousTrackTable)) {
                                checkEditedData();
                                result = JOptionPane.showConfirmDialog(mainTrackGUI, "Сохранить изменения?",
                                        "Подтверждение действия",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);
                                if (result == JOptionPane.YES_OPTION) {
                                    compareEditedData();
                                    setEditingPermit(false);
                                    setConfirmbarUnvisible();
                                } else if (result == JOptionPane.NO_OPTION) {
                                    cancelBtn.doClick();
                                }
                            } else {
                                setEditingPermit(false);
                                setConfirmbarUnvisible();
                            }
                        }
                        if (result == 0 || result == 1) {
                            stopLogging(context);
                            mainTrackGUI.dispose();
                        }
                    } catch (InvalidDataException exception) {
                        int confirm = JOptionPane.showConfirmDialog(mainTrackGUI,
                                "Данные содержат ошибку и не могут быть сохранены!\nЗакрыть окно?",
                                "Предупреждение",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (confirm == JOptionPane.OK_OPTION) {
                            cancelBtn.doClick();
                            stopLogging(context);
                            mainTrackGUI.dispose();
                        }
                    }
                }
            });

            mainTrackGUI.setBounds(200, 150, 800, 600);
            mainTrackGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            mainTrackGUI.setResizable(false);
            URL mainTeamIcon = this.getClass().getClassLoader().getResource("img/track.png");
            mainTrackGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(mainTeamIcon));
            toolBar.setFloatable(false);
            tracks.getTableHeader().setReorderingAllowed(false);

            try {
                allTracks = getTrackData();
                parentWindow.getMainRacerGUI().getTrackDao().updateFreeID(allTracks);
                setTrackTable();
            } catch (InterruptedException exception) {
                JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(), "Ошибка чтения данных из базы!",
                        JOptionPane.PLAIN_MESSAGE);
            }

            addTrackWindow = new AddTrackGUI(this);

            updateComboRacer();
            Container container = mainTrackGUI.getContentPane();
            container.setLayout(new BorderLayout());

            DefaultCellEditor editor = new DefaultCellEditor(comboRacer);
            tracks.getColumnModel().getColumn(2).setCellEditor(editor);

            URL addIcon = this.getClass().getClassLoader().getResource("img/add_track.png");
            addBtn.setIcon(new ImageIcon(new ImageIcon(addIcon).getImage().getScaledInstance(50, 50, 4)));
            addBtn.setToolTipText("Добавить трассу");
            addBtn.addActionListener(new AddEventListener());
            addBtn.setBackground(new Color(0xDFD9D9D9, false));
            addBtn.setFocusable(false);

            URL deleteIcon = this.getClass().getClassLoader().getResource("img/delete_track.png");
            deleteBtn.setIcon(new ImageIcon(new ImageIcon(deleteIcon).getImage().getScaledInstance(50, 50, 4)));
            deleteBtn.setToolTipText("Удалить трассу");
            deleteBtn.addActionListener(new DeleteEventListener());
            deleteBtn.setBackground(new Color(0xDFD9D9D9, false));
            deleteBtn.setFocusable(false);

            URL editIcon = this.getClass().getClassLoader().getResource("img/edit.png");
            editBtn.setIcon(new ImageIcon(new ImageIcon(editIcon).getImage().getScaledInstance(50, 50, 4)));
            editBtn.setToolTipText("Редактировать запись");
            editBtn.addActionListener(new EditEventListener());
            editBtn.setBackground(new Color(0xDFD9D9D9, false));
            editBtn.setFocusable(false);

            URL reportIcon = this.getClass().getClassLoader().getResource("img/report.png");
            reportBtn.setIcon(new ImageIcon(new ImageIcon(reportIcon).getImage().getScaledInstance(50, 50, 4)));
            reportBtn.setToolTipText("Сформировать отчет");
            reportBtn.addActionListener(new ReportEventListener());
            reportBtn.setBackground(new Color(0xDFD9D9D9, false));
            reportBtn.setFocusable(false);

            URL toDataBaseUrl = this.getClass().getClassLoader().getResource("img/deploytodb.png");
            toDataBaseBtn.setIcon(new ImageIcon(new ImageIcon(toDataBaseUrl).getImage().getScaledInstance(50, 50, 4)));
            toDataBaseBtn.setToolTipText("Выгрузить в базу данных");
            toDataBaseBtn.addActionListener(new ToDataBaseEventListener());
            toDataBaseBtn.setBackground(new Color(0xDFD9D9D9, false));
            toDataBaseBtn.setFocusable(false);

            URL fromDataBaseUrl = this.getClass().getClassLoader().getResource("img/downloadfromdb.png");
            fromDataBaseBtn
                    .setIcon(new ImageIcon(new ImageIcon(fromDataBaseUrl).getImage().getScaledInstance(50, 50, 4)));
            fromDataBaseBtn.setToolTipText("Загрузить данные из базы данных");
            fromDataBaseBtn.addActionListener(new FromDataBaseEventListener());
            fromDataBaseBtn.setBackground(new Color(0xDFD9D9D9, false));
            fromDataBaseBtn.setFocusable(false);

            URL confirmIcon = this.getClass().getClassLoader().getResource("img/confirm.png");
            confirmBtn.setIcon(new ImageIcon(new ImageIcon(confirmIcon).getImage().getScaledInstance(50, 50, 4)));
            confirmBtn.setVisible(false);
            confirmBtn.addActionListener(new ConfirmEventListener());
            confirmBtn.setToolTipText("Ок");
            confirmBtn.setBackground(new Color(0xDFD9D9D9, false));
            confirmBtn.setFocusable(false);

            URL cancelIcon = this.getClass().getClassLoader().getResource("img/cancel.png");
            cancelBtn.setIcon(new ImageIcon(new ImageIcon(cancelIcon).getImage().getScaledInstance(50, 50, 4)));
            cancelBtn.setVisible(false);
            cancelBtn.addActionListener(new CancelEventListener());
            cancelBtn.setToolTipText("Отмена");
            cancelBtn.setBackground(new Color(0xDFD9D9D9, false));
            cancelBtn.setFocusable(false);

            toolBar.add(fromDataBaseBtn);
            toolBar.add(toDataBaseBtn);
            toolBar.add(addBtn);
            toolBar.add(deleteBtn);
            toolBar.add(editBtn);
            toolBar.add(reportBtn);
            toolBar.add(confirmBtn);
            toolBar.add(cancelBtn);

            container.add(toolBar, BorderLayout.NORTH);
            container.add(scroll, BorderLayout.CENTER);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    /**
     * Сlass for implementing a report button listener
     */
    private static class ReportEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                URL boldFontPath = this.getClass().getClassLoader()
                        .getResource("fonts/DejaVuSans/DejaVuSans.ttf");
                CreateReport.printReport(trackTable, mainTrackGUI, "Отчет по списку трасс\n\n\n\n\n",
                        new float[] { 1f, 1f, 1f },
                        new String[] { "\nНазвание трассы\n", "\nДлина трассы\n", "\nПризер\n" },
                        boldFontPath);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(), "Ошибка формирования отчета",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Сlass for implementing a toDataBase button listener
     */
    private class ToDataBaseEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            parentWindow.getMainRacerGUI().deployToDataBase();
        }
    }

    /**
     * Сlass for implementing a fromDataBase button listener
     */
    private class FromDataBaseEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            parentWindow.getMainRacerGUI().downloadFromDataBase();
        }
    }

    /**
     * Сlass for implementing a addBtn button listener
     */
    private class AddEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            logger.info("Opening window AddRacerGUI");
            setMainTrackEnable(false);
            addTrackWindow.setAddTrackVisibility(true);
        }
    }

    /**
     * Сlass for implementing a editBtn button listener
     */
    private class EditEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                MainRacerGUI.checkEmptyData("Данные для редактирования не найдены!", trackTable);
                MainRacerGUI.copyTable(trackTable, previousTrackTable);
                setEditingPermit(true);
                setConfirmbarVisible();
            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Сlass for implementing a confirmBtn button listener
     */
    private class ConfirmEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                if (tracks.getSelectedRow() != -1)
                    tracks.getCellEditor(tracks.getSelectedRow(), tracks.getSelectedColumn()).stopCellEditing();
                if (!MainRacerGUI.isEqualTable(trackTable, previousTrackTable)) {
                    checkEditedData();
                    int result = JOptionPane.showConfirmDialog(mainTrackGUI, "Сохранить изменения?",
                            "Подтверждение действия",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        compareEditedData();
                        updateAllScores();
                        updateAllCompetitions();
                        parentWindow.getMainScoreGUI().updateComboTrack();
                        parentWindow.getMainScoreGUI().getAddScoreWindow().updateComboTrack();
                        parentWindow.getMainScoreGUI().setScoreTable();
                        parentWindow.getMainGraphicGUI().setCompetitionsTable();
                        parentWindow.getMainGraphicGUI().updateComboTrack();
                        parentWindow.getMainGraphicGUI().getAddGraphicGUI().updateComboTrack();
                        setEditingPermit(false);
                        setConfirmbarUnvisible();
                    }
                } else {
                    setEditingPermit(false);
                    setConfirmbarUnvisible();
                }
            } catch (InvalidTrackNameInputException exception) {
                logger.warn("Entered invalid track name while editing");
                JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTrackLengthInputException exception) {
                logger.warn("Entered invalid track length while editing");
                JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Сlass for implementing a cancelBtn button listener
     */
    private class CancelEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            if (tracks.getSelectedRow() != -1)
                tracks.getCellEditor(tracks.getSelectedRow(), tracks.getSelectedColumn()).stopCellEditing();
            MainRacerGUI.copyTable(previousTrackTable, trackTable);
            setEditingPermit(false);
            setConfirmbarUnvisible();
        }
    }

    private class DeleteEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                MainRacerGUI.checkEmptyData("Данные для удаления не найдены!", trackTable);
                MainRacerGUI.checkDeleteSelect(tracks);

                String message = tracks.getSelectedRows().length == 1
                        ? "Вы действительно хотите удалить выбранную запись?\nВсе рекорды, поставленные на этой трассе,\nа также будущие соревнования, которые будут проходить на ней будут удалены!\nОтменить действие будет невозможно!"
                        : "Вы действительно хотите удалить выбранные записи?\nВсе рекорды, поставленные на этих трассах,\nа также будущие соревнования, которые будут проходить на них будут удалены!\nОтменить действие будет невозможно!";
                int result = JOptionPane.showConfirmDialog(mainTrackGUI,
                        message,
                        "Подтверждение действия",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {

                    int i = tracks.getSelectedRows().length - 1;
                    while (tracks.getSelectedRows().length > 0) {
                        int j = tracks.getRowCount() - 1;
                        while (j > -1) {
                            if (j == tracks.getSelectedRows()[i]) {
                                String removingTrackName = tracks.getValueAt(tracks.getSelectedRows()[i], 0).toString();
                                String removingLength = tracks.getValueAt(tracks.getSelectedRows()[i], 1).toString();
                                trackTable.removeRow(tracks.getSelectedRows()[i]);
                                Track removingTrack = isAtTrackList(allTracks,
                                        new Track(removingTrackName, Integer.parseInt(removingLength)));
                                parentWindow.getMainRacerGUI().getTrackDao().addFreeID(removingTrack.getTrackID());
                                updateScores(removingTrack);
                                updateCompetitions(removingTrack);
                                allTracks.remove(allTracks.indexOf(removingTrack));
                                break;
                            }
                            j--;
                        }
                        i--;
                    }
                    parentWindow.getMainGraphicGUI().updateComboTrack();
                    parentWindow.getMainGraphicGUI().setCompetitionsTable();
                    parentWindow.getMainScoreGUI().updateComboTrack();
                    parentWindow.getMainScoreGUI().getAddScoreWindow().updateComboTrack();
                    parentWindow.getMainScoreGUI().setScoreTable();
                    parentWindow.getMainGraphicGUI().getAddGraphicGUI().updateComboTrack();
                    parentWindow.getMainTrackGUI().setTrackTable();
                }
            } catch (UnselectedDeleteException exception) {
                JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(), "Ошибка удаления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    public void setEditingPermit(boolean value) {
        editingPermit = value;
    }

    /***
     * The function make visible confirm bar while editing
     */
    private static void setConfirmbarVisible() {
        addBtn.setVisible(false);
        toDataBaseBtn.setVisible(false);
        fromDataBaseBtn.setVisible(false);
        deleteBtn.setVisible(false);
        editBtn.setVisible(false);
        reportBtn.setVisible(false);
        confirmBtn.setVisible(true);
        cancelBtn.setVisible(true);
    }

    /***
     * The function make unvisible confirm bar while editing
     */
    private static void setConfirmbarUnvisible() {
        addBtn.setVisible(true);
        toDataBaseBtn.setVisible(true);
        fromDataBaseBtn.setVisible(true);
        deleteBtn.setVisible(true);
        editBtn.setVisible(true);
        reportBtn.setVisible(true);
        confirmBtn.setVisible(false);
        cancelBtn.setVisible(false);
    }

    private boolean getEditingPermit() {
        return editingPermit;
    }

    public void setVisible(boolean value) {
        mainTrackGUI.setVisible(value);
    }

    public DefaultTableModel getTrackTable() {
        return trackTable;
    }

    /***
     * The function starts logging
     * 
     * @param context       the logger variable
     * @param configuration params of logging
     * @throws IOException checks whether there are input/output errors
     */

    private static void startLogging(LoggerContext context, Configuration configuration) throws IOException {
        context.start(configuration);
        logger = context.getLogger("com");
        logger.log(Level.INFO, "Start logging mainTrackGUI");
    }

    /***
     * The function stops logging
     * 
     * @param context the logger variable
     */
    public static void stopLogging(LoggerContext context) {
        logger.log(Level.INFO, "Stop logging mainTrackGUI");
        context.close();
    }

    /***
     * The function sets enability options of MainTrackGUI window
     * 
     * @param value the value to be setted
     */
    public void setMainTrackEnable(boolean value) {
        mainTrackGUI.setEnabled(value);
    }

    public MainMenuGUI getParentWindow() {
        return parentWindow;
    }

    public List<Track> getTrackData() throws InterruptedException {
        TrackDao trackDao = parentWindow.getMainRacerGUI().getTrackDao();
        return trackDao.getAllTracks();
    }

    public List<Track> getAllTracks() {
        return allTracks;
    }

    public void setTrackTable() {
        if (trackTable.getRowCount() != 0)
            MainRacerGUI.clearTable(trackTable);

        for (Track track : allTracks) {
            String winner;
            if (track.getWinner() != null)
                winner = track.getWinner().getRacerName() + " (ID: " + track.getWinner().getRacerID() + ")";
            else
                winner = "Нет";

            trackTable.addRow(new String[] { track.getTrackName(), track.getTrackLength().toString(), winner });
        }
    }

    public static Track isAtTrackList(List<Track> tracks, Track track) {
        Track answer = null;
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getTrackName().equals(track.getTrackName())
                    && tracks.get(i).getTrackLength().equals(track.getTrackLength())) {
                answer = tracks.get(i);
                break;
            }
        }
        return answer;
    }

    public static Track isAtTrackList(List<Track> tracks, String trackName) {
        Track answer = null;
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getTrackName().toLowerCase().equals(trackName.toLowerCase())) {
                answer = tracks.get(i);
                break;
            }
        }
        return answer;
    }

    public static Track isAtTrackList(List<Track> tracks, Integer id) {
        Track answer = null;
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getTrackID().equals(id)) {
                answer = tracks.get(i);
                break;
            }
        }
        return answer;
    }

    public void addToAllTracks(Track track) {
        allTracks.add(track);
    }

    public void updateComboRacer() {
        comboRacer.removeAllItems();
        comboRacer.setSelectedItem(null);
        comboRacer.addItem("Нет");
        comboRacer.setSelectedIndex(0);
        List<Racer> allRacers = parentWindow.getMainRacerGUI().getAllRacers();
        for (Racer racer : allRacers) {
            comboRacer.addItem(racer.getRacerName() + " (ID: " + racer.getRacerID() + ")");
        }
    }

    public JComboBox<String> getComboRacer() {
        return comboRacer;
    }

    public AddTrackGUI getAddTrackWindow() {
        return addTrackWindow;
    }

    public static Track isRacerAtTrackList(List<Track> tracks, Racer racer) {
        Track answer = null;
        for (Track track : tracks) {
            if (track.getWinner().getRacerID() == racer.getRacerID()) {
                answer = track;
                break;
            }
        }
        return answer;
    }

    private void compareEditedData() {
        for (int i = 0; i < trackTable.getRowCount(); i++) {
            String name = trackTable.getValueAt(i, 0).toString();
            String length = trackTable.getValueAt(i, 1).toString();
            String winner = trackTable.getValueAt(i, 2).toString();
            if (!name.equals(allTracks.get(i).getTrackName()))
                allTracks.get(i).setTrackName(name);
            if (!length.equals(allTracks.get(i).getTrackLength().toString()))
                allTracks.get(i).setTrackLength(Integer.parseInt(length));
            if (!winner.equals("Нет")) {
                int id = Integer.parseInt(winner.substring(winner.indexOf(':', 0) + 2, (winner.indexOf(')', 0))));
                if (allTracks.get(i).getWinner() == null || id != allTracks.get(i).getWinner().getRacerID()) {
                    Racer racer = MainRacerGUI.isAtRacerList(parentWindow.getMainRacerGUI().getAllRacers(), id);
                    allTracks.get(i).setWinner(racer);
                }
            } else {
                allTracks.get(i).setWinner(null);
            }
        }
    }

    /***
     * The function checks whether table data is valid
     */

    private void checkEditedData() throws InvalidTrackNameInputException, InvalidTrackLengthInputException {
        for (int i = 0; i < trackTable.getRowCount(); i++) {
            if (!Validation.isValidTrackName(trackTable.getValueAt(i, 0).toString()))
                throw new InvalidTrackNameInputException(i + 1);
            if (!Validation.isValidTrackLength(trackTable.getValueAt(i, 1).toString()))
                throw new InvalidTrackLengthInputException(i + 1);
        }
    }

    public void setAllTracks(List<Track> list) {
        allTracks = list;
    }

    public void clearAllTracks() {
        allTracks.clear();
    }

    public void clearComboRacer() {
        comboRacer.removeAllItems();
        comboRacer.setSelectedItem(null);
    }

    public void deleteFromAllTracks(int id) {
        for (Track track : allTracks) {
            if (track.getTrackID() == id) {
                allTracks.remove(track);
                break;
            }
        }
    }

    private void updateScores(Track track) {
        List<Score> scores = parentWindow.getMainScoreGUI().getAllScores();
        for (int i = scores.size() - 1; i > -1; i--) {
            if (scores.get(i).getTrackInfo().getTrackID().equals(track.getTrackID()))
                parentWindow.getMainScoreGUI().deleteFromAllScores(scores.get(i).getScoreID());
        }
    }

    private void updateCompetitions(Track track) {
        List<Competition> competitions = parentWindow.getMainGraphicGUI().getAllCompetitions();
        for (int i = competitions.size() - 1; i > -1; i--) {
            if (competitions.get(i).getTrack().getTrackID().equals(track.getTrackID())) {
                parentWindow.getMainGraphicGUI().deleteFromAllDates(competitions.get(i).getDate().getDateID());
                parentWindow.getMainGraphicGUI().deleteFromAllCompetitions(competitions.get(i).getCompetitionID());
            }
        }
    }

    private void updateAllScores() {
        for (Track track : allTracks) {
            for (Score score : parentWindow.getMainScoreGUI().getAllScores()) {
                if (score.getTrackInfo().getTrackID().equals(track.getTrackID()))
                    score.setTrackInfo(track);
            }
        }
    }

    private void updateAllCompetitions() {
        for (Track track : allTracks) {
            for (Competition competition : parentWindow.getMainGraphicGUI().getAllCompetitions()) {
                if (competition.getTrack().getTrackID().equals(track.getTrackID()))
                    competition.setTrack(track);
            }
        }
    }

}
