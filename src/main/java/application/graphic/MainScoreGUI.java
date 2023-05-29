package application.graphic;

import util.CreateReport;
import util.Validation;
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

import application.App;
import database.ScoreDao;
import exception.InvalidTimeException;
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
public class MainScoreGUI extends JFrame {
    private static JFrame mainScoreGUI = new JFrame("Рекорды");

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

    private JComboBox<String> comboTrack = new JComboBox<>();

    private JComboBox<String> comboRacer = new JComboBox<>();

    /**
     * This bar is used to store buttons
     */
    private static final JToolBar toolBar = new JToolBar();

    /**
     * Table column names
     */
    private static final String[] columns = { "Имя гонщика", "Название трассы",
            "Личный результат (время финиша в минутах)" };

    /**
     * Fields of the table
     */
    private static String[][] data = {};

    /**
     * The table model storing displaying data
     */
    private static DefaultTableModel scoreTable = new DefaultTableModel(data, columns);

    /**
     * The table model storing the version of the table before editing
     */
    private static DefaultTableModel previousScoreTable = new DefaultTableModel(data, columns);

    /**
     * Create the table
     */
    private final JTable scores = new JTable(scoreTable) {
        @Override
        public boolean isCellEditable(int i, int j) {
            return getEditingPermit();
        }
    };

    /**
     * Creation of the scroll panel
     */
    private final JScrollPane scroll = new JScrollPane(scores);

    /***
     * Variable storing table edit status
     */
    private boolean editingPermit = false;

    /***
     * The logger variable
     */
    private static Logger logger;

    private MainMenuGUI parentWindow;

    private AddScoreGUI addScoreWindow;

    private List<Score> allScores;

    private ScoreDao scoreDao = new ScoreDao(App.getEntityManager());

