/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.AircraftSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import entity.AirportEntity;
import entity.FlightRouteEntity;
import java.util.List;
import java.util.Scanner;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AirportODPairNotFoundException;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightRouteExistInOtherClassException;
import util.exception.FlightRouteODPairExistException;

/**
 *
 * @author sohqi
 */
public class FlightRoute {

    private FlightRouteSessionBeanRemote flightRouteSessionBean = null;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FlightRoute() {

        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public FlightRoute(FlightRouteSessionBeanRemote flightRouteSessionBean) {
        this();
        this.flightRouteSessionBean = flightRouteSessionBean;
    }

    public void FlightRouteApp() {
        Scanner sc = new Scanner(System.in);
        System.out.println("**Welcome to creation of air craft configuration **");
        while (true) {

            System.out.println("1. Create flight route.");
            System.out.println("2. View flight route.");
            System.out.println("3. Delete flight route.");
            System.out.println("0. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                createFlightRoute();
            } else if (choice == 2) {
                viewFlightRoute();
            } else if (choice == 3) {
                DeleteFlightRoute();
            } else if (choice == 0) {
                break;
            }
        }
    }

    public void createFlightRoute() {
        List<AirportEntity> listOfAirport = flightRouteSessionBean.getListOfAirportEntity();
        System.out.println("***Airport List***");
        System.out.printf("%16s%2s%3s", "Country", ":", "IATA Code:");
        System.out.println();
        for (AirportEntity airportEntity : listOfAirport) {

            System.out.printf("%16s%2s%3s", airportEntity.getCountry(), ":", airportEntity.getIataAirportCode());
            // .out.printf("Country: %s%10 || Airport IATA code: %s", airportEntity.getCountry(), airportEntity.getIataAirportCode());
            System.out.println();
        }
        Scanner sc = new Scanner(System.in);

        System.out.println("**Create flight route.**");

        System.out.println("Please enter the origin location of IATA airport code.");
        String oIATAAirport = sc.nextLine().toUpperCase();
        while (oIATAAirport.length() != 3) {
            System.out.println("Invalid origin location IATA code. It should only have 3 characters");
            System.out.println("Please enter the origin location of IATA airport code.");
            oIATAAirport = sc.nextLine().toUpperCase().trim();
        }

        System.out.println("Please enter the Destination location of IATA airport code.");
        String dIATAAirport = sc.nextLine().toUpperCase().trim();
        while (dIATAAirport.length() != 3) {
            System.out.println("Invalid destination location IATA code. It should only have 3 characters");
            System.out.println("Please enter the destination location of IATA airport code.");
            dIATAAirport = sc.nextLine().toUpperCase().trim();
        }

        System.out.println("Is there a return route? Yes/No");
        String returnRoute = sc.nextLine().toUpperCase();
        while (!returnRoute.equalsIgnoreCase("Yes") && !returnRoute.equalsIgnoreCase("No")) {
            System.out.println("Invalid return route.");
            System.out.println("Is there a return route? Yes/No");
            returnRoute = sc.nextLine().trim();
        }

        try {
            Long id = flightRouteSessionBean.createFlightRoute(oIATAAirport, dIATAAirport, returnRoute);
            System.out.println("You have successfully created flight route");
        } catch (FlightRouteODPairExistException ex) {
            System.out.println("Flight route origin-destination already exist in the database");
        } catch (AirportODPairNotFoundException ex) {
            System.out.println("Invalid input for O-D. Please try again.");
        }

    }

    public void viewFlightRoute() {
        List<FlightRouteEntity> listOfFlightRoute = flightRouteSessionBean.viewListOfFlightRoute();
        for (int i = 0; i < listOfFlightRoute.size(); i++) {
            FlightRouteEntity fr = listOfFlightRoute.get(i);
            System.out.println("***Flight Route***");
            System.out.println(String.format("Origin Location(IATA airport Code): %s (%s)", fr.getOriginLocation().getAirportName(), fr.getOriginLocation().getIataAirportCode()));
            System.out.println("Country: " + fr.getOriginLocation().getCountry());
            System.out.println("State: " + fr.getOriginLocation().getState());
            System.out.println("City: " + fr.getOriginLocation().getCity());
            System.out.println(String.format("Time Zone: %d hour(s) : %d minute(s)  ", fr.getOriginLocation().getTimeZoneHour(), fr.getOriginLocation().getTimeZoneMin()));

            System.out.println(String.format("Destination Location(IATA airport Code): %s (%s)", fr.getDestinationLocation().getAirportName(), fr.getDestinationLocation().getIataAirportCode()));
            System.out.println("Country: " + fr.getDestinationLocation().getCountry());
            System.out.println("State: " + fr.getDestinationLocation().getState());
            System.out.println("City: " + fr.getDestinationLocation().getCity());
            System.out.println(String.format("Time Zone: %d hour(s) : %d minute(s)  ", fr.getDestinationLocation().getTimeZoneHour(), fr.getDestinationLocation().getTimeZoneMin()));

            if (fr.getReturnRoute() != null) {
                System.out.println("*** Return Flight Route***");
                System.out.println(String.format("Origin Location(IATA airport Code): %s (%s)", fr.getReturnRoute().getOriginLocation().getAirportName(), fr.getReturnRoute().getOriginLocation().getIataAirportCode()));
                System.out.println("Country: " + fr.getReturnRoute().getOriginLocation().getCountry());
                System.out.println("State: " + fr.getReturnRoute().getOriginLocation().getState());
                System.out.println("City: " + fr.getReturnRoute().getOriginLocation().getCity());
                System.out.println(String.format("Time Zone: %d hour(s) : %d minute(s)  ", fr.getReturnRoute().getOriginLocation().getTimeZoneHour(), fr.getReturnRoute().getOriginLocation().getTimeZoneMin()));

                System.out.println(String.format("Destination Location(IATA airport Code): %s (%s)", fr.getReturnRoute().getDestinationLocation().getAirportName(), fr.getReturnRoute().getDestinationLocation().getIataAirportCode()));
                System.out.println("Country: " + fr.getReturnRoute().getDestinationLocation().getCountry());
                System.out.println("State: " + fr.getReturnRoute().getDestinationLocation().getState());
                System.out.println("City: " + fr.getReturnRoute().getDestinationLocation().getCity());
                System.out.println(String.format("Time Zone: %d hour(s) : %d minute(s)  ", fr.getReturnRoute().getDestinationLocation().getTimeZoneHour(), fr.getReturnRoute().getDestinationLocation().getTimeZoneMin()));
            }
            System.out.println("--------------------------------------------------------------------------------------------------------------------------");
        }

    }

    public void DeleteFlightRoute() {
        Scanner sc = new Scanner(System.in);
        List<FlightRouteEntity> listOfFlightRoute = flightRouteSessionBean.viewListOfFlightRoute();
        System.out.println("***List of flight route***");
        for (int i = 0; i < listOfFlightRoute.size(); i++) {
            System.out.println(String.format("%d. %s-%s", listOfFlightRoute.get(i).getFlightRouteId(), listOfFlightRoute.get(i).getOriginLocation().getIataAirportCode(), listOfFlightRoute.get(i).getDestinationLocation().getIataAirportCode()));
        }
        System.out.println("Please enter the id you wish to delete");
        Long id = sc.nextLong();
        try {
            boolean isDelete = flightRouteSessionBean.DeleteFlightRoute(id);
            if (isDelete) {
                System.out.println("You have successfully deleted");
            }
        } catch (FlightRouteDoesNotExistException ex) {
            System.out.println("The id you have entered does not exist in the database.");
        } catch (FlightRouteExistInOtherClassException ex) {
            System.out.println("The id you have entered is currently used by other flight record(s).");
        }

    }

}
