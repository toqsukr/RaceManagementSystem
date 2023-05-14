package exception;

import java.io.IOException;

public class IdenticalDataException extends IOException {
    /***
     * IdenticalDataException constructor
     */
    public IdenticalDataException(String msg) {
        super(msg);
    }
}