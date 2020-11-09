/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.CabinClassConfigurationEntity;
import entity.FareEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.MultipleFlightScheduleEntity;
import entity.RecurringScheduleEntity;
import entity.RecurringWeeklyScheduleEntity;
import entity.SeatEntity;
import entity.SingleFlightScheduleEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.CabinClassType;
import util.exception.FlightScheduleDoesNotExistException;
import util.exception.FlightSchedulePlanDoesNotExistException;
import util.exception.FlightSchedulePlanIsEmptyException;

/**
 *
 * @author nickg
 */
public class SalesManagement {

    private FlightSessionBeanRemote flightSessionBean;

    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean;

    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public SalesManagement(FlightSessionBeanRemote flightSessionBean, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean) {
        this.flightSessionBean = flightSessionBean;
        this.flightSchedulePlanSessionBean = flightSchedulePlanSessionBean;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        while (choice != 3) {

            System.out.println("===================WELCOME TO SALES MANAGEMENT MODULE======================");
            System.out.println("1. View Seats Inventory");
            System.out.println("2. View Flight Reservations");
            System.out.println("3. Exit");
            System.out.print("Please enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                viewSeatInventory(sc);
            } else if (choice == 2) {
                viewFlightReservation(sc);
            } else if (choice == 3) {
                System.out.println("Good bye!");
            } else {
                System.out.println("Invalid input! Please try again!");
            }

        }
    }

