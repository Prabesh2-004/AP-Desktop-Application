package com.project.apdesktopapplication.services;

import com.project.apdesktopapplication.exceptions.InvalidBookingDurationException;
import com.project.apdesktopapplication.exceptions.ResourceUnavailableException;
import com.project.apdesktopapplication.exceptions.UnauthorizedAccessException;
import com.project.apdesktopapplication.generics.ResourceManager;
import com.project.apdesktopapplication.models.Booking;
import com.project.apdesktopapplication.models.Resource;
import com.project.apdesktopapplication.models.User;
import com.project.apdesktopapplication.utils.DataManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Business layer for bookings. Backed by a generic ResourceManager<Booking>
 * and persisted to bookings.txt. Singleton.
 *
 * This class owns the booking rules. createBooking(), approveBooking() and
 * cancelBooking() validate every request and throw a specific custom exception
 * on failure, so the GUI never has to re-implement the rules - it just calls the
 * service and reacts to whichever exception (if any) comes back.
 */
public class BookingService {

    private final ResourceManager<Booking> bookings = new ResourceManager<>();
    private static BookingService instance;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private BookingService() {
        loadBookings();
    }

    public static BookingService getInstance() {
        if (instance == null) {
            instance = new BookingService();
        }
        return instance;
    }

    private void loadBookings() {
        List<String> lines = DataManager.readBookings();
        for (String line : lines) {
            bookings.add(Booking.fromString(line));
        }
    }

    public void saveBookings() {
        List<String> lines = new ArrayList<>();
        for (Booking booking : bookings.getAll()) {
            lines.add(booking.toString());
        }
        DataManager.writeBookings(lines);
    }

    // ---------------------------------------------------------------------
    //  Core business rules (throw custom exceptions)
    // ---------------------------------------------------------------------

    /**
     * Validates a booking request against every rule and, if it passes, creates
     * and persists the booking. The final status depends on the requester's role:
     * staff and admins are auto-approved (polymorphic canApproveBookings()),
     * students are left PENDING.
     *
     * @throws ResourceUnavailableException      resource missing, under maintenance, or slot clashes
     * @throws InvalidBookingDurationException   bad time range or over the type's hour limit
     */
    public Booking createBooking(User requester, Resource resource, String date,
                                 String start, String end)
            throws ResourceUnavailableException, InvalidBookingDurationException {

        if (resource == null || !"AVAILABLE".equals(resource.getStatus())) {
            throw new ResourceUnavailableException(
                    "This resource is currently not available for booking.");
        }

        LocalTime startTime;
        LocalTime endTime;
        try {
            startTime = LocalTime.parse(start);
            endTime = LocalTime.parse(end);
        } catch (Exception e) {
            throw new InvalidBookingDurationException("Please choose a valid start and end time.");
        }

        if (!endTime.isAfter(startTime)) {
            throw new InvalidBookingDurationException("End time must be after start time.");
        }

        // Polymorphism: each Resource subclass returns its own limit.
        long hours = Duration.between(startTime, endTime).toHours();
        if (hours > resource.getMaxBookingHours()) {
            throw new InvalidBookingDurationException(
                    resource.getType() + " bookings cannot exceed "
                            + resource.getMaxBookingHours() + " hours. " + resource.getBookingRules());
        }

        if (hasConflict(resource.getResourceId(), date, startTime, endTime)) {
            throw new ResourceUnavailableException(
                    "This time slot is already booked. Please pick a different time.");
        }

        String status = requester.canApproveBookings() ? "APPROVED" : "PENDING";
        Booking booking = new Booking(
                "BKG" + System.currentTimeMillis(),
                requester.getUserId(),
                resource.getResourceId(),
                date, start, end, status);
        addBooking(booking);

        if ("APPROVED".equals(status)) {
            resource.setStatus("BOOKED");
            ResourceService.getInstance().updateResource(resource);
        }
        return booking;
    }

    /**
     * Approves a pending booking. Only staff or admins may do this.
     * @throws UnauthorizedAccessException if the actor lacks approval rights
     */
    public void approveBooking(User approver, String bookingId) throws UnauthorizedAccessException {
        if (approver == null || !approver.canApproveBookings()) {
            throw new UnauthorizedAccessException("Only staff or administrators can approve bookings.");
        }
        Booking booking = getBookingById(bookingId);
        if (booking == null) return;
        booking.setStatus("APPROVED");
        updateBooking(booking);

        Resource resource = ResourceService.getInstance().getResourceById(booking.getResourceId());
        if (resource != null) {
            resource.setStatus("BOOKED");
            ResourceService.getInstance().updateResource(resource);
        }
    }

