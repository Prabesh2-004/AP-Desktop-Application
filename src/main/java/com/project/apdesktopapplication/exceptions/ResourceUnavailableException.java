package com.project.apdesktopapplication.exceptions;

/**
 * Thrown when a user tries to book a resource that cannot accept the request,
 * either because the resource is under maintenance / already booked, or because
 * the requested time slot overlaps an existing approved or pending booking.
 * This is what enforces the "no double-booking" rule from the specification.
 */
public class ResourceUnavailableException extends BookingException {
    private static final long serialVersionUID = 1L;
    public ResourceUnavailableException(String message) {
        super(message);
    }
}