    public void viewSeatInventory(Scanner sc) {
        System.out.print("Please enter flight number: ");
        String flightNumber = sc.nextLine().trim();

        try {
            List<FlightSchedulePlanEntity> listOfFsp = flightSchedulePlanSessionBean.getFlightSchedulePlanForFlight(flightNumber);
            viewAllFsp(listOfFsp);
            System.out.print("Please enter the ID of the Flight Schedule Plan you wish to view: ");
            Long fspId = sc.nextLong();
            sc.nextLine();

            FlightSchedulePlanEntity currentFsp = flightSchedulePlanSessionBean.viewFlightSchedulePlan(fspId);
            viewSpecificFsp(currentFsp);
            System.out.print("Please enter the ID of the Flight Schedule you wish to view: ");
            Long fsId = sc.nextLong();
            sc.nextLine();

            FlightScheduleEntity fs = flightSchedulePlanSessionBean.getFlightScheduleUsingID(fsId);

            System.out.println("========Seating Inventory For Flight Schedule " + fs.getFlightScheduleId() + " for Flight " + fs.getFlightSchedulePlan().getFlightNumber() + "================");
            System.out.println("---------Total Seat Inventory---------");
            System.out.printf("%-20s%-20s%-20s%-20s", "Flight Number", "Available Seats", "Reserved Seats", "Balanced Seats");
            System.out.println();

            int numAvailable = fs.getSeatingPlan().size();
            int numReserved = getBookedSeatInventory(fs.getSeatingPlan());
            int numBalanced = numAvailable - numReserved;

            System.out.printf("%-20s%-20d%-20d%-20d", fs.getFlightSchedulePlan().getFlightNumber(), numAvailable, numReserved, numBalanced);
            System.out.println();
            System.out.println();

            System.out.println("---------Seat Inventory For Each Cabin Class---------");

            System.out.printf("%-30s%-20s%-20s%-20s", "Cabin Type", "Available Seats", "Reserved Seats", "Balanced Seats");
            System.out.println();
            for (CabinClassConfigurationEntity cabin : fs.getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses()) {
                numReserved = getBookSeatPerCabinClass(fs.getSeatingPlan(), cabin.getCabinclassType());
                numAvailable = cabin.getAvailableSeats();
                numBalanced = numAvailable - numReserved;
                String cabinType = "";
                if (cabin.getCabinclassType().equals(CabinClassType.F)) {
                    cabinType = "First Class";
                } else if (cabin.getCabinclassType().equals(CabinClassType.J)) {
                    cabinType = "Business Class";
                } else if (cabin.getCabinclassType().equals(CabinClassType.W)) {
                    cabinType = "Premium Economy Class";
                } else if (cabin.getCabinclassType().equals(CabinClassType.Y)) {
                    cabinType = "Economy Class";
                }

                System.out.printf("%-30s%-20d%-20d%-20d", cabinType, numAvailable, numReserved, numBalanced);
                System.out.println();
            }

        } catch (FlightSchedulePlanIsEmptyException | FlightSchedulePlanDoesNotExistException | FlightScheduleDoesNotExistException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void viewAllFsp(List<FlightSchedulePlanEntity> listOfFsp) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.println("=========FLIGHT SCHEDULE PLAN==========");
        System.out.printf("%-10s%-20s%-70s%-70s%-30s", "FSP ID", "Flight Number", "Origin Airport", "Destination Airport", "Flight Schedule Plan Type");
        System.out.println();
        for (FlightSchedulePlanEntity fsp : listOfFsp) {
            String originLocation = fsp.getFlightEntity().getFlightRoute().getOriginLocation().getAirportName() + " in " + fsp.getFlightEntity().getFlightRoute().getOriginLocation().getCountry() + ", " + fsp.getFlightEntity().getFlightRoute().getOriginLocation().getCity();
            String destinationLocation = fsp.getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName() + " in " + fsp.getFlightEntity().getFlightRoute().getDestinationLocation().getCountry() + ", " + fsp.getFlightEntity().getFlightRoute().getDestinationLocation().getCity();
            String classType = "";
            if (fsp instanceof SingleFlightScheduleEntity) {
                classType = "Single Flight Schedule";
            } else if (fsp instanceof MultipleFlightScheduleEntity) {
                classType = "Multiple Flight Schedule";
            } else if (fsp instanceof RecurringScheduleEntity) {
                classType = "Recurrening Flight Schedule";
            } else if (fsp instanceof RecurringWeeklyScheduleEntity) {
                classType = "Recurring Weekly Flight Schedule";
            }
            System.out.printf("%-10s%-20s%-70s%-70s%-30s", fsp.getFlightSchedulePlanId(), fsp.getFlightNumber(), originLocation, destinationLocation, classType);
            System.out.println();
        }

        System.out.println();

    }

    public void viewSpecificFsp(FlightSchedulePlanEntity fsp) {
        System.out.println("===================Flight Route Details===================");
        System.out.printf("%-15s%-25s%-14s%-70s", "Flight ID", "Origin/Destination", "IATA Code", " Airport");
        System.out.println();
        System.out.println();
        FlightRouteEntity fr = fsp.getFlightEntity().getFlightRoute();
        String originLocation = fsp.getFlightEntity().getFlightRoute().getOriginLocation().getAirportName() + " in " + fsp.getFlightEntity().getFlightRoute().getOriginLocation().getCountry() + ", " + fsp.getFlightEntity().getFlightRoute().getOriginLocation().getCity();
        String destinationLocation = fsp.getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName() + " in " + fsp.getFlightEntity().getFlightRoute().getDestinationLocation().getCountry() + ", " + fsp.getFlightEntity().getFlightRoute().getDestinationLocation().getCity();
        System.out.printf("%-15s%-25s%-15s%-70s", fr.getFlightRouteId(), "Origin", fr.getOriginLocation().getIataAirportCode(), originLocation);
        System.out.println();
        System.out.printf("%-15s%-25s%-15s%-70s", fr.getFlightRouteId(), "Destination", fr.getDestinationLocation().getIataAirportCode(), destinationLocation);
        System.out.println();
        System.out.println();
        System.out.println("============FLIGHT SCHEDULE=============");
        System.out.println();
        System.out.printf("%-25s%-30s%-30s%-30s", "Flight Schedule ID", "Departure Date/Time", "Flight Duration", "Arrival Date/Time");
        System.out.println();
        for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
            int flightMins = fs.getFlightDuration();
            int flightHour = flightMins / 60;
            flightMins %= 60;
            String flightduration = flightHour + "hr " + flightMins + " mins";
            System.out.printf("%-25s%-30s%-30s%-30s", fs.getFlightScheduleId(), format.format(fs.getDepartureDateTime().getTime()), flightduration, format.format(fs.getArrivalDateTime().getTime()));
            System.out.println();
        }

        System.out.println();
        System.out.println("-----Fares for Flight Schedule Plan-----");
        for (FareEntity fare : fsp.getListOfFare()) {
            System.out.printf("%-20s%-15s%-15s", "Fare basis code", "Fare amount", "Fare cabin type");
            System.out.println();
            System.out.printf("%-20s%-15.2f%-15s", fare.getFareBasisCode(), fare.getFareAmount(), fare.getCabinType());
            System.out.println();
        }
    }

