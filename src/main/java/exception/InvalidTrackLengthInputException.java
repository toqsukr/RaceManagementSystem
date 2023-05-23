package exception;

public class InvalidTrackLengthInputException extends InvalidDataException {
    /***
     * InvalidTrackLengthInputException constructor
     */
    public InvalidTrackLengthInputException() {
        super("Некорректно введена длина трассы!");
    }

    public InvalidTrackLengthInputException(int strNumber) {
        super("Некорректно введена длина трассы в строке №" + strNumber + " !");
    }
}
