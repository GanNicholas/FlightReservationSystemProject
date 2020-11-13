/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.AccessFromWrongPortalException_Exception;
import ws.client.AirportEntity;
import ws.client.CabinClassType;
import ws.client.CustomerEntity;
import ws.client.CustomerHasNoReservationException_Exception;
import ws.client.CustomerLoginInvalid_Exception;
import ws.client.FareEntity;
import ws.client.FlightBundle;
import ws.client.FlightReservationDoesNotExistException_Exception;
import ws.client.FlightReservationEntity;
import ws.client.FlightRouteDoesNotExistException;
import ws.client.FlightRouteDoesNotExistException_Exception;
import ws.client.FlightScheduleEntity;
import ws.client.IncorrectFormatException_Exception;
import ws.client.IndividualFlightReservationEntity;
import ws.client.PartnerEntity;
import ws.client.PassengerEntity;
import ws.client.SeatEntity;
import ws.client.UserRole;

/**
 *
 * @author nickg
 */
public class RunApp {

    private CustomerEntity partner;

    public RunApp() {
        this.partner = null;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        while (choice != 2) {
            try {
                System.out.println("------Welcome to Hotel Reservation Partner System------");
                System.out.println("What would you like to do?");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Please enter choice: ");
                choice = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException ex) {
                System.out.println("Choice does not exist for info page, please try again!");
                sc.next();
            }

            if (choice == 1) {

                try {
                    System.out.print("Please enter username: ");
                    String username = sc.nextLine();
                    System.out.print("Please enter password: ");
                    String password = sc.nextLine();

                    partner = loginPartner(username, password);
                    postlogin();

                } catch (CustomerLoginInvalid_Exception | AccessFromWrongPortalException_Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (choice == 2) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }

    public void postlogin() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        while (choice != 4) {
            try {
                System.out.println("What would you like to do?");
                System.out.println("1. Search flight");
                System.out.println("2. View flight reservations");
                System.out.println("3. View flight reservation details");
                System.out.println("4. Log out");
                System.out.print("Please enter choice: ");
                choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    searchFlight();
                } else if (choice == 2) {
                    viewFlightReservations();
                } else if (choice == 3) {
                    viewIndividualFlightReservations();
                } else if (choice == 4) {
                    System.out.println("Good bye!");
                    System.exit(0);
                } else {
                    System.out.println("Invalid choice!");
                    System.out.println();
                }

            } catch (InputMismatchException ex) {
                System.out.println("Invalid input for operations, please try again!");
                sc.next();
            }

        }
    }

    public void viewFlightReservations() {
        Scanner sc = new Scanner(System.in);
        try {
            List<FlightReservationEntity> listOfFlightRes = retrieveListOfReservation(partner.getCustomerId());

            System.out.printf("%-30s%-60s%-60s%-50s%-50s", "Flight Reservation ID", "Origin Location", " Destination Location", "Booked by", "Total Amount");
            System.out.println();
            for (FlightReservationEntity fr : listOfFlightRes) {
                String name = "";

                if (fr.getCustomer() instanceof PartnerEntity) {
                    PartnerEntity customer = (PartnerEntity) fr.getCustomer();
                    name = customer.getPartnerName();
                }

                System.out.printf("%-30s%-60s%-60s%-50s", fr.getFlightReservationId(), fr.getOriginIATACode(), fr.getDestinationIATACode(), name, fr.getTotalAmount());
                System.out.println();
            }

        } catch (CustomerHasNoReservationException_Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println();
        }

    }

    public void viewIndividualFlightReservations() {
        Scanner sc = new Scanner(System.in);
        try {

            viewFlightReservationsThrowException();
            System.out.println();
            System.out.print("Please enter ID of flight reservation you wish to view: ");
            Long frId = sc.nextLong();
            sc.nextLine();

            // each flight leg, show passenger - name, seat number , price for seat
            // at the end show total price they paid
            FlightReservationEntity fr = retrieveIndividualFlightReservation(frId);
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
        } catch (FlightReservationDoesNotExistException_Exception | CustomerHasNoReservationException_Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println();
        }

    }

    public void viewFlightReservationsThrowException() throws CustomerHasNoReservationException_Exception {
        Scanner sc = new Scanner(System.in);
        List<FlightReservationEntity> listOfFlightRes = retrieveListOfReservation(partner.getCustomerId());

        System.out.printf("%-30s%-60s%-60s%-50s%-50s", "Flight Reservation ID", "Origin Location", " Destination Location", "Booked by", "Total Amount");
        System.out.println();
        for (FlightReservationEntity fr : listOfFlightRes) {
            String name = "";

            if (fr.getCustomer() instanceof PartnerEntity) {
                PartnerEntity customer = (PartnerEntity) fr.getCustomer();
                name = customer.getPartnerName();
            }

            System.out.printf("%-30s%-60s%-60s%-50s", fr.getFlightReservationId(), fr.getOriginIATACode(), fr.getDestinationIATACode(), name, fr.getTotalAmount());
            System.out.println();
        }

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
                GregorianCalendar searchDateFO = null;
                String[] splitDepartDate = departureDate.trim().split("/");
                if (splitDepartDate.length == 3) {
                    searchDateFO = new GregorianCalendar(Integer.valueOf(splitDepartDate[2]), Integer.valueOf(splitDepartDate[1])-1, Integer.valueOf(splitDepartDate[0]));
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
                        currentSearchReturnDate = new GregorianCalendar(Integer.valueOf(splitDepartDateReturn[2]), Integer.valueOf(splitDepartDateReturn[1])-1, Integer.valueOf(splitDepartDateReturn[0]));
                        
                    } else {
                        System.out.println("You have invalid date input for return flight date. Please be in 'dd/mm/yyyy' format");
                    }
                }
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                System.out.print("Enter number of passenger: ");
                String passenger = sc.nextLine().trim();
                int noOfPassenger = Integer.parseInt(passenger);
                List<FlightBundle> flightBundle = new ArrayList<>();
                List<FlightBundle> tempList = null;
                // start calling searh flight with respectively to (1. one way 2. two ways -> inside of each, see if they want (a)connecting flight, (b)direct flight
                if (tripType.equals("1")) {// one way
                    if (indictatorConnectFlightOrNot.equals("1")) {// connecting flight
                        tempList = getConnectingFlight(searchDateFO, cabinType, noOfPassenger, departureAirport, destinationAirport, 0);

                    } else if (indictatorConnectFlightOrNot.equals("2")) {//direct flight
                        tempList = getDirectFlight(departureAirport, destinationAirport, searchDateFO, cabinType, noOfPassenger, 0);
                    }
                } else {// two ways
                    if (indictatorConnectFlightOrNot.equals("1")) {
                        flightBundle = getConnectingFlight(searchDateFO, cabinType, noOfPassenger, departureAirport, destinationAirport, 0);
                        System.out.println("Return Flight result: ");
                        tempList = getConnectingFlight(currentSearchReturnDate, cabinType, noOfPassenger, destinationAirport, departureAirport, flightBundle.size());
                        tempList = combineAllThreeFlights(flightBundle, tempList, null);
                    } else if (indictatorConnectFlightOrNot.equals("2")) {
                        flightBundle = getDirectFlight(departureAirport, destinationAirport, searchDateFO, cabinType, noOfPassenger, 0);
                        System.out.println("Return Flight result: ");
                        tempList = getDirectFlight(destinationAirport, departureAirport, currentSearchReturnDate, cabinType, noOfPassenger, flightBundle.size());
                        tempList = combineAllThreeFlights(flightBundle, tempList, null);

                    }
                }
                System.out.println("Please enter the flight you want to reserve for flying over:");
                FlightBundle fb = new FlightBundle();
                int firstFlight = sc.nextInt() - 1;
                int secondFlight = 0;
                AirportEntity origin = null;
                AirportEntity destination = null;
                FlightBundle flyOver = tempList.get(firstFlight);
                fb.setDepartOne(flyOver.getDepartOne());
                origin = flyOver.getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation();
                destination = flyOver.getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation();

                fb.setDepartOneCabinClassType(cabinType);
                if (flyOver.getDepartTwo() != null) {
                    fb.setDepartTwoCabinClassType(cabinType);
                    fb.setDepartTwo(flyOver.getDepartTwo());
                    destination = flyOver.getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation();

                }
                if (flyOver.getDepartThree() != null) {
                    fb.setDepartThree(flyOver.getDepartThree());
                    fb.setDepartThreeCabinClassType(cabinType);
                    destination = flyOver.getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation();
                }
                if (tripType.equals("2")) {
                    System.out.println("Please enter the flight you want to reserve for flying back:");
                    secondFlight = sc.nextInt() - 1;
                    FlightBundle temp = tempList.get(secondFlight);
                    fb.setReturnOneCabinClassType(cabinType);
                    fb.setReturnOne(temp.getDepartOne());
                    if (temp.getDepartTwo() != null) {
                        fb.setReturnTwo(temp.getDepartOne());
                        fb.setReturnTwoCabinClassType(cabinType);
                    }
                    if (temp.getDepartThree() != null) {
                        fb.setReturnThreeCabinClassType(cabinType);
                        fb.setReturnThree(temp.getDepartThree());
                    }
                }
                reserveFlight(fb, origin, destination, new BigDecimal(BigInteger.ONE), new BigDecimal(BigInteger.ONE), Integer.parseInt(passenger));

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

    public BigDecimal getLowestFare(List<FareEntity> listOfFe) {
        if (listOfFe == null || listOfFe.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal min = new BigDecimal(99999);
        for (int i = 0; i < listOfFe.size(); i++) {
            BigDecimal actualVal = listOfFe.get(i).getFareAmount();
            if (min.compareTo(actualVal) == 1) {
                min = actualVal;
            }
        }
        return min;
    }

    public void printConnectingFlightResult(List<FlightBundle> listOfSearchFlight, String nDay, int index, int noOfPassenger) {
        //connecting flight
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.printf("%-15s %-45s %-45s %-25s %-25s", "", "", nDay, "", "");
        System.out.println();
        System.out.printf("%-5s %-15s %-45s %-45s %-25s %-25s %-6s", "Id", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time", "Pricing");
        System.out.println();
        BigDecimal totalSumOfConnecting = BigDecimal.ZERO;
        for (int i = 0; i < listOfSearchFlight.size(); i++) {
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

            BigDecimal totalDepartPrice = BigDecimal.ZERO;
            BigDecimal totalReturnPrice = BigDecimal.ZERO;
            totalSumOfConnecting = BigDecimal.ZERO;
            
                if (listOfSearchFlight.get(i).getDepartOne() != null) {
                    unitPriceDepartOne = getHighestFare(listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getListOfFare());
                    totalDepartOne = unitPriceDepartOne.multiply(new BigDecimal(noOfPassenger));
                    totalDepartPrice = totalDepartPrice.add(totalDepartOne);
                }
                if (listOfSearchFlight.get(i).getDepartTwo() != null) {
                    unitPriceDepartTwo = getHighestFare(listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getListOfFare());
                    totalDepartTwo = unitPriceDepartTwo.multiply(new BigDecimal(noOfPassenger));
                    totalDepartPrice = totalDepartPrice.add(totalDepartOne);
                }
                if (listOfSearchFlight.get(i).getDepartThree() != null) {
                    unitPriceDepartThree = getHighestFare(listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getListOfFare());
                    totalDepartThree = unitPriceDepartThree.multiply(new BigDecimal(noOfPassenger));
                    totalDepartPrice = totalDepartPrice.add(totalDepartOne);
                }
                // return

                if (listOfSearchFlight.get(i).getReturnOne() != null) {
                    unitPriceDepartReturnOne = getHighestFare(listOfSearchFlight.get(i).getReturnOne().getFlightSchedulePlan().getListOfFare());
                    totalReturnOne = unitPriceDepartReturnOne.multiply(new BigDecimal(noOfPassenger));
                    totalReturnPrice = totalReturnPrice.add(totalReturnOne);
                }
                if (listOfSearchFlight.get(i).getReturnTwo() != null) {
                    unitPriceDepartReturnTwo = getHighestFare(listOfSearchFlight.get(i).getReturnTwo().getFlightSchedulePlan().getListOfFare());
                    totalReturnTwo = unitPriceDepartReturnTwo.multiply(new BigDecimal(noOfPassenger));
                    totalReturnPrice = totalReturnPrice.add(totalReturnTwo);
                }
                if (listOfSearchFlight.get(i).getReturnThree() != null) {
                    unitPriceDepartReturnThree = getHighestFare(listOfSearchFlight.get(i).getReturnThree().getFlightSchedulePlan().getListOfFare());
                    totalReturnThree = unitPriceDepartReturnThree.multiply(new BigDecimal(noOfPassenger));
                    totalReturnPrice = totalReturnPrice.add(totalReturnThree);
                

            }
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            String firstDepartTime = format.format(listOfSearchFlight.get(i).getDepartOne().getDepartureDateTime().getTime());
            String firstArrTime = format.format(listOfSearchFlight.get(i).getDepartOne().getArrivalDateTime().getTime());
            System.out.printf("%-5d %-15s %-45s %-45s %-25s %-25s %-6s", index, listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    firstDepartTime, firstArrTime, String.valueOf(unitPriceDepartOne));
            System.out.println();
            System.out.println();
            System.out.printf("Sub total price for connecting flight 1: " + String.valueOf(totalDepartOne));
            System.out.println();
            System.out.println();
            System.out.println("Connecting flight: ");
            System.out.println();
            System.out.printf("%-5s %-15s %-45s %-45s %-25s %-25s %-6s", "Id", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time", "Pricing");
            String secDepartTime = format.format(listOfSearchFlight.get(i).getDepartTwo().getDepartureDateTime().getTime());
            String secArrTime = format.format(listOfSearchFlight.get(i).getDepartTwo().getArrivalDateTime().getTime());
            System.out.println();
            System.out.printf("%-5d %-15s %-45s %-45s %-25s %-25s %-6s", index, listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getDepartTwo().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    secDepartTime, secArrTime, String.valueOf(unitPriceDepartTwo));
            System.out.println();
            System.out.println();
            System.out.println("Sub total price for connecting flight 2: " + String.valueOf(totalDepartTwo));
            System.out.println();
            if (listOfSearchFlight.get(i).getDepartThree() != null) {
                System.out.printf("%-5s %-15s %-45s %-45s %-25s %-25s %-6s ", "Id", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time", "Pricing");
                String thirdDepartTime = format.format(listOfSearchFlight.get(i).getDepartThree().getDepartureDateTime().getTime());
                String thirdArrTime = format.format(listOfSearchFlight.get(i).getDepartThree().getArrivalDateTime().getTime());
                System.out.println();
                System.out.printf("%-5d %-15s %-45s %-45s %-25s %-25s %-6s ", index, listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                        listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                        listOfSearchFlight.get(i).getDepartThree().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                        thirdDepartTime, thirdArrTime, String.valueOf(unitPriceDepartThree));
                System.out.println();
                System.out.println();
                System.out.println("Sub total price for connecting flight 3: " + String.valueOf(totalDepartThree));
                System.out.println();
            }
            totalSumOfConnecting = totalSumOfConnecting.add(totalDepartOne);
            totalSumOfConnecting = totalSumOfConnecting.add(totalDepartTwo);
            totalSumOfConnecting = totalSumOfConnecting.add(totalDepartThree);
            System.out.println("Total price : " + String.valueOf(totalSumOfConnecting));
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    public void printDirectFlightResult(List<FlightBundle> listOfSearchFlight, String nDays, int index, int noOfPassenger) {
        //connecting flight
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.printf("%-15s %-45s %-45s %-25s %-25s", "", "", nDays, "", "");
        System.out.println();
        System.out.printf("%-5s %-15s %-45s %-45s %-25s %-25s %-6s", "Id", "Flight Number ", " Origin Airport ", " Destination Airport ", "Departure Date", "Arriving Time", "Pricing");
        System.out.println();
        for (int i = 0; i < listOfSearchFlight.size(); i++) {
            BigDecimal unitPriceDepartOne = BigDecimal.ZERO;

            BigDecimal totalDepartOne = BigDecimal.ZERO;

            BigDecimal unitPriceDepartReturnOne = BigDecimal.ZERO;

            BigDecimal totalReturnOne = BigDecimal.ZERO;

            BigDecimal totalDepartPrice = BigDecimal.ZERO;
            BigDecimal totalReturnPrice = BigDecimal.ZERO;
            if (customer.getUserRole().equals(UserRole.CUSTOMER)) {

                //main flight
                if (listOfSearchFlight.get(i).getDepartOne() != null) {
                    unitPriceDepartOne = getLowestFare(listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getListOfFare());
                    totalDepartOne = unitPriceDepartOne.multiply(new BigDecimal(noOfPassenger));
                    totalDepartPrice = totalDepartPrice.add(totalDepartOne);
                }
                // return

                if (listOfSearchFlight.get(i).getReturnOne() != null) {
                    unitPriceDepartReturnOne = getLowestFare(listOfSearchFlight.get(i).getReturnOne().getFlightSchedulePlan().getListOfFare());
                    totalReturnOne = unitPriceDepartReturnOne.multiply(new BigDecimal(noOfPassenger));
                    totalReturnPrice = totalReturnPrice.add(totalReturnOne);
                }

            } else if (customer.getUserRole().equals(UserRole.PARTNEREMPLOYEE) || customer.getUserRole().equals(UserRole.PARTNERRESERVATIONMANAGER)) {
                if (listOfSearchFlight.get(i).getDepartOne() != null) {
                    unitPriceDepartOne = getHighestFare(listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getListOfFare());
                    totalDepartOne = unitPriceDepartOne.multiply(new BigDecimal(noOfPassenger));
                    totalDepartPrice = totalDepartPrice.add(totalDepartOne);
                }
                // return

                if (listOfSearchFlight.get(i).getReturnOne() != null) {
                    unitPriceDepartReturnOne = getHighestFare(listOfSearchFlight.get(i).getReturnOne().getFlightSchedulePlan().getListOfFare());
                    totalReturnOne = unitPriceDepartReturnOne.multiply(new BigDecimal(noOfPassenger));
                    totalReturnPrice = totalReturnPrice.add(totalReturnOne);
                }
            }
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            String firstDepartTime = format.format(listOfSearchFlight.get(i).getDepartOne().getDepartureDateTime().getTime());
            String firstArrTime = format.format(listOfSearchFlight.get(i).getDepartOne().getArrivalDateTime().getTime());
            System.out.printf("%-5d %-15s %-45s %-45s %-25s %-25s %-6s ", index, listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightNumber(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getAirportName(),
                    listOfSearchFlight.get(i).getDepartOne().getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName(),
                    firstDepartTime, firstArrTime, String.valueOf(unitPriceDepartOne));
            System.out.println();
            System.out.println();
            System.out.println(String.valueOf(totalDepartOne));
            System.out.println();
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    public List<FlightBundle> getConnectingFlight(GregorianCalendar actualDay, CabinClassType cabinType, int noOfPassenger, String departureAirport, String destinationAirport, int seqUpTo) {
        GregorianCalendar gDepart = (GregorianCalendar) actualDay.clone();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        List<FlightBundle> listOfFlightSchedules = null;
        List<FlightBundle> flightResultLessThreeDay = null;
        List<FlightBundle> flightResultAftThreeDay = null;
        List<FlightBundle> combined = null;
        try {
            flightResultLessThreeDay = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDays(actualDay, departureAirport, destinationAirport);

            listOfFlightSchedules = flightScheduleSessionBean.listOfConnectingFlightRecords(actualDay, departureAirport, destinationAirport);
            flightResultAftThreeDay = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDays(actualDay, departureAirport, destinationAirport);

            List<FlightBundle> flightResult = new ArrayList<FlightBundle>();

            flightResultLessThreeDay = processListGetCabinClassAndSeatAva(flightResultLessThreeDay, cabinType, noOfPassenger, "Connecting");
            flightResult = processListGetCabinClassAndSeatAva(listOfFlightSchedules, cabinType, noOfPassenger, "Connecting");
            flightResultAftThreeDay = processListGetCabinClassAndSeatAva(flightResultAftThreeDay, cabinType, noOfPassenger, "Connecting");
            combined = combineAllThreeFlights(flightResultLessThreeDay, listOfFlightSchedules, flightResultAftThreeDay);
            printConnectingFlightResult(flightResultLessThreeDay, " 3 Days before the booking date", seqUpTo + 1, noOfPassenger);

            printConnectingFlightResult(flightResult, " The actual date you are looking for ", seqUpTo + flightResultLessThreeDay.size() + 1, noOfPassenger);

            printConnectingFlightResult(flightResultAftThreeDay, "3 Days after the booking date ", seqUpTo + flightResultLessThreeDay.size() + flightResult.size() + 1, noOfPassenger);
        } catch (FlightRouteDoesNotExistException fe) {
            System.out.println("You have invalid O-D");
        }
        return combined;
        // Comparator<FlightScheduleEntity> sortFlightScheduleId = (FlightScheduleEntity p1, FlightScheduleEntity p2) -> Integer.valueOf(p1.getFlightScheduleId().intValue() - p2.getFlightScheduleId().intValue());
        //System.out.println("listOfSearchFlight" + listOfSearchFlight.size());
        //listOfSearchFlight.sort(sortFlightScheduleId);
    }

    public List<FlightBundle> getDirectFlight(String originIATA, String desIATA, GregorianCalendar actual, CabinClassType cabinType, int noOfPassenger, int seqUpTo) {
        List<FlightBundle> flightBundleLess3Day = null;
        List<FlightBundle> flightBundleActualDay = null;
        List<FlightBundle> flightBundleAdd3Day = null;
        GregorianCalendar gTempActual = (GregorianCalendar) actual.clone();

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
        List<FlightBundle> combined = combineAllThreeFlights(less3DaysFlight, actualFlight, add3DaysFlight);

        printDirectFlightResult(less3DaysFlight, "  3 Days before the booking date", seqUpTo + 1, noOfPassenger);
        printDirectFlightResult(actualFlight, " The actual date you are looking for ", seqUpTo + less3DaysFlight.size() + 1, noOfPassenger);
        printDirectFlightResult(add3DaysFlight, "3 Days after the booking date ", seqUpTo + less3DaysFlight.size() + actualFlight.size() + 1, noOfPassenger);
        return combined;
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
            if (!fs1.getSeatingPlan().get(i).isReserved() && fs1.getSeatingPlan().get(i).getCabinType().equals(cabinType)) {
                countFs1++;
                if (countFs1 == noOfPassenger) {
                    return true;
                }
            }
        }

        return false;
    }

    public void reserveFlightLocal(FlightBundle flightBundleForReservation, AirportEntity origin, AirportEntity destination, BigDecimal goingTotalPrice, BigDecimal returnTotalPrice, int numberOfPassengers) {
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

        FlightReservationEntity flightRes = createFlightReservation(origin.getIataAirportCode(), destination.getIataAirportCode(), goingTotalPrice, partner);
//            List<IndividualFlightReservationEntity> listOfIndividualFlightRes = new ArrayList<>();

        //3 flights total
        if (fs2 != null && fs3 != null) {
            BigDecimal amountForFs1 = fareForFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForFs1 = createIndivFlightRes(fs1, partner, amountForFs1, flightRes);
            BigDecimal amountForFs2 = fareForFs2.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForFs2 = createIndivFlightRes(fs2, partner, amountForFs2, flightRes);
            BigDecimal amountForFs3 = fareForFs3.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForFs3 = createIndivFlightRes(fs3, partner, amountForFs3, flightRes);

            //  FOR TESTING!!
//            IndividualFlightReservationEntity indivResForFs1 = createIndivFlightRes(fs1, partner, BigDecimal.TEN, flightRes);
//            IndividualFlightReservationEntity indivResForFs2 = createIndivFlightRes(fs2, partner, BigDecimal.TEN, flightRes);
//            IndividualFlightReservationEntity indivResForFs3 = createIndivFlightRes(fs3, partner, BigDecimal.TEN, flightRes);

            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForFs1 = findSeatsForCustomer(fs1, cabinForFs1);
//           

                System.out.print("Please enter Passenger first name: ");
                String firstName = sc.nextLine();
                System.out.print("Please enter Passenger last name: ");
                String lastName = sc.nextLine();
                System.out.print("Please enter Passenger passport number: ");
                String passportNumber = sc.nextLine().trim();
                PassengerEntity passenger = createPassenger(firstName, lastName, passportNumber);
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

                System.out.print("Please enter seat number for passenger for first flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForFs1 = createFare(fareForFs1.getFareBasisCode(), fareForFs1.getFareAmount(), fareForFs1.getCabinType());
                    seat.setFare(newFareForFs1);

//                    FareEntity newFare = createFare("Y1020", BigDecimal.TEN, cabinForFs1);
//                    seat.setFare(newFare);

                    seat.setPassenger(passenger);
                    indivResForFs1.getListOfSeats().add(seat);
                    indivResForFs1.getListOfPassenger().add(passenger);

                }

                //create fs2
                List<SeatEntity> listOfSeatsForFs2 = findSeatsForCustomer(fs2, cabinForFs2);
//            

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

                System.out.print("Please enter seat number for passenger second flight: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForFs2);
                if (seatfs2 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForFs2 = createFare(fareForFs2.getFareBasisCode(), fareForFs2.getFareAmount(), fareForFs2.getCabinType());
                    seatfs2.setFare(newFareForFs2);

//                    FareEntity newFare = createFare("Y1020", BigDecimal.TEN, cabinForFs2);
//                    seatfs2.setFare(newFare);

                    seatfs2.setPassenger(passenger);
                    indivResForFs2.getListOfSeats().add(seatfs2);
                    indivResForFs2.getListOfPassenger().add(passenger);

                }

                //create fs3
                List<SeatEntity> listOfSeatsForFs3 = findSeatsForCustomer(fs3, cabinForFs3);
//                

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

                System.out.print("Please enter seat number for passenger for third flight: ");
                String seatNumberfs3 = sc.nextLine().trim();
                SeatEntity seatfs3 = findSeat(seatNumberfs3, listOfSeatsForFs3);
                if (seatfs3 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs3.setReserved(true);
                    FareEntity newFareForFs3 = createFare(fareForFs3.getFareBasisCode(), fareForFs3.getFareAmount(), fareForFs3.getCabinType());
                    seatfs3.setFare(newFareForFs3);

//                    FareEntity newFare = createFare("Y1020", BigDecimal.TEN, cabinForFs3);
//                    seatfs3.setFare(newFare);

                    seatfs3.setPassenger(passenger);
                    indivResForFs3.getListOfSeats().add(seatfs3);
                    indivResForFs3.getListOfPassenger().add(passenger);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();

            }

//            indivResForFs1.setFlightReservation(flightRes);
            flightRes.getListOfIndividualFlightRes().add(indivResForFs1);
//            indivResForFs2.setFlightReservation(flightRes);
            flightRes.getListOfIndividualFlightRes().add(indivResForFs2);
//            indivResForFs3.setFlightReservation(flightRes);
            flightRes.getListOfIndividualFlightRes().add(indivResForFs3);

        } else if (fs2 != null && fs3 == null) { // 2 flights total

            BigDecimal amountForFs1 = fareForFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForFs1 = createIndivFlightRes(fs1, partner, amountForFs1, flightRes);
            BigDecimal amountForFs2 = fareForFs2.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForFs2 = createIndivFlightRes(fs2, partner, amountForFs2, flightRes);
//            IndividualFlightReservationEntity indivResForFs1 = createIndivFlightRes(fs1, partner, BigDecimal.TEN, flightRes);
//            IndividualFlightReservationEntity indivResForFs2 = createIndivFlightRes(fs2, partner, BigDecimal.TEN, flightRes);

            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForFs1 = findSeatsForCustomer(fs1, cabinForFs1);
//                

                System.out.print("Please enter Passenger first name: ");
                String firstName = sc.nextLine();
                System.out.print("Please enter Passenger last name: ");
                String lastName = sc.nextLine();
                System.out.print("Please enter Passenger passport number: ");
                String passportNumber = sc.nextLine().trim();
                PassengerEntity passenger = createPassenger(firstName, lastName, passportNumber);
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

                System.out.print("Please enter seat number for passenger first flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForFs1 = createFare(fareForFs1.getFareBasisCode(), fareForFs1.getFareAmount(), fareForFs1.getCabinType());
                    seat.setFare(fareForFs1);

//                    FareEntity newFare = createFare("Y1020", BigDecimal.TEN, cabinForFs1);
//                    seat.setFare(newFare);
                    seat.setPassenger(passenger);
                    indivResForFs1.getListOfSeats().add(seat);
                    indivResForFs1.getListOfPassenger().add(passenger);

                }

                //create fs2
                List<SeatEntity> listOfSeatsForFs2 = findSeatsForCustomer(fs2, cabinForFs2);
//               

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

                System.out.print("Please enter seat number for passenger second flight: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForFs2);
                if (seatfs2 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForFs2 = createFare(fareForFs2.getFareBasisCode(), fareForFs2.getFareAmount(), fareForFs2.getCabinType());
                    seatfs2.setFare(newFareForFs2);

//                    FareEntity newFare = createFare("Y1020", BigDecimal.TEN, cabinForFs2);
//                    seatfs2.setFare(newFare);
                    seatfs2.setPassenger(passenger);
                    indivResForFs2.getListOfSeats().add(seatfs2);
                    indivResForFs2.getListOfPassenger().add(passenger);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();
            }
//            indivResForFs1.setFlightReservation(flightRes);
            flightRes.getListOfIndividualFlightRes().add(indivResForFs1);
//            indivResForFs2.setFlightReservation(flightRes);
            flightRes.getListOfIndividualFlightRes().add(indivResForFs2);

        } else if (fs2 == null && fs3 == null) { //1 flight only
//            IndividualFlightReservationEntity indivResForFs1 = createIndivFlightRes(fs1, partner, BigDecimal.TEN, flightRes);
            BigDecimal amountForFs1 = fareForFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForFs1 = createIndivFlightRes(fs1, partner, amountForFs1, flightRes);

            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForFs1 = findSeatsForCustomer(fs1, cabinForFs1);

                System.out.print("Please enter Passenger first name: ");
                String firstName = sc.nextLine();
                System.out.print("Please enter Passenger last name: ");
                String lastName = sc.nextLine();
                System.out.print("Please enter Passenger passport number: ");
                String passportNumber = sc.nextLine().trim();
                PassengerEntity passenger = createPassenger(firstName, lastName, passportNumber);
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

                System.out.print("Please enter seat number for passenger for first flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForFs1 = createFare(fareForFs1.getFareBasisCode(), fareForFs1.getFareAmount(), fareForFs1.getCabinType());
                    seat.setFare(newFareForFs1);

//                    FareEntity newFare = createFare("Y1020", BigDecimal.TEN, cabinForFs1);
//                    seat.setFare(newFare);
                    seat.setPassenger(passenger);
                    indivResForFs1.getListOfSeats().add(seat);
                    indivResForFs1.getListOfPassenger().add(passenger);
                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();
            }
//            indivResForFs1.setFlightReservation(flightRes);
            flightRes.getListOfIndividualFlightRes().add(indivResForFs1);

        }

        listOfFlightRes.add(flightRes);

        FlightReservationEntity returnFlightRes = null;
        if (returnFs1 != null) {
            returnFlightRes = createFlightReservation(destination.getIataAirportCode(), origin.getIataAirportCode(), returnTotalPrice, partner);
        }
        if (returnFs1 != null && returnFs2 != null && returnFs3 != null) {
            BigDecimal amountForReturnFs1 = fareForReturnFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForReturnFs1 = createIndivFlightRes(returnFs1, partner, amountForReturnFs1, returnFlightRes);
            BigDecimal amountForReturnFs2 = fareForReturnFs2.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForReturnFs2 = createIndivFlightRes(returnFs2, partner, amountForReturnFs2, returnFlightRes);
            BigDecimal amountForReturnFs3 = fareForReturnFs3.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForReturnFs3 = createIndivFlightRes(returnFs3, partner, amountForReturnFs3, returnFlightRes);

//            IndividualFlightReservationEntity indivResForReturnFs1 = createIndivFlightRes(returnFs1, partner, BigDecimal.TEN, returnFlightRes);
//            IndividualFlightReservationEntity indivResForReturnFs2 = createIndivFlightRes(returnFs2, partner, BigDecimal.TEN, returnFlightRes);
//            IndividualFlightReservationEntity indivResForReturnFs3 = createIndivFlightRes(returnFs3, partner, BigDecimal.TEN, returnFlightRes);
            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForReturnFs1 = findSeatsForCustomer(returnFs1, cabinForReturnFs1);

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

                System.out.print("Please enter seat number for passenger for first flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForReturnFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForReturnFs1 = createFare(fareForReturnFs1.getFareBasisCode(), fareForReturnFs1.getFareAmount(), fareForReturnFs1.getCabinType());
                    seat.setFare(newFareForReturnFs1);
//                    FareEntity newFare = createFare("F1020", BigDecimal.TEN, cabinForReturnFs1);
//                    seat.setFare(newFare);

                    seat.setPassenger(passenger);
                    indivResForReturnFs1.getListOfSeats().add(seat);
                    indivResForReturnFs1.getListOfPassenger().add(passenger);

                }

                //create fs2
                List<SeatEntity> listOfSeatsForReturnFs2 = findSeatsForCustomer(returnFs2, cabinForReturnFs2);

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

                System.out.print("Please enter seat number for passenger for second flight: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForReturnFs2);
                if (seatfs2 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForReturnFs2 = createFare(fareForReturnFs2.getFareBasisCode(), fareForReturnFs2.getFareAmount(), fareForReturnFs2.getCabinType());
                    seatfs2.setFare(newFareForReturnFs2);

//                    FareEntity newFare = createFare("F1020", BigDecimal.TEN, cabinForReturnFs2);
//                    seatfs2.setFare(newFare);
                    seatfs2.setPassenger(passenger);
                    indivResForReturnFs2.getListOfSeats().add(seatfs2);
                    indivResForReturnFs2.getListOfPassenger().add(passenger);

                }

                //create fs3
                List<SeatEntity> listOfSeatsForReturnFs3 = findSeatsForCustomer(returnFs3, cabinForReturnFs3);

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

                System.out.print("Please enter seat number for passenger for third flight: ");
                String seatNumberfs3 = sc.nextLine().trim();
                SeatEntity seatfs3 = findSeat(seatNumberfs3, listOfSeatsForReturnFs3);
                if (seatfs3 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs3.setReserved(true);
                    FareEntity newFareForReturnFs3 = createFare(fareForReturnFs3.getFareBasisCode(), fareForReturnFs3.getFareAmount(), fareForReturnFs3.getCabinType());
                    seatfs3.setFare(newFareForReturnFs3);

//                    FareEntity newFare = createFare("F1020", BigDecimal.TEN, cabinForReturnFs3);
//                    seatfs3.setFare(newFare);
                    seatfs3.setPassenger(passenger);
                    indivResForReturnFs3.getListOfSeats().add(seatfs3);
                    indivResForReturnFs3.getListOfPassenger().add(passenger);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();
            }
//            indivResForReturnFs1.setFlightReservation(returnFlightRes);
            returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs1);
//            indivResForReturnFs2.setFlightReservation(returnFlightRes);
            returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs2);
//            indivResForReturnFs3.setFlightReservation(returnFlightRes);
            returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs3);

        } else if (returnFs1 != null && returnFs2 != null && returnFs3 == null) { // only 2 flights
            BigDecimal amountForReturnFs1 = fareForReturnFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForReturnFs1 = createIndivFlightRes(returnFs1, partner, amountForReturnFs1, returnFlightRes);
            BigDecimal amountForReturnFs2 = fareForReturnFs2.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForReturnFs2 = createIndivFlightRes(returnFs2, partner, amountForReturnFs2, returnFlightRes);

//            IndividualFlightReservationEntity indivResForReturnFs1 = createIndivFlightRes(returnFs1, partner, BigDecimal.TEN, returnFlightRes);
//            IndividualFlightReservationEntity indivResForReturnFs2 = createIndivFlightRes(returnFs2, partner, BigDecimal.TEN, returnFlightRes);
            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForReturnFs1 = findSeatsForCustomer(returnFs1, cabinForReturnFs1);

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

                System.out.print("Please enter seat number for passenger for first flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForReturnFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForReturnFs1 = createFare(fareForReturnFs1.getFareBasisCode(), fareForReturnFs1.getFareAmount(), fareForReturnFs1.getCabinType());
                    seat.setFare(fareForReturnFs1);

//                    FareEntity newFare = createFare("F1020", BigDecimal.TEN, cabinForReturnFs1);
//                    seat.setFare(newFare);
                    seat.setPassenger(passenger);
                    indivResForReturnFs1.getListOfSeats().add(seat);
                    indivResForReturnFs1.getListOfPassenger().add(passenger);

                }

                //create fs2
                List<SeatEntity> listOfSeatsForReturnFs2 = findSeatsForCustomer(returnFs2, cabinForReturnFs2);

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

                System.out.print("Please enter seat number for passenger for second flight: ");
                String seatNumberfs2 = sc.nextLine().trim();
                SeatEntity seatfs2 = findSeat(seatNumberfs2, listOfSeatsForReturnFs2);
                if (seatfs2 == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seatfs2.setReserved(true);
                    FareEntity newFareForReturnFs2 = createFare(fareForReturnFs2.getFareBasisCode(), fareForReturnFs2.getFareAmount(), fareForReturnFs2.getCabinType());
                    seatfs2.setFare(newFareForReturnFs2);

//                    FareEntity newFare = createFare("F1020", BigDecimal.TEN, cabinForReturnFs1);
//                    seatfs2.setFare(newFare);
                    seatfs2.setPassenger(passenger);
                    indivResForReturnFs2.getListOfSeats().add(seatfs2);
                    indivResForReturnFs2.getListOfPassenger().add(passenger);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();
            }
//            indivResForReturnFs1.setFlightReservation(returnFlightRes);
            returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs1);
//            indivResForReturnFs2.setFlightReservation(returnFlightRes);
            returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs2);

        } else if (returnFs1 != null && returnFs2 == null && returnFs3 == null) {
            BigDecimal amountForReturnFs1 = fareForReturnFs1.getFareAmount().multiply(BigDecimal.valueOf(numberOfPassengers));
            IndividualFlightReservationEntity indivResForReturnFs1 = createIndivFlightRes(returnFs1, partner, amountForReturnFs1, returnFlightRes);

//            IndividualFlightReservationEntity indivResForReturnFs1 = createIndivFlightRes(returnFs1, partner, BigDecimal.TEN, returnFlightRes);
            for (int i = 0; i < numberOfPassengers; i++) {
                List<SeatEntity> listOfSeatsForReturnFs1 = findSeatsForCustomer(returnFs1, cabinForReturnFs1);

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

                System.out.print("Please enter seat number for passenger for first flight: ");
                String seatNumber = sc.nextLine().trim();
                SeatEntity seat = findSeat(seatNumber, listOfSeatsForReturnFs1);
                if (seat == null) {
                    System.out.println("Invalid Seat number!");
                    return;
                } else {

                    seat.setReserved(true);
                    FareEntity newFareForReturnFs1 = createFare(fareForReturnFs1.getFareBasisCode(), fareForReturnFs1.getFareAmount(), fareForReturnFs1.getCabinType());
                    seat.setFare(newFareForReturnFs1);

//                    FareEntity newFare = createFare("F1020", BigDecimal.TEN, cabinForReturnFs1);
//                    seat.setFare(newFare);
                    seat.setPassenger(passenger);
                    indivResForReturnFs1.getListOfSeats().add(seat);
                    indivResForReturnFs1.getListOfPassenger().add(passenger);

                }
                System.out.println("===================END OF BOOKING FOR CURRENT PASSENGER===================");
                System.out.println();
            }
//            indivResForReturnFs1.setFlightReservation(returnFlightRes);
            returnFlightRes.getListOfIndividualFlightRes().add(indivResForReturnFs1);
        }
        if (returnFlightRes != null) {
            listOfFlightRes.add(returnFlightRes);
        }

        //call flight reservation session bean
        System.out.println("Would you like to proceed with the booking? (1 for yes, 2 for no)");
        System.out.print("Please enter choice: ");
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) {
            boolean goodInput = false;

            while (!goodInput) {
                try {
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
                        XMLGregorianCalendar expiryDate = convertCalendarExpiryDate(expiryDateStr);

                        for (FlightReservationEntity fr : listOfFlightRes) {
                            fr.setCreditCardExpiryDate(expiryDate);
                            fr.setCreditCardName(creditCardName);
                            fr.setCreditCardNumber(creditCardNumber);
                            fr.setCvv(cvv);

                            fr.getCustomer().getListOfFlightReservation().add(fr);
                        }

                        goodInput = true;
                    }
                } catch (IncorrectFormatException_Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (goodInput) {
                reserveFlight(listOfFlightRes);
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

    private static java.util.List<ws.client.FlightReservationEntity> retrieveListOfReservation(java.lang.Long custId) throws CustomerHasNoReservationException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.retrieveListOfReservation(custId);
    }

    private static PartnerEntity loginPartner(java.lang.String arg0, java.lang.String arg1) throws CustomerLoginInvalid_Exception, AccessFromWrongPortalException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.loginPartner(arg0, arg1);
    }

    private static FlightReservationEntity retrieveIndividualFlightReservation(java.lang.Long arg0) throws FlightReservationDoesNotExistException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.retrieveIndividualFlightReservation(arg0);
    }

    private static java.util.List<ws.client.FlightBundle> getDirectFlight(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1, java.lang.String arg2, java.lang.String arg3) throws FlightRouteDoesNotExistException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.getDirectFlight(arg0, arg1, arg2, arg3);
    }

    private static java.util.List<ws.client.FlightBundle> listOfConnectingFlightRecords(javax.xml.datatype.XMLGregorianCalendar arg0, java.lang.String arg1, java.lang.String arg2) throws FlightRouteDoesNotExistException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.listOfConnectingFlightRecords(arg0, arg1, arg2);
    }

    private static java.util.List<ws.client.FlightBundle> listOfConnectingFlightRecordsAftThreeDays(javax.xml.datatype.XMLGregorianCalendar arg0, java.lang.String arg1, java.lang.String arg2) throws FlightRouteDoesNotExistException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.listOfConnectingFlightRecordsAftThreeDays(arg0, arg1, arg2);
    }

    private static java.util.List<ws.client.FlightBundle> listOfConnectingFlightRecordsLessThreeDays(javax.xml.datatype.XMLGregorianCalendar arg0, java.lang.String arg1, java.lang.String arg2) throws FlightRouteDoesNotExistException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.listOfConnectingFlightRecordsLessThreeDays(arg0, arg1, arg2);
    }

    private static void retrieveOD(java.lang.String oIataCode) throws FlightRouteDoesNotExistException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        port.retrieveOD(oIataCode);
    }

    private static XMLGregorianCalendar convertCalendar(java.lang.String arg0) throws IncorrectFormatException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.convertCalendar(arg0);
    }

    private static void reserveFlight(java.util.List<ws.client.FlightReservationEntity> arg0) {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        port.reserveFlight(arg0);
    }

    private static FareEntity createFare(java.lang.String arg0, java.math.BigDecimal arg1, ws.client.CabinClassType arg2) {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.createFare(arg0, arg1, arg2);
    }

    private static FlightReservationEntity createFlightReservation(java.lang.String arg0, java.lang.String arg1, java.math.BigDecimal arg2, ws.client.CustomerEntity arg3) {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.createFlightReservation(arg0, arg1, arg2, arg3);
    }

    private static IndividualFlightReservationEntity createIndivFlightRes(ws.client.FlightScheduleEntity arg0, ws.client.CustomerEntity arg1, java.math.BigDecimal arg2, ws.client.FlightReservationEntity arg3) {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.createIndivFlightRes(arg0, arg1, arg2, arg3);
    }

    private static PassengerEntity createPassenger(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.createPassenger(arg0, arg1, arg2);
    }

    private static XMLGregorianCalendar convertCalendarExpiryDate(java.lang.String arg0) throws IncorrectFormatException_Exception {
        ws.client.PartnerReservationSystem_Service service = new ws.client.PartnerReservationSystem_Service();
        ws.client.PartnerReservationSystem port = service.getPartnerReservationSystemPort();
        return port.convertCalendarExpiryDate(arg0);
    }

}
