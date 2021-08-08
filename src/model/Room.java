package model;

import java.util.Objects;

public class Room implements IRoom {

    private String roomNumber;
    private Double roomPrice;
    private RoomType roomType;

    public Room(String roomNumber, Double roomPrice, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomPrice = roomPrice;
        this.roomType = roomType;
    }

    @Override
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public Double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(Double roomPrice) {
        this.roomPrice = roomPrice;
    }

    @Override
    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    @Override
    public boolean isFree() {
        return roomPrice == 0.0;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + "\nroom price " + roomPrice + " per night\n" + "room type " + roomType + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Room) {
            Room that = (Room) o;
            return roomNumber.equals(that.roomNumber) && roomPrice.equals(that.roomPrice) && roomType.equals(that.roomType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber, roomPrice, roomType);
    }
}
