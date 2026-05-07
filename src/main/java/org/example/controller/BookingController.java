package org.example.controller;

import org.example.Model.Booking;
import org.example.Service.BookingService;

import java.sql.SQLException;
import java.util.List;

public class BookingController {
    private BookingService bookingService;

    public BookingController() {
        this.bookingService = new BookingService();
    }

    public void book(Booking b) throws SQLException {
        bookingService.book(b);
    }

    public Booking getById(int idBooking) throws SQLException {
        return bookingService.getById(idBooking);
    }

    public List<Booking> getByEleve(int idEleve) throws SQLException {
        return bookingService.getByEleve(idEleve);
    }

    public List<Booking> getAll() throws SQLException {
        return bookingService.getAll();
    }

    public void update(Booking b) throws SQLException {
        bookingService.update(b);
    }

    public void confirmPayment(int idBooking) throws SQLException {
        bookingService.confirmPayment(idBooking);
    }

    public void cancel(int idBooking) throws SQLException {
        bookingService.cancel(idBooking);
    }

    public List<Booking> getBookingsBySession(int idSession) throws SQLException {
        return bookingService.getBookingsBySession(idSession);
    }

    public boolean hasAlreadyBooked(int idEleve, int idSession) throws SQLException {
        return bookingService.hasAlreadyBooked(idEleve, idSession);
    }

    public void bookSafe(Booking b) throws SQLException {
        bookingService.bookSafe(b);
    }

    public int getNombreReservations(int idSession) throws SQLException {
        return bookingService.getNombreReservations(idSession);
    }
}
