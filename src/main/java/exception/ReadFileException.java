package exception;

/***
 * Exception controls whether data is entered when the button is clicked.
 */
public class ReadFileException extends Exception {
    /***
     * ReadFileException constructor
     */
    public ReadFileException(String msg) {
        super(msg);
    }
}