    /**
     * Rejects a pending booking. Only staff or admins may do this.
     * @throws UnauthorizedAccessException if the actor lacks approval rights
     */
    public void rejectBooking(User approver, String bookingId) throws UnauthorizedAccessException {
        if (approver == null || !approver.canApproveBookings()) {
            throw new UnauthorizedAccessException("Only staff or administrators can reject bookings.");
        }
        Booking booking = getBookingById(bookingId);
        if (booking == null) return;
        booking.setStatus("REJECTED");
        updateBooking(booking);
    }

    /**
     * Cancels a booking. A user may cancel only their own booking; admins
     * (canManageSystem()) may cancel anyone's.
     * @throws UnauthorizedAccessException if the actor may not cancel this booking
     */
    public void cancelBooking(User requester, String bookingId) throws UnauthorizedAccessException {
        Booking booking = getBookingById(bookingId);
        if (booking == null) return;
        boolean owner = requester != null && booking.getUserId().equals(requester.getUserId());
        boolean admin = requester != null && requester.canManageSystem();
        if (!owner && !admin) {
            throw new UnauthorizedAccessException("You can only cancel your own bookings.");
        }
        booking.setStatus("CANCELLED");
        updateBooking(booking);

        Resource resource = ResourceService.getInstance().getResourceById(booking.getResourceId());
        if (resource != null && "BOOKED".equals(resource.getStatus())) {
            resource.setStatus("AVAILABLE");
            ResourceService.getInstance().updateResource(resource);
        }
    }

    /**
     * Returns true if an APPROVED or PENDING booking already overlaps the given
     * slot on the same date for the same resource. This is the double-booking guard.
     */
    public boolean hasConflict(String resourceId, String date, LocalTime start, LocalTime end) {
        return bookings.getAll().stream()
                .filter(b -> b.getResourceId().equals(resourceId))
                .filter(b -> b.getStatus().equals("APPROVED") || b.getStatus().equals("PENDING"))
                .anyMatch(b -> {
                    try {
                        LocalDate bDate = LocalDate.parse(b.getDate(), DATE_FMT);
                        if (!bDate.equals(LocalDate.parse(date, DATE_FMT))) return false;
                        LocalTime bStart = LocalTime.parse(b.getStartTime());
                        LocalTime bEnd = LocalTime.parse(b.getEndTime());
                        // Overlap unless the new slot ends before this one starts or starts after it ends.
                        return !(end.compareTo(bStart) <= 0 || start.compareTo(bEnd) >= 0);
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    // ---------------------------------------------------------------------
    //  Basic CRUD / queries
    // ---------------------------------------------------------------------

    public List<Booking> getAllBookings() {
        return bookings.getAll();
    }

    public Booking getBookingById(String bookingId) {
        return bookings.getById(bookingId);
    }

    public List<Booking> getBookingsByUserId(String userId) {
        return bookings.findAll(b -> b.getUserId().equals(userId));
    }

    public List<Booking> getBookingsByResourceId(String resourceId) {
        return bookings.findAll(b -> b.getResourceId().equals(resourceId));
    }

    public List<Booking> getPendingBookings() {
        return bookings.findAll(b -> b.getStatus().equals("PENDING"));
    }

    public boolean addBooking(Booking booking) {
        if (booking.getBookingId() == null || booking.getBookingId().isEmpty()) {
            booking.setBookingId("BKG" + System.currentTimeMillis());
        }
        bookings.add(booking);
        saveBookings();
        return true;
    }

    public boolean updateBooking(Booking booking) {
        boolean ok = bookings.update(booking);
        if (ok) saveBookings();
        return ok;
    }

    public boolean deleteBooking(String bookingId) {
        boolean ok = bookings.removeById(bookingId);
        if (ok) saveBookings();
        return ok;
    }

    public long getTotalBookings() {
        return bookings.size();
    }

    public long getPendingCount() {
        return bookings.count(b -> b.getStatus().equals("PENDING"));
    }

    public long getApprovedCount() {
        return bookings.count(b -> b.getStatus().equals("APPROVED"));
    }

    public long getRejectedCount() {
        return bookings.count(b -> b.getStatus().equals("REJECTED"));
    }
}
