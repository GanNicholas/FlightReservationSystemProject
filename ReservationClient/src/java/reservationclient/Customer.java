/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightReservationSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
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
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.CabinClassType;
import util.enumeration.UserRole;
import util.exception.CustomerDoesNotExistException;
import util.exception.CustomerExistException;
import util.exception.CustomerHasNoReservationException;
import util.exception.CustomerLoginInvalid;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightReservationDoesNotExistException;

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

    public Customer() {

        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public Customer(CustomerSessionBeanRemote customerSessionBean, FlightScheduleSessionBeanRemote flightScheduleSessionBean, FlightReservationSessionBeanRemote flightReservationSessionBean) {
        this();
        this.customerSessionBean = customerSessionBean;
        this.flightScheduleSessionBean = flightScheduleSessionBean;
        this.flightReservationSessionBean = flightReservationSessionBean;
    }

    public void runApp() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("1. Register Customer");
            System.out.println("2. Customer Login");
            String input = sc.nextLine();
            //while (true) {
            if (input.equals("1")) {
                registerCustomer();
            } else if (input.equals("2")) {
                boolean loginSuccessful = customerLogin();
                if (loginSuccessful) {
                    afterLoginPage();
                }
            }
        } catch (NumberFormatException ex) {
            System.out.println("You have invalid input.");
            runApp();
        }
        //}
    }

    public void afterLoginPage() {
        System.out.println("Welcome ");
        Scanner sc = new Scanner(System.in);
        System.out.println("1. Seach for flight");
        System.out.println("2. View my flight reservations");
        System.out.println("3. View my flight reservation details");
        System.out.println("4. Log out");
        String input = sc.nextLine();
        //while (true) {
        if (input.equals("1")) {
            searchFlight();
        } else if (input.equals("2")) {
            viewFlightReservations(sc);
        } else if (input.equals("3")) {

        } else if (input.equals("4")) {

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
                String indictatorConnectFlightOrNot = sc.nextLine().trim();
                while (!indictatorConnectFlightOrNot.equals("1") && !indictatorConnectFlightOrNot.equals("2") && !indictatorConnectFlightOrNot.equals("3")) {
                    System.out.println("Invalid input");
                    System.out.println("Please choose one of the following:");
                    System.out.println("1. Connecting Flight");
                    System.out.println("2. Direct Flight");
                    indictatorConnectFlightOrNot = sc.nextLine().trim();
                }

                System.out.println("Please enter the cabin type:");
                System.out.println("1. First class.");
                System.out.println("2. Business class");
                System.out.println("3. Premium economy class");
                System.out.println("4. Economy class");
                String strCabinType = sc.nextLine().trim();
                while (Integer.parseInt(strCabinType) > 4 && Integer.parseInt(strCabinType) <= 0) {
                    System.out.println("Invalid cabin class type.");
                    System.out.println("Please enter the cabin type:");
                    System.out.println("1. First class.");
                    System.out.println("2. Business class");
                    System.out.println("3. Premium economy class");
                    System.out.println("4. Economy class");
                    strCabinType = sc.nextLine().trim();
                }
                CabinClassType cabinType = null;
                if (strCabinType.equalsIgnoreCase("1")) {
                    cabinType = CabinClassType.F;
                } else if (strCabinType.equalsIgnoreCase("2")) {
                    cabinType = CabinClassType.J;
                } else if (strCabinType.equalsIgnoreCase("3")) {
                    cabinType = CabinClassType.W;
                } else if (strCabinType.equalsIgnoreCase("4")) {
                    cabinType = CabinClassType.Y;
                }

                System.out.print("Enter departure airport: ");
                String departureAirport = sc.nextLine().trim();

                System.out.print("Enter destination airport: ");
                String destinationAirport = sc.nextLine().trim();

                System.out.print("Enter depature date:(dd/mm/yyyy) ");
                String departureDate = sc.nextLine().trim();
                LocalDate searchDateFO = null;
                String[] splitDepartDate = departureDate.trim().split("/");
                if (splitDepartDate.length == 3) {
                    searchDateFO = LocalDate.of(Integer.valueOf(splitDepartDate[2]), Integer.valueOf(splitDepartDate[1]), Integer.valueOf(splitDepartDate[0]));
                } else {
                    System.out.println("You have invalid date input for departure date. Please be in 'dd/mm/yyyy' format");
                    //   throw new DateInvalidException("You have invalid date input for departure flight date. Please be in 'dd/mm/yyyy' format");
                }

                Date actualSearchFO = Date.from(searchDateFO.atStartOfDay(ZoneId.systemDefault()).toInstant());
                String returnDate = "";
                if (tripType.equals("2")) {
                    System.out.print("Enter return date:(dd/mm/yyyy) ");
                    returnDate = sc.nextLine().trim();
                }
                //convert return time (if exist) to 3 days before and 3 days after
                LocalDate searchDateReturn = null;
                Date currentSearchReturnDate = null;
                String[] splitDepartDateReturn;
                if (tripType.equals("2")) {
                    splitDepartDateReturn = returnDate.trim().split("/");
                    if (splitDepartDateReturn.length == 3) {
                        searchDateReturn = LocalDate.of(Integer.valueOf(splitDepartDateReturn[2]), Integer.valueOf(splitDepartDateReturn[1]), Integer.valueOf(splitDepartDateReturn[0]));
                        currentSearchReturnDate = Date.from(searchDateReturn.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    } else {
                        System.out.println("You have invalid date input for return flight date. Please be in 'dd/mm/yyyy' format");
                    }
                }

                System.out.print("Enter number of passenger: ");
                String passenger = sc.nextLine().trim();
                int noOfPassenger = Integer.parseInt(passenger);

                // start calling searh flight with respectively to (1. one way 2. two ways -> inside of each, see if they want (a)connecting flight, (b)direct flight or (c)borth)
                if (tripType.equals("1")) {// one way
                    if (indictatorConnectFlightOrNot.equals("1")) {// connecting flight
                        getConnectingFlight(actualSearchFO, cabinType, noOfPassenger, departureAirport, destinationAirport);
                    } else if (indictatorConnectFlightOrNot.equals("2")) {//direct flight
                        getDirectFlight(departureAirport, destinationAirport, actualSearchFO, cabinType, noOfPassenger);
                    }
                } else {// two ways
                    if (indictatorConnectFlightOrNot.equals("1")) {
                        getConnectingFlight(actualSearchFO, cabinType, noOfPassenger, departureAirport, destinationAirport);
                        System.out.println("Return Flight result: ");
                        getConnectingFlight(currentSearchReturnDate, cabinType, noOfPassenger, destinationAirport, departureAirport);
                    } else if (indictatorConnectFlightOrNot.equals("2")) {
                        getDirectFlight(departureAirport, destinationAirport, actualSearchFO, cabinType, noOfPassenger);
                        System.out.println("Return Flight result: ");
                        getDirectFlight(destinationAirport, departureAirport, currentSearchReturnDate, cabinType, noOfPassenger);
                    }
                }
            } catch (NumberFormatException ex) {
                System.out.println("You have invalid input");
                searchFlight();
            }
            /*catch (DateInvalidException ex) {
            searchFlight();
        }*/
            System.out.println("Enter '1' to continue or any key to exit");
            String exitOrContinue = sc.nextLine();
            if (!exitOrContinue.equals("1")) {
                break;
            }
        }

    }

    public void printConnectingFlightResult(List<FlightBundle> listOfSearchFlight, String nDay) {
        //connecting flight
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.printf("%-15s %-45s %-45s %-25s %-25s", "", "", nDay, "", "");
        System.out.println();
        System.out.printf("%-15s %-45s %-45s %-25s %-25s ", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time");
        System.out.println();
        for (int i = 0; i < listOfSearchFlight.size(); i++) {
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            String firstDepartTime = format.format(listOfSearchFlight.get(i).getDepartOne().getDepartureDateTime().getTime());
            String firstArrTime = format.format(listOfSearchFlight.get(i).getDepartOne().getArrivalDateTime().getTime());
            System.out.printf("%-15s %-45s %-45s %-25s %-25s ", listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    firstDepartTime, firstArrTime);
            System.out.println();
            System.out.println();
            System.out.println("Connecting flight: ");
            System.out.println();
            System.out.printf("%-15s %-45s %-45s %-25s %-25s ", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time");
            String secDepartTime = format.format(listOfSearchFlight.get(i).getDepartTwo().getDepartureDateTime().getTime());
            String secArrTime = format.format(listOfSearchFlight.get(i).getDepartTwo().getArrivalDateTime().getTime());
            System.out.println();
            System.out.printf("%-15s %-45s %-45s %-25s %-25s ", listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    secDepartTime, secArrTime);
            System.out.println();
            System.out.println();
            if (listOfSearchFlight.get(i).getDepartThree() != null) {
                System.out.printf("%-15s %-45s %-45s %-25s %-25s ", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time");
                String thirdDepartTime = format.format(listOfSearchFlight.get(i).getDepartThree().getDepartureDateTime().getTime());
                String thirdArrTime = format.format(listOfSearchFlight.get(i).getDepartThree().getArrivalDateTime().getTime());
                System.out.println();
                System.out.printf("%-15s %-45s %-45s %-25s %-25s ", listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                        listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                        listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                        thirdDepartTime, thirdArrTime);
                System.out.println();
                System.out.println();
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    public void printDirectFlightResult(List<FlightBundle> listOfSearchFlight, String nDays) {
        //connecting flight
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.printf("%-15s %-45s %-45s %-25s %-25s", "", "", nDays, "", "");
        System.out.println();
        System.out.printf("%-15s %-45s %-45s %-25s %-25s ", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time");
        System.out.println();
        for (int i = 0; i < listOfSearchFlight.size(); i++) {
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            String firstDepartTime = format.format(listOfSearchFlight.get(i).getDepartOne().getDepartureDateTime().getTime());
            String firstArrTime = format.format(listOfSearchFlight.get(i).getDepartOne().getArrivalDateTime().getTime());
            System.out.printf("%-15s %-45s %-45s %-25s %-25s ", listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    firstDepartTime, firstArrTime);
            System.out.println();
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    public void getConnectingFlight(Date actualDay, CabinClassType cabinType, int noOfPassenger, String departureAirport, String destinationAirport) {
        GregorianCalendar gDepart = new GregorianCalendar();
        gDepart.setTime(actualDay);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        List<FlightBundle> listOfFlightSchedules = null;
        List<FlightBundle> flightResultLessThreeDay = null;
        List<FlightBundle> flightResultAftThreeDay = null;
        try {
            flightResultLessThreeDay = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDays(actualDay, departureAirport, destinationAirport);

            listOfFlightSchedules = flightScheduleSessionBean.listOfConnectingFlightRecords(actualDay, departureAirport, destinationAirport);
            flightResultAftThreeDay = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDays(actualDay, departureAirport, destinationAirport);

            List<FlightBundle> flightResult = new ArrayList<FlightBundle>();

            flightResultLessThreeDay = processListGetCabinClassAndSeatAva(flightResultLessThreeDay, cabinType, noOfPassenger, "Connecting");
            flightResult = processListGetCabinClassAndSeatAva(listOfFlightSchedules, cabinType, noOfPassenger, "Connecting");
            flightResultAftThreeDay = processListGetCabinClassAndSeatAva(flightResultAftThreeDay, cabinType, noOfPassenger, "Connecting");

            printConnectingFlightResult(flightResultLessThreeDay, " 3 Days before the booking date");

            printConnectingFlightResult(flightResult, " The actual date you are looking for ");

            printConnectingFlightResult(flightResultAftThreeDay, "3 Days after the booking date ");
        } catch (FlightRouteDoesNotExistException fe) {
            System.out.println("You have invalid O-D");
        }

        // Comparator<FlightScheduleEntity> sortFlightScheduleId = (FlightScheduleEntity p1, FlightScheduleEntity p2) -> Integer.valueOf(p1.getFlightScheduleId().intValue() - p2.getFlightScheduleId().intValue());
        //System.out.println("listOfSearchFlight" + listOfSearchFlight.size());
        //listOfSearchFlight.sort(sortFlightScheduleId);
    }

    public void getDirectFlight(String originIATA, String desIATA, Date actual, CabinClassType cabinType, int noOfPassenger) {
        List<FlightBundle> flightBundleLess3Day = null;
        List<FlightBundle> flightBundleActualDay = null;
        List<FlightBundle> flightBundleAdd3Day = null;
        GregorianCalendar gTempActual = new GregorianCalendar();
        gTempActual.setTime(actual);

        GregorianCalendar dateThreeDateBefore = (GregorianCalendar) gTempActual.clone();
        dateThreeDateBefore.add(GregorianCalendar.DATE, -3);

        GregorianCalendar gActual = (GregorianCalendar) gTempActual.clone();

        GregorianCalendar gActualEnding = (GregorianCalendar) gTempActual.clone();
        gActualEnding.add(GregorianCalendar.SECOND, -1);
        GregorianCalendar gActualEnd = (GregorianCalendar) gTempActual.clone();
        gActualEnd.add(GregorianCalendar.HOUR, 24);
        gActualEnd.add(GregorianCalendar.SECOND, -1);

        GregorianCalendar dateThreeDateAfter = (GregorianCalendar) gTempActual.clone();
        dateThreeDateAfter.add(GregorianCalendar.DATE, 4);
        dateThreeDateAfter.add(GregorianCalendar.SECOND, -1);
        try {

            flightBundleLess3Day = flightScheduleSessionBean.getDirectFlight(dateThreeDateBefore, gActualEnding, originIATA, desIATA);

        } catch (FlightRouteDoesNotExistException fr) {
            System.out.println("Fr1 dont exist1");
        }

        try {

            flightBundleActualDay = flightScheduleSessionBean.getDirectFlight(gActual, gActualEnd, originIATA, desIATA);

        } catch (FlightRouteDoesNotExistException fr) {
            System.out.println("Fr1 dont exist2");
        }

        try {
            gActualEnd.add(GregorianCalendar.SECOND, 1);
            flightBundleAdd3Day = flightScheduleSessionBean.getDirectFlight(gActualEnd, dateThreeDateAfter, originIATA, desIATA);

        } catch (FlightRouteDoesNotExistException fr) {
            System.out.println("Fr1 dont exist3");
        }
        List<FlightBundle> less3DaysFlight = processListGetCabinClassAndSeatAva(flightBundleLess3Day, cabinType, noOfPassenger, "Direct");
        List<FlightBundle> actualFlight = processListGetCabinClassAndSeatAva(flightBundleActualDay, cabinType, noOfPassenger, "Direct");
        List<FlightBundle> add3DaysFlight = processListGetCabinClassAndSeatAva(flightBundleAdd3Day, cabinType, noOfPassenger, "Direct");
        printDirectFlightResult(less3DaysFlight, "  3 Days before the booking date");
        printDirectFlightResult(actualFlight, " The actual date you are looking for ");
        printDirectFlightResult(add3DaysFlight, "3 Days after the booking date ");

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
            if (!fs1.getSeatingPlan().get(i).isReserved() && fs1.getSeatingPlan().get(i).getCabinType().equals(cabinType)) {
                countFs1++;
                if (countFs1 == noOfPassenger) {
                    return true;
                }
            }
        }

        return false;
    }

    //index 0 start flight, index 1 connecting/end flight/returnflight, index 2 connecting/end flight
    public void reserveFlight(FlightBundle flightBundleForReservation, AirportEntity origin, AirportEntity destination, BigDecimal goingTotalPrice, BigDecimal returnTotalPrice, int numberOfPassengers) {
        Scanner sc = new Scanner(System.in);

        FlightScheduleEntity fs1 = flightBundleForReservation.getDepartOne();
        CabinClassType cabinForFs1 = flightBundleForReservation.getDepartOneCabinClassType();
        FareEntity fareForFs1 = flightBundleForReservation.getDepartOneFare();

        FlightScheduleEntity fs2 = flightBundleForReservation.getDepartTwo();
        CabinClassType cabinForFs2 = flightBundleForReservation.getDepartTwoCabinClassType();
        FareEntity fareForFs2 = flightBundleForReservation.getDepartTwoFare();

        FlightScheduleEntity fs3 = flightBundleForReservation.getDepartThree();
        CabinClassType cabinForFs3 = flightBundleForReservation.getDepartThreeCabinClassType();
        FareEntity fareForFs3 = flightBundleForReservation.getDepartThreeFare();

        FlightScheduleEntity returnFs1 = flightBundleForReservation.getReturnOne();
        CabinClassType cabinForReturnFs1 = flightBundleForReservation.getReturnOneCabinClassType();
        FareEntity fareForReturnFs1 = flightBundleForReservation.getReturnOneFare();

        FlightScheduleEntity returnFs2 = flightBundleForReservation.getReturnTwo();
        CabinClassType cabinForReturnFs2 = flightBundleForReservation.getReturnTwoCabinClassType();
        FareEntity fareForReturnFs2 = flightBundleForReservation.getReturnTwoFare();

        FlightScheduleEntity returnFs3 = flightBundleForReservation.getReturnThree();
        CabinClassType cabinForReturnFs3 = flightBundleForReservation.getReturnThreeCabinClassType();
        FareEntity fareForReturnFs3 = flightBundleForReservation.getReturnThreeFare();

        List<PassengerEntity> listOfPassengers = new ArrayList<>();

        List<FlightReservationEntity> listOfFlightRes = new ArrayList<>();

        FlightReservationEntity flightRes = new FlightReservationEntity(origin.getIataAirportCode(), destination.getIataAirportCode(), goingTotalPrice, customer);
//            List<IndividualFlightReservationEntity> listOfIndividualFlightRes = new ArrayList<>();

        //3 flights total
        if (fs2 != null && fs3 != null) {

            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForFs1 = findSeatsForCustomer(fs1, cabinForFs1);
                BigDecimal amountForFs1 = fareForFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForFs1 = new IndividualFlightReservationEntity(fs1, customer, amountForFs1, flightRes);

                System.out.print("Please enter Passenger first name: ");
                String firstName = sc.nextLine();
                System.out.print("Please enter Passenger last name: ");
                String lastName = sc.nextLine();
                System.out.print("Please enter Passenger passport number: ");
                String passportNumber = sc.nextLine().trim();
                PassengerEntity passenger = new PassengerEntity(firstName, lastName, passportNumber);
                listOfPassengers.add(passenger);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seat : listOfSeatsForFs1) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seat.getSeatId(), seat.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
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
                    indivResForFs1.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs1);
                }

                //create fs2
                List<SeatEntity> listOfSeatsForFs2 = findSeatsForCustomer(fs2, cabinForFs2);
                BigDecimal amountForFs2 = fareForFs2.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForFs2 = new IndividualFlightReservationEntity(fs2, customer, amountForFs2, flightRes);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seatfs2 : listOfSeatsForFs2) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seatfs2.getSeatId(), seatfs2.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForFs2);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForFs2 = new FareEntity(fareForFs2.getFareBasisCode(), fareForFs2.getFareAmount(), fareForFs2.getCabinType());
                    seatfs2.setFare(newFareForFs2);
                    seatfs2.setPassenger(passenger);
                    indivResForFs2.getListOfSeats().add(seatfs2);
                    indivResForFs2.getListOfPassenger().add(passenger);
                    indivResForFs2.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs2);
                }

                //create fs3
                List<SeatEntity> listOfSeatsForFs3 = findSeatsForCustomer(fs3, cabinForFs3);
                BigDecimal amountForFs3 = fareForFs3.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForFs3 = new IndividualFlightReservationEntity(fs3, customer, amountForFs3, flightRes);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seatfs3 : listOfSeatsForFs3) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seatfs3.getSeatId(), seatfs3.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
                String seatNumberfs3 = sc.nextLine().trim();
                SeatEntity seatfs3 = findSeat(seatNumberfs3, listOfSeatsForFs3);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs3.setReserved(true);
                    FareEntity newFareForFs3 = new FareEntity(fareForFs3.getFareBasisCode(), fareForFs3.getFareAmount(), fareForFs3.getCabinType());
                    seatfs3.setFare(newFareForFs3);
                    seatfs3.setPassenger(passenger);
                    indivResForFs3.getListOfSeats().add(seatfs3);
                    indivResForFs3.getListOfPassenger().add(passenger);
                    indivResForFs3.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs3);
                }

            }

        } else if (fs2 != null && fs3 == null) { // 2 flights total

            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForFs1 = findSeatsForCustomer(fs1, cabinForFs1);
                BigDecimal amountForFs1 = fareForFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForFs1 = new IndividualFlightReservationEntity(fs1, customer, amountForFs1, flightRes);

                System.out.print("Please enter Passenger first name: ");
                String firstName = sc.nextLine();
                System.out.print("Please enter Passenger last name: ");
                String lastName = sc.nextLine();
                System.out.print("Please enter Passenger passport number: ");
                String passportNumber = sc.nextLine().trim();
                PassengerEntity passenger = new PassengerEntity(firstName, lastName, passportNumber);
                listOfPassengers.add(passenger);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seat : listOfSeatsForFs1) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seat.getSeatId(), seat.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForFs1 = new FareEntity(fareForFs1.getFareBasisCode(), fareForFs1.getFareAmount(), fareForFs1.getCabinType());
                    seat.setFare(fareForFs1);
                    seat.setPassenger(passenger);
                    indivResForFs1.getListOfSeats().add(seat);
                    indivResForFs1.getListOfPassenger().add(passenger);
                    indivResForFs1.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs1);
                }

                //create fs2
                List<SeatEntity> listOfSeatsForFs2 = findSeatsForCustomer(fs2, cabinForFs2);
                BigDecimal amountForFs2 = fareForFs2.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForFs2 = new IndividualFlightReservationEntity(fs2, customer, amountForFs2, flightRes);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seatfs2 : listOfSeatsForFs2) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seatfs2.getSeatId(), seatfs2.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForFs2);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForFs2 = new FareEntity(fareForFs2.getFareBasisCode(), fareForFs2.getFareAmount(), fareForFs2.getCabinType());
                    seatfs2.setFare(newFareForFs2);
                    seatfs2.setPassenger(passenger);
                    indivResForFs2.getListOfSeats().add(seatfs2);
                    indivResForFs2.getListOfPassenger().add(passenger);
                    indivResForFs2.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs2);
                }
            }

        } else if (fs2 == null && fs3 == null) { //1 flight only
            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForFs1 = findSeatsForCustomer(fs1, cabinForFs1);
                BigDecimal amountForFs1 = fareForFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForFs1 = new IndividualFlightReservationEntity(fs1, customer, amountForFs1, flightRes);

                System.out.print("Please enter Passenger first name: ");
                String firstName = sc.nextLine();
                System.out.print("Please enter Passenger last name: ");
                String lastName = sc.nextLine();
                System.out.print("Please enter Passenger passport number: ");
                String passportNumber = sc.nextLine().trim();
                PassengerEntity passenger = new PassengerEntity(firstName, lastName, passportNumber);
                listOfPassengers.add(passenger);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seat : listOfSeatsForFs1) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seat.getSeatId(), seat.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
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
                    indivResForFs1.setFlightReservation(flightRes);
                    flightRes.getListOfIndividualFlightRes().add(indivResForFs1);
                }
            }
        }

        listOfFlightRes.add(flightRes);

        FlightReservationEntity returnFlightRes = new FlightReservationEntity(destination.getIataAirportCode(), origin.getIataAirportCode(), returnTotalPrice, customer);

        if (returnFs1 != null && returnFs2 != null && returnFs3 != null) {

            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForReturnFs1 = findSeatsForCustomer(returnFs1, cabinForReturnFs1);
                BigDecimal amountForReturnFs1 = fareForReturnFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForReturnFs1 = new IndividualFlightReservationEntity(returnFs1, customer, amountForReturnFs1, returnFlightRes);

                PassengerEntity passenger = listOfPassengers.get(i);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seat : listOfSeatsForReturnFs1) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seat.getSeatId(), seat.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
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
                    indivResForReturnFs1.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs1);
                }

                //create fs2
                List<SeatEntity> listOfSeatsForReturnFs2 = findSeatsForCustomer(returnFs2, cabinForReturnFs2);
                BigDecimal amountForReturnFs2 = fareForReturnFs2.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForReturnFs2 = new IndividualFlightReservationEntity(returnFs2, customer, amountForReturnFs2, returnFlightRes);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seatfs2 : listOfSeatsForReturnFs2) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seatfs2.getSeatId(), seatfs2.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForReturnFs2);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForReturnFs2 = new FareEntity(fareForReturnFs2.getFareBasisCode(), fareForReturnFs2.getFareAmount(), fareForReturnFs2.getCabinType());
                    seatfs2.setFare(newFareForReturnFs2);
                    seatfs2.setPassenger(passenger);
                    indivResForReturnFs2.getListOfSeats().add(seatfs2);
                    indivResForReturnFs2.getListOfPassenger().add(passenger);
                    indivResForReturnFs2.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs2);
                }

                //create fs3
                List<SeatEntity> listOfSeatsForReturnFs3 = findSeatsForCustomer(returnFs3, cabinForReturnFs3);
                BigDecimal amountForReturnFs3 = fareForReturnFs3.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForReturnFs3 = new IndividualFlightReservationEntity(returnFs3, customer, amountForReturnFs3, returnFlightRes);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seatfs3 : listOfSeatsForReturnFs3) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seatfs3.getSeatId(), seatfs3.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
                String seatNumberfs3 = sc.nextLine().trim();
                SeatEntity seatfs3 = findSeat(seatNumberfs3, listOfSeatsForReturnFs3);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs3.setReserved(true);
                    FareEntity newFareForReturnFs3 = new FareEntity(fareForReturnFs3.getFareBasisCode(), fareForReturnFs3.getFareAmount(), fareForReturnFs3.getCabinType());
                    seatfs3.setFare(newFareForReturnFs3);
                    seatfs3.setPassenger(passenger);
                    indivResForReturnFs3.getListOfSeats().add(seatfs3);
                    indivResForReturnFs3.getListOfPassenger().add(passenger);
                    indivResForReturnFs3.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs3);
                }

            }

        } else if (returnFs1 != null && returnFs2 != null && returnFs3 == null) { // only 2 flights

            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForReturnFs1 = findSeatsForCustomer(returnFs1, cabinForReturnFs1);
                BigDecimal amountForReturnFs1 = fareForReturnFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForReturnFs1 = new IndividualFlightReservationEntity(returnFs1, customer, amountForReturnFs1, returnFlightRes);

                PassengerEntity passenger = listOfPassengers.get(i);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seat : listOfSeatsForReturnFs1) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seat.getSeatId(), seat.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForReturnFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForReturnFs1 = new FareEntity(fareForReturnFs1.getFareBasisCode(), fareForReturnFs1.getFareAmount(), fareForReturnFs1.getCabinType());
                    seat.setFare(fareForReturnFs1);
                    seat.setPassenger(passenger);
                    indivResForReturnFs1.getListOfSeats().add(seat);
                    indivResForReturnFs1.getListOfPassenger().add(passenger);
                    indivResForReturnFs1.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs1);
                }

                //create fs2
                List<SeatEntity> listOfSeatsForReturnFs2 = findSeatsForCustomer(returnFs2, cabinForReturnFs2);
                BigDecimal amountForReturnFs2 = fareForReturnFs2.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForReturnFs2 = new IndividualFlightReservationEntity(returnFs2, customer, amountForReturnFs2, returnFlightRes);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seatfs2 : listOfSeatsForReturnFs2) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seatfs2.getSeatId(), seatfs2.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForReturnFs2);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForReturnFs2 = new FareEntity(fareForReturnFs2.getFareBasisCode(), fareForReturnFs2.getFareAmount(), fareForReturnFs2.getCabinType());
                    seatfs2.setFare(newFareForReturnFs2);
                    seatfs2.setPassenger(passenger);
                    indivResForReturnFs2.getListOfSeats().add(seatfs2);
                    indivResForReturnFs2.getListOfPassenger().add(passenger);
                    indivResForReturnFs2.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs2);
                }
            }

        } else if (returnFs1 != null && returnFs2 == null && returnFs3 == null) {
            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForReturnFs1 = findSeatsForCustomer(returnFs1, cabinForReturnFs1);
                BigDecimal amountForReturnFs1 = fareForReturnFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
                IndividualFlightReservationEntity indivResForReturnFs1 = new IndividualFlightReservationEntity(returnFs1, customer, amountForReturnFs1, returnFlightRes);

                PassengerEntity passenger = listOfPassengers.get(i);

                System.out.printf("%-20s,%-20s%-20s", "Seat ID", "Seat Number", "Cabin Class");
                System.out.println("\n");
                for (SeatEntity seat : listOfSeatsForReturnFs1) {
                    String cabinType = "";
                    if (seat.getCabinType().equals(CabinClassType.F)) {
                        cabinType = "First Class";
                    } else if (seat.getCabinType().equals(CabinClassType.J)) {
                        cabinType = "Business Class";
                    } else if (seat.getCabinType().equals(CabinClassType.W)) {
                        cabinType = "Premium Economy Class";
                    } else if (seat.getCabinType().equals(CabinClassType.Y)) {
                        cabinType = "Economy Class";
                    }

                    System.out.printf("%-20s%-20s%-20s", seat.getSeatId(), seat.getSeatNumber(), cabinType);
                    System.out.println();
                }

                System.out.print("Please enter seat number for passenger: ");
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
                    indivResForReturnFs1.setFlightReservation(returnFlightRes);
                    returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs1);
                }
            }
        }

        listOfFlightRes.add(returnFlightRes);

        //call flight reservation session bean
        System.out.println("Would you like to proceed with the booking? (1 for yes, 2 for no)");
        System.out.print("Please enter choice: ");
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) {
            boolean goodInput = false;

            while (!goodInput) {
                System.err.print("Please enter Credit Card name: ");
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
                    }

                    goodInput = true;
                }
            }
            if (goodInput) {
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
        Scanner sc = new Scanner(System.in);
        try {
            List<FlightReservationEntity> listOfFlightReservation = flightReservationSessionBean.retrieveListOfReservation(customer.getCustomerId());
            System.out.printf("%-30s%-60s%-60s%-50s%-50s", "Flight Reservation ID", "Origin Location", " Destination Location", "Booked by", "Total Amount");
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
                System.out.printf("%-30s%-60s%-60s%-50s", fr.getFlightReservationId(), fr.getOriginIATACode(), fr.getDestinationIATACode(), name, fr.getTotalAmount());
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
                System.out.printf("%-60s%-30s%-40s%-40s%-40s%-40s", "Passenger", "Flight Number", "Origin", "Destination", "Seat Number", "Price for Seat");
                System.out.println();
                for (SeatEntity seat : indivFr.getListOfSeats()) {
                    String passengerName = seat.getPassenger().getFirstName() + " " + seat.getPassenger().getLastName();
                    String flightNumber = indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightNumber();
                    String fsOrigin = indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode();
                    String fsDestination = indivFr.getFlightSchedule().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();
                    System.out.printf("%-60s%-30s%-40s%-40s%-40s%-40s", passengerName, flightNumber, fsOrigin, fsDestination, seat.getSeatNumber(), seat.getFare().getFareAmount());
                    System.out.println();
                }
                System.out.println();
                System.out.println();
            }
        } catch (FlightReservationDoesNotExistException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
