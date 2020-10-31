/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.AircraftSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.AircraftConfigurationEntity;
import entity.CabinClassConfigurationEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.CabinClassType;
import util.exception.AircraftConfigurationNotExistException;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightExistsException;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightScheduleExistException;
import util.exception.FlightSchedulePlanDoesNotExistException;
import util.exception.FlightSchedulePlanIsEmptyException;
import util.exception.IncorrectFormatException;

/**
 *
 * @author nickg
 */
public class FlightSchedulePlan {

    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean;

    private FlightSessionBeanRemote flightSessionBean;

    private AircraftSessionBeanRemote aircraftSessionBeanRemote;

    private FlightRouteSessionBeanRemote flightRouteSessionBean;

    public FlightSchedulePlan(FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean, FlightSessionBeanRemote flightSessionBean, AircraftSessionBeanRemote aircraftSessionBeanRemote, FlightRouteSessionBeanRemote flightRouteSessionBean) {
        this.flightSchedulePlanSessionBean = flightSchedulePlanSessionBean;
        this.flightSessionBean = flightSessionBean;
        this.aircraftSessionBeanRemote = aircraftSessionBeanRemote;
        this.flightRouteSessionBean = flightRouteSessionBean;
    }

    public void runFSP() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("**Welcome to Flight Schedule Plan Management **");
            System.out.println("What would you like to do? ");
            System.out.println("1. Create Flight.");
            System.out.println("2. Update Flight.");
            System.out.println("3. View All Flights.");
            System.out.println("4. View Flight Details.");
            System.out.println("5. Delete Flight.");
            System.out.println("6. Create Flight Schedule Plan.");
            System.out.println("7. View all flight schedule plan");
            System.out.println("8. View detail of a flight schedule plan.");
            System.out.println("9. Update flight schedule plan.");
            System.out.println("10. Delete flight schedule plan");
            System.out.println("0. Exit");
            System.out.print("Please enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 1) {
                createFlight(sc);
            } else if (choice == 2) {
                updateFlight(sc);
            } else if (choice == 3) {
                viewAllFlights(sc);
            } else if (choice == 4) {

            } else if (choice == 5) {

            } else if (choice == 6) {
                createfsp(sc, flightSchedulePlanSessionBean);
            } else if (choice == 7) {
                viewAllFsp(sc);
            } else if (choice == 8) {
                viewSpecificFsp(sc);
            } else if (choice == 9) {

            } else if (choice == 10) {

            } else if (choice == 0) {
                System.out.println("Goodbye!");
                break;
            }
        }

    }

    public void createfsp(Scanner sc, FlightSchedulePlanSessionBeanRemote flightScheduleSessionBean) {
        int counter = 0;
        while (true) {
            System.out.println("----Create a new Flight Schedule Plan----");
            System.out.println("1. Create single/Multiple Flight Schedules");
            System.out.println("2. Create Recurrent Flight Schedules");
            System.out.println("3. Back");
            System.out.print("Please enter your choice: ");
            String choiceStr = sc.nextLine().trim();
            int choice = Integer.parseInt(choiceStr);

            if (choice == 1) {
                singleMultipleFlightSchedule(sc);
                counter = 0;
            } else if (choice == 2) {
                recurrentFlightSchedule(sc);
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Please enter a valid entry!");
                counter++;
            }

            if (counter == 3) {
                System.out.println("Too many tries! GoodBye!");
                break;
            }

        }
    }

    public void singleMultipleFlightSchedule(Scanner sc) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        List<FareEntity> listOfFares = new ArrayList<>();
        Integer layover = 0;
        boolean reenter = false;
        List<GregorianCalendar> listOfDepartDateTime = new ArrayList<>();
        while (true) {
            reenter = false;
            listOfDepartDateTime.clear();
            listOfFares.clear();
            System.out.print("Please enter flight number: ");
            String flightNumber = sc.nextLine().trim();
            System.out.print("Please enter number of flight schedule you wish to create: ");
            String numScheduleString = sc.nextLine().trim();
            int numSchedule = Integer.parseInt(numScheduleString);

            // run a for loop to take in a list of date and time
            for (int i = 0; i < numSchedule; i++) {
                System.out.print("Please enter departure date and time (Please enter in this format: dd/mm/yyyy/hh/mm) ");
                String dateTime = sc.nextLine().trim();
                try {
                    GregorianCalendar departDateTime = createDateTime(dateTime);
                    Date date = departDateTime.getTime();
                    System.out.println(format.format(date));

                    listOfDepartDateTime.add(departDateTime);
                } catch (IncorrectFormatException ex) {
                    System.out.println(ex.getMessage());
                }

            }

            System.out.print("Please enter flight duration (in minutes)");
            String duration = sc.nextLine().trim();
            Integer flightDuration = Integer.parseInt(duration);
            FlightEntity flight = null;
            boolean returnFlight = false;
            try {
                flight = flightSessionBean.viewFlightDetails(flightNumber);
                if (flight.getReturnFlight() != null) {
                    System.out.println("Please enter if you would like to create a return flight schedule plan for your existing flight? (1 for yes)");
                    System.out.print("Please enter your choice: ");
                    String choiceInString = sc.nextLine().trim();
                    int choice = Integer.parseInt(choiceInString);
                    if (choice == 1) {
                        returnFlight = true;
                        System.out.print("Please enter layover duration: ");
                        layover = sc.nextInt();
                        sc.nextLine();
                    } else {
                        returnFlight = false;
                    }
                }
            } catch (FlightDoesNotExistException ex) {
                System.out.println(ex.getMessage());
                reenter = true;
            }

            if (reenter == false) {
                listOfFares = createFare(sc, flight);

                for (FareEntity fare : listOfFares) {
                    Set<ConstraintViolation<FareEntity>> violations = validator.validate(fare);
                    if (!violations.isEmpty()) {
                        for (ConstraintViolation<FareEntity> violation : violations) {
                            System.out.println(violation.getPropertyPath() + "\n " + violation.getMessage() + "  " + violation.getInvalidValue());
                            reenter = true;
                        }
                    }
                    if (reenter) {
                        break;
                    }
                }
            }
            if (reenter == false) {
                try {
                    String message = flightSchedulePlanSessionBean.createNonRecurrentFlightSchedulePlan(flightNumber, listOfDepartDateTime, flightDuration, returnFlight, listOfFares, layover);
                    System.out.println(message);
                    break;
                } catch (FlightDoesNotExistException | FlightScheduleExistException ex) {
                    System.out.println(ex.getMessage());
                }
            }

        }
    }

    public void recurrentFlightSchedule(Scanner sc) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        List<FareEntity> listOfFares = new ArrayList<>();
        Integer layover = 0;
        boolean reenter = false;
        List<GregorianCalendar> listOfDepartDateTime = new ArrayList<>();

        while (true) {
            listOfDepartDateTime.clear();
            listOfFares.clear();
            reenter = false;
            System.out.print("Please enter flight number: ");
            String flightNumber = sc.nextLine().trim();
            System.out.print("Please enter frequency of flight schedule you wish to create: ");
            String numFrequencyString = sc.nextLine().trim();
            int numFrequency = Integer.parseInt(numFrequencyString);

            System.out.print("Please enter departure date and time (Please enter in this format (dd/mm/yyyy/hh/mm) : ");
            String dateTime = sc.nextLine().trim();
            GregorianCalendar departDateTime = null;
            try {
                departDateTime = createDateTime(dateTime);
            } catch (IncorrectFormatException ex) {
                System.out.println(ex.getMessage());
                reenter = true;
            }
            //POTENTIAL ERROR
            System.out.print("Please enter end date (dd/mm/yyyy/hh/mm) : ");
            String endDateTimeStr = sc.nextLine().trim();
            GregorianCalendar endDateTime = null;
            try {
                endDateTime = createDateTime(endDateTimeStr);
            } catch (IncorrectFormatException ex) {
                System.out.println(ex.getMessage());
                reenter = true;
            }
            System.out.print("Please enter flight duration (in minutes) : ");
            String duration = sc.nextLine().trim();
            Integer flightDuration = Integer.parseInt(duration);
            FlightEntity flight = null;
            boolean returnFlight = false;
            try {
                flight = flightSessionBean.viewFlightDetails(flightNumber);
                if (flight.getReturnFlight() != null) {
                    System.out.println("Please enter if you would like to create a return flight schedule plan for your existing flight? (1 for yes)");
                    System.out.print("Please enter your choice: ");
                    String choiceInString = sc.nextLine().trim();
                    int choice = Integer.parseInt(choiceInString);
                    if (choice == 1) {
                        returnFlight = true;
                        System.out.print("Please enter layover duration: ");
                        layover = sc.nextInt();
                        sc.nextLine();
                    } else {
                        returnFlight = false;
                    }
                }
            } catch (FlightDoesNotExistException ex) {
                System.out.println(ex.getMessage());
                reenter = true;
            }

            if (reenter == false) {
                listOfFares = createFare(sc, flight);

                for (FareEntity fare : listOfFares) {
                    Set<ConstraintViolation<FareEntity>> violations = validator.validate(fare);
                    if (!violations.isEmpty()) {
                        for (ConstraintViolation<FareEntity> violation : violations) {
                            System.out.println(violation.getPropertyPath() + "\n " + violation.getMessage() + " " + violation.getInvalidValue());
                            reenter = true;
                        }
                    }
                    if (reenter) {
                        break;
                    }
                }
            }
            if (reenter == false) {
                try {
                    String message = flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan(flightNumber, departDateTime, endDateTime, flightDuration, returnFlight, listOfFares, layover, numFrequency);
                    System.out.println(message);
                    break;
                } catch (FlightDoesNotExistException | FlightScheduleExistException ex) {
                    System.out.println(ex.getMessage());
                }
            }

        }
    }

    public List<FareEntity> createFare(Scanner sc, FlightEntity flight) {

        List<CabinClassConfigurationEntity> listOfCabin = flight.getAircraftConfig().getCabinClasses();
        List<FareEntity> listOfFares = new ArrayList<>();
        System.out.println("You have " + listOfCabin.size() + " in you current flight");
        for (CabinClassConfigurationEntity cabin : listOfCabin) {
            System.out.printf("%25s%20s%30s", "Cabin class type", "Avaialable Seats", "Seating Configuration");
            System.out.println();
            System.out.printf("%25s%25s%20s", cabin.getCabinclassType(), cabin.getAvailableSeats(), cabin.getSeatingConfig());
            System.out.println();
        }
        System.out.println("Please enter " + listOfCabin.size() + " number of fares");
        for (int i = 0; i < listOfCabin.size(); i++) {

            boolean incorrectFare = false;

            while (!incorrectFare) {
                System.out.println("--Please enter cabin type--");
                System.out.println("1.First Class");
                System.out.println("2.Business Class");
                System.out.println("3.Premium Economy");
                System.out.println("4.Economy");
                System.out.print("Please enter your choice: ");
                int choice = sc.nextInt();
                sc.nextLine();
                if (choice == 1) {
                    System.out.print("Please enter fare amount: $");
                    BigDecimal fareAmount = sc.nextBigDecimal();
                    FareEntity newFare = new FareEntity("F", fareAmount, CabinClassType.F);
                    listOfFares.add(newFare);
                    incorrectFare = true;
                } else if (choice == 2) {
                    System.out.print("Please enter fare amount: $");
                    BigDecimal fareAmount = sc.nextBigDecimal();
                    FareEntity newFare = new FareEntity("J", fareAmount, CabinClassType.J);
                    listOfFares.add(newFare);
                    incorrectFare = true;
                } else if (choice == 3) {
                    System.out.print("Please enter fare amount: $");
                    BigDecimal fareAmount = sc.nextBigDecimal();
                    FareEntity newFare = new FareEntity("W", fareAmount, CabinClassType.W);
                    listOfFares.add(newFare);
                    incorrectFare = true;
                } else if (choice == 4) {
                    System.out.print("Please enter fare amount: $");
                    BigDecimal fareAmount = sc.nextBigDecimal();
                    FareEntity newFare = new FareEntity("Y", fareAmount, CabinClassType.Y);
                    listOfFares.add(newFare);
                    incorrectFare = true;
                }
            }
        }

        return listOfFares;

    }

    public GregorianCalendar createDateTime(String dateTime) throws IncorrectFormatException {
        String[] information = dateTime.split("/");
        List<Integer> informationInteger = new ArrayList<>();
        for (String info : information) {
            informationInteger.add(Integer.parseInt(info));
        }

        if (informationInteger.size() != 5) {
            throw new IncorrectFormatException("Wrong date format!");
        }

        //NEED VALIDATE CALENDAR INPUT 
        GregorianCalendar newCalendar = new GregorianCalendar(informationInteger.get(2), (informationInteger.get(1) - 1), informationInteger.get(0), informationInteger.get(3), informationInteger.get(4));
        return newCalendar;
    }

    public void viewAllFsp(Scanner sc) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            List<FlightSchedulePlanEntity> listOfFsp = flightSchedulePlanSessionBean.viewAllFlightSchedulePlan();
            System.out.println("=========FLIGHT SCHEDULE PLAN==========");
            for (FlightSchedulePlanEntity fsp : listOfFsp) {
                System.out.println("Flight Schedule Plan : " + fsp.getFlightSchedulePlanId());
                System.out.println("Flight Number: " + fsp.getFlightNumber());
                System.out.println();
                for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                    System.out.println("-------Flight Schedule-------");
                    System.out.println("Flight Schedule  " + fs.getFlightScheduleId());
                    GregorianCalendar departDateTime = fs.getDepartureDateTime();
                    Date date = departDateTime.getTime();
                    System.out.println("Departure Date: " + format.format(date));
                    System.out.println("Flight Duration: " + fs.getFlightDuration());
                    System.out.println("-----------END---------------");
                    System.out.println();
                }
                System.out.println("=========FLIGHT SCHEDULE PLAN==========");
            }
        } catch (FlightSchedulePlanIsEmptyException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void viewSpecificFsp(Scanner sc) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.print("Please enter the flight number: ");
        String flightNumber = sc.nextLine().trim();
        try {
            List<FlightSchedulePlanEntity> listOfFsp = flightSchedulePlanSessionBean.viewAllFlightSchedulePlan();

            for (FlightSchedulePlanEntity fsp : listOfFsp) {
                System.out.println("------------Flight Schedule Plan------------");
                System.out.println("FSP ID: " + fsp.getFlightSchedulePlanId());
                System.out.println("Flight depart from: " + fsp.getFlightEntity().getFlightRoute().getOriginLocation().getAirportName());
                System.out.println("Flight arrive at  : " + fsp.getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName());
            }

            System.out.print("Please enter ID of FSP you wish to view: ");
            String choiceStr = sc.nextLine().trim();
            Long id = Long.parseLong(choiceStr);

            FlightSchedulePlanEntity fsp = flightSchedulePlanSessionBean.viewFlightSchedulePlan(flightNumber, id);
            System.out.println("============FLIGHT SCHEDULE=============");
            System.out.println("Flight number:   " + flightNumber);
            System.out.println("Flight depart from: " + fsp.getFlightEntity().getFlightRoute().getOriginLocation().getAirportName());
            System.out.println("Flight arrive at  : " + fsp.getFlightEntity().getFlightRoute().getDestinationLocation().getAirportName());
            System.out.println();
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                System.out.println("-------Flight Schedule-------");
                System.out.println("Flight Schedule  " + fs.getFlightScheduleId());
                GregorianCalendar departDateTime = fs.getDepartureDateTime();
                Date date = departDateTime.getTime();
                System.out.println("Departure Date: " + format.format(date));
                System.out.println("Flight Duration: " + fs.getFlightDuration());
                System.out.println("-----------END---------------");
                System.out.println();
            }

            System.out.println("-----Fares for Flight Schedule Plan-----");
            for (FareEntity fare : fsp.getListOfFare()) {
                System.out.printf("%25s%20s%20s", "Fare basis code", "Fare amount", "Fare cabin type");
                System.out.println();
                System.out.printf("%10s%8.2f%8s", fare.getFareBasisCode(), fare.getFareAmount(), fare.getCabinType());
                System.out.println();
            }
            System.out.println();
            System.out.println();
        } catch (FlightSchedulePlanDoesNotExistException | FlightSchedulePlanIsEmptyException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void createFlight(Scanner sc) {
        String choice = "";
        while (true) {
            System.out.print("Please enter Flight Number: ");
            String flightNumber = sc.nextLine().trim();
            List<FlightRouteEntity> listOfRoute = flightRouteSessionBean.viewListOfFlightRoute();
            for (FlightRouteEntity fr : listOfRoute) {
                System.out.println("=======Flight Route======");
                System.out.printf("%-20s%-30s%-30s%-20s", "Flight Route ID", "Origin Location", "Destination Location", "Return Route");
                System.out.println();
                System.out.printf("%-20d%-30s%-30s%-20b", fr.getFlightRouteId(), fr.getOriginLocation().getAirportName(), fr.getDestinationLocation().getAirportName(), fr.getReturnRoute());
                System.out.println();
            }
            System.out.print("Please select flight route ID: ");
            String flightRouteIdStr = sc.nextLine().trim();
            Long flightRouteId = Long.parseLong(flightRouteIdStr);
            System.out.println();
            try {
                FlightRouteEntity flightRoute = flightRouteSessionBean.getFlightRoute(flightRouteId);
                List<AircraftConfigurationEntity> listOfAircraftConfig = aircraftSessionBeanRemote.viewAircraftConfiguration();
                for (AircraftConfigurationEntity aircraftConfig : listOfAircraftConfig) {
                    System.out.println("Aircraft Configuration Id:" + aircraftConfig.getAircraftConfigId());
                    System.out.println("Aircraft Configuration Name:" + aircraftConfig.getAircraftName());
                    System.out.println("Aircraft Type Name:" + aircraftConfig.getAircraftType().getAircraftTypeName());
                    System.out.println("Aircraft Configuration Maximum Seating Capacity:" + aircraftConfig.getMaxSeatingCapacity());
                    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                }
                System.out.println();
                System.out.print("Please select aircraft configuration ID: ");
                String airConfigStr = sc.nextLine().trim();
                Long airConfigId = Long.parseLong(airConfigStr);
                AircraftConfigurationEntity aircraftConfig = aircraftSessionBeanRemote.viewDetailAircraftConfiguration(airConfigId);

                FlightEntity flight = flightSessionBean.createFlightWithoutReturnFlight(flightNumber, flightRoute, aircraftConfig);
                System.out.println("A new flight " + flight.getFlightNumber() + " has been created!");

                if (flightRoute.getReturnRoute() != null) {
                    System.out.println("There is a return route for this flight route.");
                    System.out.println("Woud you like to create a return flight? (y/n)");
                    System.out.print("Please enter choice: ");
                    choice = sc.nextLine().trim();
                    if (choice.toLowerCase().equals("y")) {
                        System.out.println("Please enter the return flight number: ");
                        String returnFlightNumber = sc.nextLine().trim();
                        FlightRouteEntity returnRoute = flightRoute.getReturnRoute();
                        FlightEntity returnFlight = flightSessionBean.createFlightWithReturnFlight(returnFlightNumber, returnRoute, aircraftConfig, flightNumber);
                        System.out.println("A return flight " + returnFlight.getFlightNumber() + " for flight " + flight.getFlightNumber() + " has been created!");
                        break;
                    }
                } else {
                    break;
                }
                break;
            } catch (FlightRouteDoesNotExistException | AircraftConfigurationNotExistException | FlightExistsException | FlightDoesNotExistException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void updateFlight(Scanner sc) {
        int counter = 0;
        while (counter < 3) {
            try {
                System.out.print("Please enter flight number: ");
                String flightNumber = sc.nextLine().trim();
                FlightEntity flight = flightSessionBean.viewFlightDetails(flightNumber);
                System.out.println();
                System.out.println("What woud you like to update? ");
                System.out.println("1. Flight Route");
                System.out.println("2. Aircraft Configuration");
                System.out.println("3. Back");
                System.out.print("Please enter choice: ");
                String choiceStr = sc.nextLine().trim();
                int choice = Integer.parseInt(choiceStr);
                System.out.println();
                if (choice == 1) {
                    counter = 0;
                    List<FlightRouteEntity> listOfRoute = flightRouteSessionBean.viewListOfFlightRoute();
                    for (FlightRouteEntity fr : listOfRoute) {
                        System.out.println("=======Flight Route======");
                        System.out.printf("%-20s%-30s%-30s%-20s", "Flight Route ID", "Origin Location", "Destination Location", "Return Route");
                        System.out.println();
                        System.out.printf("%-20d%-30s%-30s%-20b", fr.getFlightRouteId(), fr.getOriginLocation().getAirportName(), fr.getDestinationLocation().getAirportName(), fr.getReturnRoute());
                        System.out.println();
                    }

                    System.out.print("Please enter ID of flight route you wish to choose: ");
                    String frId = sc.nextLine().trim();
                    Long flightRouteId = Long.parseLong(frId);
                    if (flight.getReturnFlight() != null && flightRouteId != flight.getFlightRoute().getFlightRouteId() && flightRouteId != flight.getReturnFlight().getFlightRoute().getFlightRouteId()) {
                        System.out.println("Return flight detected. Would you like to change the flight route for the return flight as well? ");
                        System.out.print("Please enter choice (y/n) : ");
                        String decision = sc.nextLine().trim();
                        if (decision.toLowerCase().equals("y")) {

                            FlightEntity returnFlight = flight.getReturnFlight();
                            FlightRouteEntity flightRoute = flightRouteSessionBean.getFlightRoute(flightRouteId);
                            flight.setFlightRoute(flightRoute);
                            flightSessionBean.updateFlight(flight);

                            FlightRouteEntity returnFlightRoute = flightRoute.getReturnRoute();
                            returnFlight.setFlightRoute(returnFlightRoute);
                            flightSessionBean.updateFlight(returnFlight);
                            System.out.println("Flight routes have been updated for flights " + flight.getFlightNumber() + " and " + returnFlight.getFlightNumber());
                            System.out.println();
                            break;
                        } else {
                            System.out.println("Unable to update flight! Flight is paired to return flight!");
                            System.out.println();
                        }

                    } else if (flightRouteId != flight.getFlightRoute().getFlightRouteId()) {
                        FlightRouteEntity flightRoute = flightRouteSessionBean.getFlightRoute(flightRouteId);
                        flight.setFlightRoute(flightRoute);
                        flightSessionBean.updateFlight(flight);
                        System.out.println("Flight routes have been updated for flight " + flight.getFlightNumber());
                        System.out.println();
                        break;
                    } else {
                        System.out.println("You cannot pick the same flight route! Please select a different flight route!");
                        System.out.println();
                    }

                } else if (choice == 2) {
                    counter = 0;
                    List<AircraftConfigurationEntity> listOfAircraftConfig = aircraftSessionBeanRemote.viewAircraftConfiguration();
                    for (AircraftConfigurationEntity aircraftConfig : listOfAircraftConfig) {
                        System.out.println("Aircraft Configuration Id:" + aircraftConfig.getAircraftConfigId());
                        System.out.println("Aircraft Configuration Name:" + aircraftConfig.getAircraftName());
                        System.out.println("Aircraft Type Name:" + aircraftConfig.getAircraftType().getAircraftTypeName());
                        System.out.println("Aircraft Configuration Maximum Seating Capacity:" + aircraftConfig.getMaxSeatingCapacity());
                        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                    }
                    System.out.println();
                    System.out.print("Please select aircraft configuration ID: ");
                    String airConfigStr = sc.nextLine().trim();
                    Long airConfigId = Long.parseLong(airConfigStr);
                    AircraftConfigurationEntity aircraftConfig = aircraftSessionBeanRemote.viewDetailAircraftConfiguration(airConfigId);
                    if (airConfigId != aircraftConfig.getAircraftConfigId()) {
                        flight.setAircraftConfig(aircraftConfig);
                        flightSessionBean.updateFlight(flight);
                        System.out.println("Aircraft configuration has been changed for flight " + flight.getFlightNumber());
                        break;
                    } else {
                        System.out.println("You cannot pick the same aircraft configuration! Please select a different aircraft configuration!");
                        System.out.println();
                    }
                } else if (choice == 3) {
                    counter = 4;
                } else {
                    System.out.println("Please enter a valid choice!");
                    counter++;
                }
            } catch (FlightDoesNotExistException | FlightRouteDoesNotExistException | AircraftConfigurationNotExistException ex) {
                System.out.println(ex.getMessage());
                counter++;
            }

        }
    }

    public void viewAllFlights(Scanner sc) {
        int counter = 0;
        while (counter < 3) {

        }
    }

}
