package service;

import model.Customer;
import model.IRoom;
import model.Reservation;

import java.time.LocalDate;
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

    public Reservation reserveARoom(Customer customer, IRoom room, LocalDate checkInDate, LocalDate checkOutDate) {
        Reservation newReservation = new Reservation(customer, room, checkInDate, checkOutDate);
        reservationSet.add(newReservation);
        reservationMap.computeIfAbsent(room.getRoomNumber(), k -> new ArrayList<>()).add(newReservation);
        emailReservationMap.computeIfAbsent(customer.getEmail(), k -> new ArrayList<>()).add(newReservation);
        return newReservation;
    }

    /* default access modifier example */
    boolean dateConflict(LocalDate checkInDate, LocalDate checkOutDate, Reservation reservation) {
        return checkInDate.isBefore(reservation.getCheckOutDate()) &&
                checkOutDate.isAfter(reservation.getCheckInDate());
    }

    /* used inside this class to find rooms available with provided check-n/out dates, price type is not checked */
    private Collection<IRoom> findRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        Collection<IRoom> availableRooms = getAllRooms();
        List<IRoom> res = new ArrayList<>(availableRooms);
        if (reservationSet.isEmpty()) {
            return res;
        }
        for (Reservation r : reservationSet) {
            if (dateConflict(checkInDate, checkOutDate, r)) {
                res.remove(r.getRoom());
            }
        }
        return res;
    }

    /* this method will include options of free, paid, all for room reservations */
    public Collection<IRoom> findRooms(LocalDate checkInDate, LocalDate checkOutDate, String priceType) {
        Collection<IRoom> availableRooms = new ArrayList<>(findRooms(checkInDate, checkOutDate));
        if (priceType.equals("all")) {
            return availableRooms;
        }
        if (priceType.equals("paid")) {
            availableRooms.removeIf(room -> room.isFree());
        } else if (priceType.equals("free")) {
            availableRooms.removeIf(room -> !room.isFree());
        }
        return availableRooms;
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
