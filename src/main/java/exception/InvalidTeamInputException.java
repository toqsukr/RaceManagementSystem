package exception;

public class InvalidTeamInputException extends Exception {
    /***
     * InvalidTeamInputException constructor
     */
    public InvalidTeamInputException() {
        super("Некорректно введено название команды!");
    }
}
