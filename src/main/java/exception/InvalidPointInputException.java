package exception;

public class InvalidPointInputException extends Exception {
    /***
     * InvalidPointInputException constructor
     */
    public InvalidPointInputException() {
        super("Некорректно введены очки гонщика!");
    }

    public InvalidPointInputException(int strNumber) {
        super("Некорректно введены очки гонщика в строке №" + strNumber + " !");
    }
}
