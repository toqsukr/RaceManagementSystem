package util;

import java.awt.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;

/***
 * Auxiliary class for working with files
 */
public class FileManage {
    /***
     * Reading data from a file and entering it into a table
     * 
     * @param parent jframe type object that opens the file
     * @param table  the table in which the data will be entered
     */
    public static void readRacerFromTextFile(JFrame parent, DefaultTableModel table) {
        FileDialog load = new FileDialog(parent, "Загрузка данных", FileDialog.LOAD);
        load.setFile("*.txt");
        load.setVisible(true);
        String fileName = load.getDirectory() + load.getFile();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            int rows = table.getRowCount();
            for (int i = 0; i < rows; i++)
                table.removeRow(0); // Очистка таблицы
            String racer;
            do {
                racer = reader.readLine();
                if (racer != null) {
                    String age = reader.readLine();
                    String team = reader.readLine();
                    String points = reader.readLine();
                    table.addRow(new String[] { racer, age, team, points }); // Запись строки в таблицу
                }
            } while (racer != null);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } // файл не найден
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Writing data from a table to a file
     * 
     * @param parent jframe type object that opens the file
     * @param table  the table in which the data will be entered
     */
    public static void writeRacerToTextFile(JFrame parent, DefaultTableModel table) {
        FileDialog save = new FileDialog(parent, "Сохранение данных", FileDialog.SAVE);
        save.setFile("*.txt");
        save.setVisible(true);
        String fileName = save.getDirectory() + save.getFile();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < table.getRowCount(); i++) // Для всех строк
                for (int j = 0; j < table.getColumnCount(); j++) // Для всех столбцов
                {
                    writer.write((String) table.getValueAt(i, j)); // Записать значение из ячейки
                    writer.write("\n"); // Записать символ перевода каретки
                }
            writer.close();
        } catch (IOException e) // Ошибка записи в файл
        {
            e.printStackTrace();
        }
    }

    public static void readRacerFromXmlFile(DefaultTableModel table) {
        try {
            // Создание парсера документа
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Чтение документа из файла
            Document doc = dBuilder.parse(new File("data.xml"));
            // Нормализация документа
            doc.getDocumentElement().normalize();
            // Обработка ошибки парсера при чтении данных из XML-файла
            // Получение списка элементов с именем book
            NodeList nlRacers = doc.getElementsByTagName("racer");
            int rows = table.getRowCount();
            for (int i = 0; i < rows; i++)
                table.removeRow(0); // Очистка таблицы
            // Цикл просмотра списка элементов и запись данных в таблицу
            for (int temp = 0; temp < nlRacers.getLength(); temp++) {
                // Выбор очередного элемента списка
                Node elem = nlRacers.item(temp);
                // Получение списка атрибутов элемента
                NamedNodeMap attrs = elem.getAttributes();
                // Чтение атрибутов элемента
                String name = attrs.getNamedItem("name").getNodeValue();
                String age = attrs.getNamedItem("age").getNodeValue();
                String team = attrs.getNamedItem("team").getNodeValue();
                String points = attrs.getNamedItem("points").getNodeValue();
                // Запись данных в таблицу
                table.addRow(new String[] { name, age, team, points });
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeRacerToXmlFile(DefaultTableModel table) {
        try {
            // Создание парсера документа
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создание пустого документа
            Document doc = builder.newDocument();
            // Создание корневого элемента racerlist и добавление его в документ
            Node racerlist = doc.createElement("racerlist");
            doc.appendChild(racerlist);
            // Создание дочерних элементов racer и присвоение значений атрибутам
            for (int i = 0; i < table.getRowCount(); i++) {
                Element racer = doc.createElement("racer");
                racerlist.appendChild(racer);
                racer.setAttribute("name", (String) table.getValueAt(i, 0));
                racer.setAttribute("age", (String) table.getValueAt(i, 1));
                racer.setAttribute("team", (String) table.getValueAt(i, 2));
                racer.setAttribute("points", (String) table.getValueAt(i, 3));
            }
            // Создание преобразователя документа
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            // Создание файла с именем data.xml для записи документа
            java.io.FileWriter fw = new FileWriter("data.xml");
            // Запись документа в файл
            trans.transform(new DOMSource(doc), new StreamResult(fw));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}