package com.messismo.bar.Exceptions;

public class AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException extends Exception{
    public AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException() {
        super();
    }

    public AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException(String message) {
        super(message);
    }

    public AlreadyHaveAReservationWithACapacityHigherThanSpecifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}
