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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
        System.out.println("Enter trip type:");
        String tripType = sc.nextLine();
        System.out.println("Enter departure airport:");
        String departureAirport = sc.nextLine();
        System.out.println("Enter destination airport:");
        String destinationAirport = sc.nextLine();
        System.out.println("Enter depature date:(dd/mm/yyyy)");
        String departureDate = sc.nextLine();
        System.out.println("Enter return date:(dd/mm/yyyy)");
        String returnDate = sc.nextLine();
        System.out.println("Enter number of passenger:");
        String passenger = sc.nextLine();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        LocalDate searchDate = null;
        String[] splitDepartDate = departureDate.split("/");
        if (splitDepartDate.length == 3) {
            searchDate = LocalDate.of(Integer.valueOf(splitDepartDate[2]), Integer.valueOf(splitDepartDate[1]), Integer.valueOf(splitDepartDate[0]));
        } else {
            System.out.println("You have invalid input");
        }

        LocalDate threeDayBeforeSearchDate = searchDate.minusDays(3);
        LocalDate threeDayAftSearchDate = searchDate.plusDays(3);
        Date dateThreeDateBefore = Date.from(threeDayBeforeSearchDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dateThreeDateAfter = Date.from(threeDayAftSearchDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<FlightScheduleEntity> listOfFlightSchedules = flightScheduleSessionBean.listOfConnectingFlightRecords(dateThreeDateBefore, dateThreeDateAfter);
        List<FlightScheduleEntity> listOfSearchFlight = new ArrayList<FlightScheduleEntity>();
        for (int i = 0; i < listOfFlightSchedules.size(); i++) {
            for (int j = 0; j < listOfFlightSchedules.size(); j++) {
                FlightSchedulePlanEntity iFsp = listOfFlightSchedules.get(i).getFlightSchedulePlan();
                FlightSchedulePlanEntity jFsp = listOfFlightSchedules.get(j).getFlightSchedulePlan();

                if (iFsp.getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode().equalsIgnoreCase(departureAirport)
                        && iFsp.getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode().equalsIgnoreCase(jFsp.getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode())
                        && jFsp.getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode().equalsIgnoreCase(destinationAirport)) {
                    int firstFlightDesTimeZoneHr = iFsp.getFlightEntity().getFlightRoute().getOriginLocation().getTimeZoneHour();
                    int firstFlightDesTimeZoneMin = iFsp.getFlightEntity().getFlightRoute().getOriginLocation().getTimeZoneMin();
                    int secFlightDesTimeZoneHr = jFsp.getFlightEntity().getFlightRoute().getOriginLocation().getTimeZoneHour();
                    int secFlightDesTimeZoneMin = jFsp.getFlightEntity().getFlightRoute().getOriginLocation().getTimeZoneMin();
                    int flightDuration = listOfFlightSchedules.get(i).getFlightDuration();
                    GregorianCalendar firstFlightActualTime = (GregorianCalendar) listOfFlightSchedules.get(i).getDepartureDateTime().clone();
                    int convertFirstFlightToMin = 0;
                    int convertSecFlightToMin = 0;
                    int actualDiffTime = 0;
                    if (firstFlightDesTimeZoneHr > 0) {
                        convertFirstFlightToMin = firstFlightDesTimeZoneHr * 60;
                    }
                    convertFirstFlightToMin += convertFirstFlightToMin;
                    convertFirstFlightToMin += firstFlightDesTimeZoneMin;
                    convertFirstFlightToMin += flightDuration;
                    if (secFlightDesTimeZoneHr > 0) {
                        convertSecFlightToMin = secFlightDesTimeZoneHr * 60;
                    }
                    convertSecFlightToMin += secFlightDesTimeZoneMin;
                    actualDiffTime = convertSecFlightToMin - convertFirstFlightToMin;

                    firstFlightActualTime.add(GregorianCalendar.MINUTE, actualDiffTime);
                    listOfFlightSchedules.get(i).setArrivalDateTime(firstFlightActualTime);
                    System.out.println("Local time i :" + format.format(listOfFlightSchedules.get(i).getArrivalDateTime().getTime()));
                    firstFlightActualTime.add(GregorianCalendar.MINUTE, 120);// 2h buffer time
                    if (firstFlightActualTime.before(listOfFlightSchedules.get(j).getDepartureDateTime())) {
                        int maxWaitingConnectingFlight = 22 * 60; // max conencting flight 24hr - 2 hr (buffer) - convert to mins
                        firstFlightActualTime.add(GregorianCalendar.MINUTE, maxWaitingConnectingFlight);
                        if (firstFlightActualTime.before(listOfFlightSchedules.get(j).getDepartureDateTime())) {
                            //calculate local timing at 2nd destination
                            int secDepartInMin = 0;
                            int secArrInMin = 0;
                            int secTotalDiff = 0;
                            int secFlightDuration = listOfFlightSchedules.get(j).getFlightDuration();
                            int secDepartTimeZoneMin = jFsp.getFlightEntity().getFlightRoute().getOriginLocation().getTimeZoneMin();
                            int secDepartTimeZoneHr = jFsp.getFlightEntity().getFlightRoute().getOriginLocation().getTimeZoneHour();
                            if (secDepartTimeZoneHr > 0) {
                                secDepartInMin = secDepartTimeZoneHr * 60;
                            }
                            secDepartInMin += secDepartTimeZoneMin;
                            secDepartInMin += secFlightDuration;

                            int secArrTimeZoneMin = jFsp.getFlightEntity().getFlightRoute().getDestinationLocation().getTimeZoneMin();
                            int secArrTimeZoneHr = jFsp.getFlightEntity().getFlightRoute().getDestinationLocation().getTimeZoneHour();
                            if (secArrTimeZoneHr > 0) {
                                secArrInMin = secArrTimeZoneHr * 60;
                            }
                            secArrInMin += secArrTimeZoneMin;
                            secTotalDiff = secArrInMin - secDepartInMin;

                            GregorianCalendar secFlightActualTime = (GregorianCalendar) listOfFlightSchedules.get(j).getDepartureDateTime().clone();
                            secFlightActualTime.add(GregorianCalendar.MINUTE, secTotalDiff);
                            listOfFlightSchedules.get(j).setArrivalDateTime(secFlightActualTime);
                            listOfSearchFlight.add(listOfFlightSchedules.get(i));
                            listOfSearchFlight.add(listOfFlightSchedules.get(j));
                            System.out.println("Local time j :" + format.format(listOfFlightSchedules.get(j).getArrivalDateTime().getTime()));
                        }
                    }
                }
            }
        }

        /*test print
        for (int i = 0; i < listOfFlightSchedules.size(); i += 2) {

            FlightSchedulePlanEntity fsp = listOfFlightSchedules.get(i).getFlightSchedulePlan();
            System.out.println("fsp flight number: " + fsp.getFlightNumber());

            System.out.println(" O - D : " + fsp.getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode() + ":D:" + fsp.getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode());
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String departDate = format.format(listOfFlightSchedules.get(i).getDepartureDateTime().getTime());
            String arrivalDate = format.format(listOfFlightSchedules.get(i).getArrivalDateTime().getTime());
            System.out.println("Depart" + departDate + ": Arrival" + arrivalDate);
        }*/

 /*for (int i = 0; i < listOfFlight.size(); i++) {
            for (int j = 0; j < listOfFlight.size(); j++) {
                //if(listOfFlight.get(j).getFlightRoute().getDestinationLocation().equalsIgnoreCase(departureAirport) && listOfFlight.get(i).getFlightRoute().getOriginLocation().equals(listOfFlight.get(j).getFlightRoute().get)){
                if (listOfFlight.get(i).getFlightRoute().getOriginLocation().equals(departureAirport) && listOfFlight.get(i).getFlightRoute().getDestinationLocation().equals(listOfFlight.get(j).getFlightRoute().getOriginLocation())
                        && listOfFlight.get(j).getFlightRoute().getDestinationLocation().equals(destinationAirport)) {
                    for (int k = 0; k < listOfFlight.get(i).getListOfFlightSchedulePlan().size(); k++) {
                        if (listOfFlight.get(i).getListOfFlightSchedulePlan().get(k).get) {
                            
                        }
                    }

                }
            }
        }*/
        //Search for departure flight
        /* List<FlightEntity> listOfConnectingDepartFlights = new ArrayList<FlightEntity>();
        for (int i = 0; i < listOfFlightSchedules.size(); i++) {
            for (int j = 0; j < listOfFlightSchedules.size(); j++) {
                FlightEntity fI = listOfFlightSchedules.get(i).getFlightSchedulePlan().getFlightEntity();
                FlightEntity fJ = listOfFlightSchedules.get(j).getFlightSchedulePlan().getFlightEntity();
                if (fI.getFlightRoute().getOriginLocation().getIataAirportCode().equalsIgnoreCase(departureAirport)
                        && fI.getFlightRoute().getDestinationLocation().getIataAirportCode().equalsIgnoreCase(fJ.getFlightRoute().getOriginLocation().getIataAirportCode())
                        && fJ.getFlightRoute().getDestinationLocation().getIataAirportCode().equals(destinationAirport)) {
                    GregorianCalendar firstFlightArrTime = listOfFlightSchedules.get(i).getArrivalDateTime(); //1st flight arrivalTime
                    GregorianCalendar secondFlightDepart = listOfFlightSchedules.get(j).getDepartureDateTime(); // 2nd flight depart time

                    int departTimeZoneHr = fI.getFlightRoute().getOriginLocation().getTimeZoneHour();
                    int departTimeZoneMin = fI.getFlightRoute().getOriginLocation().getTimeZoneMin();
                    int destTimeZoneHr = fJ.getFlightRoute().getOriginLocation().getTimeZoneHour();
                    int destTimeZoneMin = fJ.getFlightRoute().getOriginLocation().getTimeZoneMin();
                    //convert depart and dest into min and get the different
                    int departTimeZoneInMin = 0;
                    int destTimeZoneInMin = 0;
                    if (departTimeZoneHr > 0) {
                        departTimeZoneInMin = departTimeZoneHr * 60;
                    }
                    if (destTimeZoneInMin > 0) {
                        destTimeZoneInMin = destTimeZoneHr * 60;
                    }
                    departTimeZoneInMin += departTimeZoneMin;
                    destTimeZoneInMin += destTimeZoneMin;

                    int diffInTimeZoneInMIn = destTimeZoneInMin - departTimeZoneInMin;
                    firstFlightArrTime.add(GregorianCalendar.MINUTE, diffInTimeZoneInMIn);
                    boolean departTimeBeforeArrive = firstFlightArrTime.before(secondFlightDepart);
                    //check if the depart and destination time within 22hrs time frame since 2 hours buffer already included
                    firstFlightArrTime.add(GregorianCalendar.HOUR_OF_DAY, 22);
                    boolean timeWithin22hrsExclude2h = secondFlightDepart.before(firstFlightArrTime);
                    if (departTimeBeforeArrive && timeWithin22hrsExclude2h) {
                        //add depart detail into list index (even)
                        FlightEntity firstFlight = fI;
                       
                        listOfConnectingDepartFlights.add(fI);
                        listOfConnectingDepartFlights.add(fJ);
                    }

                }
            }
        }*/
        //connecting flight
        System.out.printf("%-15s %-30s %-30s %-25s %-25s ", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time");
        System.out.println();
        for (int i = 0; i < listOfSearchFlight.size(); i += 2) {
            System.out.println("====================================================================================================================================");
            String firstDepartTime = format.format(listOfFlightSchedules.get(i).getArrivalDateTime().getTime());
            String firstArrTime = format.format(listOfFlightSchedules.get(i).getDepartureDateTime().getTime());
            System.out.printf("%-15s %-30s %-30s %-25s %-25s ", listOfSearchFlight.get(i).getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    firstDepartTime, firstArrTime);
            System.out.println();
            System.out.println();
            System.out.println("Connecting flight: ");
            System.out.println();
            System.out.println();
            String secDepartTime = format.format(listOfFlightSchedules.get(i + 1).getDepartureDateTime().getTime());
            String secArrTime = format.format(listOfFlightSchedules.get(i + 1).getArrivalDateTime().getTime());
            System.out.printf("%-15s %-30s %-30s %-15s %-15s ", listOfSearchFlight.get(i + 1).getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i + 1).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i + 1).getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    secDepartTime, secArrTime);
            System.out.println();
            System.out.println("========================================================================================================================================");
        }

        //Wrong retrieval  
        /*  List<FlightSchedulePlanEntity> listOfFSP = null;
        List<FlightEntity> displayListOfFlight = new ArrayList<FlightEntity>();
        for (int i = 0; i < listOfFSP.size(); i++) {
            for (int j = 0; j < listOfFSP.size(); j++) {
                if (listOfFSP.get(i).getFlightEntity().getFlightRoute().getOriginLocation().equals(departureAirport)
                        && listOfFSP.get(i).getFlightEntity().getFlightRoute().getDestinationLocation().equals(listOfFSP.get(j).getFlightEntity().getFlightRoute().getOriginLocation())
                        && listOfFSP.get(j).getFlightEntity().getFlightRoute().getDestinationLocation().equals(destinationAirport)) {
                    for (int k = 0; k < listOfFSP.size(); k++) {
                        for (int l = 0; l < listOfFSP.size(); l++) {
                            GregorianCalendar departTime = listOfFSP.get(i).getListOfFlightSchedule().get(k).getArrivalDateTime();
                            double flightDuration = listOfFSP.get(i).getListOfFlightSchedule().get(k).getFlightDuration();
                            int departTimeZoneHr = listOfFSP.get(i).getFlightEntity().getFlightRoute().getOriginLocation().getTimeZoneHour();
                            int departTimeZoneMin = listOfFSP.get(i).getFlightEntity().getFlightRoute().getDestinationLocation().getTimeZoneMin();
                            int destTimeZoneHr = listOfFSP.get(j).getFlightEntity().getFlightRoute().getOriginLocation().getTimeZoneHour();
                            int destTimeZoneMin = listOfFSP.get(j).getFlightEntity().getFlightRoute().getOriginLocation().getTimeZoneMin();

                            //convert depart and dest into min and get the different
                            int departTimeZoneInMin = 0;
                            int destTimeZoneInMin = 0;
                            if (departTimeZoneHr > 0) {
                                departTimeZoneInMin = departTimeZoneHr * 60;
                            }
                            if (destTimeZoneInMin > 0) {
                                destTimeZoneInMin = destTimeZoneHr * 60;
                            }
                            departTimeZoneInMin += departTimeZoneMin;
                            destTimeZoneInMin += destTimeZoneMin;

                            int diffInTimeZoneInMIn = destTimeZoneInMin - departTimeZoneInMin;
                            int totalDiff = (int) (flightDuration * 60) + diffInTimeZoneInMIn + 120;
                            departTime.add(GregorianCalendar.MINUTE, totalDiff);
                            GregorianCalendar arriveTime = listOfFSP.get(j).getListOfFlightSchedule().get(l).getDepartureDateTime();
                            boolean departTimeBeforeArrive = departTime.before(arriveTime);
                            //check if the depart and destination time within 22hrs time frame since 2 hours buffer already included
                            departTime.add(GregorianCalendar.HOUR_OF_DAY, 22);
                            boolean timeWithin22hrsExclude2h = arriveTime.before(departTime);

                            if (departTimeBeforeArrive && timeWithin22hrsExclude2h) {
                                //add depart detail into list index (even)
                                FlightEntity departFlight = listOfFSP.get(i).getFlightEntity();
                                FlightRouteEntity dFr = listOfFSP.get(i).getFlightEntity().getFlightRoute();
                                FlightScheduleEntity dFs = listOfFSP.get(i).getListOfFlightSchedule().get(k);
                                FlightSchedulePlanEntity dFsp = listOfFSP.get(i).getListOfFlightSchedule().get(k).getFlightSchedulePlan();
                                dFsp.getListOfFlightSchedule().add(dFs);

                                departFlight.setFlightRoute(dFr);
                                departFlight.getListOfFlightSchedulePlan().add(dFsp);
                                listOfFlight.add(departFlight);
                                // add depart detail into list index (odd)
                                FlightEntity destFlight = listOfFSP.get(j).getFlightEntity();
                                FlightRouteEntity desFr = listOfFSP.get(j).getFlightEntity().getFlightRoute();
                                FlightScheduleEntity desFs = listOfFSP.get(j).getListOfFlightSchedule().get(l);
                                FlightSchedulePlanEntity desFsp = listOfFSP.get(j).getListOfFlightSchedule().get(l).getFlightSchedulePlan();
                                desFsp.getListOfFlightSchedule().add(desFs);

                                destFlight.setFlightRoute(desFr);
                                destFlight.getListOfFlightSchedulePlan().add(desFsp);
                                listOfFlight.add(destFlight);
                            }
                        }

                    }

                }
            }
        }*/
    }
}
