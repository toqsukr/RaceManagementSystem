package application.graphic;

import race.system.Racer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import exception.*;
import util.CreateReport;
import util.FileManage;

/**
 * GUI of Race Management System
 */
public class MainRacerGUI {
    private static JFrame mainRacerGUI = new JFrame("Список гонщиков");
    /**
     * This button performs a search
     */
    private static final JButton searchBtn = new JButton("Поиск");
    private static final JButton reportBtn = new JButton("Отчет");

    private static final JButton clearInputBtn = new JButton("Очистить");
    /**
     * This button saves changes
     */
    private static final JButton saveBtn = new JButton(new ImageIcon("etu/src/img/save.png"));
    /**
     * This button adds new field into table
     */
    private static final JButton addBtn = new JButton(new ImageIcon("etu/src/img/add.png"));
    /**
     * This button deletes selected field
     */
    private static final JButton deleteBtn = new JButton(new ImageIcon("etu/src/img/delete.png"));
    /**
     * This button allows you to edit selected field
     */
    private static final JButton editBtn = new JButton(new ImageIcon("etu/src/img/edit.png"));
    private static final JButton fileBtn = new JButton(new ImageIcon("etu/src/img/file.png"));
    private static final JButton confirmBtn = new JButton(new ImageIcon("etu/src/img/confirm.png"));
    private static final JButton cancelBtn = new JButton(new ImageIcon("etu/src/img/cancel.png"));

    /**
     * This input is used to search for an entry in the table by the name of the
     * racer
     */
    private static final JTextField searchNameField = new JTextField("Имя гонщика", 10);
    /**
     * This input is used to search for an entry in the table by the team of the
     * racer
     */
    private static final JTextField searchTeamField = new JTextField("Команда", 10);
    /**
     * This bar is used to store buttons
     */
    private static final JToolBar toolBar = new JToolBar();
    /**
     * Table column names
     */
    private static final String[] columns = { "Имя гонщика", "Возраст", "Команда", "Очки" };
    /**
     * Field of the table
     */
    private static String[][] data = {};
    /**
     * Generation of the default table
     */
    private static DefaultTableModel racerTable = new DefaultTableModel(data, columns);

    private static DefaultTableModel previousRacerTable = new DefaultTableModel(data, columns);
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

    private boolean editingPermit = false;

    private static AddRacerGUI addRacerWindow = new AddRacerGUI();

    /**
     * Constructor of GUI
     */
    public void show() {
        mainRacerGUI.setVisible(true);
        mainRacerGUI.setBounds(200, 150, 800, 600);
        mainRacerGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainRacerGUI.setResizable(false);
        mainRacerGUI.setIconImage(Toolkit.getDefaultToolkit().getImage("etu/src/img/favicon.png"));
        addRacerWindow.show();

        racers.getTableHeader().setReorderingAllowed(false);

        Container container = mainRacerGUI.getContentPane();
        container.setLayout(new BorderLayout());

        searchBtn.setBackground(new Color(0xDFD9D9D9, false));
        clearInputBtn.setBackground(new Color(0xDFD9D9D9, false));
        reportBtn.setBackground(new Color(0xDFD9D9D9, false));

        searchTeamField.addFocusListener(new TeamInputFocusListener());
        searchNameField.addFocusListener(new RacerInputFocusListener());
        searchBtn.addActionListener(new SearchEventListener());
        reportBtn.addActionListener(new ReportEventListener());

        clearInputBtn.addActionListener(new ClearInputEventListener());

        filterPanel.add(searchNameField);
        filterPanel.add(searchTeamField);
        filterPanel.add(clearInputBtn);
        filterPanel.add(searchBtn);
        filterPanel.add(reportBtn);

        fileBtn.setToolTipText("Открыть файл");
        fileBtn.setBackground(new Color(0xDFD9D9D9, false));
        fileBtn.addActionListener(new FileEventListener());

        saveBtn.setToolTipText("Сохранить изменения");
        saveBtn.setBackground(new Color(0xDFD9D9D9, false));
        saveBtn.addActionListener(new SaveEventListener());

        editBtn.setToolTipText("Редактировать запись");
        editBtn.setBackground(new Color(0xDFD9D9D9, false));
        editBtn.addActionListener(new EditEventListener());

        deleteBtn.setToolTipText("Удалить запись");
        deleteBtn.setBackground(new Color(0xDFD9D9D9, false));
        deleteBtn.addActionListener(new DeleteEventListener());

        addBtn.setToolTipText("Добавить запись");
        addBtn.setBackground(new Color(0xDFD9D9D9, false));
        addBtn.addActionListener(new AddEventListener());

        confirmBtn.setVisible(false);
        confirmBtn.setToolTipText("Ок");
        confirmBtn.setBackground(new Color(0xDFD9D9D9, false));
        confirmBtn.addActionListener(new ConfirmEventListener());

        cancelBtn.setVisible(false);
        cancelBtn.setToolTipText("Отмена");
        cancelBtn.setBackground(new Color(0xDFD9D9D9, false));
        cancelBtn.addActionListener(new CancelEventListener());

        toolBar.add(fileBtn);
        toolBar.add(saveBtn);
        toolBar.add(addBtn);
        toolBar.add(deleteBtn);
        toolBar.add(editBtn);
        toolBar.add(confirmBtn);
        toolBar.add(cancelBtn);

        container.add(toolBar, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);
        container.add(filterPanel, BorderLayout.SOUTH);
    }

