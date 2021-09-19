import api.AdminResource;
import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class MainMenu {

    private static final LocalDate today = LocalDate.now();
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /* get a valid integer input from user */
    private int getAction() {
        boolean keepAsking = true;
        int userInputInt = -1;
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                mainMenuGuide();
                userInputInt = Integer.parseInt(sc.nextLine());
                if (Math.max(1, userInputInt) != Math.min(userInputInt, 5)) {
                    System.out.println("Please enter a number between 1 and 5");
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

    /* start actions */
    public void startActions() {
        int actionRes = getAction(); // collect input
        switch(actionRes) {
            case 1:
                // find and reserve a room action
                findAndReserveARoom();
                break;
            case 2:
                // see my reservations action
                getReservationsFromEmail();
                break;
            case 3:
                // create an account action
                displayCreatedNewAccount();
                break;
            case 4:
                AdminMenu adminMenu = new AdminMenu();
                adminMenu.startActions();
            case 5:
                // exit action
                System.out.println("You are leaving the system");
                System.exit(0);
        }
    }

    /**
     * Case 1 - find and reserve a room action
     * workflow:
     *  get user's input of check-in/out dates and room-price choice
     *  --> find a room, if no rooms available, try to add 7 days to the input dates and display rooms
     *  --> confirm their willingness to book a room, if not, go back to the startActions method
     *  --> check if user has an account, if not, go to case 3 action
     *  --> inform their email and preferred room number to make a reservation
     *  --> display the reservation
     *  --> return to main menu
     */
    /* use LocalDate */
    private void findAndReserveARoom() {
//        HotelResource hotelResource = HotelResource.getInstance();
        HotelResource hotelResource = HotelResource.INSTANCE;
        LocalDate checkIn = getCheckInDate();
        LocalDate checkOut = getCheckOutDate(checkIn);
        String priceTypeRes = getPriceType();
        final Collection<IRoom> roomsAvailableOnRequest = new HashSet<>(hotelResource.findARoom(checkIn, checkOut, priceTypeRes));
        final Collection<IRoom> roomsAvailableSevenDaysAdded = new HashSet<>(hotelResource.findARoom(checkIn.plusDays(7), checkOut.plusDays(7), priceTypeRes));

        if (! roomsAvailableOnRequest.isEmpty()) {
            System.out.println("Rooms available in your selected dates\n");
            roomsAvailableOnRequest.forEach(System.out::println);
        } else if (! roomsAvailableSevenDaysAdded.isEmpty()) {
            System.out.println("""
                    No rooms available in your selected dates
                    We have rooms available seven days later of your choice
                    """);
            roomsAvailableSevenDaysAdded.forEach(System.out::println);
        } else {
            System.out.println("No rooms available in your selected days\n");
            startActions();
        }

        String yORnBookARoom = getYesOrNoResponse("Would you like to book a room?");
        boolean continueBooking = yORnBookARoom.equalsIgnoreCase("y");
        if (continueBooking) {
            String yORnHaveAccount = getYesOrNoResponse("Do you have an account with us?");
            boolean haveAccount = yORnHaveAccount.equalsIgnoreCase("y");
            String userEmailInput = "";
            if (haveAccount) {
                userEmailInput = getEmail();
                while (! emailExists(userEmailInput)) {
                    System.out.println("Your email does not exist in our system. Please try to enter again or create a new account");
                    userEmailInput = getEmail();
                }
            } else {
                userEmailInput = createNewAccount();
            }

            String validRoomNumberInput = getRoomNumberValid("What room number would you like to reserve", checkIn, checkOut, priceTypeRes);
            Reservation newReservation = !roomsAvailableOnRequest.isEmpty() ? hotelResource.bookARoom(userEmailInput, hotelResource.getRoom(validRoomNumberInput), checkIn, checkOut) : hotelResource.bookARoom(userEmailInput, hotelResource.getRoom(validRoomNumberInput), checkIn.plusDays(7), checkOut.plusDays(7));
            System.out.println(newReservation);
        } else {
            startActions();
        }
        startActions();
    }

    /**
     * Case 2 - see my reservation
     * workflow:
     *  get user's input of a valid email
     *  --> find reservations related to that email, and display the result
     *  --> return to the main menu
     */
    private void getReservationsFromEmail() {
        String emailInput = getEmail();
//        HotelResource hotelResource = HotelResource.getInstance();
        HotelResource hotelResource = HotelResource.INSTANCE;
        if (emailExists(emailInput) && !Objects.isNull(hotelResource.getCustomersReservations(emailInput))) {
            hotelResource.getCustomersReservations(emailInput).forEach(System.out::println);
        } else if (! emailExists(emailInput)) {
            System.out.println("Your email does not exist in the system, please try again");
        } else {
            System.out.println("You do not have any reservation");
        }
        startActions();
    }

    /* ----------------------------------- HELPER METHODS BELOW ----------------------------------- */
    /* print out main menu instructions */
    private void mainMenuGuide() {
        System.out.println(
                """
                        Welcome to Hotel Reservation Application
                        -----------------------------------------
                        1. Find and reserve a room
                        2. See my reservations
                        3. Create an Account
                        4. Admin
                        5. Exit
                        -----------------------------------------
                        Please select a number for the menu option
                        """
        );
    }

    /* get a valid check-in date (LocalDate) from user */
    private LocalDate getCheckInDate() {
        boolean keepAsking = true;
        LocalDate checkIn = null;
        String[] prints = {
                "Enter CheckIn Date mm/dd/yyyy example 02/01/2020",
                "You cannot check in before today. Please re-enter",
        };
        while (keepAsking) {
            try {
                System.out.println(prints[0]);
                Scanner sc = new Scanner(System.in);
                String inputDate = sc.nextLine();
                checkIn = LocalDate.parse(inputDate, dateTimeFormatter);
                if (checkIn.isBefore(today)) {
                    System.out.println("Your check-in date is " + checkIn.format(dateTimeFormatter));
                    System.out.println("Today is " + today.format(dateTimeFormatter));
                    System.out.println(prints[1]);
                    continue;
                }
            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
                continue;
            }
            keepAsking = false;
        }
        return checkIn;
    }

    /* get a valid check-out date (LocalDate) from user */
    private LocalDate getCheckOutDate(LocalDate checkInDate) {
        boolean keepAsking = true;
        LocalDate checkOut = null;
        String[] prints = {
                "Enter CheckOut Date mm/dd/yyyy example 02/01/2020",
                "You cannot check out before your check-in date. Please re-enter"
        };
        while(keepAsking) {
            try {
                System.out.println(prints[0]);
                Scanner sc = new Scanner(System.in);
                String inputDate = sc.nextLine();
                checkOut = LocalDate.parse(inputDate, dateTimeFormatter);
                if (checkOut.isBefore(checkInDate)) {
                    System.out.println("Your check-out date is " + checkOut.format(dateTimeFormatter));
                    System.out.println("Your check-in date is " + checkInDate.format(dateTimeFormatter));
                    System.out.println(prints[1]);
                    continue;
                }
            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
            }
            keepAsking = false;
        }
        return checkOut;
    }

    /* get a valid yes/no response from user */
    private String getYesOrNoResponse(String msg) {
        boolean keepAsking = true;
        String res = "";
        System.out.println(msg + " y/n");
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                res = sc.nextLine();
                if (! (res.equalsIgnoreCase("y") || res.equalsIgnoreCase("n"))) {
                    System.out.println("Please enter y/n");
                    continue;
                }
            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
                continue;
            }
            keepAsking =false;
        }
        return res;
    }

    /* get a valid email input from user */
    private String getEmail() {
        boolean keepAsking = true;
        String res = "";
        String emailRegex = "^(.+)@(.+).com$";
        Pattern pattern = Pattern.compile(emailRegex);
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter Email format: name@domain.com");
                res = sc.nextLine();
                if (! pattern.matcher(res).matches()) {
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

    /* check if input email exists in the reservation system */
    private boolean emailExists(String email) {
//        AdminResource adminResource = AdminResource.getInstance();
        AdminResource adminResource = AdminResource.INSTANCE;
        Set<String> emailsInSystem = new HashSet<>(adminResource.getAllCustomersEmails());
        return emailsInSystem.contains(email);
    }

    /* create a new account */
    private String createNewAccount() {
//        HotelResource hotelResource = HotelResource.getInstance();
        HotelResource hotelResource = HotelResource.INSTANCE;
        String email = getEmail();
        String firstName = getName("First Name");
        String lastName = getName("Last Name");
        hotelResource.createACustomer(email, firstName, lastName);
        return hotelResource.getCustomer(email).getEmail();
    }

    /* display the created account */
    private void displayCreatedNewAccount() {
        String newAccountEmail = createNewAccount();
        if (newAccountEmail == null || newAccountEmail.length() < 7) {
            return;
        }
        System.out.println("Your account has been created:\n" + getCustomerFromEmail(newAccountEmail));
        startActions();
    }

    /* get customer information from email */
    private Customer getCustomerFromEmail(String email) {
//        AdminResource adminResource = AdminResource.getInstance();
        AdminResource adminResource = AdminResource.INSTANCE;
        return adminResource.getCustomer(email);
    }

    /* get a price type from user to make reservations, will return a number choice */
    private String getPriceType() {
        boolean keepAsking = true;
        String res = "";
        int checkRes = -1;
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println("""
                        Which type of room would you like to book?
                        enter 1 for paid rooms
                        enter 2 for free rooms
                        enter 3 for all rooms
                        please enter your choice
                        """);
                res = sc.nextLine();
                checkRes = Integer.parseInt(res);
                if (! (checkRes == 1 || checkRes == 2 || checkRes == 3)) {
                    System.out.println("Please choose from provided options");
                    continue;
                }
            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
            }
            keepAsking = false;
        }
        if (checkRes == 1) {
            return "paid";
        } else if (checkRes == 2) {
            return "free";
        } else {
            return "all";
        }
    }

    /* get valid name inputs from user */
    private String getName(String msg) {
        boolean keepAsking = true;
        String res = "";
        final String regex = "^[a-zA-Z]+$";
        final Pattern pattern = Pattern.compile(regex);
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println("Please enter " + msg);
                res = sc.nextLine();
                if (res.length() < 1 || res.length() > 16) {
                    System.out.println("Please enter between 1 and 16 characters");
                    continue;
                }
                if (! pattern.matcher(res).matches()) {
                    System.out.println("Please enter your name in lower cases");
                    continue;
                }
            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
            }
            keepAsking = false;
        }
        return res;
    }

    /* get a valid room number for reservation - use LocalDate */
    private String getRoomNumberValid(String msg, LocalDate checkIn, LocalDate checkOut, String priceType) {
        boolean keepAsking = true;
        String res = "";
//        HotelResource hotelResource = HotelResource.getInstance();
        HotelResource hotelResource = HotelResource.INSTANCE;
        while (keepAsking) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println(msg);
                res = sc.nextLine();
                if (hotelResource.getRoom(res) == null) {
                    System.out.println("Room does not exist, please try again");
                    continue;
                }
                final Set<IRoom> roomsAvailable = new HashSet<>(hotelResource.findARoom(checkIn, checkOut, priceType));
                final Set<IRoom> roomsAvailableSevenDaysLater = new HashSet<>(hotelResource.findARoom(checkIn.plusDays(7), checkOut.plusDays(7), priceType));
                if (roomsAvailable.isEmpty() && roomsAvailableSevenDaysLater.isEmpty()) {
                    System.out.println("No room available, please try again");
                    continue;
                }
                if (!roomsAvailable.contains(hotelResource.getRoom(res)) && !roomsAvailableSevenDaysLater.contains(hotelResource.getRoom(res))) {
                    System.out.println("The room you selected is not available, please try again");
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

    /* ----------------------------------- MAIN METHOD BELOW ----------------------------------- */
    public static void main(String[] args) {
        // initialize a MainMenu object and start it: object.startAction()
        MainMenu mainMenu = new MainMenu();
        mainMenu.startActions();
    }
}
