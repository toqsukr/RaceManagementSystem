package application.graphic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import database.CompetitionDao;
import database.MyDateDao;
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
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import race.system.Competition;
import race.system.MyDate;
import race.system.Track;
import util.CreateReport;

public class MainGraphicGUI extends JFrame {

    private static JFrame mainGraphicGUI = new JFrame("Расписание соревнований");
    private MainMenuGUI parentWindow;

    /**
     * This bar is used to store buttons
     */
    private static final JToolBar toolBar = new JToolBar();

    /**
     * Table column names
     */
    private static final String[] columns = { "Название трассы", "День", "Месяц", "Год" };

    /**
     * Fields of the table
     */
    private static String[][] data = {};

    private JComboBox<String> comboDay = new JComboBox<>();

    private JComboBox<String> comboMonth = new JComboBox<>();

    private JComboBox<String> comboYear = new JComboBox<>();

    private JComboBox<String> comboTrack = new JComboBox<>();

    /**
     * The table model storing displaying data
     */
    private static DefaultTableModel graphicsTable = new DefaultTableModel(data, columns);

    /**
     * Create the table
     */
    private final JTable graphics = new JTable(graphicsTable) {
        @Override
        public boolean isCellEditable(int i, int j) {
            return getEditingPermit();
        }
    };

    /**
     * The table model storing the full version of the table before searching
     */
    private static DefaultTableModel previousGraphicsTable = new DefaultTableModel(data, columns);

    /**
     * Creation of the scroll panel
     */
    private final JScrollPane scroll = new JScrollPane(graphics);

    /***
     * Variable storing table edit status
     */
    private boolean editingPermit = false;

    /***
     * The logger variable
     */
    private static Logger logger;

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

    private static final JButton toDataBaseBtn = new JButton();

    private static final JButton fromDataBaseBtn = new JButton();

    /**
     * This button confirms changes made
     */
    private static final JButton confirmBtn = new JButton();

    /**
     * This button cancles changes made
     */
    private static final JButton cancelBtn = new JButton();

    private CompetitionDao competitionDao = new CompetitionDao(App.getEntityManager());

    private MyDateDao myDateDao = new MyDateDao(App.getEntityManager());

    private List<Competition> allCompetitions;

    private AddGraphicGUI addGraphicWindow;

    private List<MyDate> allDates;

