package exception;

public class InvalidTeamInputException extends InvalidDataException {
    /***
     * InvalidTeamInputException constructor
     */
    public InvalidTeamInputException() {
        super("Некорректно введено название команды!");
    }

    public InvalidTeamInputException(int strNumber) {
        super("Некорректно введено название команды в строке №" + strNumber + " !");
    }
}
