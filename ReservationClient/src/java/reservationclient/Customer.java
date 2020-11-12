/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.AircraftTypeEntity;
import entity.CabinClassConfigurationEntity;
import entity.FlightBundle;
import entity.CustomerEntity;
import entity.FRSCustomerEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.SingleFlightScheduleEntity;
import java.awt.BorderLayout;
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
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.CabinClassType;
import util.enumeration.UserRole;
import util.exception.CustomerExistException;
import util.exception.CustomerLoginInvalid;
import util.exception.FlightRouteDoesNotExistException;

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

    public Customer() {

        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public Customer(CustomerSessionBeanRemote customerSessionBean, FlightScheduleSessionBeanRemote flightScheduleSessionBean) {
        this();
        this.customerSessionBean = customerSessionBean;
        this.flightScheduleSessionBean = flightScheduleSessionBean;
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
        // System.out.println("2. Customer Login");
        String input = sc.nextLine();
        //while (true) {
        if (input.equals("1")) {
            searchFlight();
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
}
