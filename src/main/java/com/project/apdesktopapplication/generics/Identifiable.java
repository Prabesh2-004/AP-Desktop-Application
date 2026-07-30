package com.project.apdesktopapplication.generics;

/**
 * Contract for any domain object that owns a unique String identifier.
 * <p>
 * Implemented by {@code User}, {@code Resource} and {@code Booking}. It exists so
 * that the generic {@link ResourceManager} can look items up by id without ever
 * knowing their concrete type - the manager only needs to know that whatever it
 * stores "has an id".
 */
public interface Identifiable {
    String getId();
}
