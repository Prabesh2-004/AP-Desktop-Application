package com.project.apdesktopapplication.exceptions;

/**
 * Thrown when the requested booking period is invalid: the end time is not
 * after the start time, the slot is in the past, or the duration exceeds the
 * maximum number of hours allowed for that particular resource type (the limit
 * is supplied polymorphically by {@code Resource.getMaxBookingHours()}).
 */
public class InvalidBookingDurationException extends BookingException {
    private static final long serialVersionUID = 1L;
    public InvalidBookingDurationException(String message) {
        super(message);
    }
}
