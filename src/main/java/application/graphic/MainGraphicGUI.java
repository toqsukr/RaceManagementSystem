package application.graphic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import database.TrackDao;
import exception.NothingDataException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import race.system.Competition;
import race.system.MyDate;
import race.system.Track;

public class MainGraphicGUI extends JFrame {

    private static JFrame mainGraphicGUI = new JFrame("Расписание");
    private MainMenuGUI parent;

    /**
     * This bar is used to store buttons
     */
    private static final JToolBar toolBar = new JToolBar();

    /**
     * Table column names
     */
    private static final String[] columns = { "ID соревнования", "Название трассы", "Длина", "День", "Месяц", "Год" };

    /**
     * Fields of the table
     */
    private static String[][] data = {};

    /**
     * The table model storing displaying data
     */
    private static DefaultTableModel tracksTable = new DefaultTableModel(data, columns);

    /**
     * The table model storing the version of the table before editing
     */
    private static DefaultTableModel previousTracksTable = new DefaultTableModel(data, columns);

    /**
     * Create the table
     */
    private final JTable graphics = new JTable(tracksTable) {
        @Override
        public boolean isCellEditable(int i, int j) {
            return j == 0 ? getEditingPermit() : false;
        }
    };

    /**
     * The table model storing the full version of the table before searching
     */
    private static DefaultTableModel fullSearchTable = new DefaultTableModel(data, columns);

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
     * This panel store 2 inputs and search button
     */
    private static final JPanel filterPanel = new JPanel();

    /**
     * This input is used to search for an entry in the table by the name of the
     * racer
     */
    private static final JTextField searchNameField = new JTextField("Название трассы", 17);

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

    private TrackDao trackDao = new TrackDao(App.getEntityManager());

    private CompetitionDao competitionDao = new CompetitionDao(App.getEntityManager());

    private MyDateDao myDateDao = new MyDateDao(App.getEntityManager());

    private List<Track> allTracks;

    private List<Competition> allCompetitions;

    private List<MyDate> allDates;

    /***
     * The function creating mainTeamGUI
     */
    public MainGraphicGUI(MainMenuGUI parent) {
        this.parent = parent;

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
                    clearInputBtn.doClick();
                    disruptInputBtn.doClick();
                    stopLogging(context);
                    parent.setMainMenuEnable(true);
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

            try {
                allTracks = getTracksData();
                // allDates = getDatesData();
                setCompetitionsTable();
            } catch (InterruptedException exception) {
                JOptionPane.showMessageDialog(mainGraphicGUI, exception.getMessage(), "Ошибка чтения данных из базы!",
                        JOptionPane.PLAIN_MESSAGE);
            }

            Container container = mainGraphicGUI.getContentPane();
            container.setLayout(new BorderLayout());

            searchBtn.setBackground(new Color(0xDFD9D9D9, false));
            searchBtn.addActionListener(new SearchEventListener());
            searchBtn.setMargin(new Insets(1, 6, 1, 6));

            clearInputBtn.addActionListener(new ClearInputEventListener());
            clearInputBtn.setMargin(new Insets(1, 6, 1, 6));
            clearInputBtn.setBackground(new Color(0xDFD9D9D9, false));

            disruptInputBtn.setBackground(new Color(0xDFD9D9D9, false));
            disruptInputBtn.addActionListener(new DisruptEventListener());
            disruptInputBtn.setMargin(new Insets(1, 6, 1, 6));

            searchNameField.addFocusListener(new GraphicsInputFocusListener());
            searchNameField.setMargin(new Insets(2, 2, 3, 0));

            filterPanel.add(searchNameField);
            filterPanel.add(searchBtn);
            filterPanel.add(clearInputBtn);
            filterPanel.add(disruptInputBtn);

            container.add(filterPanel, BorderLayout.SOUTH);
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

    /**
     * Сlass for implementing a clear input button listener
     */
    private class ClearInputEventListener implements ActionListener {
        /***
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            setInput(searchNameField, "Название трассы");
        }
    }

    public void setCompetitionsTable() {
        if (tracksTable.getRowCount() != 0)
            clearTable(tracksTable);

        for (Track track : allTracks) {
            tracksTable.addRow(
                    new String[] {
                            String.valueOf(track.getTrackID()),
                            track.getTrackName(),
                            String.valueOf(track.getTrackLength()),
                            "Sun",
                            "July",
                            "1999",
                    });
        }
        copyTable(tracksTable, fullSearchTable);
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
                copyTable(fullSearchTable, tracksTable);
                copyTable(tracksTable, fullSearchTable);
                for (int i = tracksTable.getRowCount() - 1; i > -1; i--) {
                    if (!searchNameField.getText().equals("Название трассы")
                            & !tracksTable.getValueAt(i, 1).toString()
                                    .toLowerCase().contains(searchNameField.getText().toLowerCase())) {
                        tracksTable.removeRow(i);
                        continue;
                    }
                }
            } catch (NothingDataException exception) {
                logger.info("NothingDataException exception");
                JOptionPane.showMessageDialog(mainGraphicGUI, exception.getMessage(), "Ошибка поиска",
                        JOptionPane.PLAIN_MESSAGE);
            }

        }
    }

    public static class GraphicsInputFocusListener implements FocusListener {
        /***
         * @param e the event to be processed
         */
        public void focusGained(FocusEvent e) {
            if (searchNameField.getText().equals("Название трассы"))
                setInput(searchNameField, "");
        }

        /***
         * @param e the event to be processed
         */
        public void focusLost(FocusEvent e) {
            if (searchNameField.getText().equals(""))
                setInput(searchNameField, "Название трассы");
        }

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

    public List<Track> getTracksData() throws InterruptedException {
        return trackDao.getAllTracks();
    }

    public List<MyDate> getDatesData() throws InterruptedException {
        return myDateDao.getAllDates();
    }

    public List<Competition> getCompetitionsData() throws InterruptedException {
        return competitionDao.getAllCompetitions();
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
}
