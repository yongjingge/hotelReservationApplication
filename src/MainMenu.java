
import api.AdminResource;
import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Reservation;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class MainMenu {

    private static final Date today = Calendar.getInstance().getTime();
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

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
    private void findAndReserveARoom() {
        HotelResource hotelResource = HotelResource.getInstance();
        Date checkIn = getCheckInDate();
        Date checkOut = getCheckOutDate(checkIn);
        String priceTypeRes = getPriceType();
        final Collection<IRoom> roomsAvailableOnRequest = new HashSet<>(hotelResource.findARoom(checkIn, checkOut, priceTypeRes));
        final Collection<IRoom> roomsAvailableSevenDaysAdded = new HashSet<>(hotelResource.findARoom(addSevenDays(checkIn), addSevenDays(checkOut), priceTypeRes));

        // system based on the dates will list the rooms available for reservation
        if (! roomsAvailableOnRequest.isEmpty()) {
            System.out.println("Rooms available in your selected dates\n");
            roomsAvailableOnRequest.forEach(System.out::println);
        } else if (! roomsAvailableSevenDaysAdded.isEmpty()) {
            System.out.println("""
                    No rooms available in your selected dates
                    We have rooms available seven day later of your selected dates
                    """);
            roomsAvailableSevenDaysAdded.forEach(System.out::println);
        } else {
            System.out.println("No rooms available in your selected days\n");
            startActions();
        }

        // user confirm their willingness to book a room
        String yORnBookARoom = getYesOrNoResponse("Would you like to book a room?");
        boolean continueBooking = yORnBookARoom.equalsIgnoreCase("y");
        if (continueBooking) {
            String yORnHaveAccount = getYesOrNoResponse("Do you have an account with us?");
            boolean haveAccount = yORnHaveAccount.equalsIgnoreCase("y");
            String userEmailInput = "";
            // get account email --> used to make reservations
            if (haveAccount) {
                // get user email input --> get customer from email, email input has to both match the regex and be inside the system
                // select a room (from room number input)
                // display reservation
                userEmailInput = getEmail();
                while (! emailExists(userEmailInput)) {
                    System.out.println("Your email does not exist in our system. Please try to enter again or create a new account");
                    userEmailInput = getEmail();
                }
            } else {
                // does not have an account, --> create a new account in this block -->
                // create a new account and return the email of this account
                userEmailInput = createNewAccount();
            }

            // continue reservations with the email get from above
            String validRoomNumberInput = getRoomNumberValid("what room number would you like to reserve", checkIn, checkOut, priceTypeRes);

            // make a new reservation and display it
            Reservation newReservation = !roomsAvailableOnRequest.isEmpty() ? hotelResource.bookARoom(userEmailInput, hotelResource.getRoom(validRoomNumberInput), checkIn, checkOut) : hotelResource.bookARoom(userEmailInput, hotelResource.getRoom(validRoomNumberInput), addSevenDays(checkIn), addSevenDays(checkOut));
            System.out.println(newReservation);
        } else {
            // answer 'n' to the question: would u like to book a room --> go back to the main menu
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
        HotelResource hotelResource = HotelResource.getInstance();
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

    /* get a valid check-in date from user */
    private Date getCheckInDate() {
        boolean keepAsking = true;
        Date checkIn = null;
        String[] prints = {
                "Enter CheckIn Date mm/dd/yyyy example 02/01/2020",
                "You cannot check in before today. Please re-enter",
        };
        while (keepAsking) {
            try {
                System.out.println(prints[0]);
                Scanner sc = new Scanner(System.in);
                String inputDate = sc.nextLine();
                checkIn = formatter.parse(inputDate);
                if (checkIn.before(today)) {
                    System.out.println("Your check-in date is " + formatter.format(checkIn));
                    System.out.println("Today is " + formatter.format(today));
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

    /* get a valid check-out date from user */
    private Date getCheckOutDate(Date checkInDate) {
        boolean keepAsking = true;
        Date checkOut = new Date();
        String[] prints = {
                "Enter CheckOut Date mm/dd/yyyy example 02/01/2020",
                "You cannot check out before your check-in date. Please re-enter"
        };
        while (keepAsking) {
            try {
                System.out.println(prints[0]);
                Scanner sc = new Scanner(System.in);
                String inputDate = sc.nextLine();
                checkOut = formatter.parse(inputDate);
                if (checkOut.before(checkInDate)) {
                    System.out.println("Your check-out date is " + formatter.format(checkOut));
                    System.out.println("Your check-in date is " + formatter.format(checkInDate));
                    System.out.println(prints[1]);
                    continue;
                }

            } catch (Exception ex) {
                System.out.println("Invalid input! " + ex.getLocalizedMessage());
                continue;
            }
            keepAsking = false;
        }
        return checkOut;
    }

    /* add 7 days to a given Date argument */
    private Date addSevenDays(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, 7);
        return c.getTime();
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
        AdminResource adminResource = AdminResource.getInstance();
        Set<String> emailsInSystem = new HashSet<>(adminResource.getAllCustomersEmails());
        return emailsInSystem.contains(email);
    }

    /* create a new account */
    private String createNewAccount() {
        HotelResource hotelResource = HotelResource.getInstance();
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
        AdminResource adminResource = AdminResource.getInstance();
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

    /* get a valid room number for reservation */
    private String getRoomNumberValid(String msg, Date checkIn, Date checkOut, String priceType) {
        boolean keepAsking = true;
        String res = "";
        HotelResource hotelResource = HotelResource.getInstance();
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
                final Set<IRoom> roomsAvailableSevenDaysLater = new HashSet<>(hotelResource.findARoom(addSevenDays(checkIn), addSevenDays(checkOut), priceType));
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
