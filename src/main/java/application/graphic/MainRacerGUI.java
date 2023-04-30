package application.graphic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import org.codehaus.groovy.syntax.ReadException;

import com.mysql.cj.exceptions.DataReadException;

import exception.EmptySearchInputException;
import exception.InvalidAgeInputException;
import exception.InvalidNameInputException;
import exception.InvalidPointInputException;
import exception.InvalidTeamInputException;
import exception.UnselectedDeleteException;
import exception.NothingDataException;
import race.system.Racer;
import util.CreateReport;
import util.FileManage;
import util.Validation;

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

    public void show() {
        mainRacerGUI.addWindowListener((WindowListener) new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (racers.getSelectedRow() != -1)
                        racers.getCellEditor(racers.getSelectedRow(), racers.getSelectedColumn()).stopCellEditing();
                    checkEditedData();
                    saveBeforeClose("Сохранить изменения?\nПосле закрытия окна\nнесохраненные данные будут утеряны!");
                    setConfirmbarUnvisible();
                    if (editingPermit == true)
                        changeEditingPermit();
                    clearTable(racerTable);
                    mainRacerGUI.dispose();
                } catch (Exception exception) {
                    int confirm = JOptionPane.showConfirmDialog(mainRacerGUI,
                            "Данные содержат ошибку и не могут быть сохранены!\nЗакрыть окно?",
                            "Предупреждение",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (confirm == JOptionPane.OK_OPTION) {
                        setConfirmbarUnvisible();
                        if (editingPermit == true)
                            changeEditingPermit();
                        clearTable(racerTable);
                        mainRacerGUI.dispose();
                    }
                }
            }
        });
        mainRacerGUI.setBounds(200, 150, 800, 600);
        mainRacerGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainRacerGUI.setResizable(false);
        mainRacerGUI.setIconImage(Toolkit.getDefaultToolkit().getImage("etu/src/img/favicon.png"));
        addRacerWindow.show();

        toolBar.setFloatable(false);
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

        // FlatSVGIcon svgIcon = new FlatSVGIcon("etu/src/img/close.svg", 50, 50);
        // fileBtn.setIcon(svgIcon);

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

    private static void saveBeforeClose(String message) {
        if (racerTable.getRowCount() > 0) {
            int result = JOptionPane.showConfirmDialog(mainRacerGUI,
                    message,
                    "Подтверждение действия",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                saveBtn.doClick();
            }
        }
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

    private void checkDeleteSelect() throws UnselectedDeleteException {
        if (racers.getSelectedRow() == -1)
            throw new UnselectedDeleteException();
    }

    private static void checkEditedData() throws InvalidNameInputException, InvalidAgeInputException,
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
                    if (load.getFile().endsWith("txt"))
                        FileManage.readRacerFromTextFile(racerTable, filename);
                    else
                        FileManage.readRacerFromXmlFile(racerTable, filename);
                }

            } catch (FileNotFoundException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Файл не найден!",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (ReadException exception) {
                clearTable(racerTable);
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка чтения файла",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка открытия файла",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    private void checkFileFormat(FileDialog file) throws DataReadException {
        if (!file.getFile().endsWith(".txt") && !file.getFile().endsWith(".xml"))
            throw new DataReadException("Некорректный формат файла!\nВыберите файл формата .txt или .xml!");
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
                checkEmptyData("Данные для редактирования не найдены!");
                if (racers.getSelectedRow() != -1)
                    racers.getCellEditor(racers.getSelectedRow(), racers.getSelectedColumn()).stopCellEditing();
                if (!isEqualTable(racerTable, previousRacerTable)) {
                    checkEditedData();
                    int result = JOptionPane.showConfirmDialog(mainRacerGUI, "Сохранить изменения?",
                            "Подтверждение действия",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        setConfirmbarUnvisible();
                        changeEditingPermit();
                    }
                } else {
                    setConfirmbarUnvisible();
                    changeEditingPermit();
                }

            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidNameInputException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidAgeInputException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTeamInputException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidPointInputException exception) {
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
            try {
                checkEmptyData("Данные для редактирования не найдены!");
                if (racers.getSelectedRow() != -1)
                    racers.getCellEditor(racers.getSelectedRow(), racers.getSelectedColumn()).stopCellEditing();
                copyTable(previousRacerTable, racerTable);
                changeEditingPermit();
                setConfirmbarUnvisible();
            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
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
                checkEmptyData("Данные для редактирования не найдены!");
                copyTable(racerTable, previousRacerTable);
                changeEditingPermit();
                setConfirmbarVisible();

            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

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

    private void checkEmptyData(String msg) throws NothingDataException {
        if (racerTable.getRowCount() == 0)
            throw new NothingDataException(msg);
    }

    public static void addRacer(Racer racer) {
        racerTable.addRow(new String[] { racer.getName(), racer.getAge().toString(), racer.getTeam(),
                racer.getPoints().toString() });
    }

    private boolean getEditingPermit() {
        return editingPermit;
    }

    private void changeEditingPermit() {

        editingPermit = !editingPermit;
    }

    public static void setInput(JTextField input, String text) {
        input.setText(text);
    }

    public static void setAddRacerVisible(boolean value) {
        addRacerWindow.addRacerGUI.setVisible(value);
    }

    public static void setMainRacerEnable(boolean value) {
        mainRacerGUI.setEnabled(value);
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
    private class SaveEventListener implements ActionListener {

        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                checkEmptyData("Данные для сохранения не найдены!");
                FileDialog save = new FileDialog(mainRacerGUI, "Сохранение данных", FileDialog.SAVE);
                save.setFile("data.xml");
                save.setVisible(true);
                if (save.getFile() != null) {
                    String filename = save.getDirectory() + save.getFile();
                    if (!filename.endsWith(".xml") && !filename.endsWith(".txt")) {
                        filename += ".xml";
                    }
                    if (filename.endsWith("txt"))
                        FileManage.writeRacerToTextFile(racerTable, filename);
                    else
                        FileManage.writeRacerToXmlFile(racerTable, filename);
                }
            } catch (DataReadException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка сохранения файла",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (NothingDataException exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка редактирования",
                        JOptionPane.PLAIN_MESSAGE);
            }
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
            setMainRacerEnable(false);
            setAddRacerVisible(true);
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
                checkEmptyData("Данные для удаления не найдены!");
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
                                racerTable.removeRow(racers.getSelectedRows()[i]);
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

    private static class ReportEventListener implements ActionListener {
        /***
         *
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            try {
                CreateReport.printReport(racerTable, mainRacerGUI);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка формирования отчета",
                        JOptionPane.PLAIN_MESSAGE);
            }
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
                JOptionPane.showMessageDialog(mainRacerGUI, exception.getMessage(), "Ошибка поиска",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    public void setVisible(boolean value) {
        mainRacerGUI.setVisible(value);
    }

}
