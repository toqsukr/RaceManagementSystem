package exception;

public class InvalidTrackNameInputException extends InvalidDataException {
    /***
     * InvalidTrackNameInputException constructor
     */
    public InvalidTrackNameInputException() {
        super("Некорректно введено название трассы!");
    }

    public InvalidTrackNameInputException(int strNumber) {
        super("Некорректно введено название трассы в строке №" + strNumber + " !");
    }
}
