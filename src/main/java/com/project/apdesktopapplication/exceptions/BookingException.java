package com.project.apdesktopapplication.exceptions;

/**
 * Root of the application's custom, checked exception hierarchy.
 * <p>
 * Every domain-specific failure in the booking workflow extends this class,
 * which in turn extends {@link java.lang.Exception}. Because the subclasses
 * share a common ancestor, a controller can catch the whole family with a
 * single {@code catch (BookingException e)} block, or catch an individual
 * subtype when it needs to react differently. Each exception carries a
 * human-readable message that the GUI layer can display directly to the user.
 */
public class BookingException extends Exception {
    private static final long serialVersionUID = 1L;
    public BookingException(String message) {
        super(message);
    }
}