    public static void setRacerWindowEnable(boolean value) {
        mainRacerGUI.setEnabled(value);
    }

    private static void setConfirmbarVisible() {
        fileBtn.setVisible(false);
        saveBtn.setVisible(false);
        addBtn.setVisible(false);
        deleteBtn.setVisible(false);
        editBtn.setVisible(false);
        confirmBtn.setVisible(true);
        cancelBtn.setVisible(true);
    }

    private static void setConfirmbarUnvisible() {
        fileBtn.setVisible(true);
        saveBtn.setVisible(true);
        addBtn.setVisible(true);
        deleteBtn.setVisible(true);
        editBtn.setVisible(true);
        confirmBtn.setVisible(false);
        cancelBtn.setVisible(false);
    }

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

    private void clearTable(DefaultTableModel table) {
        int n = table.getRowCount();
        for (int i = 0; i < n; i++) {
            table.removeRow(n - i - 1);
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
            // FileManage.readRacerFromTextFile(mainRacerGUI, racerTable);
            FileManage.readRacerFromXmlFile(racerTable);
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
                int result = JOptionPane.showConfirmDialog(mainRacerGUI, "Sure? You want to exit?", "Swing Tester",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    setConfirmbarUnvisible();
                    changeEditingPermit();
                }

            } catch (UnselectedEditException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка редактирования",
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
            try {
                copyTable(previousRacerTable, racerTable);
                changeEditingPermit();
                setConfirmbarUnvisible();
            } catch (UnselectedEditException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
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
                copyTable(racerTable, previousRacerTable);
                changeEditingPermit();
                setConfirmbarVisible();

            } catch (UnselectedEditException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    public static void addRacer(Racer racer) {
        racerTable.addRow(new String[] { racer.getName(), racer.getAge().toString(), racer.getTeam(),
                racer.getPoints().toString() });
    }

    private boolean getEditingPermit() {
        return editingPermit;
    }

    private void changeEditingPermit() throws UnselectedEditException {
        if (racerTable.getRowCount() == 0)
            throw new UnselectedEditException();
        editingPermit = !editingPermit;
    }

    public static void setInput(JTextField input, String text) {
        input.setText(text);
    }

    private static class ClearInputEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setInput(searchTeamField, "Команда");
            setInput(searchNameField, "Имя гонщика");
        }
    }

    public static class TeamInputFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            if (searchTeamField.getText().equals("Команда"))
                setInput(searchTeamField, "");
        }

        public void focusLost(FocusEvent e) {
            if (searchTeamField.getText().equals(""))
                setInput(searchTeamField, "Команда");
        }

    }

    public static class RacerInputFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            if (searchNameField.getText().equals("Имя гонщика"))
                setInput(searchNameField, "");
        }

        public void focusLost(FocusEvent e) {
            if (searchNameField.getText().equals(""))
                setInput(searchNameField, "Имя гонщика");
        }

    }

    /**
     * Сlass for implementing a save button listener
     */
    private static class SaveEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            // FileManage.writeRacerToTextFile(mainRacerGUI, racerTable);
            FileManage.writeRacerToXmlFile(racerTable);
        }
    }

    /**
     * Сlass for implementing a add button listener
     */
    private static class AddEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            mainRacerGUI.setEnabled(false);
            addRacerWindow.addRacerGUI.setVisible(true);
        }
    }

    /**
     * Сlass for implementing a delete button listener
     */
    private static class DeleteEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            String message = "Нажата кнопка удаления из таблицы";
            JOptionPane.showMessageDialog(null, message, "Успешное удаление", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private static class ReportEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            CreateReport.printReport(racerTable);
        }
    }

    /**
     * Сlass for implementing a search button listener
     */
    private static class SearchEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                String teamInputText = searchTeamField.getText();
                String nameInputText = searchNameField.getText();
                if (teamInputText.equals("Команда") & nameInputText.equals("Имя гонщика"))
                    throw new EmptySearchInputException();
                else {
                    String message = "Поиск успешно выполнен!";
                    JOptionPane.showMessageDialog(null, message, "Успешный поиск", JOptionPane.PLAIN_MESSAGE);
                }
            } catch (EmptySearchInputException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Ошибка поиска",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

}
