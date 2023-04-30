package exception;

/***
 * Exception controls whether an entry is selected when the button is pressed.
 */
public class NothingDataException extends Exception {
    /***
     * NothingDataException constructor
     */
    public NothingDataException(String msg) {
        super(msg);
    }
}
