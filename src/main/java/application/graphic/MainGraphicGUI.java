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
import database.TrackDao;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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

    private static JFrame mainGraphicGUI = new JFrame("Расписание");
    private MainMenuGUI parentWindow;

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
    private static DefaultTableModel graphicsTable = new DefaultTableModel(data, columns);

    /**
     * Create the table
     */
    private final JTable graphics = new JTable(graphicsTable) {
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
     * This button forms team data report
     */
    private static final JButton reportBtn = new JButton();

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

            try {
                allTracks = getTracksData();
                allDates = getDatesData();
                setCompetitionsTable();
            } catch (InterruptedException exception) {
                JOptionPane.showMessageDialog(mainGraphicGUI, exception.getMessage(), "Ошибка чтения данных из базы!",
                        JOptionPane.PLAIN_MESSAGE);
            }

            Container container = mainGraphicGUI.getContentPane();
            container.setLayout(new BorderLayout());

            URL reportIcon = this.getClass().getClassLoader().getResource("img/report.png");
            reportBtn.setIcon(new ImageIcon(new ImageIcon(reportIcon).getImage().getScaledInstance(50, 50, 4)));
            reportBtn.setToolTipText("Сформировать отчет");
            reportBtn.addActionListener(new ReportEventListener());
            reportBtn.setBackground(new Color(0xDFD9D9D9, false));
            reportBtn.setFocusable(false);

            toolBar.add(reportBtn);

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

        for (Track track : allTracks) {
            graphicsTable.addRow(
                    new String[] {
                            String.valueOf(track.getTrackID()),
                            track.getTrackName(),
                            String.valueOf(track.getTrackLength()),
                            "changeme",
                            "pls",
                            "DB doesn't connect:(",
                    });
        }
        MainRacerGUI.copyTable(graphicsTable, fullSearchTable);
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
                CreateReport.printReport(graphicsTable, mainGraphicGUI, "Отчет по списку соревнований\n\n\n\n\n",
                        new float[] { 1f, 1f, 1f, 1f, 1f, 1f },
                        new String[] { "\nID соревнования\n", "\nНазвание трассы\n", "\nДлина\n", "\nДень\n",
                                "\nМесяц\n", "\nГод\n" },
                        boldFontPath);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainGraphicGUI, exception.getMessage(), "Ошибка формирования отчета",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }
}
