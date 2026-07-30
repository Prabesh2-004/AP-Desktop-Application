package com.project.apdesktopapplication.exceptions;

/**
 * Thrown when a user attempts an action their role does not permit, for example
 * a student trying to approve a booking, or any user trying to cancel a booking
 * that is not their own. This is the server-side half of role-based access
 * control: even if a button were somehow reachable in the GUI, the service layer
 * still refuses the operation.
 */
public class UnauthorizedAccessException extends BookingException {
    private static final long serialVersionUID = 1L;
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
