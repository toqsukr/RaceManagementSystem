package exception;

public class InvalidAgeInputException extends InvalidInputException {
    /***
     * InvalidAgeInputException constructor
     */
    public InvalidAgeInputException() {
        super("Некорректный возраст гонщика!");
    }
}
