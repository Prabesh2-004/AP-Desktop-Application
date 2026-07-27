package com.project.apdesktopapplication.services;

import com.project.apdesktopapplication.models.Booking;
import com.project.apdesktopapplication.utils.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService {
    private List<Booking> bookings;
    private static BookingService instance;

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
        bookings = new ArrayList<>();
        List<String> lines = DataManager.readBookings();
        for (String line : lines) {
            bookings.add(Booking.fromString(line));
        }
    }

    public void saveBookings() {
        List<String> lines = new ArrayList<>();
        for (Booking booking : bookings) {
            lines.add(booking.toString());
        }
        DataManager.writeBookings(lines);
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public Booking getBookingById(String bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }
        return null;
    }

    public List<Booking> getBookingsByUserId(String userId) {
        return bookings.stream()
                .filter(b -> b.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByResourceId(String resourceId) {
        return bookings.stream()
                .filter(b -> b.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
    }

    public List<Booking> getPendingBookings() {
        return bookings.stream()
                .filter(b -> b.getStatus().equals("PENDING"))
                .collect(Collectors.toList());
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
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getBookingId().equals(booking.getBookingId())) {
                bookings.set(i, booking);
                saveBookings();
                return true;
            }
        }
        return false;
    }

    public boolean deleteBooking(String bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking != null) {
            bookings.remove(booking);
            saveBookings();
            return true;
        }
        return false;
    }

    public long getTotalBookings() {
        return bookings.size();
    }

    public long getPendingCount() {
        return bookings.stream().filter(b -> b.getStatus().equals("PENDING")).count();
    }

    public long getApprovedCount() {
        return bookings.stream().filter(b -> b.getStatus().equals("APPROVED")).count();
    }

    public long getRejectedCount() {
        return bookings.stream().filter(b -> b.getStatus().equals("REJECTED")).count();
    }
}