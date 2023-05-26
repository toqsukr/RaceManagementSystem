package application.graphic.eventListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import util.CreateReport;

/**
 * Сlass for implementing a report button listener
 */
public class ReportEventListener implements ActionListener {
    private DefaultTableModel table;
    private JFrame window;
    private String title;
    private float[] widths;
    private String[] headers;
    private String fontPath;

    /***
     *
     * @param table    the table print
     * @param window   parent window
     * @param title    title of report
     * @param widths   array of columnWidths in report
     * @param headers  the table's names of columns in report
     * @param fontPath the path to font location for use in report
     */
    public ReportEventListener(DefaultTableModel table, JFrame window, String title, float[] widths, String[] headers,
            String fontPath) {
        this.table = table;
        this.window = window;
        this.title = title;
        this.widths = widths;
        this.headers = headers;
        this.fontPath = fontPath;
    }

    /***
     *
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        try {
            URL boldFontPath = this.getClass().getClassLoader()
                    .getResource(fontPath);

            CreateReport.printReport(
                    table,
                    window,
                    title + "\n\n\n\n\n",
                    widths,
                    Stream.of(headers).map(x -> "\n" + x + "\n").toArray(String[]::new),
                    boldFontPath);

        } catch (Exception exception) {
            JOptionPane.showMessageDialog(window, exception.getMessage(), "Ошибка формирования отчета",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }
}
