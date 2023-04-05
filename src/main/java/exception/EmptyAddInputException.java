package exception;

/***
 * Exception controls whether data is entered when the button is clicked.
 */
public class EmptyAddInputException extends Exception {
    /***
     * EmptyInputException constructor
     */
    public EmptyAddInputException() {
        super("Для добавления записи заполните все поля!");
    }
}