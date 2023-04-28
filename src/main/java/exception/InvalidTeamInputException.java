package exception;

public class InvalidTeamInputException extends InvalidInputException {
    /***
     * InvalidTeamInputException constructor
     */
    public InvalidTeamInputException() {
        super("Некорректное название команды!");
    }
}
