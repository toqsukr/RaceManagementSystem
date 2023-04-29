package exception;

public class InvalidAgeInputException extends Exception {
    /***
     * InvalidAgeInputException constructor
     */
    public InvalidAgeInputException() {
        super("Некорректный введён возраст гонщика!");
    }

    public InvalidAgeInputException(int strNumber) {
        super("Некорректный введён возраст гонщика в строке №" + strNumber + " !");
    }
}