    public int getBookedSeatInventory(List<SeatEntity> listOfSeat) {
        int numReserved = 0;
        for (SeatEntity seat : listOfSeat) {
            if (seat.isReserved()) {
                numReserved++;
            }
        }

        return numReserved;

    }

    public int getBookSeatPerCabinClass(List<SeatEntity> listOfSeat, CabinClassType cabinClassType) {
        int numReserved = 0;
        for (SeatEntity seat : listOfSeat) {
            if (seat.getCabinType().equals(cabinClassType) && seat.isReserved()) {
                numReserved++;
            }
        }

        return numReserved;
    }

    public void viewFlightReservation(Scanner sc) {
        System.out.print("Please enter flight number: ");
        String flightNumber = sc.nextLine().trim();

        try {
            List<FlightSchedulePlanEntity> listOfFsp = flightSchedulePlanSessionBean.getFlightSchedulePlanForFlight(flightNumber);
            viewAllFsp(listOfFsp);
            System.out.print("Please enter the ID of the Flight Schedule Plan you wish to view: ");
            Long fspId = sc.nextLong();
            sc.nextLine();

            FlightSchedulePlanEntity currentFsp = flightSchedulePlanSessionBean.viewFlightSchedulePlan(fspId);
            viewSpecificFsp(currentFsp);
            System.out.print("Please enter the ID of the Flight Schedule you wish to view: ");
            Long fsId = sc.nextLong();
            sc.nextLine();

            FlightScheduleEntity fs = flightSchedulePlanSessionBean.getFlightScheduleUsingID(fsId);
            List<SeatEntity> listOfAllReservedSeats = new ArrayList<>();
            for (CabinClassConfigurationEntity cabin : fs.getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses()) {
                listOfAllReservedSeats.addAll(getPassengersPerCabinClass(fs.getSeatingPlan(), cabin.getCabinclassType()));
            }
            
            for(SeatEntity seat : listOfAllReservedSeats){
                System.out.printf("%-30s%-40s%-20s", "Seating Number", "Passenger Name", "Fare Basis Code");
                System.out.println();
                String passsengerName = seat.getPassenger().getFirstName() + " " + seat.getPassenger().getLastName();
                System.out.printf("%-30s%-40s%-20s", seat.getSeatNumber(),passsengerName, seat.getFare().getFareBasisCode());
                System.out.println();
            }
            

        } catch (FlightSchedulePlanIsEmptyException | FlightSchedulePlanDoesNotExistException | FlightScheduleDoesNotExistException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<SeatEntity> getPassengersPerCabinClass(List<SeatEntity> listOfSeat, CabinClassType cabinClassType) {
        List<SeatEntity> listOfReservedSeats = new ArrayList<>();
        for (SeatEntity seat : listOfSeat) {
            if (seat.getCabinType().equals(cabinClassType) && seat.isReserved()) {
                listOfReservedSeats.add(seat);
            }
        }

        return listOfReservedSeats;
    }
}
