package exception;

public class InvalidTimeException extends InvalidDataException {
    /***
     * InvalidTimeException constructor
     */
    public InvalidTimeException() {
        super("Некорректно введено личный результат (время финиша) гонщика!");
    }

    public InvalidTimeException(int strNumber) {
        super("Некорректно введен личный результат (время финиша) гонщика в строке №" + strNumber + " !");
    }
}
