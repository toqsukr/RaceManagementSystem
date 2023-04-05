package exception;

/***
 * Exception controls whether data is entered when the button is clicked.
 */
public class EmptySearchInputException extends Exception {
    /***
     * EmptyInputException constructor
     */
    public EmptySearchInputException() {
        super("Введите название команды или имя гонщика!");
    }
}