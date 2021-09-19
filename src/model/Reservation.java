package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Reservation {

    private Customer customer;
    private IRoom room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public Reservation(Customer customer, IRoom room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    @Override
    public String toString() {
        return "Name:\n" + customer.getFirstName() + " " + customer.getLastName() + "\n" + "Room:\n" + room + "starts on " + checkInDate.format(dateTimeFormatter) + "\nends on " + checkOutDate.format(dateTimeFormatter) + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Reservation) {
            Reservation that = (Reservation) o;
            return Objects.equals(customer, that.customer)
                    && Objects.equals(room, that.room)
                    && Objects.equals(checkInDate, that.checkInDate)
                    && Objects.equals(checkOutDate, that.checkOutDate);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, room, checkInDate, checkOutDate);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public IRoom getRoom() {
        return room;
    }

    public void setRoom(IRoom room) {
        this.room = room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
}
