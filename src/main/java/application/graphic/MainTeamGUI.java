package application.graphic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import org.apache.logging.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * GUI of Race Management System
 */
public class MainTeamGUI extends JFrame {
    private static JFrame mainTeamGUI = new JFrame("Список команд (База данных)");

    /**
     * This button opens file
     */
    private static final JButton squadBtn = new JButton();

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

    /**
     * This bar is used to store buttons
     */
    private static final JToolBar toolBar = new JToolBar();

    /**
     * Table column names
     */
    private static final String[] columns = { "Название команды", "Количество участников", "Очки" };

    /**
     * Fields of the table
     */
    private static String[][] data = {};

    /**
     * The table model storing displaying data
     */
    private static DefaultTableModel teamTable = new DefaultTableModel(data, columns);

    /**
     * The table model storing the version of the table before editing
     */
    private static DefaultTableModel previousTeamTable = new DefaultTableModel(data, columns);

    /**
     * Create the table
     */
    private final JTable teams = new JTable(teamTable) {
        @Override
        public boolean isCellEditable(int i, int j) {
            return getEditingPermit();
        }
    };

    /**
     * Creation of the scroll panel
     */
    private final JScrollPane scroll = new JScrollPane(teams);

    /***
     * Variable storing table edit status
     */
    private boolean editingPermit = false;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("rms_persistence");

    private EntityManager em = emf.createEntityManager();

    /***
     * The logger variable
     */
    private static Logger logger;

    private MainMenuGUI parentWindow;

    /***
     * The function creating mainTeamGUI
     */
    public MainTeamGUI(MainMenuGUI parent) {
        parentWindow = parent;

        mainTeamGUI.addWindowListener((WindowListener) new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentWindow.setMainMenuEnable(true);
                mainTeamGUI.dispose();
            }
        });

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
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }

        mainTeamGUI.setBounds(200, 150, 800, 600);
        mainTeamGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainTeamGUI.setResizable(false);
        URL mainTeamIcon = this.getClass().getClassLoader().getResource("img/team.png");
        mainTeamGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(mainTeamIcon));
        toolBar.setFloatable(false);
        teams.getTableHeader().setReorderingAllowed(false);

        Container container = mainTeamGUI.getContentPane();
        container.setLayout(new BorderLayout());

        URL squadIcon = this.getClass().getClassLoader().getResource("img/squad.png");
        squadBtn.setIcon(new ImageIcon(new ImageIcon(squadIcon).getImage().getScaledInstance(50, 50, 4)));
        squadBtn.setToolTipText("Посмотреть состав команды");
        squadBtn.setBackground(new Color(0xDFD9D9D9, false));
        squadBtn.setFocusable(false);

        URL deleteIcon = this.getClass().getClassLoader().getResource("img/delete_team.png");
        deleteBtn.setIcon(new ImageIcon(new ImageIcon(deleteIcon).getImage().getScaledInstance(50, 50, 4)));
        deleteBtn.setToolTipText("Удалить гонщика");
        deleteBtn.setBackground(new Color(0xDFD9D9D9, false));
        deleteBtn.setFocusable(false);

        URL editIcon = this.getClass().getClassLoader().getResource("img/edit.png");
        editBtn.setIcon(new ImageIcon(new ImageIcon(editIcon).getImage().getScaledInstance(50, 50, 4)));
        editBtn.setToolTipText("Редактировать запись");
        editBtn.setBackground(new Color(0xDFD9D9D9, false));
        editBtn.setFocusable(false);

        URL reportIcon = this.getClass().getClassLoader().getResource("img/report.png");
        reportBtn.setIcon(new ImageIcon(new ImageIcon(reportIcon).getImage().getScaledInstance(50, 50, 4)));
        reportBtn.setToolTipText("Сформировать отчет");
        reportBtn.setBackground(new Color(0xDFD9D9D9, false));
        reportBtn.setFocusable(false);

        URL toDataBaseUrl = this.getClass().getClassLoader().getResource("img/deploytodb.png");
        toDataBaseBtn.setIcon(new ImageIcon(new ImageIcon(toDataBaseUrl).getImage().getScaledInstance(50, 50, 4)));
        toDataBaseBtn.setToolTipText("Выгрузить в базу данных");
        toDataBaseBtn.setBackground(new Color(0xDFD9D9D9, false));
        toDataBaseBtn.setFocusable(false);

        URL fromDataBaseUrl = this.getClass().getClassLoader().getResource("img/downloadfromdb.png");
        fromDataBaseBtn
                .setIcon(new ImageIcon(new ImageIcon(fromDataBaseUrl).getImage().getScaledInstance(50, 50, 4)));
        fromDataBaseBtn.setToolTipText("Загрузить данные из базы данных");
        fromDataBaseBtn.setBackground(new Color(0xDFD9D9D9, false));
        fromDataBaseBtn.setFocusable(false);

        URL confirmIcon = this.getClass().getClassLoader().getResource("img/confirm.png");
        confirmBtn.setIcon(new ImageIcon(new ImageIcon(confirmIcon).getImage().getScaledInstance(50, 50, 4)));
        confirmBtn.setVisible(false);
        confirmBtn.setToolTipText("Ок");
        confirmBtn.setBackground(new Color(0xDFD9D9D9, false));
        confirmBtn.setFocusable(false);

        URL cancelIcon = this.getClass().getClassLoader().getResource("img/cancel.png");
        cancelBtn.setIcon(new ImageIcon(new ImageIcon(cancelIcon).getImage().getScaledInstance(50, 50, 4)));
        cancelBtn.setVisible(false);
        cancelBtn.setToolTipText("Отмена");
        cancelBtn.setBackground(new Color(0xDFD9D9D9, false));
        cancelBtn.setFocusable(false);

        toolBar.add(squadBtn);
        toolBar.add(fromDataBaseBtn);
        toolBar.add(toDataBaseBtn);
        toolBar.add(deleteBtn);
        toolBar.add(editBtn);
        toolBar.add(reportBtn);
        toolBar.add(confirmBtn);
        toolBar.add(cancelBtn);

        container.add(toolBar, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);
    }

    private boolean getEditingPermit() {
        return editingPermit;
    }

    public void setVisible(boolean value) {
        mainTeamGUI.setVisible(value);
    }
}
