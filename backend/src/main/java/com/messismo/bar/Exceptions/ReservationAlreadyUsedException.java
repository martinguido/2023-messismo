package com.messismo.bar.Exceptions;

public class ReservationAlreadyUsedException extends Exception{
    public ReservationAlreadyUsedException() {
        super();
    }

    public ReservationAlreadyUsedException(String message) {
        super(message);
    }

    public ReservationAlreadyUsedException(String message, Throwable cause) {
        super(message, cause);
    }

}
