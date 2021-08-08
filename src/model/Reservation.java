package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Reservation {

    private Customer customer;
    private IRoom room;
    private Date checkInDate;
    private Date checkOutDate;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    public Reservation(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    @Override
    public String toString() {
        return "Name:\n" + customer.getFirstName() + " " + customer.getLastName() + "\n" + "Room:\n" + room + "starts on " + formatter.format(checkInDate) + "\nends on " + formatter.format(checkOutDate) + "\n";
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

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
}
