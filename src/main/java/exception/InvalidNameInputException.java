package exception;

public class InvalidNameInputException extends Exception {
    /***
     * InvalidNameInputException constructor
     */
    public InvalidNameInputException() {
        super("Некорректно введено имя гонщика!");
    }
}
