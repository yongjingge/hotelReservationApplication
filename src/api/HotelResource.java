package api;

import model.Customer;
import model.IRoom;
import model.Reservation;
import service.CustomerService;
import service.ReservationService;

import java.time.LocalDate;
import java.util.Collection;

public class HotelResource {
    /* provide a static reference */
    private static class HotelResourceHolder {
        private static final HotelResource instance = new HotelResource();
    }
    private HotelResource() {}
    public static HotelResource getInstance() {
        return HotelResourceHolder.instance;
    }

    private final CustomerService customerService = CustomerService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    public Customer getCustomer(String email) {
        return customerService.getCustomer(email);
    }

    public void createACustomer(String email, String firstName, String lastName) {
        customerService.addCustomer(email, firstName, lastName);
    }

    public IRoom getRoom(String roomNumber) {
        return reservationService.getARoom(roomNumber);
    }

    /* use LocalDate */
    public Reservation bookARoom(String customEmail, IRoom room, LocalDate checkInDate, LocalDate checkOutDate) {
        return reservationService.reserveARoom(customerService.getCustomer(customEmail), room, checkInDate, checkOutDate);
    }

    public Collection<Reservation> getCustomersReservations(String customerEmail) {
        return reservationService.getCustomersReservationFromCustomer(customerService.getCustomer(customerEmail));
    }

    /* use LocalDate */
    public Collection<IRoom> findARoom(LocalDate checkIn, LocalDate checkOut, String priceType) {
        return reservationService.findRooms(checkIn, checkOut, priceType);
    }

}
