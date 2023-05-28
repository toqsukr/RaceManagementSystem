package application.graphic;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import application.graphic.buttons.ToolbarButton;
import application.graphic.interfaces.CallbackInterface;
import util.CreateReport;
import util.Logging;

public class BaseGUI extends JFrame {

    /***
     * The logger variable
     */
    public Logger logger;

    private LoggerContext context;

    public BaseGUI() {
        this.context = Logging.getContext();
        try {
            logger = createLoggerAndStart(this.context, Logging.getConfiguration(this.getClass()));
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Start logging " + this.getClass().getName());
    }

    public void setGUIIcon(String path) {
        URL icon = this.getClass().getClassLoader().getResource(path);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(icon));
    }

    public void sizeInit(JFrame cl) {
        cl.setBounds(200, 150, 800, 600);
        cl.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        cl.setResizable(false);
    }

    /***
     * The function starts logging
     * 
     * @param context       the logger variable
     * @param configuration params of logging
     * @throws IOException checks whether there are input/output errors
     */
    public Logger createLoggerAndStart(LoggerContext context, Configuration configuration) throws IOException {
        context.start(configuration);
        return this.context.getLogger("com");
    }

    /***
     * The function stops logging
     * 
     * @param context the logger variable
     */
    public void stopLogging() {
        logger.log(Level.INFO, "Stop logging " + super.getClass().getName());
        this.context.close();
    }

    public ToolbarButton getReportBtn(DefaultTableModel targetTable, JFrame frame, String title, float[] widths,
            String[] columns) {
        return new ToolbarButton(
                "Сформировать отчет",
                "img/report.png",
                new CallbackInterface() {
                    @Override
                    public void onEvent() {
                        try {
                            URL boldFontPath = this.getClass().getClassLoader()
                                    .getResource("fonts/DejaVuSans/DejaVuSans.ttf");
                            CreateReport.printReport(targetTable, frame, title,
                                    widths,
                                    columns,
                                    boldFontPath);
                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(frame, exception.getMessage(),
                                    "Ошибка формирования отчета",
                                    JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                });
    }

    public static float[] getWidths(String[] columns) {
        float[] ar = new float[columns.length];
        for (int i = 0; i < columns.length; i++) {
            ar[i] = 1f;
        }
        return ar;
    }
}
