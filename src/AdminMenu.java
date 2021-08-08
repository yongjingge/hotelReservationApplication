import api.AdminResource;
import model.*;

import java.util.Collection;
import java.util.Scanner;

public class AdminMenu {

    private static final AdminResource adminResource = AdminResource.getInstance();

    /* get a valid integer input from user*/
    private int getAction() {
        boolean keepAsking = true;
        int userInputInt = -1;
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                adminMenuGuide();
                userInputInt = Integer.parseInt(sc.nextLine());
                if (Math.max(1, userInputInt) != Math.min(userInputInt, 6)) {
                    System.out.println("Please enter a number between 1 and 6");
                    continue;
                }
            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
                continue;
            }
            keepAsking = false;
        }
        return userInputInt;
    }

    public void startActions() {
        int actionRes = getAction(); // collect input
        switch (actionRes) {
            case 1:
                // see all customers
                printAllCustomers();
                break;
            case 2:
                // see all rooms
                printAllRooms();
                break;
            case 3:
                // see all reservations
                printAllReservations();
                break;
            case 4:
                // add a room
                addARoom();
                break;
            case 5:
                // back to main menu
                System.out.println("You will be lead to the main menu page");
                MainMenu mainMenu = new MainMenu();
                mainMenu.startActions();
            case 6:
                System.out.println("Admin menu will be closed");
                System.exit(0);
            default:
                System.out.println("Invalid input! Please try again");
                startActions();
        }
    }

    /* print out all customers */
    private void printAllCustomers() {
        Collection<Customer> getAllCustomers = adminResource.getAllCustomers();
        if (getAllCustomers.isEmpty()) {
            System.out.println("No customers in the system");
            System.out.println();
        }
        getAllCustomers.forEach(System.out::println);
        System.out.println();
        startActions();
    }

    /* print out all rooms */
    private void printAllRooms() {
        Collection<IRoom> getAllRooms = adminResource.getAllRooms();
        if (getAllRooms.isEmpty()) {
            System.out.println("No rooms in the system");
            System.out.println();
        }
        getAllRooms.forEach(System.out::println);
        System.out.println();
        startActions();
    }

    /* print out all reservations */
    private void printAllReservations() {
        Collection<Reservation> getAllReservations = adminResource.getAllReservations();
        if (getAllReservations.isEmpty()) {
            System.out.println("No reservations in the system");
            System.out.println();
        }
        adminResource.displayAllReservations();
        System.out.println();
        startActions();
    }

    /* add a new room into the system */
    private void addARoom() {
        boolean addAnotherRoom = false;
        String addAnotherRoomResponse = "";
        do {
            String roomNumber = getValidRoomNumber();
            Double roomPrice = getRoomPrice();
            RoomType roomType = getRoomTypeEnum(getRoomTypeInteger());
            adminResource.addARoom(new Room(roomNumber, roomPrice, roomType));

            addAnotherRoomResponse = getYesOrNoResponse("Would you like to add another room?");
            addAnotherRoom = addAnotherRoomResponse.equalsIgnoreCase("y");
        } while (addAnotherRoom);
        startActions();
    }

    /* ----------------------------------- HELPER METHODS BELOW ----------------------------------- */
    /* get a room number input */
    private String getRoomNumber() {
        boolean keepAsking = true;
        String res = "";
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter room number");
                res = sc.nextLine();
                if (Integer.parseInt(res) > 10000) {
                    System.out.println("Please enter a number less than 10000");
                    continue;
                }
            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
                continue;
            }
            keepAsking = false;
        }
        return res;
    }

    /* check availability of user's input number */
    private String getValidRoomNumber() {
        boolean invalid = false;
        String res = "";
        do {
            res = getRoomNumber();
            if (adminResource.roomNumberConflict(res)) {
                System.out.println("Your input number " + res + " already exists in the system, please try another one");
                invalid = true;
            } else {
                invalid = false;
            }
        } while (invalid);
        return res;
    }

    /* get a price input (double) */
    private Double getRoomPrice() {
        boolean keepAsking = true;
        double res = 0.0;
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter price per night (please enter a double number)");
                res = Double.parseDouble(sc.nextLine());
            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
                continue;
            }
            keepAsking = false;
        }
        return res;
    }

    /* get a room enum type input (get an integer 1 or 2 for enum mapping) */
    private Integer getRoomTypeInteger() {
        boolean keepAsking = true;
        int res = -1;
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter room type: 1 for single bed, 2 for double bed");
                res = Integer.parseInt(sc.nextLine());
                if (! (res == 1 || res == 2)) {
                    System.out.println("Please choose from the provided options");
                    continue;
                }

            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
                continue;
            }
            keepAsking = false;
        }
        return res;
    }

    /* get a room type mapping, return null if no mapping matched */
    private RoomType getRoomTypeEnum(Integer i) {
        if (! (i == 1 || i == 2)) {
            System.out.println("Room type does not exist in the system");
            return null;
        }
        return i == 1 ? RoomType.SINGLE : RoomType.DOUBLE;
    }

    /* get a yes or no res */
    private String getYesOrNoResponse(String msg) {
        boolean keepAsking = true;
        String res = "";
        System.out.println(msg + " y/n");
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                res = sc.nextLine();
                if (! (res.equalsIgnoreCase("y") ||
                        res.equalsIgnoreCase("n"))) {
                    System.out.println("Please enter y/n");
                    continue;
                }
            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
                continue;
            }
            keepAsking = false;
        }
        return res;
    }

    /* print out admin menu instructions */
    private void adminMenuGuide() {
        System.out.println(
                """
                    Admin Menu
                    -----------------------------------------
                    1. See all Customers
                    2. See all Rooms
                    3. See all Reservations
                    4. Add a Room
                    5. Back to Main Menu
                    6. Exit
                    -----------------------------------------
                    Please select a number for the menu option
                   """
        );
    }

    /* ----------------------------------- MAIN METHOD BELOW ----------------------------------- */
    public static void main(String[] args) {
        AdminMenu adminMenu = new AdminMenu();
        adminMenu.startActions();
    }
}
