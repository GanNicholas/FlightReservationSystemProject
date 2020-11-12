/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import ws.client.AccessFromWrongPortalException_Exception;
import ws.client.CustomerHasNoReservationException_Exception;
import ws.client.CustomerLoginInvalid_Exception;
import ws.client.FlightReservationDoesNotExistException_Exception;
import ws.client.FlightReservationEntity;
import ws.client.IndividualFlightReservationEntity;
import ws.client.PartnerEntity;
import ws.client.SeatEntity;

/**
 *
 * @author nickg
 */
public class RunApp {

    private PartnerEntity partner;

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

}
