package exception;

public class InvalidInputException extends Exception {
    /***
     * InvalidInputException constructor
     * 
     */
    public InvalidInputException() {
        super("Некорректный ввод!");
    }

    public InvalidInputException(String string) {
    }
}