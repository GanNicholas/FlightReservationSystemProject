/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightReservationSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.AirportEntity;
import entity.CabinClassConfigurationEntity;
import entity.FlightBundle;
import entity.CustomerEntity;
import entity.FRSCustomerEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightReservationEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.IndividualFlightReservationEntity;
import entity.PartnerEntity;
import entity.PassengerEntity;
import entity.SeatEntity;
import entity.SingleFlightScheduleEntity;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.MINUTE;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import sun.nio.cs.ext.Big5;
import util.enumeration.CabinClassType;
import util.enumeration.UserRole;
import util.exception.CustomerDoesNotExistException;
import util.exception.CustomerExistException;
import util.exception.CustomerHasNoReservationException;
import util.exception.CustomerLoginInvalid;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightReservationDoesNotExistException;
import util.exception.FlightRouteODPairExistException;

/**
 *
 * @author sohqi
 */
public class Customer {

    CustomerEntity customer = null;
    private CustomerSessionBeanRemote customerSessionBean;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private FlightScheduleSessionBeanRemote flightScheduleSessionBean;
    private FlightReservationSessionBeanRemote flightReservationSessionBean;
    private FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;

    public Customer() {

        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public Customer(CustomerSessionBeanRemote customerSessionBean, FlightScheduleSessionBeanRemote flightScheduleSessionBean, FlightReservationSessionBeanRemote flightReservationSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBean) {
        this();
        this.customerSessionBean = customerSessionBean;
        this.flightScheduleSessionBean = flightScheduleSessionBean;
        this.flightReservationSessionBean = flightReservationSessionBean;
        this.flightRouteSessionBeanRemote = flightRouteSessionBean;
    }

    public void runApp() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("1. Register Customer");
            System.out.println("2. Customer Login");
            System.out.println("3. Exit");
            String input = sc.nextLine();
            //while (true) {
            if (input.equals("1")) {
                registerCustomer();
            } else if (input.equals("2")) {
                boolean loginSuccessful = customerLogin();
                if (loginSuccessful) {
                    afterLoginPage();
                }
            } else if (input.equals("3")) {
                System.out.println("Goodbye!");
                System.exit(0);
            }
        } catch (NumberFormatException ex) {
            System.out.println("You have invalid input.");
            runApp();
        }
        //}
    }

    public void afterLoginPage() {
        String input = "";
        while (!input.equals("4")) {
            System.out.println("Welcome ");
            Scanner sc = new Scanner(System.in);
            System.out.println("1. Search for flight");
            System.out.println("2. View my flight reservations");
            System.out.println("3. View my flight reservation details");
            System.out.println("4. Log out");
            input = sc.nextLine();
            //while (true) {
            if (input.equals("1")) {
                searchFlight();
            } else if (input.equals("2")) {
                viewFlightReservations();
            } else if (input.equals("3")) {
                viewFlightReservationDetails();
            } else if (input.equals("4")) {

            }
        }
    }

    public void registerCustomer() {//need to do validation factory to for bean validation @unique for user name;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter First Name:");
        String fName = sc.nextLine().trim();
        System.out.println("Enter Last Name:");
        String lName = sc.nextLine().trim();
        System.out.println("Enter Email:");
        String email = sc.nextLine().trim();
        System.out.println("Enter Mobile phone:");
        String mobilePhone = sc.nextLine().trim();
        System.out.println("Enter Address:");
        String address = sc.nextLine().trim();
        System.out.println("Enter Login:");
        String login = sc.nextLine().trim();
        System.out.println("Enter Password:");
        String password = sc.nextLine().trim();

        CustomerEntity customer = new FRSCustomerEntity(login, password, UserRole.CUSTOMER, fName, lName, email, mobilePhone, address);
        Set<ConstraintViolation<CustomerEntity>> constraintViolationsCabin = validator.validate(customer);
        if (!constraintViolationsCabin.isEmpty()) {
            System.out.println("\nInput data validation error!:");
            for (ConstraintViolation constraintViolation : constraintViolationsCabin) {
                System.out.println("\t" + constraintViolation.getMessage());

            }

        } else {
            try {
                Long id = customerSessionBean.registerCustomer(customer);
                System.out.println("You have successfully created an account with the id" + id);
            } catch (CustomerExistException ex) {
                System.out.println("The account is already exist");
            }
        }
    }

    public boolean customerLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your user name");
        String username = sc.nextLine();
        System.out.println("Please enter your password");
        String password = sc.nextLine();

        try {
            if (username.length() > 5 && username.length() <= 16 && password.length() > 7 && password.length() <= 16) {
                customer = customerSessionBean.customerLogin(username, password);
                System.out.println("You have successfully login");
                return true;
            } else {
                System.out.println("Please fill in your login credential. Username should have at least 6 characters and maximum of 16 characters. Password should have at least 8 character and maximum of 16 characters");
                return false;
            }
        } catch (CustomerLoginInvalid ex) {
            System.out.println("Invalid username or password. Please try again.");
        }

        return false;
    }

    public void searchFlight() { // no validation yet
        Scanner sc = new Scanner(System.in);
        boolean invalidInput = false;
        String destinationAirport = "";
        String departureAirport = "";
        String passenger = "";
        while (true) {
            try {

                System.out.println("Enter trip type:");
                System.out.println("1. One Way");
                System.out.println("2. Two ways");
                String tripType = sc.nextLine().trim();
                while (!tripType.equals("1") && !tripType.equals("2")) {
                    System.out.println("Invalid input");
                    System.out.println("Enter trip type:");
                    System.out.println("1. One Way");
                    System.out.println("2. Two ways");
                    tripType = sc.nextLine().trim();
                }

                System.out.println("Please choose one of the following:");
                System.out.println("1. Connecting Flight");
                System.out.println("2. Direct Flight");
                System.out.println("3. No preference of(Direct/Connecting) Flight");
                String indictatorConnectFlightOrNot = sc.nextLine().trim();
                while (!indictatorConnectFlightOrNot.equals("1") && !indictatorConnectFlightOrNot.equals("2") && !indictatorConnectFlightOrNot.equals("3")) {
                    System.out.println("Invalid input");
                    System.out.println("Please choose one of the following:");
                    System.out.println("1. Connecting Flight");
                    System.out.println("2. Direct Flight");
                    System.out.println("3. No preference of(Direct/Connecting) Flight");
                    indictatorConnectFlightOrNot = sc.nextLine().trim();
                }
                ArrayList<String> cabinInput = new ArrayList<String>();
                System.out.println("Please enter the cabin type:");
                System.out.println("1. First class.");
                System.out.println("2. Business class");
                System.out.println("3. Premium economy class");
                System.out.println("4. Economy class");
                System.out.println("5. Any cabin type");
                String inputCabin = sc.nextLine();
                cabinInput.add(inputCabin);
                if (!inputCabin.equals("5")) {
                    System.out.println("Do you want to search for another cabin as well? Y/N");
                    String searchAnotherCabin = sc.nextLine();
                    while (searchAnotherCabin.equalsIgnoreCase("Y")) {
                        System.out.println("1. First class.");
                        System.out.println("2. Business class");
                        System.out.println("3. Premium economy class");
                        System.out.println("4. Economy class");
                        inputCabin = sc.nextLine();
                        if (inputCabin.equals("1") || inputCabin.equals("2") || inputCabin.equals("3") || inputCabin.equals("4")) {
                            cabinInput.add(inputCabin);
                            System.out.println("Do you want to search for another cabin as well? Y/N");
                            searchAnotherCabin = sc.nextLine();
                        } else {
                            invalidInput = true;
                            break;
                        }

                    }
                }
                //System.out.println("CabinInput at front : " + cabinInput.size());
                if (invalidInput) {
                    System.out.println("Invalid cabin type input. Please check your input again.");
                    break;
                }

                List<String> cabinType = new ArrayList<String>();
                for (int i = 0; i < cabinInput.size(); i++) {

                    if (cabinInput.get(i).equalsIgnoreCase("1")) {
                        cabinType.add("F");
                    } else if (cabinInput.get(i).equalsIgnoreCase("2")) {
                        cabinType.add("J");
                    } else if (cabinInput.get(i).equalsIgnoreCase("3")) {
                        cabinType.add("W");
                    } else if (cabinInput.get(i).equalsIgnoreCase("4")) {
                        cabinType.add("Y");
                    } else if (cabinInput.get(i).equalsIgnoreCase("5")) {
                        cabinType.add("All");
                    }
                }
                System.out.print("Enter departure airport: (IATA CODE) ");
                departureAirport = sc.nextLine().trim();

                System.out.print("Enter destination airport:(IATA CODE) ");
                destinationAirport = sc.nextLine().trim();

                System.out.print("Enter depature date:(dd/mm/yyyy) ");
                String departureDate = sc.nextLine().trim();
                GregorianCalendar searchDateFO = null;
                String[] splitDepartDate = departureDate.trim().split("/");
                if (splitDepartDate.length == 3) {
                    searchDateFO = new GregorianCalendar(Integer.valueOf(splitDepartDate[2]), Integer.valueOf(splitDepartDate[1]) - 1, Integer.valueOf(splitDepartDate[0]));
                } else {
                    System.out.println("You have invalid date input for departure date. Please be in 'dd/mm/yyyy' format");
                    //   throw new DateInvalidException("You have invalid date input for departure flight date. Please be in 'dd/mm/yyyy' format");
                }

                String returnDate = "";
                if (tripType.equals("2")) {
                    System.out.print("Enter return date:(dd/mm/yyyy) ");
                    returnDate = sc.nextLine().trim();
                }
                //convert return time (if exist) to 3 days before and 3 days after

                GregorianCalendar currentSearchReturnDate = null;
                String[] splitDepartDateReturn;
                if (tripType.equals("2")) {
                    splitDepartDateReturn = returnDate.trim().split("/");
                    if (splitDepartDateReturn.length == 3) {
                        currentSearchReturnDate = new GregorianCalendar(Integer.valueOf(splitDepartDateReturn[2]), Integer.valueOf(splitDepartDateReturn[1]) - 1, Integer.valueOf(splitDepartDateReturn[0]));

                    } else {
                        System.out.println("You have invalid date input for return flight date. Please be in 'dd/mm/yyyy' format");
                    }
                }
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                System.out.print("Enter number of passenger: ");
                passenger = sc.nextLine().trim();
                int noOfPassenger = Integer.parseInt(passenger);
                List<FlightBundle> flightBefore3Days = new ArrayList<>();
                List<FlightBundle> flightAfter3Days = new ArrayList<>();
                List<FlightBundle> flightOnActualDay = new ArrayList<>();
                List<FlightBundle> flightBundle = new ArrayList<>();
                List<FlightBundle> listOfFlightBundles = new ArrayList<>();
                GregorianCalendar dateThreeDateBefore = (GregorianCalendar) searchDateFO.clone();
                dateThreeDateBefore.add(GregorianCalendar.DATE, -3);
                dateThreeDateBefore.add(GregorianCalendar.SECOND, -1);

                GregorianCalendar gActualEnd = (GregorianCalendar) searchDateFO.clone();
                gActualEnd.add(GregorianCalendar.HOUR, 24);

                GregorianCalendar dateThreeDateAfter = (GregorianCalendar) searchDateFO.clone();
                dateThreeDateAfter.add(GregorianCalendar.DATE, 4);
                GregorianCalendar returnDateThreeDateBefore = null;
                GregorianCalendar returnGActualEnd = null;
                GregorianCalendar returnDateThreeDateAfter = null;
                if (currentSearchReturnDate != null) {
                    returnDateThreeDateBefore = (GregorianCalendar) currentSearchReturnDate.clone();
                    returnDateThreeDateBefore.add(GregorianCalendar.DATE, -3);
                    returnDateThreeDateBefore.add(GregorianCalendar.SECOND, -1);

                    returnGActualEnd = (GregorianCalendar) currentSearchReturnDate.clone();
                    returnGActualEnd.add(GregorianCalendar.HOUR, 24);

                    returnDateThreeDateAfter = (GregorianCalendar) currentSearchReturnDate.clone();
                    returnDateThreeDateAfter.add(GregorianCalendar.DATE, 4);

                }
                Comparator<FlightBundle> sortFlightScheduleDepart = (FlightBundle p1, FlightBundle p2) -> (int) p1.getDepartOne().getDepartureDateTime().getTime().getTime() - (int) p2.getDepartOne().getDepartureDateTime().getTime().getTime();

                List<FlightBundle> flightsPassingOver1 = new ArrayList<>();
                List<FlightBundle> flightsPassingOver2 = new ArrayList<>();
                List<FlightBundle> flightsPassingOver3 = new ArrayList<>();

                List<FlightBundle> flightsPassingOver4 = new ArrayList<>();
                List<FlightBundle> flightsPassingOver5 = new ArrayList<>();
                List<FlightBundle> flightsPassingOver6 = new ArrayList<>();
                // start calling searh flight with respectively to (1. one way 2. two ways -> inside of each, see if they want (a)connecting flight, (b)direct flight
                try {
                    if (tripType.equals("1")) {// one way
                        if (indictatorConnectFlightOrNot.equals("1")) {// connecting flight

                            flightBefore3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDays(searchDateFO, departureAirport, destinationAirport);
                            flightsPassingOver1 = printSearchResult(flightBefore3Days, "3 days before the search date", 0, cabinType, noOfPassenger);
                            flightOnActualDay = flightScheduleSessionBean.listOfConnectingFlightRecords(searchDateFO, departureAirport, destinationAirport);
                            flightsPassingOver2 = printSearchResult(flightOnActualDay, "On the actual day of the search date", flightsPassingOver1.size(), cabinType, noOfPassenger);

                            flightAfter3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDays(searchDateFO, departureAirport, destinationAirport);
                            flightsPassingOver3 = printSearchResult(flightAfter3Days, "3 days after the search date", flightsPassingOver1.size() + flightsPassingOver2.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(flightsPassingOver1, flightsPassingOver2, flightsPassingOver3);
                        } else if (indictatorConnectFlightOrNot.equals("2")) {//direct flight
                            flightBefore3Days = flightScheduleSessionBean.getDirectFlight(dateThreeDateBefore, searchDateFO, departureAirport, destinationAirport);
                            flightsPassingOver1 = printSearchResult(flightBefore3Days, "3 days before the search date", 0, cabinType, noOfPassenger);

                            flightOnActualDay = flightScheduleSessionBean.getDirectFlight(searchDateFO, gActualEnd, departureAirport, destinationAirport);
                            flightsPassingOver2 = printSearchResult(flightOnActualDay, "On the actual day of the search date", flightsPassingOver1.size(), cabinType, noOfPassenger);

                            flightAfter3Days = flightScheduleSessionBean.getDirectFlight(gActualEnd, dateThreeDateAfter, departureAirport, destinationAirport);
                            flightsPassingOver3 = printSearchResult(flightAfter3Days, "3 days after the search date", flightsPassingOver1.size() + flightsPassingOver2.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(flightsPassingOver1, flightsPassingOver2, flightsPassingOver3);
                        } else if (indictatorConnectFlightOrNot.equals("3")) { // no preference
                            flightBefore3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDays(searchDateFO, departureAirport, destinationAirport);
                            flightBundle = flightScheduleSessionBean.getDirectFlight(dateThreeDateBefore, searchDateFO, departureAirport, destinationAirport);
                            flightBundle = combineAllThreeFlights(flightBefore3Days, flightBundle, null);
                            flightBundle.sort(sortFlightScheduleDepart);
                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightBundle, null);

                            flightsPassingOver1 = printSearchResult(flightBundle, "3 days before the search date", 0, cabinType, noOfPassenger);

                            flightOnActualDay = flightScheduleSessionBean.listOfConnectingFlightRecords(searchDateFO, departureAirport, destinationAirport);
                            flightBundle = flightScheduleSessionBean.getDirectFlight(searchDateFO, gActualEnd, departureAirport, destinationAirport);
                            flightBundle = combineAllThreeFlights(flightOnActualDay, flightBundle, null);
                            flightBundle.sort(sortFlightScheduleDepart);

                            flightsPassingOver2 = printSearchResult(flightBundle, "On the actual day of the search date", flightsPassingOver1.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightBundle, null);

                            flightAfter3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDays(searchDateFO, departureAirport, destinationAirport);
                            flightBundle = flightScheduleSessionBean.getDirectFlight(gActualEnd, dateThreeDateAfter, departureAirport, destinationAirport);
                            flightBundle = combineAllThreeFlights(flightAfter3Days, flightBundle, null);
                            flightBundle.sort(sortFlightScheduleDepart);
                            flightsPassingOver3 = printSearchResult(flightBundle, "3 days after the search date", flightsPassingOver1.size() + flightsPassingOver2.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(flightsPassingOver1, flightsPassingOver2, flightsPassingOver3);

                        }
                    } else if (tripType.equals("2")) {// two ways
                        if (indictatorConnectFlightOrNot.equals("1")) {// connecting
                            System.out.println("Fly - Over Date:");
                            //fly over
                            flightBefore3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDays(searchDateFO, departureAirport, destinationAirport);
                            flightsPassingOver1 = printSearchResult(flightBefore3Days, "3 days before the search date", 0, cabinType, noOfPassenger);

                            flightOnActualDay = flightScheduleSessionBean.listOfConnectingFlightRecords(searchDateFO, departureAirport, destinationAirport);
                            flightsPassingOver2 = printSearchResult(flightOnActualDay, "On the actual day of the search date", flightsPassingOver1.size(), cabinType, noOfPassenger);

                            flightAfter3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDays(searchDateFO, departureAirport, destinationAirport);
                            flightsPassingOver3 = printSearchResult(flightAfter3Days, "3 days after the search date", flightsPassingOver1.size() + flightsPassingOver2.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(flightsPassingOver1, flightsPassingOver2, flightsPassingOver3);
                            // fly back
                            System.out.println("Fly Back Date : ");
                            flightBefore3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDays(currentSearchReturnDate, destinationAirport, departureAirport);
                            flightsPassingOver4 = printSearchResult(flightBefore3Days, "3 days before the search date", flightsPassingOver1.size() + flightsPassingOver3.size() + flightsPassingOver2.size(), cabinType, noOfPassenger);

                            flightOnActualDay = flightScheduleSessionBean.listOfConnectingFlightRecords(currentSearchReturnDate, destinationAirport, departureAirport);
                            flightsPassingOver5 = printSearchResult(flightOnActualDay, "On the actual day of the search date", flightsPassingOver1.size() + flightsPassingOver3.size() + flightsPassingOver2.size() + flightsPassingOver4.size(), cabinType, noOfPassenger);

                            flightAfter3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDays(currentSearchReturnDate, destinationAirport, departureAirport);
                            flightsPassingOver6 = printSearchResult(flightAfter3Days, "3 days after the search date", flightsPassingOver1.size() + flightsPassingOver3.size() + flightsPassingOver2.size() + flightsPassingOver4.size() + flightsPassingOver5.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver4, flightsPassingOver5);
                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver6, null);

                        } else if (indictatorConnectFlightOrNot.equals("2")) {// direct
                            //fly over
                            flightBefore3Days = flightScheduleSessionBean.getDirectFlight(dateThreeDateBefore, searchDateFO, departureAirport, destinationAirport);
                            flightsPassingOver1 = printSearchResult(flightBefore3Days, "3 days before the search date", 0, cabinType, noOfPassenger);

                            flightOnActualDay = flightScheduleSessionBean.getDirectFlight(searchDateFO, gActualEnd, departureAirport, destinationAirport);
                            flightsPassingOver2 = printSearchResult(flightOnActualDay, "On the actual day of the search date", flightsPassingOver1.size(), cabinType, noOfPassenger);
                            flightAfter3Days = flightScheduleSessionBean.getDirectFlight(gActualEnd, dateThreeDateAfter, departureAirport, destinationAirport);
                            flightsPassingOver3 = printSearchResult(flightAfter3Days, "3 days after the search date", flightsPassingOver1.size() + flightsPassingOver2.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(flightsPassingOver1, flightsPassingOver2, flightsPassingOver3);

                            //fly back
                            flightBefore3Days = flightScheduleSessionBean.getDirectFlight(returnDateThreeDateBefore, currentSearchReturnDate, destinationAirport, departureAirport);
                            flightsPassingOver4 = printSearchResult(flightBefore3Days, "3 days before the search date", flightsPassingOver1.size() + flightsPassingOver2.size() + flightsPassingOver3.size(), cabinType, noOfPassenger);

                            flightOnActualDay = flightScheduleSessionBean.getDirectFlight(currentSearchReturnDate, returnGActualEnd, destinationAirport, departureAirport);
                            flightsPassingOver5 = printSearchResult(flightOnActualDay, "On the actual day of the search date", flightsPassingOver1.size() + flightsPassingOver2.size() + flightsPassingOver3.size() + flightsPassingOver4.size(), cabinType, noOfPassenger);

                            flightAfter3Days = flightScheduleSessionBean.getDirectFlight(returnGActualEnd, returnDateThreeDateAfter, destinationAirport, departureAirport);
                            flightsPassingOver6 = printSearchResult(flightAfter3Days, "3 days after the search date", flightsPassingOver1.size() + flightsPassingOver2.size() + flightsPassingOver3.size() + flightsPassingOver4.size() + flightsPassingOver5.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver4, flightsPassingOver5);
                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver6, null);
                        } else if (indictatorConnectFlightOrNot.equals("3")) {// no preference
                            // fly over
                            flightBefore3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDays(searchDateFO, departureAirport, destinationAirport);
                            flightBundle = flightScheduleSessionBean.getDirectFlight(dateThreeDateBefore, searchDateFO, departureAirport, destinationAirport);
                            flightBundle = combineAllThreeFlights(flightBefore3Days, flightBundle, null);
                            flightBundle.sort(sortFlightScheduleDepart);
                            flightsPassingOver1 = printSearchResult(flightBundle, "3 days before the search date", 0, cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver1, null);

                            flightOnActualDay = flightScheduleSessionBean.listOfConnectingFlightRecords(searchDateFO, departureAirport, destinationAirport);
                            flightBundle = flightScheduleSessionBean.getDirectFlight(searchDateFO, gActualEnd, departureAirport, destinationAirport);
                            flightBundle = combineAllThreeFlights(flightOnActualDay, flightBundle, null);
                            flightBundle.sort(sortFlightScheduleDepart);
                            flightsPassingOver2 = printSearchResult(flightBundle, "On the actual day of the search date", flightsPassingOver1.size(), cabinType, noOfPassenger);
                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver2, null);

                            flightAfter3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDays(searchDateFO, departureAirport, destinationAirport);
                            flightBundle = flightScheduleSessionBean.getDirectFlight(gActualEnd, dateThreeDateAfter, departureAirport, destinationAirport);
                            flightBundle = combineAllThreeFlights(flightAfter3Days, flightBundle, null);
                            flightBundle.sort(sortFlightScheduleDepart);
                            flightsPassingOver3 = printSearchResult(flightBundle, "3 days after the search date", flightsPassingOver1.size() + flightsPassingOver2.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver3, null);
                            //fly back
                            flightBefore3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDays(currentSearchReturnDate, destinationAirport, departureAirport);
                            flightBundle = flightScheduleSessionBean.getDirectFlight(returnDateThreeDateBefore, currentSearchReturnDate, destinationAirport, departureAirport);
                            flightBundle = combineAllThreeFlights(flightBefore3Days, flightBundle, null);
                            flightBundle.sort(sortFlightScheduleDepart);
                            flightsPassingOver4 = printSearchResult(flightBundle, "3 days before the search date", flightsPassingOver1.size() + flightsPassingOver2.size() + flightsPassingOver3.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver4, null);

                            flightOnActualDay = flightScheduleSessionBean.listOfConnectingFlightRecords(currentSearchReturnDate, destinationAirport, departureAirport);
                            flightBundle = flightScheduleSessionBean.getDirectFlight(currentSearchReturnDate, returnGActualEnd, destinationAirport, departureAirport);
                            flightBundle = combineAllThreeFlights(flightOnActualDay, flightBundle, null);
                            flightBundle.sort(sortFlightScheduleDepart);
                            flightsPassingOver5 = printSearchResult(flightBundle, "On the actual day of the search date", flightsPassingOver1.size() + flightsPassingOver2.size() + flightsPassingOver3.size() + flightsPassingOver4.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver5, null);

                            flightAfter3Days = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDays(currentSearchReturnDate, destinationAirport, departureAirport);
                            flightBundle = flightScheduleSessionBean.getDirectFlight(returnGActualEnd, dateThreeDateAfter, destinationAirport, departureAirport);
                            flightBundle = combineAllThreeFlights(flightAfter3Days, flightBundle, null);
                            flightBundle.sort(sortFlightScheduleDepart);
                            flightsPassingOver6 = printSearchResult(flightBundle, "3 days after the search date", flightsPassingOver1.size() + flightsPassingOver2.size() + flightsPassingOver3.size() + flightsPassingOver4.size() + flightsPassingOver5.size(), cabinType, noOfPassenger);

                            listOfFlightBundles = combineAllThreeFlights(listOfFlightBundles, flightsPassingOver6, null);
                        }
                    }
                } catch (FlightRouteDoesNotExistException ex) {
                    System.out.println("There is no flights");
                } catch (NumberFormatException ex) {
                    System.out.println("You have invalid input");
                    break;
                }
                try {
                    System.out.println("Please enter the flight you want to reserve for flying over:");
                    List<FlightBundle> passingOverToReservation = new ArrayList<>();
                    String flight = sc.nextLine();
                    FlightBundle fb = listOfFlightBundles.get(Integer.parseInt(flight) - 1);
                    AirportEntity origin = fb.getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation();
                    AirportEntity destination = fb.getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation();
                    if (fb.getDepartTwo() != null) {
                        destination = fb.getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation();
                    }
                    if (fb.getDepartThree() != null) {
                        destination = fb.getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation();
                    }
                    passingOverToReservation.add(fb);
                    while (!flight.equalsIgnoreCase("Y")) {
                        System.out.println("Do you still want to add? Y/N");
                        flight = sc.nextLine();
                        if (!flight.equalsIgnoreCase("Y")) {
                            break;
                        }
                        System.out.println("Please enter the flight you want to reserve:");
                        flight = sc.nextLine();
                        if (fb.getDepartOne().getFlightSchedulePlan().getFlightNumber().equals(listOfFlightBundles.get(Integer.parseInt(flight) - 1).getDepartOne().getFlightSchedulePlan().getFlightNumber())) {
                            if (fb.getDepartTwo() != null && listOfFlightBundles.get(Integer.parseInt(flight) - 1).getDepartTwo() != null) {
                                if (!fb.getDepartTwo().getFlightSchedulePlan().getFlightNumber().equals(listOfFlightBundles.get(Integer.parseInt(flight) - 1).getDepartTwo().getFlightSchedulePlan().getFlightNumber())) {
                                    System.out.println("Invalid flight number. You must only select the same flight number");
                                    break;
                                } else if (fb.getDepartTwo().getFlightSchedulePlan().getFlightNumber().equals(listOfFlightBundles.get(Integer.parseInt(flight) - 1).getDepartTwo().getFlightSchedulePlan().getFlightNumber())) {
                                    if (fb.getDepartThree() != null && listOfFlightBundles.get(Integer.parseInt(flight) - 1).getDepartThree() != null) {
                                        if (!fb.getDepartThree().getFlightSchedulePlan().getFlightNumber().equals(listOfFlightBundles.get(Integer.parseInt(flight) - 1).getDepartThree().getFlightSchedulePlan().getFlightNumber())) {
                                            System.out.println("Invalid flight number. You must only select the same flight number");
                                            break;
                                        }
                                    }
                                }

                            }
                        } else if (!fb.getDepartOne().getFlightSchedulePlan().getFlightNumber().equals(listOfFlightBundles.get(Integer.parseInt(flight) - 1).getDepartOne().getFlightSchedulePlan().getFlightNumber())) {
                            System.out.println("Invalid flight number. You must only select the same flight number");
                            break;
                        }
                        FlightBundle f2 = listOfFlightBundles.get(Integer.parseInt(flight) - 1);
                        passingOverToReservation.add(f2);
                    }
                    for (int i = 0; i < passingOverToReservation.size(); i++) {
                        System.out.println("---------------------");
                        System.out.println("flight no:" + passingOverToReservation.get(i).getDepartOne().getFlightSchedulePlan().getFlightNumber() + " || departure date time" + format.format(passingOverToReservation.get(i).getDepartOne().getDepartureDateTime().getTime()) + " cabin type: " + passingOverToReservation.get(i).getDepartOneCabinClassType() + " fare :" + passingOverToReservation.get(i).getDepartOneFare().getFareAmount());
                        if (passingOverToReservation.get(i).getDepartTwo() != null) {
                            System.out.println("flight no:" + passingOverToReservation.get(i).getDepartTwo().getFlightSchedulePlan().getFlightNumber() + " || departure date time" + format.format(passingOverToReservation.get(i).getDepartTwo().getDepartureDateTime().getTime()) + " cabin type: " + passingOverToReservation.get(i).getDepartTwoCabinClassType() + " fare :" + passingOverToReservation.get(i).getDepartTwoFare().getFareAmount());

                        }
                        if (passingOverToReservation.get(i).getDepartThree() != null) {
                            System.out.println("flight no:" + passingOverToReservation.get(i).getDepartThree().getFlightSchedulePlan().getFlightNumber() + " || departure date time" + format.format(passingOverToReservation.get(i).getDepartThree().getDepartureDateTime().getTime()) + " cabin type: " + passingOverToReservation.get(i).getDepartThreeCabinClassType() + " fare :" + passingOverToReservation.get(i).getDepartThreeFare().getFareAmount());

                        }
                        System.out.println("---------------------");
                    }
                    reserveFlight(passingOverToReservation, origin, destination, Integer.parseInt(passenger));
                } catch (NullPointerException | IndexOutOfBoundsException ex) {
                    System.out.println("You have input invalid id. Please try again");
                    break;
                }
            } catch (NumberFormatException ex) {
                System.out.println("You have invalid input");
                break;

            }

        }
    }

    public List<FlightBundle> checkCabinClassDisplayable(List<FlightBundle> listOfSearchFlight, List<String> cabinType, int noOfPassenger) {

        List<FlightBundle> tempFlightList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        if (cabinType != null && cabinType.size() == 1 && cabinType.get(0).equalsIgnoreCase("All")) {
            cabinType = new ArrayList<String>();
            cabinType.add("F");
            cabinType.add("W");
            cabinType.add("J");
            cabinType.add("Y");
        }
        for (int i = 0; i < listOfSearchFlight.size(); i++) {
            CabinClassType tempDisplayCabin = null;

            HashMap<String, FareEntity> value = getAvaiableCabinTypeReturnFareEntity(listOfSearchFlight.get(i).getDepartOne(), noOfPassenger);
            for (int j = 0; j < cabinType.size(); j++) {
                FlightBundle fb = new FlightBundle();
                if (value.get(cabinType.get(j)) != null && !value.get(cabinType.get(j)).getFareAmount().equals(BigDecimal.ZERO)) {
                    fb.setDepartOne(listOfSearchFlight.get(i).getDepartOne());
                    fb.setDepartOneFare(value.get(cabinType.get(j)));
                    if (cabinType.get(j).equals("F")) {
                        tempDisplayCabin = CabinClassType.F;
                    } else if (cabinType.get(j).equals("J")) {
                        tempDisplayCabin = CabinClassType.J;
                    } else if (cabinType.get(j).equals("W")) {
                        tempDisplayCabin = CabinClassType.W;
                    } else if (cabinType.get(j).equals("Y")) {
                        tempDisplayCabin = CabinClassType.Y;
                    }
                    fb.setDepartOneCabinClassType(tempDisplayCabin);

                    if (listOfSearchFlight.get(i).getDepartTwo() != null) {

                        HashMap<String, FareEntity> value1 = getAvaiableCabinTypeReturnFareEntity(listOfSearchFlight.get(i).getDepartTwo(), noOfPassenger);
                        for (int k = 0; k < cabinType.size(); k++) {
                            FlightBundle fbTwo = new FlightBundle();
                            if (value1.get(cabinType.get(k)) != null && !value1.get(cabinType.get(k)).getFareAmount().equals(BigDecimal.ZERO)) {
                                CabinClassType tempDisplayCabin1 = null;
                                if (cabinType.get(k).equals("F")) {
                                    tempDisplayCabin1 = CabinClassType.F;
                                } else if (cabinType.get(k).equals("J")) {
                                    tempDisplayCabin1 = CabinClassType.J;
                                } else if (cabinType.get(k).equals("W")) {
                                    tempDisplayCabin1 = CabinClassType.W;
                                } else if (cabinType.get(k).equals("Y")) {
                                    tempDisplayCabin1 = CabinClassType.Y;
                                }

                                fbTwo.setDepartTwo(listOfSearchFlight.get(i).getDepartTwo());
                                fbTwo.setDepartTwoFare(value1.get(cabinType.get(k)));
                                fbTwo.setDepartTwoCabinClassType(tempDisplayCabin1);
                                fbTwo.setDepartOne(listOfSearchFlight.get(i).getDepartOne());
                                fbTwo.setDepartOneFare(value.get(cabinType.get(j)));
                                CabinClassType tempCabinTypeOne = null;

                                if (cabinType.get(j).equals("F")) {
                                    tempCabinTypeOne = CabinClassType.F;
                                } else if (cabinType.get(j).equals("J")) {
                                    tempCabinTypeOne = CabinClassType.J;
                                } else if (cabinType.get(j).equals("W")) {
                                    tempCabinTypeOne = CabinClassType.W;
                                } else if (cabinType.get(j).equals("Y")) {
                                    tempCabinTypeOne = CabinClassType.Y;
                                }
                                fbTwo.setDepartOneCabinClassType(tempCabinTypeOne);
                                //fbTwo.setDepartOne(fb.getDepartOne());
                                //fbTwo.setDepartOneFare(fb.getDepartOneFare());
                                //fbTwo.setDepartOneCabinClassType(fb.getDepartOneCabinClassType());

                                if (listOfSearchFlight.get(i).getDepartThree() != null) {

                                    HashMap<String, FareEntity> value2 = getAvaiableCabinTypeReturnFareEntity(listOfSearchFlight.get(i).getDepartThree(), noOfPassenger);
                                    for (int l = 0; l < cabinType.size(); l++) {
                                        FlightBundle fbThree = new FlightBundle();
                                        if (value2.get(cabinType.get(l)) != null && !value2.get(cabinType.get(l)).getFareAmount().equals(BigDecimal.ZERO)) {
                                            fbThree.setDepartThree(listOfSearchFlight.get(i).getDepartThree());
                                            fbThree.setDepartThreeFare(value2.get(cabinType.get(l)));
                                            CabinClassType tempDisplayCabin2 = null;
                                            if (cabinType.get(l).equals("F")) {
                                                tempDisplayCabin2 = CabinClassType.F;
                                            } else if (cabinType.get(l).equals("J")) {
                                                tempDisplayCabin2 = CabinClassType.J;
                                            } else if (cabinType.get(l).equals("W")) {
                                                tempDisplayCabin2 = CabinClassType.W;
                                            } else if (cabinType.get(l).equals("Y")) {
                                                tempDisplayCabin2 = CabinClassType.Y;
                                            }
                                            fbThree.setDepartThreeCabinClassType(tempDisplayCabin2);

                                            fbThree.setDepartOne(listOfSearchFlight.get(i).getDepartOne());
                                            fbThree.setDepartOneFare(value.get(cabinType.get(j)));
                                            if (cabinType.get(j).equals("F")) {
                                                tempCabinTypeOne = CabinClassType.F;
                                            } else if (cabinType.get(j).equals("J")) {
                                                tempCabinTypeOne = CabinClassType.J;
                                            } else if (cabinType.get(j).equals("W")) {
                                                tempCabinTypeOne = CabinClassType.W;
                                            } else if (cabinType.get(j).equals("Y")) {
                                                tempCabinTypeOne = CabinClassType.Y;
                                            }
                                            fbThree.setDepartOneCabinClassType(tempCabinTypeOne);

                                            fbThree.setDepartTwo(listOfSearchFlight.get(i).getDepartTwo());
                                            fbThree.setDepartTwoCabinClassType(tempDisplayCabin1);
                                            fbThree.setDepartTwoFare(value1.get(cabinType.get(k)));
                                            tempFlightList.add(fbThree);
                                        }

                                    }

                                } else {

                                    tempFlightList.add(fbTwo);
                                }
                            }
                        }
                    } else {
                        //fb.one 
                        tempFlightList.add(fb);
                    }
                }
            }

        }

        return tempFlightList;
    }

    public List<FlightBundle> printSearchResult(List<FlightBundle> tempSearchFlight, String nDays, int tempIndex, List<String> cabinType, int noOfPassenger) {
        int index = tempIndex;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        List<FlightBundle> listOfSearchFlight = checkCabinClassDisplayable(tempSearchFlight, cabinType, noOfPassenger);

        System.out.printf("%-15s %-45s %-45s %-25s %-25s", "", "", nDays, "", "");
        System.out.println();
        System.out.printf("%-5s %-15s %-45s %-45s %-25s %-25s %-10s %-17s", "Id", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time", "Pricing", "Cabin Type");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        BigDecimal unitPriceDepartOne = BigDecimal.ZERO;
        BigDecimal unitPriceDepartTwo = BigDecimal.ZERO;
        BigDecimal unitPriceDepartThree = BigDecimal.ZERO;

        BigDecimal totalDepartOne = BigDecimal.ZERO;
        BigDecimal totalDepartTwo = BigDecimal.ZERO;
        BigDecimal totalDepartThree = BigDecimal.ZERO;

        BigDecimal unitPriceDepartReturnOne = BigDecimal.ZERO;
        BigDecimal unitPriceDepartReturnTwo = BigDecimal.ZERO;
        BigDecimal unitPriceDepartReturnThree = BigDecimal.ZERO;

        BigDecimal totalReturnOne = BigDecimal.ZERO;
        BigDecimal totalReturnTwo = BigDecimal.ZERO;
        BigDecimal totalReturnThree = BigDecimal.ZERO;
        int i = 0;

        for (i = 0; i < listOfSearchFlight.size(); i++) {

            index++;

            String tempDisplayCabin = "";
            CabinClassType cabinClassType1 = listOfSearchFlight.get(i).getDepartOneCabinClassType();
            if (cabinClassType1.equals(CabinClassType.F)) {
                tempDisplayCabin = "First Class";
            } else if (cabinClassType1.equals(CabinClassType.J)) {
                tempDisplayCabin = "Business Class";
            } else if (cabinClassType1.equals(CabinClassType.W)) {
                tempDisplayCabin = "Premium Economic";
            } else if (cabinClassType1.equals(CabinClassType.Y)) {
                tempDisplayCabin = "Economy";
            }
            unitPriceDepartOne = listOfSearchFlight.get(i).getDepartOneFare().getFareAmount();
            totalDepartOne = unitPriceDepartOne.multiply(new BigDecimal(noOfPassenger));
            String firstDepartTime = format.format(listOfSearchFlight.get(i).getDepartOne().getDepartureDateTime().getTime());
            String firstArrTime = format.format(listOfSearchFlight.get(i).getDepartOne().getArrivalDateTime().getTime());
            System.out.printf("%-5d %-15s %-45s %-45s %-25s %-25s %-10s %-17s", index, listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    firstDepartTime, firstArrTime, String.valueOf(unitPriceDepartOne), tempDisplayCabin);
            System.out.println();
            System.out.println();
            System.out.printf("Sub total price for flight 1: " + String.valueOf(totalDepartOne));
            System.out.println();
            System.out.println();

            if (listOfSearchFlight.get(i).getDepartTwo() != null) {
                System.out.println();
                CabinClassType cabinClassType2 = listOfSearchFlight.get(i).getDepartTwoCabinClassType();
                String tempDisplayCabin1 = "";
                if (cabinClassType2.equals(CabinClassType.F)) {
                    tempDisplayCabin1 = "First Class";
                } else if (cabinClassType2.equals(CabinClassType.J)) {
                    tempDisplayCabin1 = "Business Class";
                } else if (cabinClassType2.equals(CabinClassType.W)) {
                    tempDisplayCabin1 = "Premium Economic";
                } else if (cabinClassType2.equals(CabinClassType.Y)) {
                    tempDisplayCabin1 = "Economy";
                }
                unitPriceDepartTwo = listOfSearchFlight.get(i).getDepartTwoFare().getFareAmount();
                totalDepartTwo = unitPriceDepartTwo.multiply(new BigDecimal(noOfPassenger));
                System.out.println("Connecting flight: ");
                System.out.println();
                System.out.printf("%-5s %-15s %-45s %-45s %-25s %-25s %-10s %-17s", "Id", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time", "Pricing", "Cabin Type");
                String secDepartTime = format.format(listOfSearchFlight.get(i).getDepartTwo().getDepartureDateTime().getTime());
                String secArrTime = format.format(listOfSearchFlight.get(i).getDepartTwo().getArrivalDateTime().getTime());
                System.out.println();
                System.out.printf("%-5d %-15s %-45s %-45s %-25s %-25s %-10s %-17s", index, listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                        listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                        listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                        secDepartTime, secArrTime, String.valueOf(unitPriceDepartTwo), tempDisplayCabin1);
                System.out.println();
                System.out.println();
                System.out.println("Sub total price for flight 2: " + String.valueOf(totalDepartTwo));
                System.out.println();

            }

            if (listOfSearchFlight.get(i).getDepartThree() != null) {
                System.out.println();
                CabinClassType cabinClassType3 = listOfSearchFlight.get(i).getDepartThreeCabinClassType();
                String tempDisplayCabin2 = "";
                if (cabinClassType3.equals(CabinClassType.F)) {
                    tempDisplayCabin2 = "First Class";
                } else if (cabinClassType3.equals(CabinClassType.J)) {
                    tempDisplayCabin2 = "Business Class";
                } else if (cabinClassType3.equals(CabinClassType.W)) {
                    tempDisplayCabin2 = "Premium Economic";
                } else if (cabinClassType3.equals(CabinClassType.Y)) {
                    tempDisplayCabin2 = "Economy";
                }
                unitPriceDepartThree = listOfSearchFlight.get(i).getDepartThreeFare().getFareAmount();
                totalDepartThree = unitPriceDepartThree.multiply(new BigDecimal(noOfPassenger));
                System.out.printf("%-5s %-15s %-45s %-45s %-25s %-25s %-10s %-17s ", "Id", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time", "Pricing", "Cabin Type");
                String thirdDepartTime = format.format(listOfSearchFlight.get(i).getDepartThree().getDepartureDateTime().getTime());
                String thirdArrTime = format.format(listOfSearchFlight.get(i).getDepartThree().getArrivalDateTime().getTime());
                System.out.println();
                System.out.printf("%-5d %-15s %-45s %-45s %-25s %-25s %-10s %-17s ", index, listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                        listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                        listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                        thirdDepartTime, thirdArrTime, String.valueOf(unitPriceDepartThree), tempDisplayCabin2);
                System.out.println();
                System.out.println();
                System.out.println("Sub total price for flight 3: " + String.valueOf(totalDepartThree));
                System.out.println();
            }
            System.out.println("****************************************************************************************************************************************************************************************************************************");

        }
        System.out.println("listofSearchFlight size: " + listOfSearchFlight.size());
        return listOfSearchFlight;
    }

    public FareEntity getFareForCustomer(List<FareEntity> fe, CabinClassType cabinType) {
        FareEntity temp = null;
        BigDecimal min = new BigDecimal("9999999");

        for (int i = 0; i < fe.size(); i++) {
            if (fe.get(i).getCabinType().equals(cabinType) && min.compareTo(fe.get(i).getFareAmount()) == 1) {
                temp = fe.get(i);
                min = fe.get(i).getFareAmount();
            }
        }
        return temp;
    }

    public BigDecimal getHighestFare(List<FareEntity> listOfFe) {
        if (listOfFe == null || listOfFe.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal max = new BigDecimal(-999999);
        for (int i = 0; i < listOfFe.size(); i++) {
            BigDecimal actualVal = listOfFe.get(i).getFareAmount();
            if (max.compareTo(actualVal) == -1) {
                max = actualVal;
            }
        }
        return max;
    }

    public BigDecimal[] getLowestFare(List<FareEntity> listOfFe) {
        //first class (F), business class (j), premium econ(W), econ(y)
        if (listOfFe == null || listOfFe.isEmpty()) {
            return new BigDecimal[4];
        }
        BigDecimal[] min = new BigDecimal[4];
        for (int i = 0; i < listOfFe.size(); i++) {
            if (i == 0) {
                min[0] = new BigDecimal(999999999);
                min[1] = new BigDecimal(999999999);
                min[2] = new BigDecimal(999999999);
                min[3] = new BigDecimal(999999999);
            }
            BigDecimal actualVal = listOfFe.get(i).getFareAmount();
            if (min[0].compareTo(actualVal) == 1 && listOfFe.get(i).getCabinType().equals(CabinClassType.F)) {
                min[0] = actualVal;

            }
            if (min[1].compareTo(actualVal) == 1 && listOfFe.get(i).getCabinType().equals(CabinClassType.J)) {
                min[1] = actualVal;

            }
            if (min[2].compareTo(actualVal) == 1 && listOfFe.get(i).getCabinType().equals(CabinClassType.W)) {
                min[2] = actualVal;

            }
            if (min[3].compareTo(actualVal) == 1 && listOfFe.get(i).getCabinType().equals(CabinClassType.Y)) {
                min[3] = actualVal;

            }
        }
        if (min[0].equals(new BigDecimal("999999999"))) {
            min[0] = BigDecimal.ZERO;

        }
        if (min[1].equals(new BigDecimal("999999999"))) {
            min[1] = BigDecimal.ZERO;

        }
        if (min[2].equals(new BigDecimal("999999999"))) {
            min[2] = BigDecimal.ZERO;

        }
        if (min[3].equals(new BigDecimal("999999999"))) {
            min[3] = BigDecimal.ZERO;

        }
        return min;
    }

    // check if any cabin class has enough capacity to fit the passenger with all cabins
    public HashMap<String, BigDecimal> getAvaiableCabinType(FlightScheduleEntity fs1, int noOfPassenger) {
        //first class (F), business class (j), premium econ(W), econ(y)
        //System.out.println("GetAvailableCabinType:" + fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode() + " : " + fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode() + " flight no" + fs1.getFlightSchedulePlan().getFlightNumber());
        HashMap<String, BigDecimal> availableCabinNPrice = new HashMap<String, BigDecimal>();
        int[] temp = new int[4];
        for (int i = 0; i < fs1.getSeatingPlan().size(); i++) {
            if (!fs1.getSeatingPlan().get(i).isReserved()) {
                if (fs1.getSeatingPlan().get(i).getCabinType().equals(CabinClassType.F)) {
                    temp[0] = temp[0] + 1;
                } else if (fs1.getSeatingPlan().get(i).getCabinType().equals(CabinClassType.J)) {
                    temp[1] = temp[1] + 1;
                } else if (fs1.getSeatingPlan().get(i).getCabinType().equals(CabinClassType.W)) {
                    temp[2] = temp[2] + 1;
                } else if (fs1.getSeatingPlan().get(i).getCabinType().equals(CabinClassType.Y)) {
                    temp[3] = temp[3] + 1;
                }

            }

            if (temp[0] >= noOfPassenger && temp[1] >= noOfPassenger && temp[2] >= noOfPassenger && temp[3] >= noOfPassenger) {
                break;
            }
        }
        BigDecimal[] fare = getLowestFare(fs1.getFlightSchedulePlan().getListOfFare());
        if (temp[0] >= noOfPassenger) {
            availableCabinNPrice.put("F", fare[0]);
        }
        if (temp[1] >= noOfPassenger) {
            availableCabinNPrice.put("J", fare[1]);
        }
        if (temp[0] >= noOfPassenger) {
            availableCabinNPrice.put("W", fare[2]);
        }
        if (temp[0] >= noOfPassenger) {
            availableCabinNPrice.put("Y", fare[3]);
        }
        // System.out.println("availableCabinNPrice" + availableCabinNPrice.size());
        return availableCabinNPrice;

    }

    public HashMap<String, FareEntity> getAvaiableCabinTypeReturnFareEntity(FlightScheduleEntity fs1, int noOfPassenger) {
        //first class (F), business class (j), premium econ(W), econ(y)
        //System.out.println("GetAvailableCabinType:" + fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode() + " : " + fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode() + " flight no" + fs1.getFlightSchedulePlan().getFlightNumber());
        HashMap<String, FareEntity> availableCabinNPrice = new HashMap<String, FareEntity>();
        int[] temp = new int[4];
        for (int i = 0; i < fs1.getSeatingPlan().size(); i++) {
            if (!fs1.getSeatingPlan().get(i).isReserved()) {
                if (fs1.getSeatingPlan().get(i).getCabinType().equals(CabinClassType.F)) {
                    temp[0] = temp[0] + 1;
                } else if (fs1.getSeatingPlan().get(i).getCabinType().equals(CabinClassType.J)) {
                    temp[1] = temp[1] + 1;
                } else if (fs1.getSeatingPlan().get(i).getCabinType().equals(CabinClassType.W)) {
                    temp[2] = temp[2] + 1;
                } else if (fs1.getSeatingPlan().get(i).getCabinType().equals(CabinClassType.Y)) {
                    temp[3] = temp[3] + 1;
                }

            }

            if (temp[0] >= noOfPassenger && temp[1] >= noOfPassenger && temp[2] >= noOfPassenger && temp[3] >= noOfPassenger) {
                break;
            }
        }
        FareEntity[] fare = getLowestFareReturnFareEntity(fs1.getFlightSchedulePlan().getListOfFare());
        if (temp[0] >= noOfPassenger) {
            availableCabinNPrice.put("F", fare[0]);
        }
        if (temp[1] >= noOfPassenger) {
            availableCabinNPrice.put("J", fare[1]);
        }
        if (temp[0] >= noOfPassenger) {
            availableCabinNPrice.put("W", fare[2]);
        }
        if (temp[0] >= noOfPassenger) {
            availableCabinNPrice.put("Y", fare[3]);
        }
        // System.out.println("availableCabinNPrice" + availableCabinNPrice.size());
        return availableCabinNPrice;

    }

    public FareEntity[] getLowestFareReturnFareEntity(List<FareEntity> listOfFe) {
        //first class (F), business class (j), premium econ(W), econ(y)
        if (listOfFe == null || listOfFe.isEmpty()) {
            return null;
        }
        FareEntity[] fe = new FareEntity[4];
        BigDecimal[] min = new BigDecimal[4];
        for (int i = 0; i < listOfFe.size(); i++) {
            if (i == 0) {
                min[0] = new BigDecimal(999999999);
                min[1] = new BigDecimal(999999999);
                min[2] = new BigDecimal(999999999);
                min[3] = new BigDecimal(999999999);
            }
            BigDecimal actualVal = listOfFe.get(i).getFareAmount();
            if (min[0].compareTo(actualVal) == 1 && listOfFe.get(i).getCabinType().equals(CabinClassType.F)) {
                min[0] = actualVal;
                fe[0] = listOfFe.get(i);

            }
            if (min[1].compareTo(actualVal) == 1 && listOfFe.get(i).getCabinType().equals(CabinClassType.J)) {
                min[1] = actualVal;
                fe[1] = listOfFe.get(i);
            }
            if (min[2].compareTo(actualVal) == 1 && listOfFe.get(i).getCabinType().equals(CabinClassType.W)) {
                min[2] = actualVal;
                fe[2] = listOfFe.get(i);
            }
            if (min[3].compareTo(actualVal) == 1 && listOfFe.get(i).getCabinType().equals(CabinClassType.Y)) {
                min[3] = actualVal;
                fe[3] = listOfFe.get(i);
            }
        }
        if (min[0].equals(new BigDecimal("999999999"))) {
            fe[0] = null;

        }
        if (min[1].equals(new BigDecimal("999999999"))) {
            fe[1] = null;

        }
        if (min[2].equals(new BigDecimal("999999999"))) {
            fe[2] = null;

        }
        if (min[3].equals(new BigDecimal("999999999"))) {
            fe[3] = null;

        }
        return fe;
    }

    public List<FlightBundle> combineAllThreeFlights(List<FlightBundle> threeDaysBefore, List<FlightBundle> onTheDay, List<FlightBundle> threeDaysAfter) {
        List<FlightBundle> combination = new ArrayList<>();
        if (threeDaysBefore != null && !threeDaysBefore.isEmpty()) {
            for (FlightBundle before : threeDaysBefore) {
                combination.add(before);
            }
        }

        if (onTheDay != null && !onTheDay.isEmpty()) {
            for (FlightBundle before : onTheDay) {
                combination.add(before);
            }
        }
        if (threeDaysAfter != null && !threeDaysAfter.isEmpty()) {
            for (FlightBundle after : threeDaysAfter) {
                combination.add(after);
            }
        }
        return combination;
    }

    public List<FlightBundle> processListGetCabinClassAndSeatAva(List<FlightBundle> listOfODQuery, CabinClassType cabinType, int noOfPassenger, String typeOfFlight) {

        List<FlightBundle> tempList = new ArrayList<FlightBundle>();
        if (typeOfFlight.equals("Connecting")) {

            for (int i = 0; i < listOfODQuery.size(); i++) {
                int count = 0;
                int actualCount = 0;
                if (listOfODQuery.get(i).getDepartOne() != null) {
                    count++;
                }
                if (listOfODQuery.get(i).getDepartTwo() != null) {
                    count++;
                }
                if (listOfODQuery.get(i).getDepartThree() != null) {
                    count++;
                }
                if (checkSeatGreaterThanPassenger(listOfODQuery.get(i).getDepartOne(), cabinType, noOfPassenger)) {
                    actualCount++;
                }
                if (listOfODQuery.get(i).getDepartTwo() != null && checkSeatGreaterThanPassenger(listOfODQuery.get(i).getDepartTwo(), cabinType, noOfPassenger)) {
                    actualCount++;
                }
                if (listOfODQuery.get(i).getDepartThree() != null && checkSeatGreaterThanPassenger(listOfODQuery.get(i).getDepartThree(), cabinType, noOfPassenger)) {
                    actualCount++;
                }
                if (count == actualCount) {
                    tempList.add(listOfODQuery.get(i));
                }
            }
        } else if (typeOfFlight.equals("Direct")) {
            for (int i = 0; i < listOfODQuery.size(); i++) {
                if (checkSeatGreaterThanPassenger(listOfODQuery.get(i).getDepartOne(), cabinType, noOfPassenger)) {
                    tempList.add(listOfODQuery.get(i));
                }
            }
        }

        return tempList;
    }

    public boolean checkSeatGreaterThanPassenger(FlightScheduleEntity fs1, CabinClassType cabinType, int noOfPassenger) {

        int countFs1 = 0;
        int countFs2 = 0;
        int countFs3 = 0;

        for (int i = 0; i < fs1.getSeatingPlan().size(); i++) {
            if ((cabinType == null && !fs1.getSeatingPlan().get(i).isReserved()) || (cabinType != null && !fs1.getSeatingPlan().get(i).isReserved() && fs1.getSeatingPlan().get(i).getCabinType().equals(cabinType))) {
                countFs1++;
                if (countFs1 == noOfPassenger) {
                    return true;
                }
            }
        }

        return false;
    }

    //index 0 start flight, index 1 connecting/end flight/returnflight, index 2 connecting/end flight
    public void reserveFlight(List<FlightBundle> listOfflightBundleForReservation, AirportEntity origin, AirportEntity destination, int numberOfPassengers) {
        Scanner sc = new Scanner(System.in);
        List<FlightReservationEntity> listOfFlightRes = new ArrayList<>();
        List<PassengerEntity> listOfPassengers = new ArrayList<>();

        FlightBundle firstBundle = listOfflightBundleForReservation.get(0);
        FlightScheduleEntity fs1 = firstBundle.getDepartOne();
        CabinClassType cabinForFs1 = firstBundle.getDepartOneCabinClassType();
        FareEntity fareForFs1 = firstBundle.getDepartOneFare();

        FlightScheduleEntity fs2 = firstBundle.getDepartTwo();
        CabinClassType cabinForFs2 = firstBundle.getDepartTwoCabinClassType();
        FareEntity fareForFs2 = firstBundle.getDepartTwoFare();

        FlightScheduleEntity fs3 = firstBundle.getDepartThree();
        CabinClassType cabinForFs3 = firstBundle.getDepartThreeCabinClassType();
        FareEntity fareForFs3 = firstBundle.getDepartThreeFare();

        FlightScheduleEntity returnFs1 = firstBundle.getReturnOne();
        CabinClassType cabinForReturnFs1 = firstBundle.getReturnOneCabinClassType();
        FareEntity fareForReturnFs1 = firstBundle.getReturnOneFare();

        FlightScheduleEntity returnFs2 = firstBundle.getReturnTwo();
        CabinClassType cabinForReturnFs2 = firstBundle.getReturnTwoCabinClassType();
        FareEntity fareForReturnFs2 = firstBundle.getReturnTwoFare();

        FlightScheduleEntity returnFs3 = firstBundle.getReturnThree();
        CabinClassType cabinForReturnFs3 = firstBundle.getReturnThreeCabinClassType();
        FareEntity fareForReturnFs3 = firstBundle.getReturnThreeFare();

        FlightReservationEntity flightRes = new FlightReservationEntity(origin.getIataAirportCode(), destination.getIataAirportCode(), BigDecimal.ZERO, customer);

        FlightReservationEntity returnFlightRes = null;
        if (returnFs1 != null) {
            returnFlightRes = new FlightReservationEntity(destination.getIataAirportCode(), origin.getIataAirportCode(), BigDecimal.ZERO, customer);
        }

        IndividualFlightReservationEntity indivResForFs1 = new IndividualFlightReservationEntity(fs1, customer, BigDecimal.ZERO, flightRes);
        IndividualFlightReservationEntity indivResForFs2 = new IndividualFlightReservationEntity(fs2, customer, BigDecimal.ZERO, flightRes);
        IndividualFlightReservationEntity indivResForFs3 = new IndividualFlightReservationEntity(fs3, customer, BigDecimal.ZERO, flightRes);

        IndividualFlightReservationEntity indivResForReturnFs1 = new IndividualFlightReservationEntity(returnFs1, customer, BigDecimal.ZERO, returnFlightRes);
        IndividualFlightReservationEntity indivResForReturnFs2 = new IndividualFlightReservationEntity(returnFs2, customer, BigDecimal.ZERO, returnFlightRes);
        IndividualFlightReservationEntity indivResForReturnFs3 = new IndividualFlightReservationEntity(returnFs3, customer, BigDecimal.ZERO, returnFlightRes);

        for (FlightBundle flightBundleForReservation : listOfflightBundleForReservation) {
            System.out.print("Please enter Passenger first name: ");
            String firstName = sc.nextLine();
            System.out.print("Please enter Passenger last name: ");
            String lastName = sc.nextLine();
            System.out.print("Please enter Passenger passport number: ");
            String passportNumber = sc.nextLine().trim();
            PassengerEntity passenger = new PassengerEntity(firstName, lastName, passportNumber);
            listOfPassengers.add(passenger);

            cabinForFs1 = flightBundleForReservation.getDepartOneCabinClassType();

            cabinForFs2 = flightBundleForReservation.getDepartTwoCabinClassType();

            cabinForFs3 = flightBundleForReservation.getDepartThreeCabinClassType();

            cabinForReturnFs1 = flightBundleForReservation.getReturnOneCabinClassType();

            cabinForReturnFs2 = flightBundleForReservation.getReturnTwoCabinClassType();

            cabinForReturnFs3 = flightBundleForReservation.getReturnThreeCabinClassType();

            numberOfPassengers += 1;
            //3 flights total
            if (fs2 != null && fs3 != null) {

                List<SeatEntity> listOfSeatsForFs1 = findSeatsForCustomer(fs1, cabinForFs1);

                printSeats(listOfSeatsForFs1);

                System.out.print("Please enter seat number for passenger " + "for passenger " + passenger.getFirstName() + " first flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForFs1 = new FareEntity(fareForFs1.getFareBasisCode(), fareForFs1.getFareAmount(), fareForFs1.getCabinType());
                    seat.setFare(newFareForFs1);
                    seat.setPassenger(passenger);
                    indivResForFs1.getListOfSeats().add(seat);
                    indivResForFs1.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForFs1.getAmount().add(newFareForFs1.getFareAmount());
                    indivResForFs1.setAmount(totalAmt);
                }

                //create fs2
                List<SeatEntity> listOfSeatsForFs2 = findSeatsForCustomer(fs2, cabinForFs2);

                printSeats(listOfSeatsForFs2);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " second flight: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForFs2);
                if (seatfs2 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForFs2 = new FareEntity(fareForFs2.getFareBasisCode(), fareForFs2.getFareAmount(), fareForFs2.getCabinType());
                    seatfs2.setFare(newFareForFs2);

                    seatfs2.setPassenger(passenger);
                    indivResForFs2.getListOfSeats().add(seatfs2);
                    indivResForFs2.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForFs2.getAmount().add(newFareForFs2.getFareAmount());
                    indivResForFs2.setAmount(totalAmt);

                }

                //create fs3
                List<SeatEntity> listOfSeatsForFs3 = findSeatsForCustomer(fs3, cabinForFs3);

                printSeats(listOfSeatsForFs3);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " third flight: ");
                String seatNumberfs3 = sc.nextLine().trim();
                SeatEntity seatfs3 = findSeat(seatNumberfs3, listOfSeatsForFs3);
                if (seatfs3 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs3.setReserved(true);
                    FareEntity newFareForFs3 = new FareEntity(fareForFs3.getFareBasisCode(), fareForFs3.getFareAmount(), fareForFs3.getCabinType());
                    seatfs3.setFare(newFareForFs3);

                    seatfs3.setPassenger(passenger);
                    indivResForFs3.getListOfSeats().add(seatfs3);
                    indivResForFs3.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForFs3.getAmount().add(newFareForFs3.getFareAmount());
                    indivResForFs3.setAmount(totalAmt);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();

                if (!flightRes.getListOfIndividualFlightRes().contains(indivResForFs1) && !flightRes.getListOfIndividualFlightRes().contains(indivResForFs2)
                        && !flightRes.getListOfIndividualFlightRes().contains(indivResForFs3)) {
                    indivResForFs1.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs1);
                    indivResForFs2.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs2);
                    indivResForFs3.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs3);
                }

            } else if (fs2 != null && fs3 == null) { // 2 flights total

                List<SeatEntity> listOfSeatsForFs1 = findSeatsForCustomer(fs1, cabinForFs1);

                printSeats(listOfSeatsForFs1);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " first flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForFs1 = new FareEntity(fareForFs1.getFareBasisCode(), fareForFs1.getFareAmount(), fareForFs1.getCabinType());
                    seat.setFare(newFareForFs1);

                    seat.setPassenger(passenger);
                    indivResForFs1.getListOfSeats().add(seat);
                    indivResForFs1.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForFs1.getAmount().add(newFareForFs1.getFareAmount());
                    indivResForFs1.setAmount(totalAmt);

                }

                //create fs2
                List<SeatEntity> listOfSeatsForFs2 = findSeatsForCustomer(fs2, cabinForFs2);

                printSeats(listOfSeatsForFs2);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " second flight: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForFs2);
                if (seatfs2 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForFs2 = new FareEntity(fareForFs2.getFareBasisCode(), fareForFs2.getFareAmount(), fareForFs2.getCabinType());
                    seatfs2.setFare(newFareForFs2);

                    seatfs2.setPassenger(passenger);
                    indivResForFs2.getListOfSeats().add(seatfs2);
                    indivResForFs2.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForFs2.getAmount().add(newFareForFs2.getFareAmount());
                    indivResForFs2.setAmount(totalAmt);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();
//                }
                if (!flightRes.getListOfIndividualFlightRes().contains(indivResForFs1)
                        && !flightRes.getListOfIndividualFlightRes().contains(indivResForFs2)) {
                    indivResForFs1.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs1);
                    indivResForFs2.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs2);
                }

            } else if (fs2 == null && fs3 == null) { //1 flight only

                List<SeatEntity> listOfSeatsForFs1 = findSeatsForCustomer(fs1, cabinForFs1);

//             
                printSeats(listOfSeatsForFs1);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " first flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {
                    seat.setReserved(true);
                    FareEntity newFareForFs1 = new FareEntity(fareForFs1.getFareBasisCode(), fareForFs1.getFareAmount(), fareForFs1.getCabinType());
                    seat.setFare(newFareForFs1);

                    seat.setPassenger(passenger);
                    indivResForFs1.getListOfSeats().add(seat);
                    indivResForFs1.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForFs1.getAmount().add(newFareForFs1.getFareAmount());
                    indivResForFs1.setAmount(totalAmt);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();

                if (!flightRes.getListOfIndividualFlightRes().contains(indivResForFs1)) {
                    indivResForFs1.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs1);
                }

            }

            if (!listOfFlightRes.contains(flightRes)) {
                listOfFlightRes.add(flightRes);
            }

            if (returnFs1 != null && returnFs2 != null && returnFs3 != null) {

                List<SeatEntity> listOfSeatsForReturnFs1 = findSeatsForCustomer(returnFs1, cabinForReturnFs1);

                printSeats(listOfSeatsForReturnFs1);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " first return flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForReturnFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForReturnFs1 = new FareEntity(fareForReturnFs1.getFareBasisCode(), fareForReturnFs1.getFareAmount(), fareForReturnFs1.getCabinType());
                    seat.setFare(newFareForReturnFs1);

                    seat.setPassenger(passenger);
                    indivResForReturnFs1.getListOfSeats().add(seat);
                    indivResForReturnFs1.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForReturnFs1.getAmount().add(newFareForReturnFs1.getFareAmount());
                    indivResForReturnFs1.setAmount(totalAmt);

                }

                //create fs2
                List<SeatEntity> listOfSeatsForReturnFs2 = findSeatsForCustomer(returnFs2, cabinForReturnFs2);

                printSeats(listOfSeatsForReturnFs2);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " second return flight: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForReturnFs2);
                if (seatfs2 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForReturnFs2 = new FareEntity(fareForReturnFs2.getFareBasisCode(), fareForReturnFs2.getFareAmount(), fareForReturnFs2.getCabinType());
                    seatfs2.setFare(newFareForReturnFs2);

                    seatfs2.setPassenger(passenger);
                    indivResForReturnFs2.getListOfSeats().add(seatfs2);
                    indivResForReturnFs2.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForReturnFs2.getAmount().add(newFareForReturnFs2.getFareAmount());
                    indivResForReturnFs2.setAmount(totalAmt);

                }

                //create fs3
                List<SeatEntity> listOfSeatsForReturnFs3 = findSeatsForCustomer(returnFs3, cabinForReturnFs3);

                printSeats(listOfSeatsForReturnFs3);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " third return flight: ");
                String seatNumberfs3 = sc.nextLine().trim();
                SeatEntity seatfs3 = findSeat(seatNumberfs3, listOfSeatsForReturnFs3);
                if (seatfs3 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs3.setReserved(true);
                    FareEntity newFareForReturnFs3 = new FareEntity(fareForReturnFs3.getFareBasisCode(), fareForReturnFs3.getFareAmount(), fareForReturnFs3.getCabinType());
                    seatfs3.setFare(newFareForReturnFs3);

                    seatfs3.setPassenger(passenger);
                    indivResForReturnFs3.getListOfSeats().add(seatfs3);
                    indivResForReturnFs3.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForReturnFs3.getAmount().add(newFareForReturnFs3.getFareAmount());
                    indivResForReturnFs3.setAmount(totalAmt);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();
//                }
                if (!returnFlightRes.getListOfIndividualFlightRes().contains(indivResForReturnFs1) && !returnFlightRes.getListOfIndividualFlightRes().contains(indivResForReturnFs2)
                        && !returnFlightRes.getListOfIndividualFlightRes().contains(indivResForReturnFs3)) {
                    indivResForReturnFs1.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs1);
                    indivResForReturnFs2.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs2);
                    indivResForReturnFs3.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs3);
                }

            } else if (returnFs1 != null && returnFs2 != null && returnFs3 == null) { // only 2 flights

                List<SeatEntity> listOfSeatsForReturnFs1 = findSeatsForCustomer(returnFs1, cabinForReturnFs1);

                printSeats(listOfSeatsForReturnFs1);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " first return flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForReturnFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForReturnFs1 = new FareEntity(fareForReturnFs1.getFareBasisCode(), fareForReturnFs1.getFareAmount(), fareForReturnFs1.getCabinType());
                    seat.setFare(newFareForReturnFs1);

                    seat.setPassenger(passenger);
                    indivResForReturnFs1.getListOfSeats().add(seat);
                    indivResForReturnFs1.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForReturnFs1.getAmount().add(newFareForReturnFs1.getFareAmount());
                    indivResForReturnFs1.setAmount(totalAmt);

                }

                //create fs2
                List<SeatEntity> listOfSeatsForReturnFs2 = findSeatsForCustomer(returnFs2, cabinForReturnFs2);

                printSeats(listOfSeatsForReturnFs2);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " second return flight: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForReturnFs2);
                if (seatfs2 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForReturnFs2 = new FareEntity(fareForReturnFs2.getFareBasisCode(), fareForReturnFs2.getFareAmount(), fareForReturnFs2.getCabinType());
                    seatfs2.setFare(newFareForReturnFs2);

                    seatfs2.setPassenger(passenger);
                    indivResForReturnFs2.getListOfSeats().add(seatfs2);
                    indivResForReturnFs2.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForReturnFs2.getAmount().add(newFareForReturnFs2.getFareAmount());
                    indivResForReturnFs2.setAmount(totalAmt);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();
//                }
                if (!returnFlightRes.getListOfIndividualFlightRes().contains(indivResForReturnFs1)
                        && !returnFlightRes.getListOfIndividualFlightRes().contains(indivResForReturnFs2)) {
                    indivResForReturnFs1.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs1);
                    indivResForReturnFs2.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs2);
                }

            } else if (returnFs1 != null && returnFs2 == null && returnFs3 == null) {

                List<SeatEntity> listOfSeatsForReturnFs1 = findSeatsForCustomer(returnFs1, cabinForReturnFs1);

                printSeats(listOfSeatsForReturnFs1);

                System.out.print("Please enter seat number " + "for passenger " + passenger.getFirstName() + " first return flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForReturnFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForReturnFs1 = new FareEntity(fareForReturnFs1.getFareBasisCode(), fareForReturnFs1.getFareAmount(), fareForReturnFs1.getCabinType());
                    seat.setFare(newFareForReturnFs1);

                    seat.setPassenger(passenger);
                    indivResForReturnFs1.getListOfSeats().add(seat);
                    indivResForReturnFs1.getListOfPassenger().add(passenger);
                    BigDecimal totalAmt = indivResForReturnFs1.getAmount().add(newFareForReturnFs1.getFareAmount());
                    indivResForReturnFs1.setAmount(totalAmt);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();

                if (!returnFlightRes.getListOfIndividualFlightRes().contains(indivResForReturnFs1)) {
                    indivResForReturnFs1.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs1);
                }
            }
            if (returnFlightRes != null && !listOfFlightRes.contains(returnFlightRes)) {
                listOfFlightRes.add(returnFlightRes);
            }

        }
        //end for loop

        //call flight reservation session bean
        System.out.println("Would you like to proceed with the booking? (1 for yes, 2 for no)");
        System.out.print("Please enter choice: ");
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) {
            boolean goodInput = false;

            while (!goodInput) {
                System.out.print("Please enter Credit Card name: ");
                String creditCardName = sc.nextLine();
                System.out.print("Please enter Credit Card Number: ");
                String creditCardNumber = sc.nextLine().trim();
                System.out.print("Please enter CVV: ");
                String cvv = sc.nextLine().trim();
                System.out.print("Please enter Credit Card expiry date (mm/yyyy) : ");
                String expiryDateStr = sc.nextLine();

                String[] dateSplit = expiryDateStr.split("/");
                if (dateSplit.length < 2) {
                    System.out.println("Incorrect input for date!");
                } else {
                    GregorianCalendar expiryDate = new GregorianCalendar();
                    expiryDate.set(GregorianCalendar.YEAR, Integer.parseInt(dateSplit[1]));
                    expiryDate.set(GregorianCalendar.MONTH, Integer.parseInt(dateSplit[0]));

                    for (FlightReservationEntity fr : listOfFlightRes) {
                        fr.setCreditCardExpiryDate(expiryDate);
                        fr.setCreditCardName(creditCardName);
                        fr.setCreditCardNumber(creditCardNumber);
                        fr.setCvv(cvv);

                        fr.getCustomer().getListOfFlightReservation().add(fr);
                    }

                    goodInput = true;
                }
            }
            if (goodInput) {

                for (FlightReservationEntity fr : listOfFlightRes) {
                    BigDecimal totalAmt = BigDecimal.ZERO;
                    for (IndividualFlightReservationEntity indivFlightRes : fr.getListOfIndividualFlightRes()) {
                        totalAmt = totalAmt.add(indivFlightRes.getAmount());
                    }

                    fr.setTotalAmount(totalAmt);
                }

                flightReservationSessionBean.reserveFlights(listOfFlightRes);
                System.out.println("Reservation has been made! Have a good day!");
            }
        } else if (choice.equals("2")) {
            System.out.println("Booking is not created! Have a good day!");
        }
    }

    public List<SeatEntity> findSeatsForCustomer(FlightScheduleEntity fs, CabinClassType cabinType) {
        //get list of seats for cabin customer wants
        List<SeatEntity> listOfSeatsForCabin = new ArrayList<>();
        for (SeatEntity seat : fs.getSeatingPlan()) {
            if (seat.getCabinType() == cabinType && !seat.isReserved()) {
                listOfSeatsForCabin.add(seat);
            }
        }

        return listOfSeatsForCabin;
    }

    public SeatEntity findSeat(String seatNumber, List<SeatEntity> listOfSeats) {

        for (SeatEntity seat : listOfSeats) {
            if (seat.getSeatNumber().equals(seatNumber)) {
                return seat;
            }
        }

        return null;
    }

    public void viewFlightReservations() {
//        Scanner sc = new Scanner(System.in);
        try {
            List<FlightReservationEntity> listOfFlightReservation = flightReservationSessionBean.retrieveListOfReservation(customer.getCustomerId());
            System.out.printf("%-30s%-39s%-40s%-40s%-40s", "Flight Reservation ID", "Origin Location", " Destination Location", "Booked by", "Total Amount");
            System.out.println();
            for (FlightReservationEntity fr : listOfFlightReservation) {
                String name = "";

                if (fr.getCustomer() instanceof FRSCustomerEntity) {
                    FRSCustomerEntity customer = (FRSCustomerEntity) fr.getCustomer();
                    name = customer.getFirstName() + " " + customer.getLastName();
                } else if (fr.getCustomer() instanceof PartnerEntity) {
                    PartnerEntity partner = (PartnerEntity) fr.getCustomer();
                    name = partner.getPartnerName();
                }
                System.out.printf("%-30s%-40s%-39s%-41s%-40s", fr.getFlightReservationId(), fr.getOriginIATACode(), fr.getDestinationIATACode(), name, fr.getTotalAmount());
                System.out.println();
            }

        } catch (CustomerHasNoReservationException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void viewFlightReservationDetails() {
        Scanner sc = new Scanner(System.in);

        viewFlightReservations();
        System.out.println();
        System.out.print("Please enter ID of flight reservation you wish to view: ");
        Long frId = sc.nextLong();
        sc.nextLine();

        try {
            // each flight leg, show passenger - name, seat number , price for seat
            // at the end show total price they paid

            FlightReservationEntity fr = flightReservationSessionBean.getIndividualFlightReservation(frId);
            System.out.println("------Flight Reservations-----");
            System.out.println();
            System.out.println();
            for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
                System.out.println("=====================Individual Flight Reservation=====================");
                System.out.printf("%-30s%-30s%-30s%-30s%-30s%-30s", "Passenger", "Flight Number", "Origin", "Destination", "Seat Number", "Price for Seat");
                System.out.println();
                for (SeatEntity seat : indivFr.getListOfSeats()) {
                    String passengerName = seat.getPassenger().getFirstName() + " " + seat.getPassenger().getLastName();
                    String flightNumber = indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightNumber();
                    String fsOrigin = indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode();
                    String fsDestination = indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();
                    System.out.printf("%-30s%-30s%-30s%-30s%-30s%-30s", passengerName, flightNumber, fsOrigin, fsDestination, seat.getSeatNumber(), seat.getFare().getFareAmount());
                    System.out.println();
                }
                System.out.println();
                System.out.println();
            }
        } catch (FlightReservationDoesNotExistException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public List<CabinClassType> getCabinTypes(FlightScheduleEntity fs) {
        AircraftConfigurationEntity aircraftConfig = fs.getFlightSchedulePlan().getFlightEntity().getAircraftConfig();
        List<CabinClassType> listOfTypes = new ArrayList<>();
        for (CabinClassConfigurationEntity cabin : aircraftConfig.getCabinClasses()) {
            if (!listOfTypes.contains(cabin.getCabinclassType())) {
                listOfTypes.add(cabin.getCabinclassType());
            }
        }
        return listOfTypes;
    }

    public FareEntity getFare(CabinClassType type, List<FareEntity> listOfFare) {
        for (FareEntity fare : listOfFare) {
            if (fare.getCabinType().equals(type)) {
                return fare;
            }
        }
        return null;
    }

    public void printSeats(List<SeatEntity> listOfSeats) {
        System.out.printf("%-15s%-15s%-30s%-15s%-15s%-15s", "Seat ID", "Seat Number", "Cabin Class", "Seat ID", "Seat Number", "Cabin Class");
        System.out.println("\n");
        for (int i = 0; i < listOfSeats.size(); i += 2) {
            SeatEntity seatDisplay1 = listOfSeats.get(i);
            SeatEntity seatDisplay2 = null;
            String cabinType1 = "";
            String cabinType2 = "";

            if (i + 1 < listOfSeats.size()) {
                seatDisplay2 = listOfSeats.get(i + 1);

                if (seatDisplay2.getCabinType().equals(CabinClassType.F)) {
                    cabinType2 = "First Class";
                } else if (seatDisplay2.getCabinType().equals(CabinClassType.J)) {
                    cabinType2 = "Business Class";
                } else if (seatDisplay2.getCabinType().equals(CabinClassType.W)) {
                    cabinType2 = "Premium Economy Class";
                } else if (seatDisplay2.getCabinType().equals(CabinClassType.Y)) {
                    cabinType2 = "Economy Class";
                }
            }

            if (seatDisplay1.getCabinType().equals(CabinClassType.F)) {
                cabinType1 = "First Class";
            } else if (seatDisplay1.getCabinType().equals(CabinClassType.J)) {
                cabinType1 = "Business Class";
            } else if (seatDisplay1.getCabinType().equals(CabinClassType.W)) {
                cabinType1 = "Premium Economy Class";
            } else if (seatDisplay1.getCabinType().equals(CabinClassType.Y)) {
                cabinType1 = "Economy Class";
            }

            if (i + 1 < listOfSeats.size()) {
                System.out.printf("%-15s%-15s%-30s%-15s%-15s%-15s", seatDisplay1.getSeatId(), seatDisplay1.getSeatNumber(), cabinType1, seatDisplay2.getSeatId(), seatDisplay2.getSeatNumber(), cabinType2);
            } else {
                System.out.printf("%-15s%-15s%-30s%-15s%-15s%-15s", seatDisplay1.getSeatId(), seatDisplay1.getSeatNumber(), cabinType1, "-", "-", "-");
            }
            System.out.println();
        }
    }
}
