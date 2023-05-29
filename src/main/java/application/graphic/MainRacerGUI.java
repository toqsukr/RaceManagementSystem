package application.graphic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
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

import application.App;
import database.RacerDao;
import database.TeamDao;
import database.TrackDao;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import exception.FileFormatException;
import exception.IdenticalDataException;
import exception.InvalidDataException;
import exception.InvalidNameInputException;
import exception.InvalidPointInputException;
import exception.InvalidTeamInputException;
import exception.UnselectedDeleteException;
import exception.NothingDataException;
import race.system.Competition;
import race.system.MyDate;
import race.system.Racer;
import race.system.Score;
import race.system.Team;
import race.system.Track;
import util.CreateReport;
import util.FileManage;
import util.Validation;

/**
 * GUI of Race Management System
 */
public class MainRacerGUI extends JFrame {
    private static JFrame mainRacerGUI = new JFrame("Список гонщиков");

    /**
     * This button performs a search
     */
    private static final JButton searchBtn = new JButton("Искать");

    /**
     * This button performs a clear search inputs
     */
    private static final JButton clearInputBtn = new JButton("Очистить");

    /**
     * This button performs a disrupt values of search inputs
     */
    private static final JButton disruptInputBtn = new JButton("Сбросить фильтр");

    /**
     * This button opens file
     */
    private static final JButton fileBtn = new JButton();

    /**
     * This button saves changes
     */
    private static final JButton saveBtn = new JButton();

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
     * This button forms racer data report
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

    private JComboBox<String> comboTeam = new JComboBox<>();

    /**
     * This input is used to search for an entry in the table by the name of the
     * racer
     */
    private static final JTextField searchNameField = new JTextField("Имя гонщика", 17);

    /**
     * This input is used to search for an entry in the table by the team of the
     * racer
     */
    private JComboBox<String> searchTeam = new JComboBox<>();

    private JComboBox<String> comboAge = new JComboBox<>();

    /**
     * This bar is used to store buttons
     */
    private static final JToolBar toolBar = new JToolBar();

    /**
     * Table column names
     */
    private static final String[] columns = { "ID", "Имя гонщика", "Возраст", "Команда", "Очки" };

    /**
     * Fields of the table
     */
    private static String[][] data = {};

    /**
     * The table model storing displaying data
     */
    private static DefaultTableModel racerTable = new DefaultTableModel(data, columns);

    /**
     * The table model storing the version of the table before editing
     */
    private static DefaultTableModel previousRacerTable = new DefaultTableModel(data, columns);

    /**
     * The table model storing the full version of the table before searching
     */
    private static DefaultTableModel fullSearchTable = new DefaultTableModel(data, columns);

    /**
     * Create the table
     */
    private final JTable racers = new JTable(racerTable) {
        @Override
        public boolean isCellEditable(int i, int j) {
            return j != 0 && getEditingPermit();
        }
    };

    /**
     * Creation of the scroll panel
     */
    private final JScrollPane scroll = new JScrollPane(racers);

    /**
     * This panel store 2 inputs and search button
     */
    private static final JPanel filterPanel = new JPanel();

    /***
     * Variable storing table edit status
     */
    private boolean editingPermit = false;

    /***
     * The add racer window
     */
    private AddRacerGUI addRacerWindow = new AddRacerGUI(this);;

    private List<Team> allTeams;

    private List<Racer> allRacers;
    /***
     * The logger variable
     */
    private static Logger logger;

    private boolean isOpenFile = false;

    private MainMenuGUI parentWindow;

    private RacerDao racerDao = new RacerDao(App.getEntityManager());

    private TeamDao teamDao = new TeamDao(App.getEntityManager());

    private TrackDao trackDao = new TrackDao(App.getEntityManager());

