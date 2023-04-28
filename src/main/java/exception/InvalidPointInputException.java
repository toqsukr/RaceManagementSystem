package exception;

public class InvalidPointInputException extends InvalidInputException {
    /***
     * InvalidPointInputException constructor
     */
    public InvalidPointInputException() {
        super("Некорректные очки гонщика!");
    }
}
