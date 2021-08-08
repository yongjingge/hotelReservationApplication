package service;

import model.Customer;
import model.IRoom;
import model.Reservation;

import java.util.*;

public class ReservationService {

    private static class ReservationServiceHolder {
        private static final ReservationService instance = new ReservationService();
    }
    private ReservationService() {}
    public static ReservationService getInstance() {
        return ReservationServiceHolder.instance;
    }

    private final Map<String, IRoom> roomMap = new HashMap<>(); // record rooms and their id in a hashmap
    /* Date  Structures regarding reservations */
    private final Set<Reservation> reservationSet = new HashSet<>(); // record reservations in a hashset
    private final Map<String, List<Reservation>> reservationMap = new HashMap<>(); // mapping room's id to reservations of this room
    private final Map<String, List<Reservation>> emailReservationMap = new HashMap<>(); // mapping customer's email to reservations of this customer's

    public void addRoom(IRoom room) {
        roomMap.put(room.getRoomNumber(), room);
    }

    public Collection<IRoom> getAllRooms() {
        return roomMap.values();
    }

    public IRoom getARoom(String roomId) {
        return roomMap.getOrDefault(roomId, null);
    }

    public boolean roomNumberConflict(String roomNumber) {
        return roomMap.containsKey(roomNumber) && !Objects.isNull(roomMap.get(roomNumber));
    }

    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
        Reservation newReservation = new Reservation(customer, room, checkInDate, checkOutDate);
        reservationSet.add(newReservation);
        reservationMap.computeIfAbsent(room.getRoomNumber(), k -> new ArrayList<>()).add(newReservation);
        emailReservationMap.computeIfAbsent(customer.getEmail(), k -> new ArrayList<>()).add(newReservation);
        return newReservation;
    }

    private boolean dateConflict(Date checkInDate, Date checkOutDate, Reservation reservation) {
        return checkInDate.before(reservation.getCheckOutDate()) ||
                checkOutDate.after(reservation.getCheckInDate());
    }

    public Collection<IRoom> findRooms(Date checkInDate, Date checkOutDate) {
        List<IRoom> roomList = new ArrayList<>(roomMap.values());
        for (Reservation r : reservationSet) {
            if (dateConflict(checkInDate, checkOutDate, r)) {
                roomList.remove(r.getRoom());
            }
        }
        return roomList;
    }

    public Collection<Reservation> getCustomersReservationFromCustomer(Customer customer) {
        return emailReservationMap.getOrDefault(customer.getEmail(), null);
    }

    public Collection<Reservation> getAllReservations() {
        return reservationSet;
    }

    public void printAllReservation() {
        reservationSet.forEach(System.out::println);
    }
}
