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
import entity.CustomerEntity;
import entity.FRSCustomerEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.SingleFlightScheduleEntity;
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
                System.out.println("3. Both Connecting and Direct Flight ");
                String indictatorConnectFlightOrNot = sc.nextLine().trim();
                while (!indictatorConnectFlightOrNot.equals("1") && !indictatorConnectFlightOrNot.equals("2") && !indictatorConnectFlightOrNot.equals("3")) {
                    System.out.println("Invalid input");
                    System.out.println("Please choose one of the following:");
                    System.out.println("1. Connecting Flight");
                    System.out.println("2. Direct Flight");
                    System.out.println("3. Both Connecting and Direct Flight ");
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

                LocalDate threeDayBeforeSearchDateFO = searchDateFO.minusDays(3);
                LocalDate threeDayAftSearchDateFO = searchDateFO.plusDays(3);
                Date dateThreeDateBeforeFO = Date.from(threeDayBeforeSearchDateFO.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date dateThreeDateAfterFO = Date.from(threeDayAftSearchDateFO.atStartOfDay(ZoneId.systemDefault()).toInstant());

                String returnDate = "";
                if (tripType.equals("2")) {
                    System.out.print("Enter return date:(dd/mm/yyyy) ");
                    returnDate = sc.nextLine().trim();
                }
                //convert return time (if exist) to 3 days before and 3 days after
                LocalDate searchDateReturn = null;
                LocalDate threeDayBeforeSearchDateReturn = null;
                LocalDate threeDayAftSearchDateReturn = null;
                Date dateThreeDateBeforeReturn = null;
                Date dateThreeDateAfterReturn = null;
                String[] splitDepartDateReturn;

                if (tripType.equals("2")) {
                    splitDepartDateReturn = returnDate.trim().split("/");
                    if (splitDepartDateReturn.length == 3) {
                        searchDateReturn = LocalDate.of(Integer.valueOf(splitDepartDateReturn[2]), Integer.valueOf(splitDepartDateReturn[1]), Integer.valueOf(splitDepartDateReturn[0]));
                        threeDayBeforeSearchDateReturn = searchDateReturn.minusDays(3);
                        threeDayAftSearchDateReturn = searchDateReturn.plusDays(3);
                        dateThreeDateBeforeReturn = Date.from(threeDayBeforeSearchDateReturn.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        dateThreeDateAfterReturn = Date.from(threeDayAftSearchDateReturn.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    } else {
                        System.out.println("You have invalid date input for return flight date. Please be in 'dd/mm/yyyy' format");
                        //throw new DateInvalidException("You have invalid date input for return flight date. Please be in 'dd/mm/yyyy' format");
                    }
                }

                System.out.print("Enter number of passenger: ");
                String passenger = sc.nextLine().trim();
                int noOfPassenger = Integer.parseInt(passenger);

                // start calling searh flight with respectively to (1. one way 2. two ways -> inside of each, see if they want (a)connecting flight, (b)direct flight or (c)borth)
                if (tripType.equals("1")) {// one way
                    if (indictatorConnectFlightOrNot.equals("1")) {// connecting flight
                        getConnectingFlight(dateThreeDateBeforeFO, dateThreeDateAfterFO, cabinType, noOfPassenger, departureAirport, destinationAirport);
                    } else if (indictatorConnectFlightOrNot.equals("2")) {//direct flight
                        getDirectFlight(departureAirport, destinationAirport, dateThreeDateBeforeFO, dateThreeDateAfterFO, cabinType, noOfPassenger);
                    } else if (indictatorConnectFlightOrNot.equals("3")) {//both

                    }
                } else {// two ways
                    if (indictatorConnectFlightOrNot.equals("1")) {
                        getConnectingFlight(dateThreeDateBeforeFO, dateThreeDateAfterFO, cabinType, noOfPassenger, departureAirport, destinationAirport);
                        getConnectingFlight(dateThreeDateBeforeReturn, dateThreeDateAfterReturn, cabinType, noOfPassenger, departureAirport, destinationAirport);
                    } else if (indictatorConnectFlightOrNot.equals("2")) {
                        getDirectFlight(departureAirport, destinationAirport, dateThreeDateBeforeFO, dateThreeDateAfterFO, cabinType, noOfPassenger);
                        getDirectFlight(destinationAirport, departureAirport, dateThreeDateBeforeReturn, dateThreeDateAfterReturn, cabinType, noOfPassenger);
                    } else if (indictatorConnectFlightOrNot.equals("3")) {

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

    public void printConnectingFlightResult(List<FlightScheduleEntity> listOfSearchFlight) {
        //connecting flight
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.printf("%-15s %-45s %-45s %-25s %-25s ", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time");
        System.out.println();
        for (int i = 0; i < listOfSearchFlight.size(); i += 2) {
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            String firstDepartTime = format.format(listOfSearchFlight.get(i).getDepartureDateTime().getTime());
            String firstArrTime = format.format(listOfSearchFlight.get(i).getArrivalDateTime().getTime());
            System.out.printf("%-15s %-45s %-45s %-25s %-25s ", listOfSearchFlight.get(i).getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    firstDepartTime, firstArrTime);
            System.out.println();
            System.out.println();
            System.out.println("Connecting flight: ");
            System.out.println();
            System.out.printf("%-15s %-45s %-45s %-25s %-25s ", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time");
            String secDepartTime = format.format(listOfSearchFlight.get(i + 1).getDepartureDateTime().getTime());
            String secArrTime = format.format(listOfSearchFlight.get(i + 1).getArrivalDateTime().getTime());
            System.out.println();
            System.out.printf("%-15s %-45s %-45s %-25s %-25s ", listOfSearchFlight.get(i + 1).getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i + 1).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i + 1).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    secDepartTime, secArrTime);
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    public void printDirectFlightResult(List<FlightScheduleEntity> listOfSearchFlight) {
        //connecting flight
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.printf("%-15s %-45s %-45s %-25s %-25s ", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time");
        System.out.println();
        for (int i = 0; i < listOfSearchFlight.size(); i++) {
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            String firstDepartTime = format.format(listOfSearchFlight.get(i).getDepartureDateTime().getTime());
            String firstArrTime = format.format(listOfSearchFlight.get(i).getArrivalDateTime().getTime());
            System.out.printf("%-15s %-45s %-45s %-25s %-25s ", listOfSearchFlight.get(i).getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    firstDepartTime, firstArrTime);
            System.out.println();
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    public void getConnectingFlight(Date dateThreeDateBefore, Date dateThreeDateAfter, CabinClassType cabinType, int noOfPassenger, String departureAirport, String destinationAirport) {
        System.out.println("Came connecting flight");
        GregorianCalendar gDepart = new GregorianCalendar();
        gDepart.setTime(dateThreeDateBefore);

        GregorianCalendar gEndDate = new GregorianCalendar();
        gEndDate.setTime(dateThreeDateAfter);
        gEndDate.add(GregorianCalendar.HOUR_OF_DAY, 23);
        gEndDate.add(GregorianCalendar.MINUTE, 59);
        gEndDate.add(GregorianCalendar.SECOND, 59);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        HashMap<Long, FlightScheduleEntity> hm = new HashMap<>();
        List<FlightSchedulePlanEntity> listOfFlightSchedules = flightScheduleSessionBean.listOfConnectingFlightRecords(dateThreeDateBefore, dateThreeDateAfter);
        List<FlightScheduleEntity> listOfSearchFlight = new ArrayList<FlightScheduleEntity>();
        for (int i = 0; i < listOfFlightSchedules.size(); i++) {
            for (int j = 0; j < listOfFlightSchedules.size() - i; j++) {
                FlightSchedulePlanEntity fsp1 = listOfFlightSchedules.get(i);
                FlightSchedulePlanEntity fsp2 = listOfFlightSchedules.get(j);
                for (int k = 0; k < fsp1.getListOfFlightSchedule().size(); k++) {
                    FlightScheduleEntity fs1 = fsp1.getListOfFlightSchedule().get(k);
                    for (int l = 0; l < fsp2.getListOfFlightSchedule().size(); l++) {

                        FlightScheduleEntity fs2 = fsp2.getListOfFlightSchedule().get(l);

                        if ((hm.get(fs1.getFlightScheduleId()) == null || ((hm.get(fs1.getFlightScheduleId())) != null && !hm.get(fs1.getFlightScheduleId()).equals(fs1)))
                                && (hm.get(fs2.getFlightScheduleId()) == null || ((hm.get(fs2.getFlightScheduleId())) != null && !hm.get(fs2.getFlightScheduleId()).equals(fs2)))
                                && fs1.getDepartureDateTime().after(gDepart) && fs2.getArrivalDateTime().before(gEndDate)
                                && fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode().equals(departureAirport)
                                && fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode()
                                        .equals(fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode())
                                && fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode().equals(destinationAirport)) {

                            GregorianCalendar firstFlightArrivalTime = (GregorianCalendar) fs1.getArrivalDateTime().clone();
                            GregorianCalendar secondFlightDepartTime = (GregorianCalendar) fs2.getDepartureDateTime().clone();
                            int twoHoursBuffer = 60 * 2;
                            firstFlightArrivalTime.add(GregorianCalendar.MINUTE, twoHoursBuffer);
                            if (firstFlightArrivalTime.before(secondFlightDepartTime)) {
                                {

                                    int twentyTwoHours = 22 * 60;
                                    firstFlightArrivalTime.add(GregorianCalendar.MINUTE, twentyTwoHours);
                                    if (firstFlightArrivalTime.after(secondFlightDepartTime)) {
                                        listOfSearchFlight.add(fs1);
                                        listOfSearchFlight.add(fs2);
                                        hm.put(fs1.getFlightScheduleId(), fs1);
                                        hm.put(fs2.getFlightScheduleId(), fs2);
                                        System.out.println("hm.get(fs1.getFlightScheduleId())" + hm.get(fs1.getFlightScheduleId()));
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }

        // Comparator<FlightScheduleEntity> sortFlightScheduleId = (FlightScheduleEntity p1, FlightScheduleEntity p2) -> Integer.valueOf(p1.getFlightScheduleId().intValue() - p2.getFlightScheduleId().intValue());
        //System.out.println("listOfSearchFlight" + listOfSearchFlight.size());
        //listOfSearchFlight.sort(sortFlightScheduleId);
        List<FlightScheduleEntity> flightResult = new ArrayList<FlightScheduleEntity>();

        flightResult = processListGetCabinClassAndSeatAva(listOfSearchFlight, cabinType, noOfPassenger, "Connecting");

        printConnectingFlightResult(flightResult);
    }

    public void getDirectFlight(String originIATA, String desIATA, Date dateThreeDateBefore, Date dateThreeDateAfter, CabinClassType cabinType, int noOfPassenger) {
        List<FlightSchedulePlanEntity> listOfODQuery = flightScheduleSessionBean.listOfConnectingFlightRecords(dateThreeDateAfter, dateThreeDateAfter);
        List<FlightScheduleEntity> listOfUnprocessDirectFlight = new ArrayList<FlightScheduleEntity>();
        GregorianCalendar gDepart = new GregorianCalendar();
        gDepart.setTime(dateThreeDateBefore);

        GregorianCalendar gEndDate = new GregorianCalendar();
        gEndDate.setTime(dateThreeDateAfter);
        gEndDate.add(GregorianCalendar.HOUR_OF_DAY, 23);
        gEndDate.add(GregorianCalendar.MINUTE, 59);
        gEndDate.add(GregorianCalendar.SECOND, 59);
        for (int h = 0; h < listOfODQuery.size(); h++) {
            FlightSchedulePlanEntity fsp = listOfODQuery.get(h);
            for (int i = 0; i < fsp.getListOfFlightSchedule().size(); i++) {

                String queryOrigin = fsp.getListOfFlightSchedule().get(i).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode();
                String queryDes = fsp.getListOfFlightSchedule().get(i).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();
                if (fsp.getListOfFlightSchedule().get(i).getDepartureDateTime().after(gDepart) && fsp.getListOfFlightSchedule().get(i).getArrivalDateTime().before(gEndDate)
                        && queryOrigin.equalsIgnoreCase(originIATA) && queryDes.equalsIgnoreCase(desIATA)) {
                    listOfUnprocessDirectFlight.add(fsp.getListOfFlightSchedule().get(i));
                }
            }
            List<FlightScheduleEntity> odResult = processListGetCabinClassAndSeatAva(listOfUnprocessDirectFlight, cabinType, noOfPassenger, "Direct");
            Comparator<FlightScheduleEntity> sortFlightScheduleId = (FlightScheduleEntity p1, FlightScheduleEntity p2) -> ((int) p1.getDepartureDateTime().getTime().getTime() - (int) p2.getDepartureDateTime().getTime().getTime());

            odResult.sort(sortFlightScheduleId);
            printDirectFlightResult(odResult);

        }
    }

    public List<FlightScheduleEntity> processListGetCabinClassAndSeatAva(List<FlightScheduleEntity> listOfODQuery, CabinClassType cabinType, int noOfPassenger, String typeOfFlight) {

        List<FlightScheduleEntity> tempList = new ArrayList<FlightScheduleEntity>();
        if (typeOfFlight.equals("Connecting")) {

            for (int i = 0; i < listOfODQuery.size(); i += 2) {
                if (checkSeatGreaterThanPassenger(listOfODQuery.get(i), cabinType, noOfPassenger) && checkSeatGreaterThanPassenger(listOfODQuery.get(i + 1), cabinType, noOfPassenger)) {
                    tempList.add(listOfODQuery.get(i));
                    tempList.add(listOfODQuery.get(i + 1));
                }
            }
        } else {
            for (int i = 0; i < listOfODQuery.size(); i++) {
                if (checkSeatGreaterThanPassenger(listOfODQuery.get(i), cabinType, noOfPassenger)) {
                    tempList.add(listOfODQuery.get(i));
                }
            }
        }

        return tempList;
    }

    public boolean checkSeatGreaterThanPassenger(FlightScheduleEntity fs1, CabinClassType cabinType, int noOfPassenger) {
        int countNoOfSeatFirstFlight = 0;
        for (int j = 0; j < fs1.getSeatingPlan().size(); j++) {
            System.out.println("Seating reserved" + fs1.getSeatingPlan().get(j).isReserved());
            if (fs1.getSeatingPlan().get(j).getCabinType().equals(cabinType) && !fs1.getSeatingPlan().get(j).isReserved()) {
                countNoOfSeatFirstFlight++;
                if (countNoOfSeatFirstFlight == noOfPassenger) {
                    return true;
                }
            }
        }
        return false;
    }
}
