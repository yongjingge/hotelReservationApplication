package api;

import model.Customer;
import model.IRoom;
import model.Reservation;
import service.CustomerService;
import service.ReservationService;

import java.util.Collection;
import java.util.List;

public class AdminResource {

    private static class AdminResourceHolder {
        private static final AdminResource instance = new AdminResource();
    }
    private AdminResource() {}
    public static AdminResource getInstance() {
        return AdminResourceHolder.instance;
    }

    private final CustomerService customerService = CustomerService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    public Customer getCustomer(String email) {
        return customerService.getCustomer(email);
    }

    public void addRooms(List<IRoom> rooms) {
        rooms.forEach(reservationService::addRoom);
    }

    public void addARoom(IRoom room) {
        reservationService.addRoom(room);
    }

    public Collection<IRoom> getAllRooms() {
        return reservationService.getAllRooms();
    }

    public Collection<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    public Collection<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    public void displayAllReservations() {
        reservationService.printAllReservation();
    }

    public Collection<String> getAllCustomersEmails() {
        return customerService.getAllCustomersEmails();
    }

    public boolean roomNumberConflict(String roomNumber) {
        return reservationService.roomNumberConflict(roomNumber);
    }
}
