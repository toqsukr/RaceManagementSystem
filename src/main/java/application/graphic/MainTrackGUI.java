package application.graphic;

import util.Validation;
import race.system.Competition;
import race.system.Racer;
import race.system.Score;
import race.system.Track;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import application.graphic.interfaces.CallbackInterface;
import application.graphic.buttons.ToolbarButton;
import database.TrackDao;
import exception.InvalidDataException;
import exception.InvalidTrackLengthInputException;
import exception.InvalidTrackNameInputException;
import exception.NothingDataException;
import exception.UnselectedDeleteException;

import javax.swing.DefaultCellEditor;
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
public class MainTrackGUI extends BaseGUI {
    private static JFrame mainTrackGUI = new JFrame("Список трасс");

    /**
     * This button adds new field into table
     */
    private final ToolbarButton addBtn = new ToolbarButton(
            "Добавить трассу",
            "img/add_track.png",
            new CallbackInterface() {
                @Override
                public void onEvent() {
                    logger.info("Opening window AddTrackGUI");
                    setMainTrackEnable(false);
                    addTrackWindow.setAddTrackVisibility(true);
                }
            });

    /**
     * This button deletes selected field
     */
    private final ToolbarButton deleteBtn = new ToolbarButton(
            "Удалить трассу",
            "img/delete_track.png",
            new CallbackInterface() {
                @Override
                public void onEvent() {
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
                                        String removingTrackName = tracks
                                                .getValueAt(tracks.getSelectedRows()[i], 0)
                                                .toString();
                                        String removingLength = tracks
                                                .getValueAt(tracks.getSelectedRows()[i], 1)
                                                .toString();
                                        trackTable.removeRow(tracks.getSelectedRows()[i]);
                                        Track removingTrack = isAtTrackList(allTracks,
                                                new Track(removingTrackName, Integer.parseInt(removingLength)));
                                        parentWindow.getMainRacerGUI().getTrackDao()
                                                .addFreeID(removingTrack.getTrackID());
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
                        JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(),
                                "Ошибка редактирования",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });

    /**
     * This button allows you to edit selected field
     */
    private final ToolbarButton editBtn = new ToolbarButton(
            "Редактировать запись",
            "img/edit.png",
            new CallbackInterface() {
                @Override
                public void onEvent() {
                    try {
                        MainRacerGUI.checkEmptyData("Данные для редактирования не найдены!", trackTable);
                        MainRacerGUI.copyTable(trackTable, previousTrackTable);
                        setEditingPermit(true);
                        setConfirmbarVisible();
                    } catch (NothingDataException exception) {
                        JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(),
                                "Ошибка редактирования",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });

    /**
     * This button forms team data report
     */
    private final ToolbarButton reportBtn = this.getReportBtn(
            trackTable,
            mainTrackGUI,
            "Список трасс\n\n\n\n\n",
            widths, columns);

    /**
     * This button exports data to the database
     */
    private final ToolbarButton toDataBaseBtn = new ToolbarButton(
            "Выгрузить в базу данных",
            "img/deploytodb.png",
            new CallbackInterface() {
                @Override
                public void onEvent() {
                    parentWindow.getMainRacerGUI().deployToDataBase();
                }
            });

    /**
     * This button imports data into table from database
     */
    private final ToolbarButton fromDataBaseBtn = new ToolbarButton(
            "Загрузить данные из базы данных",
            "img/downloadfromdb.png",
            new CallbackInterface() {
                @Override
                public void onEvent() {
                    parentWindow.getMainRacerGUI().downloadFromDataBase();
                }
            });

    /**
     * This button confirms changes made
     */
    private final ToolbarButton confirmBtn = new ToolbarButton(
            "Ок",
            "img/confirm.png",
            new CallbackInterface() {
                @Override
                public void onEvent() {
                    try {
                        if (tracks.getSelectedRow() != -1)
                            tracks.getCellEditor(tracks.getSelectedRow(), tracks.getSelectedColumn())
                                    .stopCellEditing();
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
                        JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(),
                                "Ошибка редактирования",
                                JOptionPane.PLAIN_MESSAGE);
                    } catch (InvalidTrackLengthInputException exception) {
                        logger.warn("Entered invalid track length while editing");
                        JOptionPane.showMessageDialog(mainTrackGUI, exception.getMessage(),
                                "Ошибка редактирования",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });

    /**
     * This button cancles changes made
     */
    private final ToolbarButton cancelBtn = new ToolbarButton(
            "Отмена",
            "img/cancel.png",
            new CallbackInterface() {
                @Override
                public void onEvent() {
                    if (tracks.getSelectedRow() != -1)
                        tracks.getCellEditor(tracks.getSelectedRow(), tracks.getSelectedColumn())
                                .stopCellEditing();
                    MainRacerGUI.copyTable(previousTrackTable, trackTable);
                    setEditingPermit(false);
                    setConfirmbarUnvisible();
                }
            });

    private JComboBox<String> comboRacer = new JComboBox<>();

    /**
     * This bar is used to store buttons
     */
    private static final JToolBar toolBar = new JToolBar();

    /**
     * Table column names
     */
    private static final String[] columns = { "Название трассы", "Длина трассы", "Призер" };

    private static final float[] widths = BaseGUI.getWidths(columns);

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

    private MainMenuGUI parentWindow;

    private AddTrackGUI addTrackWindow;

    private List<Track> allTracks;

    /***
     * The function creating mainTrackGUI
     */
    public MainTrackGUI(MainMenuGUI parent) {
        this.sizeInit(mainTrackGUI);

        parentWindow = parent;
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
                        closeWindow();
                    }
                } catch (InvalidDataException exception) {
                    int confirm = JOptionPane.showConfirmDialog(mainTrackGUI,
                            "Данные содержат ошибку и не могут быть сохранены!\nЗакрыть окно?",
                            "Предупреждение",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (confirm == JOptionPane.OK_OPTION) {
                        cancelBtn.doClick();
                        closeWindow();
                    }
                }
            }

            private void closeWindow() {
                stopLogging();
                mainTrackGUI.dispose();
            }
        });

        this.setGUIIcon("img/track.png");

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

        confirmBtn.setVisible(false);
        cancelBtn.setVisible(false);

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
    }

    public void setEditingPermit(boolean value) {
        editingPermit = value;
    }

    /***
     * The function make visible confirm bar while editing
     */
    private void setConfirmbarVisible() {
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
    private void setConfirmbarUnvisible() {
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