    /***
     * The function creating mainScoreGUI
     */
    public MainScoreGUI(MainMenuGUI parent) {
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

            mainScoreGUI.addWindowListener((WindowListener) new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    stopLogging(context);
                    mainScoreGUI.dispose();
                }
            });

            mainScoreGUI.setBounds(200, 150, 800, 600);
            mainScoreGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            mainScoreGUI.setResizable(false);
            URL mainTeamIcon = this.getClass().getClassLoader().getResource("img/score.png");
            mainScoreGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(mainTeamIcon));
            toolBar.setFloatable(false);
            scores.getTableHeader().setReorderingAllowed(false);
            scores.setRowHeight(scores.getRowHeight() + 4);

            try {
                allScores = getScoreData();
                scoreDao.updateFreeID(allScores);
                setScoreTable();
            } catch (InterruptedException exception) {
                JOptionPane.showMessageDialog(mainScoreGUI, exception.getMessage(), "Ошибка чтения данных из базы!",
                        JOptionPane.PLAIN_MESSAGE);
            }

            addScoreWindow = new AddScoreGUI(this);

            updateComboRacer();
            updateComboTrack();

            Container container = mainScoreGUI.getContentPane();
            container.setLayout(new BorderLayout());

            DefaultCellEditor editorRacer = new DefaultCellEditor(comboRacer);
            scores.getColumnModel().getColumn(0).setCellEditor(editorRacer);

            DefaultCellEditor editorTrack = new DefaultCellEditor(comboTrack);
            scores.getColumnModel().getColumn(1).setCellEditor(editorTrack);

            URL addIcon = this.getClass().getClassLoader().getResource("img/add_score.png");
            addBtn.setIcon(new ImageIcon(new ImageIcon(addIcon).getImage().getScaledInstance(50, 50, 4)));
            addBtn.setToolTipText("Добавить рекорд");
            addBtn.addActionListener(new AddEventListener());
            addBtn.setBackground(new Color(0xDFD9D9D9, false));
            addBtn.setFocusable(false);

            URL deleteIcon = this.getClass().getClassLoader().getResource("img/delete_score.png");
            deleteBtn.setIcon(new ImageIcon(new ImageIcon(deleteIcon).getImage().getScaledInstance(50, 50, 4)));
            deleteBtn.setToolTipText("Удалить рекорд");
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
                CreateReport.printReport(scoreTable, mainScoreGUI, "Отчет по списку трасс\n\n\n\n\n",
                        new float[] { 1f, 1f, 0.6f },
                        new String[] { "\nИмя гонщика\n", "\nНазвание трассы\n", "\nЛичный результат\n\n" },
                        boldFontPath);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainScoreGUI, exception.getMessage(), "Ошибка формирования отчета",
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
            if (parentWindow.getMainRacerGUI().getAllRacers().size() > 0
                    && parentWindow.getMainTrackGUI().getAllTracks().size() > 0) {

                logger.info("Opening window AddRacerGUI");
                setMainScoreEnable(false);
                addScoreWindow.setAddScoreVisibility(true);
            } else
                JOptionPane.showMessageDialog(mainScoreGUI,
                        "Недостаточно данных для добавления рекорда!\nПроверьте наличие информации о гонщиках и трассах в системе!",
                        "Сообщение",
                        JOptionPane.PLAIN_MESSAGE);
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
                MainRacerGUI.checkEmptyData("Данные для редактирования не найдены!", scoreTable);
                MainRacerGUI.copyTable(scoreTable, previousScoreTable);
                setEditingPermit(true);
                setConfirmbarVisible();
            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainScoreGUI, exception.getMessage(), "Ошибка редактирования",
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
                if (scores.getSelectedRow() != -1)
                    scores.getCellEditor(scores.getSelectedRow(),
                            scores.getSelectedColumn()).stopCellEditing();
                if (!MainRacerGUI.isEqualTable(scoreTable, previousScoreTable)) {
                    checkEditedData();
                    int result = JOptionPane.showConfirmDialog(mainScoreGUI, "Сохранить изменения?",
                            "Подтверждение действия",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        compareEditedData();
                        setEditingPermit(false);
                        setConfirmbarUnvisible();
                    }
                } else {
                    setEditingPermit(false);
                    setConfirmbarUnvisible();
                }
            } catch (InvalidTimeException exception) {
                logger.warn("Entered invalid finish time while editing");
                JOptionPane.showMessageDialog(mainScoreGUI, exception.getMessage(), "Ошибка редактирования",
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
            if (scores.getSelectedRow() != -1)
                scores.getCellEditor(scores.getSelectedRow(), scores.getSelectedColumn()).stopCellEditing();
            MainRacerGUI.copyTable(previousScoreTable, scoreTable);
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
                MainRacerGUI.checkEmptyData("Данные для удаления не найдены!", scoreTable);
                MainRacerGUI.checkDeleteSelect(scores);

                String message = scores.getSelectedRows().length == 1
                        ? "Вы действительно хотите удалить выбранную запись?\nОтменить действие будет невозможно!"
                        : "Вы действительно хотите удалить выбранные записи?\nОтменить действие будет невозможно!";
                int result = JOptionPane.showConfirmDialog(mainScoreGUI,
                        message,
                        "Подтверждение действия",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {

                    int i = scores.getSelectedRows().length - 1;
                    while (scores.getSelectedRows().length > 0) {
                        int j = scores.getRowCount() - 1;
                        while (j > -1) {
                            if (j == scores.getSelectedRows()[i]) {
                                String removingRacerName = scores.getValueAt(scores.getSelectedRows()[i],
                                        0).toString();
                                String removingTrackName = scores.getValueAt(scores.getSelectedRows()[i],
                                        1).toString();
                                String removingTime = scores.getValueAt(scores.getSelectedRows()[i], 2).toString();
                                scoreTable.removeRow(scores.getSelectedRows()[i]);
                                Track removingTrack = MainTrackGUI.isAtTrackList(
                                        parentWindow.getMainTrackGUI().getAllTracks(),
                                        removingTrackName);
                                Racer removingRacer = MainRacerGUI.isAtRacerList(
                                        parentWindow.getMainRacerGUI().getAllRacers(),
                                        Integer.parseInt(
                                                removingRacerName.substring(removingRacerName.indexOf(':', 0) + 2,
                                                        (removingRacerName.indexOf(')', 0)))));

                                Integer time = Integer
                                        .parseInt(removingTime);
                                Score score = isAtScoreList(allScores,
                                        new Score(removingRacer, removingTrack, time));
                                scoreDao.addFreeID(score.getScoreID());
                                allScores.remove(score);
                                break;
                            }
                            j--;
                        }
                        i--;
                    }
                    setScoreTable();
                }
            } catch (UnselectedDeleteException exception) {
                JOptionPane.showMessageDialog(mainScoreGUI, exception.getMessage(), "Ошибка удаления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainScoreGUI, exception.getMessage(), "Ошибка удаления",
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
        mainScoreGUI.setVisible(value);
    }

    public DefaultTableModel getScoreTable() {
        return scoreTable;
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
        logger.log(Level.INFO, "Start logging mainScoreGUI");
    }

    /***
     * The function stops logging
     * 
     * @param context the logger variable
     */
    public void stopLogging(LoggerContext context) {
        logger.log(Level.INFO, "Stop logging mainScoreGUI");
        context.close();
    }

    /***
     * The function sets enability options of mainScoreGUI window
     * 
     * @param value the value to be setted
     */
    public void setMainScoreEnable(boolean value) {
        mainScoreGUI.setEnabled(value);
    }

    public MainMenuGUI getParentWindow() {
        return parentWindow;
    }

    public List<Score> getScoreData() throws InterruptedException {
        return scoreDao.getAllScores();
    }

    public List<Score> getAllScores() {
        return allScores;
    }

    public void setScoreTable() {
        if (scoreTable.getRowCount() != 0)
            MainRacerGUI.clearTable(scoreTable);

        for (Score score : allScores) {
            String winner = score.getRacerInfo().getRacerName() + " (ID: " + score.getRacerInfo().getRacerID()
                    + ")";
            scoreTable.addRow(
                    new String[] { winner, score.getTrackInfo().getTrackName(), score.getFinishTime().toString() });
        }
    }

    public static Score isAtScoreList(List<Score> scores, Score score) {
        Score answer = null;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).getRacerInfo().getRacerName().equals(score.getRacerInfo().getRacerName())
                    && scores.get(i).getTrackInfo().getTrackName().equals(score.getTrackInfo().getTrackName())
                    && scores.get(i).getFinishTime().equals(score.getFinishTime())) {
                answer = scores.get(i);
                break;
            }
        }
        return answer;
    }

    public static Score isAtScoreList(List<Score> scores, Integer id) {
        Score answer = null;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).getScoreID().equals(id)) {
                answer = scores.get(i);
                break;
            }
        }
        return answer;
    }

    public void addToAllScores(Score score) {
        allScores.add(score);
    }

    public void updateComboRacer() {
        comboRacer.removeAllItems();
        comboRacer.setSelectedItem(null);
        List<Racer> allRacers = parentWindow.getMainRacerGUI().getAllRacers();
        for (Racer racer : allRacers) {
            comboRacer.addItem(racer.getRacerName() + " (ID: " + racer.getRacerID() + ")");
        }
    }

    public void updateComboTrack() {
        comboTrack.removeAllItems();
        comboTrack.setSelectedItem(null);
        List<Track> allTracks = parentWindow.getMainTrackGUI().getAllTracks();
        for (Track track : allTracks) {
            comboTrack.addItem(track.getTrackName());
        }
    }

    public AddScoreGUI getAddScoreWindow() {
        return addScoreWindow;
    }

    private void compareEditedData() {
        for (int i = 0; i < scoreTable.getRowCount(); i++) {
            String name = scoreTable.getValueAt(i, 0).toString();
            String track = scoreTable.getValueAt(i, 1).toString();
            String time = scoreTable.getValueAt(i, 2).toString();
            allScores.get(i).setRacerInfo(MainRacerGUI.isAtRacerList(parentWindow.getMainRacerGUI().getAllRacers(),
                    Integer.parseInt(name.substring(name.indexOf(':', 0) + 2, (name.indexOf(')', 0))))));
            if (!track.equals(allScores.get(i).getTrackInfo().getTrackName()))
                allScores.get(i)
                        .setTrackInfo(MainTrackGUI.isAtTrackList(parentWindow.getMainTrackGUI().getAllTracks(), track));
            if (!time.equals(allScores.get(i).getFinishTime().toString())) {
                allScores.get(i).setFinishTime(Integer.parseInt(time));
            }
        }
    }

    /***
     * The function checks whether table data is valid
     */

    private void checkEditedData() throws InvalidTimeException {
        for (int i = 0; i < scoreTable.getRowCount(); i++) {
            if (!Validation.isValidTime(scoreTable.getValueAt(i, 2).toString()))
                throw new InvalidTimeException(i + 1);
        }
    }

    public void setAllScores(List<Score> list) {
        allScores = list;
    }

    public void clearAllScores() {
        allScores.clear();
    }

    public void clearComboRacer() {
        comboRacer.removeAllItems();
        comboRacer.setSelectedItem(null);
    }

    public void clearComboTrack() {
        comboRacer.removeAllItems();
        comboRacer.setSelectedItem(null);
    }

    public ScoreDao getScoreDao() {
        return scoreDao;
    }

    public void deleteFromAllScores(int id) {
        scoreDao.addFreeID(id);
        for (Score score : allScores) {
            if (score.getScoreID() == id) {
                allScores.remove(score);
                break;
            }
        }
    }
}
