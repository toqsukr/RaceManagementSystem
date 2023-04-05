package exception;

/***
 * Exception controls whether an entry is selected when the button is pressed.
 */
public class UnselectedEditException extends Exception {
    /***
     * UnselectedEditException constructor
     */
    public UnselectedEditException() {
        super("Выберите запись для редактирования!");
    }
}
