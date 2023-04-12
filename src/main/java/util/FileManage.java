package util;

import java.awt.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;


/***
 * Auxiliary class for working with files
 */
public class FileManage {
    /***
     * Reading data from a file and entering it into a table
     * @param parent jframe type object that opens the file
     * @param table the table in which the data will be entered
     */
    public static void readRacerFromFile(JFrame parent, DefaultTableModel table) {
        FileDialog load = new FileDialog(parent, "Загрузка данных", FileDialog.LOAD);
        load.setFile("*.txt");
        load.setVisible(true);
        String fileName = load.getDirectory() + load.getFile();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            int rows = table.getRowCount();
            for (int i = 0; i < rows; i++) table.removeRow(0); // Очистка таблицы
            String racer;
            do {
                racer = reader.readLine();
                if(racer != null)
                {
                    String age = reader.readLine();
                    String team = reader.readLine();
                    String points = reader.readLine();
                    table.addRow(new String[]{racer, age, team, points}); // Запись строки в таблицу
                }
            } while(racer != null);
            reader.close();
        } catch (FileNotFoundException e) {e.printStackTrace();} // файл не найден
        catch (IOException e) {e.printStackTrace();}
    }

    /***
     * Writing data from a table to a file
     * @param parent jframe type object that opens the file
     * @param table the table in which the data will be entered
     */
    public static void writeRacerToFile(JFrame parent, DefaultTableModel table) {
        FileDialog save = new FileDialog(parent, "Сохранение данных", FileDialog.SAVE);
        save.setFile("*.txt");
        save.setVisible(true);
        String fileName = save.getDirectory() + save.getFile();
        try {
            BufferedWriter writer = new BufferedWriter (new FileWriter(fileName));
            for (int i = 0; i < table.getRowCount(); i++) // Для всех строк
                for (int j = 0; j < table.getColumnCount(); j++) // Для всех столбцов
                {writer.write ((String) table.getValueAt(i, j)); // Записать значение из ячейки
                    writer.write("\n"); // Записать символ перевода каретки
                }
            writer.close();
        }
        catch(IOException e) // Ошибка записи в файл
        { e.printStackTrace(); }
    }
}