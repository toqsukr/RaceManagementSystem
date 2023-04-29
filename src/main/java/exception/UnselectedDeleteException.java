package exception;

/***
 * Exception controls whether an entry is selected when the button is pressed.
 */
public class UnselectedDeleteException extends Exception {
    /***
     * UnselectedDeleteException constructor
     */
    public UnselectedDeleteException() {
        super("Выберите запись для удаления!");
    }
}
