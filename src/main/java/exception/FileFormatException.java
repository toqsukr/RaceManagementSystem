package exception;

import java.io.IOException;

public class FileFormatException extends IOException {
    /***
     * FileFormatException constructor
     */
    public FileFormatException(String msg) {
        super(msg);
    }
}