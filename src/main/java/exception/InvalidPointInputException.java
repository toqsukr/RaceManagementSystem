package exception;

public class InvalidPointInputException extends Exception {
    /***
     * InvalidPointInputException constructor
     */
    public InvalidPointInputException() {
        super("Некорректно введены очки гонщика!");
    }
}
