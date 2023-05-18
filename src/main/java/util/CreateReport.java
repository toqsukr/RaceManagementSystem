package util;

import java.awt.FileDialog;
import java.io.FileOutputStream;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/***
 * This class generate pdf report from data
 */

public class CreateReport {
    /***
     * 
     * @param table table containing data
     */
    public static void printReport(DefaultTableModel table, JFrame parent, String title, float[] columnWidths,
            String[] headersPdfExport, URL fontPath) throws Exception {

        if (table.getRowCount() == 0)
            throw new Exception("Нет данных для отчета!");
        FileDialog save = new FileDialog(parent, "Сохранение отчета", FileDialog.SAVE);
        String fileTitle;
        if (parent.getTitle().contains("гонщиков"))
            fileTitle = "racers";
        else
            fileTitle = "teams";
        save.setFile(fileTitle + "_report.pdf");
        save.setVisible(true);
        if (save.getFile() != null) {
            String filename = save.getDirectory() + save.getFile();
            // Append .pdf extension if necessary
            if (!filename.endsWith(".pdf")) {
                filename += ".pdf";
            }
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            Paragraph head = new Paragraph(
                    new Phrase(4f, title,
                            FontFactory.getFont(fontPath.toString(), "cp1251", 18)));
            head.setAlignment(Element.ALIGN_CENTER);
            document.add(head);
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());

            // Create font for table headers
            Font headerFont = FontFactory.getFont(fontPath.toString(), "cp1251", 12);

            // Set column headers
            for (int i = 0; i < table.getColumnCount(); i++) {
                PdfPCell header = new PdfPCell(new Phrase(headersPdfExport[i], headerFont));
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setBorderWidth(2);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                // Give more weight to the first row
                pdfTable.addCell(header);
            }

            // Create font for table data
            Font dataFont = FontFactory.getFont(fontPath.toString(), "cp1251", 10);

            pdfTable.setWidths(columnWidths);

            // Add table data
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    PdfPCell data = new PdfPCell(new Phrase(table.getValueAt(i, j).toString(), dataFont));
                    data.setMinimumHeight(20);
                    data.setBackgroundColor(BaseColor.WHITE);
                    data.setBorderWidth(1);
                    data.setHorizontalAlignment(Element.ALIGN_LEFT);
                    pdfTable.addCell(data);
                }
            }
            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(parent, "Данные сохранены в " + filename);

        }
    }
}
