package util;

import java.io.*;

import javax.swing.table.DefaultTableModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import application.graphic.MainRacerGUI;
import exception.ReadFileException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.ParserConfigurationException;

/***
 * Auxiliary class for working with files
 */
public class FileManage {
    /***
     * Reading data from a file and entering it into a table
     * \
     * 
     * @param table    the table in which the data will be entered
     * @param filename path of the file is to be opened
     */
    public static void readRacerFromTextFile(DefaultTableModel table, String fileName)
            throws FileNotFoundException, IOException, ReadFileException {
        if (!new File(fileName).exists())
            throw new FileNotFoundException("Файл " + fileName + " не найден!");
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        int rows = table.getRowCount();
        String id;
        id = reader.readLine();
        if (id != null) {
            for (int i = 0; i < rows; i++)
                table.removeRow(0); // Очистка таблицы
        } else {
            reader.close();
            throw new ReadFileException("Ошибка чтения файла!\nПроверьте корректность данных!");
        }
        do {
            if (id != null) {
                String name = reader.readLine();
                String age = reader.readLine();
                String team = reader.readLine();
                String points = reader.readLine();
                if (!Validation.isValidID(id) || !Validation.isValidName(name) || !Validation.isValidAge(age)
                        || !Validation.isValidTeam(team)
                        || !Validation.isValidPoint(points)) {
                    reader.close();
                    throw new ReadFileException("Ошибка чтения файла!\nПроверьте корректность данных!");
                }
                table.addRow(new String[] { id, name, age, team, points }); // Запись строки в таблицу
            }
            id = reader.readLine();

        } while (id != null);
        reader.close();
    }

    /***
     * Writing data from a table to a file
     * 
     * @param parent   jframe type object that opens the file
     * @param filename path of the file is to be opened
     * 
     */
    public static void writeRacerToTextFile(DefaultTableModel table, String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
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

    public static void readRacerFromXmlFile(DefaultTableModel table, String filename)
            throws FileNotFoundException, ReadFileException, SAXException, ParserConfigurationException, IOException {
        if (!new File(filename).exists())
            throw new FileNotFoundException("Файл " + filename + " не найден!");
        // Создание парсера документа
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        // Чтение документа из файла
        Document doc = dBuilder.parse(new File(filename));
        if (doc == null)
            throw new ReadFileException("Ошибка чтения файла!\nПроверьте корректность данных!");
        // Нормализация документа
        doc.getDocumentElement().normalize();
        // Обработка ошибки парсера при чтении данных из XML-файла
        // Получение списка элементов с именем book
        NodeList nlRacers = doc.getElementsByTagName("racer");
        int rows = table.getRowCount();
        for (int i = 0; i < rows; i++)
            table.removeRow(0); // Очистка таблицы
        // Цикл просмотра списка элементов и запись данных в таблицу
        if (nlRacers.getLength() == 0)
            throw new ReadFileException("Ошибка чтения файла!\nПроверьте корректность данных!");
        for (int temp = 0; temp < nlRacers.getLength(); temp++) {
            // Выбор очередного элемента списка
            Node elem = nlRacers.item(temp);
            // Получение списка атрибутов элемента
            NamedNodeMap attrs = elem.getAttributes();
            // Чтение атрибутов элемента
            if (attrs.getNamedItem("id") == null || attrs.getNamedItem("name") == null
                    || attrs.getNamedItem("age") == null
                    || attrs.getNamedItem("team") == null || attrs.getNamedItem("points") == null)
                throw new ReadFileException("Ошибка чтения файла!\nПроверьте корректность данных!");
            String id = attrs.getNamedItem("id").getNodeValue();
            String name = attrs.getNamedItem("name").getNodeValue();
            String age = attrs.getNamedItem("age").getNodeValue();
            String team = attrs.getNamedItem("team").getNodeValue();
            String points = attrs.getNamedItem("points").getNodeValue();
            // Запись данных в таблицу
            if (!MainRacerGUI.isAtTable(table, id, name, age, team, points))
                table.addRow(new String[] { id, name, age, team, points });
        }
    }

    public static void writeRacerToXmlFile(DefaultTableModel table, String filename) {
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
                racer.setAttribute("id", (String) table.getValueAt(i, 0));
                racer.setAttribute("name", (String) table.getValueAt(i, 1));
                racer.setAttribute("age", (String) table.getValueAt(i, 2));
                racer.setAttribute("team", (String) table.getValueAt(i, 3));
                racer.setAttribute("points", (String) table.getValueAt(i, 4));
            }
            // Создание преобразователя документа
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            // Создание файла с именем data.xml для записи документа
            java.io.FileWriter fw = new FileWriter(filename);
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