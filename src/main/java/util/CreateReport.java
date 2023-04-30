package util;

import java.awt.FileDialog;
import java.io.FileOutputStream;

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
    public static void printReport(DefaultTableModel table, JFrame parent) throws Exception {

        if (table.getRowCount() == 0)
            throw new Exception("Нет данных для отчета!");
        FileDialog save = new FileDialog(parent, "Сохранение данных", FileDialog.SAVE);
        save.setFile("report_racer.pdf");
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
                    new Phrase(5f, "Laboratory work report\n\n\n\n\n",
                            FontFactory.getFont(FontFactory.COURIER, 18f)));
            head.setAlignment(Element.ALIGN_CENTER);
            document.add(head);
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());

            // Create font for table headers
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            String[] headersPdfExport = { "\nName\n\n", "\nAge", "\nTeam", "\nPoints" };

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
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

            // Set custom widths for each row
            float[] columnWidths = { 0.2f, 0.2f, 0.2f, 0.2f };
            pdfTable.setWidths(columnWidths);

            // Add table data
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    PdfPCell data = new PdfPCell(new Phrase(table.getValueAt(i, j).toString(), dataFont));
                    data.setBackgroundColor(BaseColor.WHITE);
                    data.setBorderWidth(1);
                    data.setHorizontalAlignment(Element.ALIGN_LEFT);
                    pdfTable.addCell(data);
                }
            }
            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(null, "Данные сохранены в " + filename);

        }
    }
}
