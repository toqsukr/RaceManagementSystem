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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import database.RacerDao;
import database.TeamDao;

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

import exception.EmptySearchInputException;
import exception.FileFormatException;
import exception.InvalidAgeInputException;
import exception.InvalidDataException;
import exception.InvalidNameInputException;
import exception.InvalidPointInputException;
import exception.InvalidTeamInputException;
import exception.UnselectedDeleteException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import exception.NothingDataException;
import exception.ReadFileException;
import race.system.Racer;
import race.system.Team;
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

    /**
     * This input is used to search for an entry in the table by the name of the
     * racer
     */
    private static final JTextField searchNameField = new JTextField("Имя гонщика", 17);

    /**
     * This input is used to search for an entry in the table by the team of the
     * racer
     */
    private static final JTextField searchTeamField = new JTextField("Команда", 15);

    /**
     * This bar is used to store buttons
     */
    private static final JToolBar toolBar = new JToolBar();

    /**
     * Table column names
     */
    private static final String[] columns = { "Имя гонщика", "Возраст", "Команда", "Очки" };

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
            return getEditingPermit();
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
    private AddRacerGUI addRacerWindow;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("rms_persistence");
    private EntityManager em = emf.createEntityManager();

    private List<Team> allTeams;

    private List<Racer> allRacers;

    private JComboBox<String> comboTeam;

    /***
     * The logger variable
     */
    private static Logger logger;
    private MainMenuGUI parentWindow;

    /***
     * The function creating MainRacerGUI
     */
    public MainRacerGUI(MainMenuGUI parent) {
        parentWindow = parent;
        try {

            ConfigurationFactory factory = XmlConfigurationFactory.getInstance();
            ConfigurationSource configurationSource = new ConfigurationSource(
                    new FileInputStream(new File("etu/src/main/java/configuration.xml")));

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
                        stopEditCell();
                        checkEditedData();
                        saveBeforeClose(
                                "Сохранить изменения?\nПосле закрытия окна\nнесохраненные данные будут утеряны!");
                        setConfirmbarUnvisible();
                        if (editingPermit == true)
                            changeEditingPermit();
                        mainRacerGUI.setTitle("Список гонщиков");
                        stopLogging(context);
                        mainRacerGUI.dispose();
                    } catch (InvalidDataException exception) {
                        int confirm = JOptionPane.showConfirmDialog(mainRacerGUI,
                                "Данные содержат ошибку и не могут быть сохранены!\nЗакрыть окно?",
                                "Предупреждение",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (confirm == JOptionPane.OK_OPTION) {
                            setConfirmbarUnvisible();
                            if (editingPermit == true)
                                changeEditingPermit();
                            mainRacerGUI.setTitle("Список гонщиков");
                            stopLogging(context);
                            mainRacerGUI.dispose();
                        }
                    } catch (InterruptedException exception) {
                        JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(),
                                "Ошибка синхронизации с базой данных!",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });

            mainRacerGUI.setBounds(200, 150, 800, 600);
            mainRacerGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            mainRacerGUI.setResizable(false);
            URL mainRacerIcon = this.getClass().getClassLoader().getResource("img/racer.png");
            mainRacerGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(mainRacerIcon));
            addRacerWindow = new AddRacerGUI(this);
            toolBar.setFloatable(false);
            racers.getTableHeader().setReorderingAllowed(false);

            try {
                allTeams = getTeamData();
                allRacers = getRacerData();
                initialSetRacerTable();
            } catch (InterruptedException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка чтения данных из базы!",
                        JOptionPane.PLAIN_MESSAGE);
            }

            updateComboBox();

            DefaultCellEditor editor = new DefaultCellEditor(comboTeam);
            racers.getColumnModel().getColumn(2).setCellEditor(editor);

            Container container = mainRacerGUI.getContentPane();
            container.setLayout(new BorderLayout());

            searchBtn.setBackground(new Color(0xDFD9D9D9, false));
            clearInputBtn.setBackground(new Color(0xDFD9D9D9, false));
            disruptInputBtn.setBackground(new Color(0xDFD9D9D9, false));

            searchTeamField.addFocusListener(new TeamInputFocusListener());

            searchNameField.addFocusListener(new RacerInputFocusListener());

            searchNameField.setMargin(new Insets(2, 2, 3, 0));
            searchTeamField.setMargin(new Insets(2, 2, 3, 0));

            searchBtn.addActionListener(new SearchEventListener());
            searchBtn.setMargin(new Insets(1, 6, 1, 6));

            disruptInputBtn.addActionListener(new DisruptEventListener());
            disruptInputBtn.setMargin(new Insets(1, 6, 1, 6));

            clearInputBtn.addActionListener(new ClearInputEventListener());
            clearInputBtn.setMargin(new Insets(1, 6, 1, 6));

            filterPanel.add(searchNameField);
            filterPanel.add(searchTeamField);
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

            URL toDataBaseUrl = this.getClass().getClassLoader().getResource("img/database.png");
            toDataBaseBtn.setIcon(new ImageIcon(new ImageIcon(toDataBaseUrl).getImage().getScaledInstance(50, 50, 4)));
            toDataBaseBtn.setToolTipText("Выгрузить в базу данных");
            toDataBaseBtn.setBackground(new Color(0xDFD9D9D9, false));
            toDataBaseBtn.setFocusable(false);

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

            toolBar.add(fileBtn);
            toolBar.add(saveBtn);
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
            setAddRacerVisible(true);
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
                checkDeleteSelect();

                String message = racers.getSelectedRows().length == 1
                        ? "Вы действительно хотите удалить выбранную запись?\nОтменить действие будет невозможно!"
                        : "Вы действительно хотите удалить выбранные записи?\nОтменить действие будет невозможно!";
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
                                String removingName = racers.getValueAt(racers.getSelectedRows()[i], 0).toString();
                                String removingAge = racers.getValueAt(racers.getSelectedRows()[i], 1).toString();
                                String removingTeamName = racers.getValueAt(racers.getSelectedRows()[i], 2).toString();
                                String removingPoints = racers.getValueAt(racers.getSelectedRows()[i], 3).toString();
                                additionalSearchDelete(fullSearchTable, removingName, removingAge, removingTeamName,
                                        removingPoints);
                                Team removingTeam = isAtTeamList(allTeams, new Team(removingTeamName));
                                racerTable.removeRow(racers.getSelectedRows()[i]);
                                Racer removingRacer = isAtRacerList(allRacers, new Racer(removingName,
                                        Integer.parseInt(removingAge), removingTeam, Integer.parseInt(removingPoints)));
                                if (removingRacer != null) {
                                    allRacers.remove(allRacers.indexOf(removingRacer));
                                    if (!isTeamAtRacerList(allRacers, removingTeam.getTeamID()))
                                        allTeams.remove(allTeams.indexOf(removingTeam));
                                }
                                break;
                            }
                            j--;
                        }
                        i--;
                    }
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
            try {
                saveBeforeClose(
                        "Сохранить изменения?\nПосле открытия нового файла\nнесохраненные данные будут утеряны!");
                FileDialog load = new FileDialog(mainRacerGUI, "Загрузка данных",
                        FileDialog.LOAD);
                load.setFile("data.xml");
                load.setVisible(true);
                if (load.getFile() != null) {
                    checkFileFormat(load);
                    String filename = load.getDirectory() + load.getFile();
                    mainRacerGUI.setTitle("Список гонщиков");
                    if (load.getFile().endsWith("txt"))
                        FileManage.readRacerFromTextFile(racerTable, filename);
                    else
                        FileManage.readRacerFromXmlFile(racerTable, filename);
                    for (int i = 0; i < racerTable.getRowCount(); i++) {
                        String name = racerTable.getValueAt(i, 0).toString();
                        String age = racerTable.getValueAt(i, 1).toString();
                        String teamName = racerTable.getValueAt(i, 2).toString();
                        String points = racerTable.getValueAt(i, 3).toString();
                        Team team = isAtTeamList(allTeams, teamName);
                        if (team == null) {
                            team = new Team(teamName);
                            allTeams.add(team);
                        }
                        Racer racer = new Racer(name, Integer.parseInt(age), team,
                                Integer.parseInt(points));
                        if (isAtRacerList(allRacers, racer) == null)
                            allRacers.add(racer);
                    }
                    copyTable(racerTable, fullSearchTable);
                    logger.debug("Data is opened successful");
                    mainRacerGUI.setTitle("Список гонщиков (файл " + load.getFile() + ")");
                }

            } catch (FileNotFoundException exception) {
                logger.info("FileNotFound exception");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Файл не найден!",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (ReadFileException exception) {
                clearTable(racerTable);
                logger.info("ReadFileException exception");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка чтения файла",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка открытия файла",
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
                        setConfirmbarUnvisible();
                        changeEditingPermit();
                        mainRacerGUI
                                .setTitle(mainRacerGUI.getTitle().substring(0, mainRacerGUI.getTitle().length() - 23));
                    }
                } else {
                    mainRacerGUI
                            .setTitle(mainRacerGUI.getTitle().substring(0, mainRacerGUI.getTitle().length() - 23));
                    setConfirmbarUnvisible();
                    changeEditingPermit();
                }
            } catch (InvalidNameInputException exception) {
                logger.warn("Enterd invalid name while editing");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidAgeInputException exception) {
                logger.warn("Enterd invalid age while editing");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTeamInputException exception) {
                logger.warn("Enterd invalid team while editing");
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidPointInputException exception) {
                logger.warn("Enterd invalid point while editing");
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
            mainRacerGUI.setTitle(mainRacerGUI.getTitle().substring(0, mainRacerGUI.getTitle().length() - 23));
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
                mainRacerGUI.setTitle(mainRacerGUI.getTitle() + " - Режим редактирования");
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
                        new float[] { 1f, 0.6f, 1f, 0.4f },
                        new String[] { "\nИмя гонщика\n\n", "\nВозраст", "\nКоманда", "\nОчки" }, boldFontPath);
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
                checkEmptyInput();
                copyTable(racerTable, fullSearchTable);
                for (int i = racerTable.getRowCount() - 1; i > -1; i--) {
                    if (!searchNameField.getText().equals("Имя гонщика") & !racerTable.getValueAt(i, 0).toString()
                            .toLowerCase().contains(searchNameField.getText().toLowerCase())) {
                        racerTable.removeRow(i);
                        continue;
                    }

                    if (!searchTeamField.getText().equals("Команда") & !racerTable.getValueAt(i, 2).toString()
                            .toLowerCase().contains(searchTeamField.getText().toLowerCase()))
                        racerTable.removeRow(i);
                }

            } catch (EmptySearchInputException exception) {

                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка поиска",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (NothingDataException exception) {
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
            setInput(searchTeamField, "Команда");
            setInput(searchNameField, "Имя гонщика");
        }
    }

    /**
     * Сlass for implementing a team input focus listener
     */
    public static class TeamInputFocusListener implements FocusListener {
        /***
         * @param e the event to be processed
         */
        public void focusGained(FocusEvent e) {
            if (searchTeamField.getText().equals("Команда"))
                setInput(searchTeamField, "");
        }

        /***
         * @param e the event to be processed
         */
        public void focusLost(FocusEvent e) {
            if (searchTeamField.getText().equals(""))
                setInput(searchTeamField, "Команда");
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

    /***
     * The function stops cell editing
     */
    public void stopEditCell() {
        if (racers.getSelectedRow() != -1)
            racers.getCellEditor(racers.getSelectedRow(), racers.getSelectedColumn()).stopCellEditing();
    }

    /***
     * The function offers to save racer data before closing window
     * 
     * @param message the text to be shown while the window is closing
     */
    public void saveBeforeClose(String message) throws InterruptedException {
        if (racerTable.getRowCount() > 0) {
            int result = JOptionPane.showConfirmDialog(mainRacerGUI,
                    message,
                    "Подтверждение действия",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                em.getTransaction().begin();
                Thread threadData = new Thread(() -> {
                    syncronizeData();
                    em.getTransaction().commit();
                });
                threadData.start();
                saveBtn.doClick();
                threadData.join();
            }
        }
    }

    /***
     * The function make visible confirm bar while editing
     */
    private static void setConfirmbarVisible() {
        fileBtn.setVisible(false);
        saveBtn.setVisible(false);
        toDataBaseBtn.setVisible(false);
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
    private void copyTable(DefaultTableModel table, DefaultTableModel newTable) {
        clearTable(newTable);
        for (int i = 0; i < table.getRowCount(); i++) {
            String name = table.getValueAt(i, 0).toString();
            String age = table.getValueAt(i, 1).toString();
            String team = table.getValueAt(i, 2).toString();
            String points = table.getValueAt(i, 3).toString();
            newTable.addRow(new String[] { name, age, team, points }); // Запись строки в таблицу
        }
    }

    /***
     * The function clears table
     * 
     * @param table the table to be cleared
     */
    private void clearTable(DefaultTableModel table) {
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
    private void checkDeleteSelect() throws UnselectedDeleteException {
        if (racers.getSelectedRow() == -1)
            throw new UnselectedDeleteException();
    }

    /***
     * The function checks whether table data is valid
     * 
     * @throws InvalidNameInputException  the exception throws if any edited name
     *                                    isn't valid
     * @throws InvalidAgeInputException   the exception throws if any edited age
     *                                    isn't valid
     * @throws InvalidTeamInputException  the exception throws if any edited team
     *                                    isn't valid
     * @throws InvalidPointInputException the exception throws if any edited point
     *                                    isn't valid
     */
    public void checkEditedData() throws InvalidNameInputException, InvalidAgeInputException,
            InvalidTeamInputException, InvalidPointInputException {
        for (int i = 0; i < racerTable.getRowCount(); i++) {
            if (!Validation.isValidName(racerTable.getValueAt(i, 0).toString()))
                throw new InvalidNameInputException(i + 1);
            if (!Validation.isValidAge(racerTable.getValueAt(i, 1).toString()))
                throw new InvalidAgeInputException(i + 1);
            if (!Validation.isValidTeam(racerTable.getValueAt(i, 2).toString()))
                throw new InvalidTeamInputException(i + 1);
            if (!Validation.isValidPoint(racerTable.getValueAt(i, 3).toString()))
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
    private boolean isEqualTable(DefaultTableModel table, DefaultTableModel prevTable) {
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
    private void checkEmptyData(String msg, DefaultTableModel table) throws NothingDataException {
        if (table.getRowCount() == 0) {
            logger.warn("Table is empty");
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
                new String[] { racer.getRacerName(), racer.getRacerAge().toString(), racer.getTeam().getTeamName(),
                        racer.getRacerPoints().toString() });
        fullSearchTable.addRow(
                new String[] { racer.getRacerName(), racer.getRacerAge().toString(), racer.getTeam().getTeamName(),
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
     * The function sets visibility options of AddRacerGUI window
     * 
     * @param value the value to be setted
     */
    public void setAddRacerVisible(boolean value) {
        addRacerWindow.addRacerGUI.setVisible(value);
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
    private void additionalSearchDelete(DefaultTableModel table, String name, String age, String team,
            String points) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!name.isEmpty() && !age.isEmpty() && !team.isEmpty() && !points.isEmpty()) {
                if (table.getValueAt(i, 0).equals(name) && table.getValueAt(i, 1).equals(age)
                        && table.getValueAt(i, 2).equals(team) && table.getValueAt(i, 3).equals(points)) {
                    table.removeRow(i);
                }
            }
        }
    }

    /***
     * The function checks whether search inputs aren't empty
     * 
     * @throws EmptySearchInputException the exception throws if any of search
     *                                   inputs is empty
     */
    private void checkEmptyInput() throws EmptySearchInputException {
        if (searchTeamField.getText().equals("Команда") & searchNameField.getText().equals("Имя гонщика"))
            throw new EmptySearchInputException();
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

    private static void startLogging(LoggerContext context, Configuration configuration) throws IOException {
        context.start(configuration);
        logger = context.getLogger("com");
        logger.log(Level.INFO, "Start logging MainRacerGUI");
    }

    /***
     * The function stops logging
     * 
     * @param context the logger variable
     */
    public static void stopLogging(LoggerContext context) {
        logger.log(Level.INFO, "Stop logging MainRacerGUI");
        context.close();
    }

    public List<Team> getTeamData() throws InterruptedException {
        List<Team> teams = new ArrayList<>();
        Runnable foo = () -> {
            em.getTransaction().begin();
            List<Team> dbTeams = em.createQuery("FROM Team", Team.class).getResultList();
            em.getTransaction().commit();

            for (Team team : dbTeams) {
                teams.add(team);
            }

        };
        Thread thread = new Thread(foo);
        thread.start();
        thread.join();
        return teams;
    }

    public List<Racer> getRacerData() throws InterruptedException {
        List<Racer> racers = new ArrayList<>();
        Runnable foo = () -> {
            em.getTransaction().begin();
            List<Racer> dbRacers = em.createQuery("FROM Racer", Racer.class).getResultList();
            em.getTransaction().commit();

            for (Racer racer : dbRacers) {
                racers.add(racer);
            }

        };
        Thread thread = new Thread(foo);
        thread.start();
        thread.join();
        return racers;
    }

    private static Racer isAtRacerList(List<Racer> racers, Racer racer) {
        Racer answer = null;
        for (int i = 0; i < racers.size(); i++) {
            if (racers.get(i).getRacerName().equals(racer.getRacerName())
                    && racers.get(i).getRacerAge().equals(racer.getRacerAge())
                    && racers.get(i).getTeam().getTeamName().equals(racer.getTeam().getTeamName())
                    && racers.get(i).getRacerPoints().equals(racer.getRacerPoints())) {
                answer = racers.get(i);
                break;
            }
        }
        return answer;
    }

    private static boolean isTeamAtRacerList(List<Racer> racers, Integer id) {
        boolean answer = false;
        for (int i = 0; i < racers.size(); i++) {
            if (racers.get(i).getTeam().getTeamID().equals(id)) {
                answer = true;
                break;
            }
        }
        return answer;
    }

    private static Team isAtTeamList(List<Team> teams, Team team) {
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
            if (teams.get(i).getTeamName().equals(team)) {
                answer = teams.get(i);
                break;
            }
        }
        return answer;
    }

    public void updateComboBox() {
        String[] arr = new String[allTeams.size()];
        for (int i = 0; i < allTeams.size(); i++) {
            arr[i] = allTeams.get(i).getTeamName();
        }
        comboTeam = new JComboBox<String>(arr);
    }

    public void syncronizeData() {
        RacerDao racerDao = new RacerDao(em);
        TeamDao teamDao = new TeamDao(em);
        for (Team team : allTeams) {
            if (em.find(Team.class, team.getTeamID()) != null) {
                em.merge(team);
            } else {
                team.setTeamID(null);
                em.persist(team);
            }
        }

        for (Racer racer : allRacers) {
            if (em.find(Racer.class, racer.getRacerID()) != null) {
                em.merge(racer);
            } else {
                racer.setRacerID(null);
                em.persist(racer);
            }
        }

        List<Racer> dbRacers = racerDao.getAllRacers();
        for (Racer racer : dbRacers) {
            if (isAtRacerList(allRacers, racer) == null)
                racerDao.deleteRacer(racer);
        }

        List<Team> dbTeams = teamDao.getAllTeams();
        for (Team team : dbTeams) {
            if (isAtTeamList(allTeams, team) == null)
                teamDao.deleteTeam(team);
        }
    }

    public List<Team> getAllTeams() {
        return allTeams;
    }

    public List<Racer> getAllRacers() {
        return allRacers;
    }

    public void addtoAllTeam(Team team) {
        allTeams.add(team);
    }

    public void addToAllRacer(Racer racer) {
        allRacers.add(racer);
    }

    public void initialSetRacerTable() {
        for (Racer racer : allRacers) {
            racerTable.addRow(new String[] { racer.getRacerName(), racer.getRacerAge().toString(),
                    racer.getTeam().getTeamName(), racer.getRacerPoints().toString() });
        }
        copyTable(racerTable, fullSearchTable);
    }

    private void compareEditedData() {
        for (int i = 0; i < fullSearchTable.getRowCount(); i++) {
            String name = fullSearchTable.getValueAt(i, 0).toString();
            String age = fullSearchTable.getValueAt(i, 1).toString();
            String team = fullSearchTable.getValueAt(i, 2).toString();
            String point = fullSearchTable.getValueAt(i, 3).toString();
            if (!name.equals(allRacers.get(i).getRacerName()))
                allRacers.get(i).setRacerName(name);
            if (!age.equals(allRacers.get(i).getRacerAge().toString()))
                allRacers.get(i).setRacerAge(Integer.parseInt(age));
            if (!team.equals(allRacers.get(i).getTeam().getTeamName())) {
                Team newTeam = isAtTeamList(allTeams, team);
                if (newTeam == null)
                    newTeam = new Team(team);
                allRacers.get(i).setTeam(newTeam);
            }
            if (!point.equals(allRacers.get(i).getRacerPoints().toString()))
                allRacers.get(i).setRacerPoints(Integer.parseInt(point));
        }
    }
}
