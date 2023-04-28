package exception;

public class InvalidNameInputException extends InvalidInputException {
    /***
     * InvalidNameInputException constructor
     */
    public InvalidNameInputException() {
        super("Некорректное имя гонщика!");
    }
}