    /***
     * The function creating MainRacerGUI
     */
    public MainRacerGUI(MainMenuGUI parent) {
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

            mainRacerGUI.addWindowListener((WindowListener) new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        stopEditCell(racers);
                        checkEditedData();
                        int result = 0;
                        if (editingPermit) {
                            if (racers.getSelectedRow() != -1)
                                racers.getCellEditor(racers.getSelectedRow(), racers.getSelectedColumn())
                                        .stopCellEditing();
                            if (!isEqualTable(racerTable, previousRacerTable)) {
                                result = JOptionPane.showConfirmDialog(mainRacerGUI, "Сохранить изменения?",
                                        "Подтверждение действия",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);
                                if (result == JOptionPane.YES_OPTION) {
                                    additionalSearchEdit();
                                    compareEditedData();
                                    deleteEmptyTeams();
                                    parentWindow.getMainTeamGUI().setTeamTable();
                                    updateComboTeam();
                                    addRacerWindow.updateComboTeam();
                                    parentWindow.getMainTrackGUI().updateComboRacer();
                                    parentWindow.getMainTrackGUI().getAddTrackWindow().updateComboRacer();
                                    setConfirmbarUnvisible();
                                    changeEditingPermit();
                                } else if (result == JOptionPane.NO_OPTION) {
                                    cancelBtn.doClick();
                                }
                            } else {
                                setConfirmbarUnvisible();
                                changeEditingPermit();
                            }
                        }
                        if (result == 0 || result == 1) {
                            clearInputBtn.doClick();
                            disruptInputBtn.doClick();
                            stopLogging(context);
                            mainRacerGUI.dispose();
                        }

                    } catch (InvalidDataException exception) {
                        int confirm = JOptionPane.showConfirmDialog(mainRacerGUI,
                                "Данные содержат ошибку и не могут быть сохранены!\nЗакрыть окно?",
                                "Предупреждение",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (confirm == JOptionPane.OK_OPTION) {
                            cancelBtn.doClick();
                            stopLogging(context);
                            parentWindow.setMainMenuEnable(true);
                            mainRacerGUI.dispose();
                        }
                    }
                }
            });

            mainRacerGUI.setBounds(200, 150, 800, 600);
            mainRacerGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            mainRacerGUI.setResizable(false);
            URL mainRacerIcon = this.getClass().getClassLoader().getResource("img/racer.png");
            mainRacerGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(mainRacerIcon));
            toolBar.setFloatable(false);
            racers.getTableHeader().setReorderingAllowed(false);
            racers.setRowHeight(racers.getRowHeight() + 4);
            try {
                allTeams = getTeamData();
                allRacers = getRacerData();
                setRacerTable();
                racerDao.updateFreeID(allRacers);
                teamDao.updateFreeID(allTeams);
            } catch (InterruptedException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка чтения данных из базы!",
                        JOptionPane.PLAIN_MESSAGE);
            }

            setComboAge();
            updateComboTeam();
            DefaultCellEditor editorTeam = new DefaultCellEditor(comboTeam);
            racers.getColumnModel().getColumn(3).setCellEditor(editorTeam);

            DefaultCellEditor editorAge = new DefaultCellEditor(comboAge);
            racers.getColumnModel().getColumn(2).setCellEditor(editorAge);

            Container container = mainRacerGUI.getContentPane();
            container.setLayout(new BorderLayout());

            searchBtn.setBackground(new Color(0xDFD9D9D9, false));
            clearInputBtn.setBackground(new Color(0xDFD9D9D9, false));
            disruptInputBtn.setBackground(new Color(0xDFD9D9D9, false));

            searchNameField.addFocusListener(new RacerInputFocusListener());

            searchNameField.setMargin(new Insets(2, 2, 3, 0));

            searchBtn.addActionListener(new SearchEventListener());
            searchBtn.setMargin(new Insets(1, 6, 1, 6));

            disruptInputBtn.addActionListener(new DisruptEventListener());
            disruptInputBtn.setMargin(new Insets(1, 6, 1, 6));

            clearInputBtn.addActionListener(new ClearInputEventListener());
            clearInputBtn.setMargin(new Insets(1, 6, 1, 6));

            filterPanel.add(searchNameField);
            filterPanel.add(searchTeam);
            filterPanel.add(searchBtn);
            filterPanel.add(clearInputBtn);
            filterPanel.add(disruptInputBtn);

            // FlatSVGIcon svgIcon = new FlatSVGIcon("etu/src/img/close.svg", 50, 50);
            // fileBtn.setIcon(svgIcon);

            URL fileIcon = this.getClass().getClassLoader().getResource("img/file.png");
            fileBtn.setIcon(new ImageIcon(new ImageIcon(fileIcon).getImage().getScaledInstance(50, 50, 4)));
            fileBtn.setToolTipText("Открыть файл");
            fileBtn.setBackground(new Color(0xDFD9D9D9, false));
            fileBtn.addActionListener(new FileEventListener());
            fileBtn.setFocusable(false);

            URL saveIcon = this.getClass().getClassLoader().getResource("img/save.png");
            saveBtn.setIcon(new ImageIcon(new ImageIcon(saveIcon).getImage().getScaledInstance(50, 50, 4)));
            saveBtn.setToolTipText("Сохранить файл");
            saveBtn.setBackground(new Color(0xDFD9D9D9, false));
            saveBtn.addActionListener(new SaveEventListener());
            saveBtn.setFocusable(false);

            URL addIcon = this.getClass().getClassLoader().getResource("img/add.png");
            addBtn.setIcon(new ImageIcon(new ImageIcon(addIcon).getImage().getScaledInstance(50, 50, 4)));
            addBtn.setToolTipText("Добавить гонщика");
            addBtn.setBackground(new Color(0xDFD9D9D9, false));
            addBtn.addActionListener(new AddEventListener());
            addBtn.setFocusable(false);

            URL deleteIcon = this.getClass().getClassLoader().getResource("img/delete.png");
            deleteBtn.setIcon(new ImageIcon(new ImageIcon(deleteIcon).getImage().getScaledInstance(50, 50, 4)));
            deleteBtn.setToolTipText("Удалить гонщика");
            deleteBtn.setBackground(new Color(0xDFD9D9D9, false));
            deleteBtn.addActionListener(new DeleteEventListener());
            deleteBtn.setFocusable(false);

            URL editIcon = this.getClass().getClassLoader().getResource("img/edit.png");
            editBtn.setIcon(new ImageIcon(new ImageIcon(editIcon).getImage().getScaledInstance(50, 50, 4)));
            editBtn.setToolTipText("Редактировать запись");
            editBtn.setBackground(new Color(0xDFD9D9D9, false));
            editBtn.addActionListener(new EditEventListener());
            editBtn.setFocusable(false);

            URL reportIcon = this.getClass().getClassLoader().getResource("img/report.png");
            reportBtn.setIcon(new ImageIcon(new ImageIcon(reportIcon).getImage().getScaledInstance(50, 50, 4)));
            reportBtn.setToolTipText("Сформировать отчет");
            reportBtn.setBackground(new Color(0xDFD9D9D9, false));
            reportBtn.addActionListener(new ReportEventListener());
            reportBtn.setFocusable(false);

            URL toDataBaseUrl = this.getClass().getClassLoader().getResource("img/deploytodb.png");
            toDataBaseBtn.setIcon(new ImageIcon(new ImageIcon(toDataBaseUrl).getImage().getScaledInstance(50, 50, 4)));
            toDataBaseBtn.setToolTipText("Выгрузить в базу данных");
            toDataBaseBtn.setBackground(new Color(0xDFD9D9D9, false));
            toDataBaseBtn.addActionListener(new ToDataBaseEventListener());
            toDataBaseBtn.setFocusable(false);

            URL fromDataBaseUrl = this.getClass().getClassLoader().getResource("img/downloadfromdb.png");
            fromDataBaseBtn
                    .setIcon(new ImageIcon(new ImageIcon(fromDataBaseUrl).getImage().getScaledInstance(50, 50, 4)));
            fromDataBaseBtn.setToolTipText("Загрузить данные из базы данных");
            fromDataBaseBtn.setBackground(new Color(0xDFD9D9D9, false));
            fromDataBaseBtn.addActionListener(new FromDataBaseEventListener());
            fromDataBaseBtn.setFocusable(false);

            URL confirmIcon = this.getClass().getClassLoader().getResource("img/confirm.png");
            confirmBtn.setIcon(new ImageIcon(new ImageIcon(confirmIcon).getImage().getScaledInstance(50, 50, 4)));
            confirmBtn.setVisible(false);
            confirmBtn.setToolTipText("Ок");
            confirmBtn.setBackground(new Color(0xDFD9D9D9, false));
            confirmBtn.addActionListener(new ConfirmEventListener());
            confirmBtn.setFocusable(false);

            URL cancelIcon = this.getClass().getClassLoader().getResource("img/cancel.png");
            cancelBtn.setIcon(new ImageIcon(new ImageIcon(cancelIcon).getImage().getScaledInstance(50, 50, 4)));
            cancelBtn.setVisible(false);
            cancelBtn.setToolTipText("Отмена");
            cancelBtn.setBackground(new Color(0xDFD9D9D9, false));
            cancelBtn.addActionListener(new CancelEventListener());
            cancelBtn.setFocusable(false);

            // toolBar.add(fileBtn);
            toolBar.add(fromDataBaseBtn);
            // toolBar.add(saveBtn);
            toolBar.add(toDataBaseBtn);
            toolBar.add(addBtn);
            toolBar.add(deleteBtn);
            toolBar.add(editBtn);
            toolBar.add(reportBtn);
            toolBar.add(confirmBtn);
            toolBar.add(cancelBtn);

            container.add(toolBar, BorderLayout.NORTH);
            container.add(scroll, BorderLayout.CENTER);
            container.add(filterPanel, BorderLayout.SOUTH);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
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
            try {
                logger.info("Downloading data from database");
                checkIdenticalData();
                int result = JOptionPane.showConfirmDialog(mainRacerGUI,
                        "Загрузить данные из базы данных?\nНесохраненные изменения будут утеряны!",
                        "Подтверждение действия",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    setIsOpenFile(false);
                    comboTeam.removeAllItems();
                    comboTeam.setSelectedItem(null);
                    searchTeam.removeAllItems();
                    searchTeam.setSelectedItem(null);
                    parentWindow.getMainTrackGUI().clearComboRacer();
                    addRacerWindow.clearComboTeam();

                    allTeams.clear();
                    allRacers.clear();
                    parentWindow.getMainTrackGUI().clearAllTracks();
                    parentWindow.getMainScoreGUI().clearAllScores();
                    parentWindow.getMainGraphicGUI().clearAllCompetitions();
                    parentWindow.getMainGraphicGUI().clearAllDates();

                    allTeams = getTeamData();
                    allRacers = getRacerData();
                    parentWindow.getMainScoreGUI().setAllScores(parentWindow.getMainScoreGUI().getScoreData());
                    parentWindow.getMainGraphicGUI()
                            .setAllCompetitions(parentWindow.getMainGraphicGUI().getCompetitionData());
                    parentWindow.getMainGraphicGUI().setAllDates(parentWindow.getMainGraphicGUI().getDateData());
                    parentWindow.getMainTrackGUI().setAllTracks(parentWindow.getMainTrackGUI().getTrackData());

                    racerDao.updateFreeID(allRacers);
                    teamDao.updateFreeID(allTeams);
                    trackDao.updateFreeID(parentWindow.getMainTrackGUI().getAllTracks());
                    parentWindow.getMainScoreGUI().getScoreDao()
                            .updateFreeID(parentWindow.getMainScoreGUI().getAllScores());
                    parentWindow.getMainGraphicGUI().getCompetitionDao()
                            .updateFreeID(parentWindow.getMainGraphicGUI().getAllCompetitions());
                    parentWindow.getMainGraphicGUI().getMyDateDao()
                            .updateFreeID(parentWindow.getMainGraphicGUI().getAllDates());

                    addRacerWindow.updateComboTeam();
                    updateComboTeam();
                    setRacerTable();
                    if (addRacerWindow.getComboTeam().getItemAt(0) == null) {
                        addRacerWindow.setComboTeamVisibility(false);
                        addRacerWindow.setTeamCheckBoxVisibility(false);
                        addRacerWindow.setInputTeamVisibility(true);
                    }

                    parentWindow.getMainScoreGUI().updateComboTrack();
                    parentWindow.getMainScoreGUI().updateComboRacer();
                    parentWindow.getMainScoreGUI().getAddScoreWindow().updateComboRacer();
                    parentWindow.getMainScoreGUI().getAddScoreWindow().updateComboTrack();
                    parentWindow.getMainScoreGUI().setScoreTable();

                    parentWindow.getMainGraphicGUI().getAddGraphicGUI().updateComboTrack();
                    parentWindow.getMainGraphicGUI().updateComboTrack();
                    parentWindow.getMainGraphicGUI().setCompetitionsTable();

                    parentWindow.getMainTrackGUI().getAddTrackWindow().updateComboRacer();
                    parentWindow.getMainTrackGUI().updateComboRacer();
                    parentWindow.getMainTrackGUI().setTrackTable();

                    parentWindow.getMainTeamGUI().setTeamTable();
                }
            } catch (IdenticalDataException exception) {
                logger.warn(exception.getMessage());
            } catch (InterruptedException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка чтения данных из базы!",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Сlass for implementing a save button listener
     */
    private class SaveEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                checkEmptyData("Данные для сохранения не найдены!", fullSearchTable);
                copyTable(fullSearchTable, racerTable);
                clearInputBtn.doClick();
                FileDialog save = new FileDialog(mainRacerGUI, "Сохранение данных", FileDialog.SAVE);
                save.setFile("data.xml");
                save.setVisible(true);
                if (save.getFile() != null) {
                    String filename = save.getDirectory() + save.getFile();
                    if (!filename.endsWith(".xml") && !filename.endsWith(".txt")) {
                        filename += ".xml";
                    }
                    if (filename.endsWith("txt")) {

                        FileManage.writeRacerToTextFile(fullSearchTable, filename);
                        logger.log(Level.DEBUG, "Data is saved successful");
                    }

                    else {
                        FileManage.writeRacerToXmlFile(fullSearchTable, filename);
                        logger.debug("Data is saved successful");
                    }

                }
            } catch (NothingDataException exception) {
                logger.info("NothingDataException exception");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (Exception exception) {
                logger.info("Saving exception", exception);
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка сохранения файла",
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
            try {
                checkIdenticalData();
                logger.info("Deploy data to database");
                int emptyResult = 1, result = 1;
                if (allRacers.size() == 0) {
                    logger.warn("Deploy empty table!");
                    emptyResult = JOptionPane.showConfirmDialog(mainRacerGUI,
                            "Таблица пуста! При выгрузке в базу все данные в ней удалятся!\nПродолжить?",
                            "Подтверждение действия",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                } else {
                    result = JOptionPane.showConfirmDialog(mainRacerGUI,
                            "Выгрузить данные в базу?",
                            "Подтверждение действия",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                }
                if (result == JOptionPane.YES_OPTION || emptyResult == JOptionPane.YES_OPTION) {
                    syncronizeData();
                }
            } catch (IdenticalDataException exception) {
                logger.warn("Full Identical data");
            }
        }
    }

    /**
     * Сlass for implementing a add button listener
     */
    private class AddEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            logger.info("Opening window AddRacerGUI");
            setMainRacerEnable(false);
            addRacerWindow.setAddRacerVisibility(true);
            copyTable(fullSearchTable, racerTable);
        }
    }

    /**
     * Сlass for implementing a delete button listener
     */
    private class DeleteEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                checkEmptyData("Данные для удаления не найдены!", fullSearchTable);
                checkDeleteSelect(racers);

                String message = racers.getSelectedRows().length == 1
                        ? "Вы действительно хотите удалить выбранную запись?\nВсе рекорды,связанные с этим гонщиком будут удалены!\nОтменить действие будет невозможно!"
                        : "Вы действительно хотите удалить выбранные записи?\nВсе рекорды,связанные с этими гонщиками будут удалены!\nОтменить действие будет невозможно!";
                int result = JOptionPane.showConfirmDialog(mainRacerGUI,
                        message,
                        "Подтверждение действия",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {

                    int i = racers.getSelectedRows().length - 1;
                    while (racers.getSelectedRows().length > 0) {
                        int j = racers.getRowCount() - 1;
                        while (j > -1) {
                            if (j == racers.getSelectedRows()[i]) {
                                String removingId = racers.getValueAt(racers.getSelectedRows()[i], 0).toString();
                                String removingName = racers.getValueAt(racers.getSelectedRows()[i], 1).toString();
                                String removingAge = racers.getValueAt(racers.getSelectedRows()[i], 2).toString();
                                String removingTeamName = racers.getValueAt(racers.getSelectedRows()[i], 3).toString();
                                String removingPoints = racers.getValueAt(racers.getSelectedRows()[i], 4).toString();
                                additionalSearchDelete(fullSearchTable, removingId);
                                Team removingTeam = isAtTeamList(allTeams, removingTeamName);
                                racerTable.removeRow(racers.getSelectedRows()[i]);
                                Racer removingRacer = isAtRacerList(allRacers, removingName,
                                        removingAge, removingTeamName, removingPoints);
                                if (removingRacer != null) {
                                    racerDao.addFreeID(removingRacer.getRacerID());
                                    updateTrackWinner(removingRacer);
                                    updateScores(removingRacer);
                                    allRacers.remove(allRacers.indexOf(removingRacer));
                                    if (!isTeamAtRacerList(allRacers, removingTeam.getTeamID())) {
                                        MainRacerGUI.deleteItemComboBox(comboTeam, allTeams.indexOf(removingTeam));
                                        MainRacerGUI.deleteItemComboBox(searchTeam, allTeams.indexOf(removingTeam) + 1);
                                        MainRacerGUI.deleteItemComboBox(addRacerWindow.getComboTeam(),
                                                allTeams.indexOf(removingTeam));
                                        if (addRacerWindow.getComboTeam().getItemAt(0) == null) {
                                            addRacerWindow.setComboTeamVisibility(false);
                                            addRacerWindow.setTeamCheckBoxVisibility(false);
                                            addRacerWindow.setInputTeamVisibility(true);
                                        }
                                        teamDao.addFreeID(removingTeam.getTeamID());
                                        allTeams.remove(allTeams.indexOf(removingTeam));
                                    } else {
                                        if (removingTeam.getRacerNumber() > 1) {
                                            removingTeam.reduceRacerNumber();
                                            removingTeam.deductPoints(Integer.parseInt(removingPoints));
                                        }
                                    }

                                    parentWindow.getMainTeamGUI().setTeamTable();

                                }
                                break;
                            }
                            j--;
                        }
                        i--;
                    }
                    parentWindow.getMainScoreGUI().updateComboRacer();
                    parentWindow.getMainScoreGUI().getAddScoreWindow().updateComboRacer();
                    parentWindow.getMainScoreGUI().setScoreTable();
                    parentWindow.getMainTrackGUI().setTrackTable();
                    parentWindow.getMainTrackGUI().updateComboRacer();
                    parentWindow.getMainTrackGUI().getAddTrackWindow().updateComboRacer();
                }
            } catch (UnselectedDeleteException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка удаления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Сlass for implementing a open button listener
     */
    private class FileEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            DefaultTableModel exceptionRacerTable = new DefaultTableModel(data, columns);
            copyTable(racerTable, exceptionRacerTable);
            try {
                int result = saveBeforeClose(
                        "Сохранить изменения в списке гонщиков локально?\nПосле открытия нового файла\nнесохраненные данные будут утеряны!");
                if (result != -1) {
                    FileDialog load = new FileDialog(mainRacerGUI, "Загрузка данных",
                            FileDialog.LOAD);
                    load.setFile("data.xml");
                    load.setVisible(true);
                    if (load.getFile() != null) {
                        checkFileFormat(load);
                        setIsOpenFile(true);
                        String filename = load.getDirectory() + load.getFile();
                        if (load.getFile().endsWith("txt"))
                            FileManage.readRacerFromTextFile(racerTable, filename);
                        else
                            FileManage.readRacerFromXmlFile(racerTable, filename);
                        addRacerWindow.clearComboTeam();
                        allRacers.clear();
                        allTeams.clear();
                        setTeamsAndRacers();
                        updateComboTeam();
                        parentWindow.getMainTrackGUI().getAddTrackWindow().updateComboRacer();
                        parentWindow.getMainTrackGUI().updateComboRacer();
                        parentWindow.getMainScoreGUI().updateComboRacer();
                        parentWindow.getMainScoreGUI().getAddScoreWindow().updateComboRacer();
                        if (comboTeam.getComponentCount() == 0) {
                            addRacerWindow.setComboTeamVisibility(false);
                            addRacerWindow.setTeamCheckBoxVisibility(false);

                        } else if (!comboTeam.isVisible()) {
                            addRacerWindow.setComboTeamVisibility(true);
                            addRacerWindow.setTeamCheckBoxVisibility(true);
                        }
                        parentWindow.getMainTeamGUI().setTeamTable();
                        copyTable(racerTable, fullSearchTable);
                        logger.debug("Data is opened successful");
                    }
                }

            } catch (FileNotFoundException exception) {
                logger.info("FileNotFound exception");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Файл не найден!",
                        JOptionPane.PLAIN_MESSAGE);

            } catch (Exception exception) {
                copyTable(exceptionRacerTable, racerTable);
                logger.error("Reading file error!");
                JOptionPane.showMessageDialog(mainRacerGUI, "Файл поврежден!\nПроверьте корректность данных!",
                        "Ошибка чтения файла",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Сlass for implementing a open button listener
     */
    private class ConfirmEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                if (racers.getSelectedRow() != -1)
                    racers.getCellEditor(racers.getSelectedRow(), racers.getSelectedColumn()).stopCellEditing();
                if (!isEqualTable(racerTable, previousRacerTable)) {
                    checkEditedData();
                    int result = JOptionPane.showConfirmDialog(mainRacerGUI, "Сохранить изменения?",
                            "Подтверждение действия",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        additionalSearchEdit();
                        compareEditedData();
                        deleteEmptyTeams();
                        parentWindow.getMainTeamGUI().setTeamTable();
                        updateComboTeam();
                        updateAllScores();
                        parentWindow.getMainScoreGUI().updateComboRacer();
                        parentWindow.getMainScoreGUI().getAddScoreWindow().updateComboRacer();
                        parentWindow.getMainScoreGUI().setScoreTable();
                        parentWindow.getMainTrackGUI().setTrackTable();
                        parentWindow.getMainTrackGUI().updateComboRacer();
                        parentWindow.getMainTrackGUI().getAddTrackWindow().updateComboRacer();
                        addRacerWindow.updateComboTeam();
                        parentWindow.getMainTrackGUI().updateComboRacer();
                        parentWindow.getMainTrackGUI().getAddTrackWindow().updateComboRacer();
                        disruptInputBtn.doClick();
                        setConfirmbarUnvisible();
                        changeEditingPermit();
                    }
                } else {
                    setConfirmbarUnvisible();
                    changeEditingPermit();
                }
            } catch (InvalidNameInputException exception) {
                logger.warn("Entered invalid name while editing");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);

            } catch (InvalidTeamInputException exception) {
                logger.warn("Entered invalid team while editing");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidPointInputException exception) {
                logger.warn("Entered invalid point while editing");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Сlass for implementing a open button listener
     */
    private class CancelEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */

        public void actionPerformed(ActionEvent e) {
            if (racers.getSelectedRow() != -1)
                racers.getCellEditor(racers.getSelectedRow(), racers.getSelectedColumn()).stopCellEditing();
            copyTable(previousRacerTable, racerTable);
            changeEditingPermit();
            setConfirmbarUnvisible();
        }
    }

    /**
     * Сlass for implementing a edit button listener
     */
    private class EditEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                checkEmptyData("Данные для редактирования не найдены!", fullSearchTable);
                copyTable(racerTable, previousRacerTable);
                changeEditingPermit();
                setConfirmbarVisible();

            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
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
                CreateReport.printReport(fullSearchTable, mainRacerGUI, "Отчет по списку гонщиков\n\n\n\n\n",
                        new float[] { 0.4f, 1f, 0.6f, 1f, 0.4f },
                        new String[] { "\nID\n", "\nИмя гонщика\n\n", "\nВозраст", "\nКоманда", "\nОчки" },
                        boldFontPath);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка формирования отчета",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Сlass for implementing a search button listener
     */
    private class SearchEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                checkEmptyData("Данные для поиска не найдены!", fullSearchTable);
                if (getEditingPermit())
                    confirmBtn.doClick();
                copyTable(fullSearchTable, racerTable);
                copyTable(racerTable, fullSearchTable);
                for (int i = racerTable.getRowCount() - 1; i > -1; i--) {
                    if (!searchNameField.getText().equals("Имя гонщика") & !racerTable.getValueAt(i, 1).toString()
                            .toLowerCase().contains(searchNameField.getText().toLowerCase())) {
                        racerTable.removeRow(i);
                        continue;
                    }

                    if (!searchTeam.getSelectedItem().toString().equals("Не выбрано")
                            && !racerTable.getValueAt(i, 3).toString()
                                    .toLowerCase().contains(searchTeam.getSelectedItem().toString().toLowerCase()))
                        racerTable.removeRow(i);
                }
            } catch (NothingDataException exception) {
                logger.info("NothingDataException exception");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка поиска",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Сlass for implementing a disrupt button listener
     */
    private class DisruptEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            copyTable(fullSearchTable, racerTable);
        }
    }

    /**
     * Сlass for implementing a clear input button listener
     */
    private class ClearInputEventListener implements ActionListener {
        /***
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            setInput(searchNameField, "Имя гонщика");
            searchTeam.setSelectedIndex(0);
        }
    }

    /**
     * Сlass for implementing a racer input focus listener
     */
    public static class RacerInputFocusListener implements FocusListener {
        /***
         * @param e the event to be processed
         */
        public void focusGained(FocusEvent e) {
            if (searchNameField.getText().equals("Имя гонщика"))
                setInput(searchNameField, "");
        }

        /***
         * @param e the event to be processed
         */
        public void focusLost(FocusEvent e) {
            if (searchNameField.getText().equals(""))
                setInput(searchNameField, "Имя гонщика");
        }

    }

    public void setIsOpenFile(boolean value) {
        isOpenFile = value;
    }

    /***
     * The function stops cell editing
     */
    public static void stopEditCell(JTable table) {
        if (table.getSelectedRow() != -1)
            table.getCellEditor(table.getSelectedRow(), table.getSelectedColumn()).stopCellEditing();
    }

    /***
     * The function offers to save racer data before closing window
     * 
     * @param message the text to be shown while the window is closing
     */
    public int saveBeforeClose(String message) {
        int result = 1;
        if (racerTable.getRowCount() > 0) {
            result = JOptionPane.showConfirmDialog(mainRacerGUI,
                    message,
                    "Подтверждение действия",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                // saveBtn.doClick();
                toDataBaseBtn.doClick();
            }
        }
        return result;
    }

    /***
     * The function make visible confirm bar while editing
     */
    private static void setConfirmbarVisible() {
        fileBtn.setVisible(false);
        saveBtn.setVisible(false);
        toDataBaseBtn.setVisible(false);
        fromDataBaseBtn.setVisible(false);
        addBtn.setVisible(false);
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
        fileBtn.setVisible(true);
        saveBtn.setVisible(true);
        toDataBaseBtn.setVisible(true);
        fromDataBaseBtn.setVisible(true);
        addBtn.setVisible(true);
        deleteBtn.setVisible(true);
        editBtn.setVisible(true);
        reportBtn.setVisible(true);
        confirmBtn.setVisible(false);
        cancelBtn.setVisible(false);
    }

    /***
     * The function copies data from one table model to another
     * 
     * @param table    the table containing data to be copied
     * @param newTable the table to which data to be copied
     */
    public static void copyTable(DefaultTableModel table, DefaultTableModel newTable) {
        clearTable(newTable);
        for (int i = 0; i < table.getRowCount(); i++) {
            String[] row = new String[table.getColumnCount()];
            for (int j = 0; j < table.getColumnCount(); j++) {
                row[j] = table.getValueAt(i, j).toString();
            }
            newTable.addRow(row); // Запись строки в таблицу
        }
    }

    /***
     * The function clears table
     * 
     * @param table the table to be cleared
     */
    public static void clearTable(DefaultTableModel table) {
        int n = table.getRowCount();
        for (int i = 0; i < n; i++) {
            table.removeRow(n - i - 1);
        }
    }

    /***
     * The function checks whether any row has selected to delete
     * 
     * @throws UnselectedDeleteException the exception throws if there aren't
     *                                   selected rows
     */
    public static void checkDeleteSelect(JTable table) throws UnselectedDeleteException {
        if (table.getSelectedRow() == -1)
            throw new UnselectedDeleteException();
    }

    /***
     * The function checks whether table data is valid
     * 
     * @throws InvalidNameInputException  the exception throws if any edited name
     *                                    isn't valid
     * @throws InvalidTeamInputException  the exception throws if any edited team
     *                                    isn't valid
     * @throws InvalidPointInputException the exception throws if any edited point
     *                                    isn't valid
     */
    public void checkEditedData() throws InvalidNameInputException,
            InvalidTeamInputException, InvalidPointInputException {
        for (int i = 0; i < racerTable.getRowCount(); i++) {
            if (!Validation.isValidName(racerTable.getValueAt(i, 1).toString()))
                throw new InvalidNameInputException(i + 1);
            if (!Validation.isValidTeam(racerTable.getValueAt(i, 3).toString()))
                throw new InvalidTeamInputException(i + 1);
            if (!Validation.isValidPoint(racerTable.getValueAt(i, 4).toString()))
                throw new InvalidPointInputException(i + 1);
        }
    }

    /***
     * The function checks whether input file format matchs the requested required
     * 
     * @param file the path to file to be opened
     * @throws FileFormatException the exception throws if selected file format
     *                             isn't valid
     */
    private void checkFileFormat(FileDialog file) throws FileFormatException {
        if (!file.getFile().endsWith(".txt") && !file.getFile().endsWith(".xml"))
            throw new FileFormatException("Некорректный формат файла!\nВыберите файл формата .txt или .xml!");
    }

    /***
     * The function edits real table, so that all changes made in edition mode are
     * saved. It's necessary if while editing search
     * filter is applied and displaying table doesn't equal of real one
     */
    private void additionalSearchEdit() {
        for (int i = 0; i < fullSearchTable.getRowCount(); i++) {
            for (int j = 0; j < previousRacerTable.getRowCount(); j++) {
                if (fullSearchTable.getValueAt(i, 0).equals(previousRacerTable.getValueAt(j, 0))
                        && fullSearchTable.getValueAt(i, 0).equals(previousRacerTable.getValueAt(j, 0))
                        && fullSearchTable.getValueAt(i, 0).equals(previousRacerTable.getValueAt(j, 0))
                        && fullSearchTable.getValueAt(i, 0).equals(previousRacerTable.getValueAt(j, 0))) {
                    for (int k = 0; k < fullSearchTable.getColumnCount(); k++) {
                        fullSearchTable.setValueAt(racerTable.getValueAt(j, k), i, k);
                    }
                }
            }
        }
    }

    /***
     * The function checks whether transfered tables equaled
     * 
     * @param table     the first table to be compared
     * @param prevTable the second table to be compared
     * @return result of table comparing
     */
    public static boolean isEqualTable(DefaultTableModel table, DefaultTableModel prevTable) {
        boolean isEqual = true;
        if (table.getRowCount() != prevTable.getRowCount() || table.getColumnCount() != prevTable.getColumnCount())
            isEqual = false;
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    if (!table.getValueAt(i, j).equals(prevTable.getValueAt(i, j))) {
                        isEqual = false;
                        break;
                    }
                }
                if (!isEqual)
                    break;
            }
        }
        return isEqual;
    }

    /***
     * The function checks whether table isn't empty
     * 
     * @param msg   the message to be shown if the table is empty
     * @param table the table to be checked
     * @throws NothingDataException the exception to be thrown if the table is empty
     */
    public static void checkEmptyData(String msg, DefaultTableModel table) throws NothingDataException {
        if (table.getRowCount() == 0) {
            throw new NothingDataException(msg);
        }
    }

    /***
     * The function adds new racer to the table
     * 
     * @param racer the racer to be added
     */
    public void addRacer(Racer racer) {
        racerTable.addRow(
                new String[] { racer.getRacerID().toString(), racer.getRacerName(), racer.getRacerAge().toString(),
                        racer.getTeam().getTeamName(),
                        racer.getRacerPoints().toString() });
        fullSearchTable.addRow(
                new String[] { racer.getRacerID().toString(), racer.getRacerName(), racer.getRacerAge().toString(),
                        racer.getTeam().getTeamName(),
                        racer.getRacerPoints().toString() });
    }

    /***
     * The function returns editingPermit variable
     * 
     * @return editingPermit variable
     */
    private boolean getEditingPermit() {
        return editingPermit;
    }

    /***
     * The function changes editingPermit variable to the opposite
     */
    private void changeEditingPermit() {

        editingPermit = !editingPermit;
    }

    /***
     * The function sets the value of the input
     * 
     * @param input the input that value to be changed
     * @param text  the value to be setted
     */
    public static void setInput(JTextField input, String text) {
        input.setText(text);
    }

    /***
     * The function sets enability options of MainRacerGUI window
     * 
     * @param value the value to be setted
     */
    public void setMainRacerEnable(boolean value) {
        mainRacerGUI.setEnabled(value);
    }

    /***
     * The function removes real table rows, so that all changes made in deleting
     * mode are saved. It's necessary if while deleting search
     * filter is applied and displaying table doesn't equal of real one
     * 
     * @param table  the table to be changed
     * @param name   the value of column name of transfered row
     * @param age    the value of column age of transfered row
     * @param team   the value of column team of transfered row
     * @param points the value of column points of transfered row
     */
    private void additionalSearchDelete(DefaultTableModel table, String id) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!id.isEmpty()) {
                if (table.getValueAt(i, 0).equals(id)) {
                    table.removeRow(i);
                }
            }
        }
    }

    /***
     * The function sets visibility options of mainRacerGUI
     */
    public void setVisible(boolean value) {
        mainRacerGUI.setVisible(value);
    }

    /***
     * The function starts logging
     * 
     * @param context       the logger variable
     * @param configuration params of logging
     * @throws IOException checks whether there are input/output errors
     */

    private void startLogging(LoggerContext context, Configuration configuration) throws IOException {
        context.start(configuration);
        logger = context.getLogger("com");
        logger.log(Level.INFO, "Start logging MainRacerGUI");
    }

    /***
     * The function stops logging
     * 
     * @param context the logger variable
     */
    public void stopLogging(LoggerContext context) {
        logger.log(Level.INFO, "Stop logging MainRacerGUI");
        context.close();
    }

    public List<Team> getTeamData() throws InterruptedException {
        return teamDao.getAllTeams();
    }

    public List<Racer> getRacerData() throws InterruptedException {
        return racerDao.getAllRacers();
    }

    public static Racer isAtRacerList(List<Racer> racers, Racer racer) {
        Racer answer = null;
        for (int i = 0; i < racers.size(); i++) {
            if (racers.get(i).getRacerID() == racer.getRacerID()) {
                answer = racer;
                break;
            }
        }
        return answer;
    }

    public static Racer isAtRacerList(List<Racer> racers, Integer id) {
        Racer answer = null;
        for (int i = 0; i < racers.size(); i++) {
            if (racers.get(i).getRacerID().equals(id)) {
                answer = racers.get(i);
                break;
            }
        }
        return answer;
    }

    public static Racer isAtRacerList(List<Racer> racers, String name, String age, String team, String points) {
        Racer answer = null;
        for (int i = 0; i < racers.size(); i++) {
            if (racers.get(i).getRacerName().equals(name)
                    && racers.get(i).getRacerAge().toString().equals(age)
                    && racers.get(i).getTeam().getTeamName().equals(team)
                    && racers.get(i).getRacerPoints().toString().equals(points)) {
                answer = racers.get(i);
                break;
            }
        }
        return answer;
    }

    public static Racer isAtRacerList(List<Racer> racers, String name, String team) {
        Racer answer = null;
        for (int i = 0; i < racers.size(); i++) {
            if (racers.get(i).getRacerName().toLowerCase().equals(name.toLowerCase())
                    && racers.get(i).getTeam().getTeamName().toLowerCase().equals(team.toLowerCase())) {
                answer = racers.get(i);
                break;
            }
        }
        return answer;
    }

    public static boolean isTeamAtRacerList(List<Racer> racers, Integer id) {
        boolean answer = false;
        for (int i = 0; i < racers.size(); i++) {
            if (racers.get(i).getTeam().getTeamID().equals(id)) {
                answer = true;
                break;
            }
        }
        return answer;
    }

    public static Team isAtTeamList(List<Team> teams, Team team) {
        Team answer = null;
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getTeamName().equals(team.getTeamName())) {
                answer = teams.get(i);
                break;
            }
        }
        return answer;
    }

    public static Team isAtTeamList(List<Team> teams, String team) {
        Team answer = null;
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getTeamName().toLowerCase().equals(team.toLowerCase())) {
                answer = teams.get(i);
                break;
            }
        }
        return answer;
    }

    public static Team isAtTeamList(List<Team> teams, Integer id) {
        Team answer = null;
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getTeamID().equals(id)) {
                answer = teams.get(i);
                break;
            }
        }
        return answer;
    }

    public void updateComboTeam() {
        comboTeam.removeAllItems();
        comboTeam.setSelectedItem(null);
        searchTeam.removeAllItems();
        searchTeam.setSelectedItem(null);
        addItemSearchTeam("Не выбрано");
        searchTeam.setSelectedIndex(0);
        for (Team team : allTeams) {
            addItemComboTeam(team.getTeamName());
            addItemSearchTeam(team.getTeamName());
        }
    }

    private void addItemSearchTeam(String item) {
        searchTeam.addItem(item);
    }

    public void addItemComboTeam(String item) {
        comboTeam.addItem(item);
    }

    public void syncronizeData() {
        if (isOpenFile) {
            racerDao.clearRacer();
            teamDao.clearTeam();
            trackDao.clearTrack();
            parentWindow.getMainGraphicGUI().getCompetitionDao().clearCompetition();
            parentWindow.getMainGraphicGUI().getMyDateDao().clearDate();
            setIsOpenFile(false);
        }

        List<MyDate> allDates = parentWindow.getMainGraphicGUI().getAllDates();
        for (MyDate date : allDates) {
            if (parentWindow.getMainGraphicGUI().getMyDateDao().findDate(date.getDateID()) == null) {
                parentWindow.getMainGraphicGUI().getMyDateDao().saveDate(date);
            } else
                parentWindow.getMainGraphicGUI().getMyDateDao().updateDate(date);
        }

        for (Team team : allTeams) {
            if (teamDao.findTeam(team.getTeamID()) == null) {
                teamDao.saveTeam(team);
            } else
                teamDao.updateTeam(team);
        }

        for (Racer racer : allRacers) {
            if (racerDao.findRacer(racer.getRacerID()) == null) {
                racerDao.saveRacer(racer);
            } else
                racerDao.updateRacer(racer);
        }

        List<Track> allTracks = parentWindow.getMainTrackGUI().getAllTracks();
        for (Track track : allTracks) {
            if (trackDao.findTrack(track.getTrackID()) == null) {
                trackDao.saveTrack(track);
            } else
                trackDao.updateTrack(track);
        }

        List<Score> allScores = parentWindow.getMainScoreGUI().getAllScores();
        for (Score score : allScores) {
            if (parentWindow.getMainScoreGUI().getScoreDao().findScore(score.getScoreID()) == null) {
                parentWindow.getMainScoreGUI().getScoreDao().saveScore(score);
            } else
                parentWindow.getMainScoreGUI().getScoreDao().updateScore(score);
        }

        List<Competition> allCompetitions = parentWindow.getMainGraphicGUI().getAllCompetitions();
        for (Competition competition : allCompetitions) {
            if (parentWindow.getMainGraphicGUI().getCompetitionDao()
                    .findCompetition(competition.getCompetitionID()) == null) {
                parentWindow.getMainGraphicGUI().getCompetitionDao().saveCompetition(competition);
            } else
                parentWindow.getMainGraphicGUI().getCompetitionDao().updateCompetition(competition);
        }

        List<Racer> dbRacers = racerDao.getAllRacers();
        List<Team> dbTeams = teamDao.getAllTeams();
        List<Track> dbTracks = trackDao.getAllTracks();
        List<Competition> dbCompetitions = parentWindow.getMainGraphicGUI().getCompetitionDao().getAllCompetitions();
        List<MyDate> dbDates = parentWindow.getMainGraphicGUI().getMyDateDao().getAllDates();
        List<Score> dbScores = parentWindow.getMainScoreGUI().getScoreDao().getAllScores();

        for (Score score : dbScores) {
            if (MainScoreGUI.isAtScoreList(parentWindow.getMainScoreGUI().getAllScores(), score) == null)
                parentWindow.getMainScoreGUI().getScoreDao().deleteScore(score);
        }

        for (Competition competition : dbCompetitions) {
            if (MainGraphicGUI.isAtCompetitionList(parentWindow.getMainGraphicGUI().getAllCompetitions(),
                    competition) == null)
                parentWindow.getMainGraphicGUI().getCompetitionDao().deleteCompetition(competition);
        }

        for (Track track : dbTracks) {
            if (MainTrackGUI.isAtTrackList(allTracks, track.getTrackID()) == null)
                trackDao.deleteTrack(track);
        }

        for (Racer racer : dbRacers) {
            if (isAtRacerList(allRacers, racer.getRacerID()) == null)
                racerDao.deleteRacer(racer);
        }

        for (Team team : dbTeams) {
            if (isAtTeamList(allTeams, team.getTeamID()) == null)
                teamDao.deleteTeam(team);
        }

        for (MyDate date : dbDates) {
            if (MainGraphicGUI.isAtDateList(parentWindow.getMainGraphicGUI().getAllDates(), date.getDay(),
                    date.getMonth(), date.getYear()) == null)
                parentWindow.getMainGraphicGUI().getMyDateDao().deleteDate(date);
        }

    }

    public boolean getIsOpenFile() {
        return isOpenFile;
    }

    public List<Team> getAllTeams() {
        return allTeams;
    }

    public List<Racer> getAllRacers() {
        return allRacers;
    }

    public void setAllTeams(List<Team> list) {
        allTeams = list;
    }

    public void setAllRacers(List<Racer> list) {
        allRacers = list;
    }

    public void addtoAllTeam(Team team) {
        allTeams.add(team);
        teamDao.updateFreeID(allTeams);
    }

    public void addToAllRacer(Racer racer) {
        allRacers.add(racer);
        racerDao.updateFreeID(allRacers);
    }

    public void setRacerTable() {
        if (racerTable.getRowCount() != 0)
            clearTable(racerTable);

        for (Racer racer : allRacers) {
            racerTable.addRow(
                    new String[] { racer.getRacerID().toString(), racer.getRacerName(), racer.getRacerAge().toString(),
                            racer.getTeam().getTeamName(), racer.getRacerPoints().toString() });
        }
        copyTable(racerTable, fullSearchTable);
    }

    private void compareEditedData() {
        for (int i = 0; i < fullSearchTable.getRowCount(); i++) {
            String name = fullSearchTable.getValueAt(i, 1).toString();
            String age = fullSearchTable.getValueAt(i, 2).toString();
            String team = fullSearchTable.getValueAt(i, 3).toString();
            String point = fullSearchTable.getValueAt(i, 4).toString();
            if (!name.equals(allRacers.get(i).getRacerName()))
                allRacers.get(i).setRacerName(name);
            if (!age.equals(allRacers.get(i).getRacerAge().toString()))
                allRacers.get(i).setRacerAge(Integer.parseInt(age));
            if (!team.equals(allRacers.get(i).getTeam().getTeamName())) {
                Team newTeam = isAtTeamList(allTeams, team);
                if (allRacers.get(i).getTeam().getRacerNumber() > 1) {
                    allRacers.get(i).getTeam().reduceRacerNumber();
                    allRacers.get(i).getTeam().deductPoints(allRacers.get(i).getRacerPoints());
                }
                newTeam.expandRacerNumber();
                if (!point.equals(allRacers.get(i).getRacerPoints().toString()))
                    allRacers.get(i).setRacerPoints(Integer.parseInt(point));

                newTeam.addPoints(allRacers.get(i).getRacerPoints());

                allRacers.get(i).setTeam(newTeam);
            } else if (!point.equals(allRacers.get(i).getRacerPoints().toString())) {
                allRacers.get(i).getTeam().deductPoints(allRacers.get(i).getRacerPoints());
                allRacers.get(i).setRacerPoints(Integer.parseInt(point));
                allRacers.get(i).getTeam().addPoints(allRacers.get(i).getRacerPoints());
            }

        }
    }

    private boolean areEqualTeamLists(List<Team> teams1, List<Team> teams2) {
        boolean answer = false;
        if (teams1.size() == teams2.size() && teams1.size() != 0) {
            for (int i = 0; i < teams1.size(); i++) {
                answer = false;
                for (int j = 0; j < teams2.size(); j++) {
                    if (teams1.get(i).getTeamName().equals(teams2.get(j).getTeamName())) {
                        answer = true;
                    }
                }
                if (!answer)
                    break;
            }
        } else if (teams1.size() == teams2.size() && teams1.size() == 0)
            answer = true;
        return answer;
    }

    private boolean areEqualRacerLists(List<Racer> racers1, List<Racer> racers2) {
        boolean answer = false;
        if (racers1.size() == racers2.size() && racers1.size() != 0) {
            for (int i = 0; i < racers1.size(); i++) {
                answer = false;
                for (int j = 0; j < racers2.size(); j++) {
                    if (racers1.get(i).getRacerName().equals(racers2.get(j).getRacerName())
                            && racers1.get(i).getRacerAge().equals(racers2.get(j).getRacerAge())
                            && racers1.get(i).getTeam().equals(racers2.get(j).getTeam())
                            && racers1.get(i).getRacerPoints().equals(racers2.get(j).getRacerPoints())) {
                        answer = true;
                    }
                }
                if (!answer)
                    break;
            }
        } else if (racers1.size() == racers2.size() && racers1.size() == 0)
            answer = true;
        return answer;
    }

    public void checkIdenticalData() throws IdenticalDataException {
        List<Racer> dbRacers = racerDao.getAllRacers();
        List<Team> dbTeams = teamDao.getAllTeams();
        if (areEqualTeamLists(allTeams, dbTeams) && areEqualRacerLists(allRacers, dbRacers))
            throw new IdenticalDataException("Full identical data!");
    }

    public boolean getMainRacerVisibility() {
        return mainRacerGUI.isShowing();
    }

    private void setTeamsAndRacers() {
        boolean isTeam;
        for (int i = 0; i < racerTable.getRowCount(); i++) {
            isTeam = false;
            String id = racerTable.getValueAt(i, 0).toString();
            String name = racerTable.getValueAt(i, 1).toString();
            String age = racerTable.getValueAt(i, 2).toString();
            String teamName = racerTable.getValueAt(i, 3).toString();
            String points = racerTable.getValueAt(i, 4).toString();
            Team team = isAtTeamList(allTeams, teamName);
            if (team == null) {
                team = new Team(teamName);
                team.setTeamID(teamDao.getFreeID());
                allTeams.add(team);
                teamDao.updateFreeID(allTeams);
                addItemComboTeam(teamName);
                addRacerWindow.addItemComboTeam(teamName);
            } else
                isTeam = true;

            if (isAtRacerList(allRacers, Integer.parseInt(id)) == null) {
                Racer racer = new Racer(name, Integer.parseInt(age), team,
                        Integer.parseInt(points));
                racer.setRacerID(Integer.parseInt(id));
                team.addPoints(Integer.parseInt(points));
                if (isTeam)
                    team.expandRacerNumber();
                allRacers.add(racer);
                racerDao.updateFreeID(allRacers);
            }
        }
        teamDao.updateFreeID(allTeams);
        racerDao.updateFreeID(allRacers);
    }

    public void downloadFromDataBase() {
        fromDataBaseBtn.doClick();
    }

    public void deployToDataBase() {
        toDataBaseBtn.doClick();
    }

    public MainMenuGUI getParentWindow() {
        return parentWindow;
    }

    public static boolean isAtTable(DefaultTableModel table, String id, String name, String age, String team,
            String points) {
        boolean answer = false;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0).equals(id) && table.getValueAt(i, 1).equals(name)
                    && table.getValueAt(i, 2).equals(age)
                    && table.getValueAt(i, 3).equals(team) && table.getValueAt(i, 4).equals(points)) {
                answer = true;
                break;
            }
        }
        return answer;
    }

    public static void deleteItemComboBox(JComboBox<String> combo, int index) {
        int itemCount = combo.getItemCount();
        if (index >= 0 && index < itemCount) {
            combo.removeItemAt(index);
        }
    }

    public JComboBox<String> getComboTeam() {
        return comboTeam;
    }

    public JComboBox<String> getSearchTeam() {
        return searchTeam;
    }

    public AddRacerGUI getAddRacerWindow() {
        return addRacerWindow;
    }

    public void deleteFromAllTeams(int id) {
        teamDao.addFreeID(id);
        for (Team team : allTeams) {
            if (team.getTeamID() == id) {
                allTeams.remove(team);
                break;
            }
        }
    }

    public void deleteFromAllRacers(int id) {
        racerDao.addFreeID(id);
        for (Racer racer : allRacers) {
            if (racer.getRacerID() == id) {
                allRacers.remove(racer);
                break;
            }
        }
    }

    public RacerDao getRacerDao() {
        return racerDao;
    }

    public TeamDao getTeamDao() {
        return teamDao;
    }

    public TrackDao getTrackDao() {
        return trackDao;
    }

    private void deleteEmptyTeams() {
        for (int i = allTeams.size() - 1; i > -1; i--) {
            if (!isTeamAtRacerList(allRacers, allTeams.get(i).getTeamID()))
                allTeams.remove(i);
        }
    }

    public void updateTrackWinner(Racer racer) {
        for (Track track : parentWindow.getMainTrackGUI().getAllTracks()) {
            if (track.getWinner() != null && track.getWinner().getRacerID().equals(racer.getRacerID()))
                track.setWinner(null);
        }
    }

    public JTable getRacerTable() {
        return racers;
    }

    public void updateScores(Racer racer) {
        List<Score> scores = parentWindow.getMainScoreGUI().getAllScores();
        for (int i = scores.size() - 1; i > -1; i--) {
            if (scores.get(i).getRacerInfo().getRacerID().equals(racer.getRacerID()))
                parentWindow.getMainScoreGUI().deleteFromAllScores(scores.get(i).getScoreID());
        }
    }

    private void updateAllScores() {
        for (Racer racer : allRacers) {
            for (Score score : parentWindow.getMainScoreGUI().getAllScores()) {
                if (score.getRacerInfo().getRacerID().equals(racer.getRacerID()))
                    score.setRacerInfo(racer);
            }
        }
    }

    private void setComboAge() {
        for (int i = 18; i < 100; i++) {
            comboAge.addItem(Integer.valueOf(i).toString());
        }
    }

}