    /***
     * The function creating mainTeamGUI
     */
    public MainGraphicGUI(MainMenuGUI parent) {
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

            mainGraphicGUI.addWindowListener((WindowListener) new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    stopLogging(context);
                    parentWindow.setMainMenuEnable(true);
                    mainGraphicGUI.dispose();
                }
            });

            mainGraphicGUI.setBounds(200, 150, 800, 600);
            mainGraphicGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            mainGraphicGUI.setResizable(false);
            URL mainGraphicsIcon = this.getClass().getClassLoader().getResource("img/graphic.png");
            mainGraphicGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(mainGraphicsIcon));
            toolBar.setFloatable(false);
            graphics.getTableHeader().setReorderingAllowed(false);
            graphics.setRowHeight(graphics.getRowHeight() + 4);

            updateComboYear();
            updateComboMonth();
            updateComboDay();
            updateComboTrack();

            DefaultCellEditor editorTrack = new DefaultCellEditor(comboTrack);
            graphics.getColumnModel().getColumn(0).setCellEditor(editorTrack);

            DefaultCellEditor editorDay = new DefaultCellEditor(comboDay);
            graphics.getColumnModel().getColumn(1).setCellEditor(editorDay);

            comboMonth.addActionListener(new SelectEventListener());
            DefaultCellEditor editorMonth = new DefaultCellEditor(comboMonth);
            graphics.getColumnModel().getColumn(2).setCellEditor(editorMonth);

            comboYear.addActionListener(new SelectEventListener());
            DefaultCellEditor editorYear = new DefaultCellEditor(comboYear);
            graphics.getColumnModel().getColumn(3).setCellEditor(editorYear);

            addGraphicWindow = new AddGraphicGUI(this);

            try {
                allCompetitions = getCompetitionsData();
                allDates = getDatesData();
                myDateDao.updateFreeID(allDates);
                competitionDao.updateFreeID(allCompetitions);
                setCompetitionsTable();
            } catch (InterruptedException exception) {
                JOptionPane.showMessageDialog(mainGraphicGUI, exception.getMessage(), "Ошибка чтения данных из базы!",
                        JOptionPane.PLAIN_MESSAGE);
            }

            Container container = mainGraphicGUI.getContentPane();
            container.setLayout(new BorderLayout());

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

            URL addIcon = this.getClass().getClassLoader().getResource("img/add_competition.png");
            addBtn.setIcon(new ImageIcon(new ImageIcon(addIcon).getImage().getScaledInstance(50, 50, 4)));
            addBtn.setToolTipText("Добавить соревнование");
            addBtn.addActionListener(new AddEventListener());
            addBtn.setBackground(new Color(0xDFD9D9D9, false));
            addBtn.setFocusable(false);

            URL deleteIcon = this.getClass().getClassLoader().getResource("img/delete_competition.png");
            deleteBtn.setIcon(new ImageIcon(new ImageIcon(deleteIcon).getImage().getScaledInstance(50, 50, 4)));
            deleteBtn.setToolTipText("Удалить соревнование");
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
        logger.log(Level.INFO, "Start logging MainGraphicsGUI");
    }

    /***
     * The function stops logging
     * 
     * @param context the logger variable
     */
    public static void stopLogging(LoggerContext context) {
        logger.log(Level.INFO, "Stop logging MainGraphicsGUI");
        context.close();
    }

    public void setVisible(boolean value) {
        mainGraphicGUI.setVisible(value);
    }

    private boolean getEditingPermit() {
        return editingPermit;
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

    public void setCompetitionsTable() {
        if (graphicsTable.getRowCount() != 0)
            MainRacerGUI.clearTable(graphicsTable);

        for (Competition competition : allCompetitions) {
            graphicsTable.addRow(
                    new String[] {
                            competition.getTrack().getTrackName(),
                            competition.getDate().getDay().toString(),
                            competition.getDate().getMonth().toString(),
                            competition.getDate().getYear().toString()
                    });
        }
    }

    public List<Competition> getCompetitionsData() throws InterruptedException {
        return competitionDao.getAllCompetitions();
    }

    public List<MyDate> getDatesData() throws InterruptedException {
        return myDateDao.getAllDates();
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
            if (parentWindow.getMainTrackGUI().getAllTracks().size() > 0) {
                logger.info("Opening window AddGraphicGUI");
                setMainGraphicEnable(false);
                addGraphicWindow.setAddGraphicVisibility(true);
            } else
                JOptionPane.showMessageDialog(mainGraphicGUI,
                        "Недостаточно данных для добавления соревнования!\nПроверьте наличие информации о трассах в системе!",
                        "Сообщение",
                        JOptionPane.PLAIN_MESSAGE);
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
                CreateReport.printReport(graphicsTable, mainGraphicGUI, "Отчет по графику соревнований\n\n\n\n\n",
                        new float[] { 1f, 0.5f, 0.5f, 0.5f },
                        new String[] { "\nНазвание трассы\n\n", "\nДень\n\n",
                                "\nМесяц\n\n", "\nГод\n\n" },
                        boldFontPath);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainGraphicGUI, exception.getMessage(), "Ошибка формирования отчета",
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
     * Сlass for implementing a editBtn button listener
     */
    private class EditEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                MainRacerGUI.checkEmptyData("Данные для редактирования не найдены!", graphicsTable);
                MainRacerGUI.copyTable(graphicsTable, previousGraphicsTable);
                setEditingPermit(true);
                setConfirmbarVisible();
            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainGraphicGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    private class DeleteEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                MainRacerGUI.checkEmptyData("Данные для удаления не найдены!", graphicsTable);
                MainRacerGUI.checkDeleteSelect(graphics);

                String message = graphics.getSelectedRows().length == 1
                        ? "Вы действительно хотите удалить выбранную запись?\nОтменить действие будет невозможно!"
                        : "Вы действительно хотите удалить выбранные записи?\nОтменить действие будет невозможно!";
                int result = JOptionPane.showConfirmDialog(mainGraphicGUI,
                        message,
                        "Подтверждение действия",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {

                    int i = graphics.getSelectedRows().length - 1;
                    while (graphics.getSelectedRows().length > 0) {
                        int j = graphics.getRowCount() - 1;
                        while (j > -1) {
                            if (j == graphics.getSelectedRows()[i]) {
                                String removingTrackName = graphics.getValueAt(graphics.getSelectedRows()[i],
                                        0).toString();
                                String removingDay = graphics.getValueAt(graphics.getSelectedRows()[i],
                                        1).toString();
                                String removingMonth = graphics.getValueAt(graphics.getSelectedRows()[i], 2).toString();
                                String removingYear = graphics.getValueAt(graphics.getSelectedRows()[i], 3).toString();

                                graphicsTable.removeRow(graphics.getSelectedRows()[i]);
                                Track removingTrack = MainTrackGUI.isAtTrackList(
                                        parentWindow.getMainTrackGUI().getAllTracks(),
                                        removingTrackName);

                                MyDate removingDate = isAtDateList(allDates, Integer.parseInt(removingDay),
                                        Integer.parseInt(removingMonth), Integer.parseInt(removingYear));
                                myDateDao.addFreeID(removingDate.getDateID());
                                allDates.remove(removingDate);
                                Competition removingCompetition = isAtCompetitionList(allCompetitions,
                                        new Competition(removingDate, removingTrack));
                                competitionDao.addFreeID(removingCompetition.getCompetitionID());
                                allCompetitions.remove(removingCompetition);
                                break;
                            }
                            j--;
                        }
                        i--;
                    }
                }
            } catch (UnselectedDeleteException exception) {
                JOptionPane.showMessageDialog(mainGraphicGUI, exception.getMessage(), "Ошибка удаления",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainGraphicGUI, exception.getMessage(), "Ошибка удаления",
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
            if (graphics.getSelectedRow() != -1)
                graphics.getCellEditor(graphics.getSelectedRow(),
                        graphics.getSelectedColumn()).stopCellEditing();
            if (!MainRacerGUI.isEqualTable(graphicsTable, previousGraphicsTable)) {
                MyDate date = isAtDateList(allDates,
                        Integer.parseInt(comboDay.getSelectedItem().toString()),
                        Integer.parseInt(comboMonth.getSelectedItem().toString()),
                        Integer.parseInt(comboYear.getSelectedItem().toString()));
                if (date == null) {
                    int result = JOptionPane.showConfirmDialog(mainGraphicGUI, "Сохранить изменения?",
                            "Подтверждение действия",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        compareEditedData();
                        setEditingPermit(false);
                        setConfirmbarUnvisible();
                    }
                } else {
                    JOptionPane.showMessageDialog(mainGraphicGUI, "В эту дату уже проводится соревнование!",
                            "Сообщение",
                            JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                setEditingPermit(false);
                setConfirmbarUnvisible();
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
            if (graphics.getSelectedRow() != -1)
                graphics.getCellEditor(graphics.getSelectedRow(), graphics.getSelectedColumn()).stopCellEditing();
            MainRacerGUI.copyTable(previousGraphicsTable, graphicsTable);
            setEditingPermit(false);
            setConfirmbarUnvisible();
        }
    }

    private class SelectEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            comboDay.removeAllItems();
            updateComboDay();
            comboDay.setSelectedIndex(0);
            if (graphics.getSelectedRow() != -1)
                graphicsTable.setValueAt('1', graphics.getSelectedRow(), 1);
        }
    }

    private void setEditingPermit(boolean value) {
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

    /***
     * The function sets enability options of mainScoreGUI window
     * 
     * @param value the value to be setted
     */
    public void setMainGraphicEnable(boolean value) {
        mainGraphicGUI.setEnabled(value);
    }

    public MainMenuGUI getParentWindow() {
        return parentWindow;
    }

    public void updateComboDay() {
        int last;
        if (Integer.parseInt(comboMonth.getSelectedItem().toString()) != 2) {
            last = " 1 3 5 7 8 10 12 ".contains(comboMonth.getSelectedItem().toString()) ? 32 : 31;
        } else
            last = Integer.parseInt(comboYear.getSelectedItem().toString()) % 4 == 0 ? 30 : 29;
        for (int i = 1; i < last; i++) {
            comboDay.addItem(Integer.valueOf(i).toString());
        }

    }

    public void updateComboMonth() {
        for (int i = 1; i < 13; i++) {
            comboMonth.addItem(Integer.valueOf(i).toString());
        }
    }

    public void updateComboYear() {
        for (int i = 2024; i < 2100; i++) {
            comboYear.addItem(Integer.valueOf(i).toString());
        }
    }

    public static MyDate isAtDateList(List<MyDate> dates, Integer day, Integer month, Integer year) {
        MyDate answer = null;
        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i).getDay().equals(day) && dates.get(i).getMonth().equals(month)
                    && dates.get(i).getYear().equals(year)) {
                answer = dates.get(i);
                break;
            }
        }
        return answer;
    }

    public List<MyDate> getAllDates() {
        return allDates;
    }

    public void addToAllCompetitions(Competition competition) {
        allCompetitions.add(competition);
    }

    public void addToAllDates(MyDate date) {
        allDates.add(date);
    }

    public CompetitionDao getCompetitionDao() {
        return competitionDao;
    }

    public MyDateDao getMyDateDao() {
        return myDateDao;
    }

    public List<Competition> getAllCompetitions() {
        return allCompetitions;
    }

    public static Competition isAtCompetitionList(List<Competition> competitions, Competition competition) {
        Competition answer = null;
        for (int i = 0; i < competitions.size(); i++) {
            if (competitions.get(i).getTrack().getTrackName().equals(competition.getTrack().getTrackName())
                    && competitions.get(i).getDate().getDay().equals(competition.getDate().getDay())
                    && competitions.get(i).getDate().getMonth().equals(competition.getDate().getMonth()) &&
                    competitions.get(i).getDate().getYear().equals(competition.getDate().getYear())) {
                answer = competitions.get(i);
                break;
            }
        }
        return answer;
    }

    public AddGraphicGUI getAddGraphicGUI() {
        return addGraphicWindow;
    }

    public void deleteFromAllCompetitions(int id) {
        competitionDao.addFreeID(id);
        for (Competition competition : allCompetitions) {
            if (competition.getCompetitionID() == id) {
                allCompetitions.remove(competition);
                break;
            }
        }
    }

    public void deleteFromAllDates(int id) {
        myDateDao.addFreeID(id);
        for (MyDate date : allDates) {
            if (date.getDateID() == id) {
                allDates.remove(date);
                break;
            }
        }
    }

    private void compareEditedData() {
        for (int i = 0; i < graphicsTable.getRowCount(); i++) {
            String track = graphicsTable.getValueAt(i, 0).toString();
            String day = graphicsTable.getValueAt(i, 1).toString();
            String month = graphicsTable.getValueAt(i, 2).toString();
            String year = graphicsTable.getValueAt(i, 3).toString();

            if (!track.equals(allCompetitions.get(i).getTrack().getTrackName()))
                allCompetitions.get(i)
                        .setTrack(MainTrackGUI.isAtTrackList(parentWindow.getMainTrackGUI().getAllTracks(), track));
            if (!day.equals(allCompetitions.get(i).getDate().getDay().toString())
                    && !month.equals(allCompetitions.get(i).getDate().getMonth().toString())
                    && !year.equals(allCompetitions.get(i).getDate().getYear().toString())) {
                allCompetitions.get(i).setDate(
                        isAtDateList(allDates, Integer.parseInt(day), Integer.parseInt(month),
                                Integer.parseInt(year)));
            }
        }
    }

    public void setAllCompetitions(List<Competition> list) {
        allCompetitions = list;
    }

    public void setAllDates(List<MyDate> list) {
        allDates = list;
    }

    public void clearAllCompetitions() {
        allCompetitions.clear();
    }

    public void clearAllDates() {
        allDates.clear();
    }

    public void updateComboTrack() {
        comboTrack.removeAllItems();
        comboTrack.setSelectedItem(null);
        List<Track> allTracks = parentWindow.getMainTrackGUI().getAllTracks();
        for (Track track : allTracks) {
            comboTrack.addItem(track.getTrackName());
        }
    }

    public List<MyDate> getDateData() throws InterruptedException {
        return myDateDao.getAllDates();
    }

    public List<Competition> getCompetitionData() throws InterruptedException {
        return competitionDao.getAllCompetitions();
    }
}
