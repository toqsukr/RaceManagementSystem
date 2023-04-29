package exception;

/***
 * Exception controls whether an entry is selected when the button is pressed.
 */
public class NothingDataException extends Exception {
    /***
     * NothingDataException constructor
     */
    public NothingDataException() {
        super("Данные для редактирования не найдены!");
    }
}
