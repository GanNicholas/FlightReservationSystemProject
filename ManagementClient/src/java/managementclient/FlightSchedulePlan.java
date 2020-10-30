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
import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
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
import util.exception.FlightDoesNotExistException;
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

    public FlightSchedulePlan(FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean, FlightSessionBeanRemote flightSessionBean) {
        this.flightSchedulePlanSessionBean = flightSchedulePlanSessionBean;
        this.flightSessionBean = flightSessionBean;
    }

    public void runFSP() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("**Welcome to Flight Schedule Plan Management **");
            System.out.println("What would you like to do? ");
            System.out.println("1. Create Flight Schedule Plan.");
            System.out.println("2. View all flight schedule plan");
            System.out.println("3. View detail of a flight schedule plan.");
            System.out.println("4. Update flight schedule plan.");
            System.out.println("5. Delete flight schedule plan");
            System.out.println("0. Exit");
            System.out.print("Please enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 1) {
                createfsp(sc, flightSchedulePlanSessionBean);
            } else if (choice == 2) {
                viewAllFsp(sc);
            } else if (choice == 3) {
                viewSpecificFsp(sc);
            } else if (choice == 4) {

            } else if (choice == 5) {

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
            System.out.print("Please enter fare basis code: ");
            String fareBasis = sc.nextLine().trim();
            System.out.print("Please enter fare amount: $");
            BigDecimal fareAmount = sc.nextBigDecimal();
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
                    FareEntity newFare = new FareEntity(fareBasis, fareAmount, CabinClassType.F);
                    listOfFares.add(newFare);
                    incorrectFare = true;
                } else if (choice == 2) {
                    FareEntity newFare = new FareEntity(fareBasis, fareAmount, CabinClassType.J);
                    listOfFares.add(newFare);
                    incorrectFare = true;
                } else if (choice == 3) {
                    FareEntity newFare = new FareEntity(fareBasis, fareAmount, CabinClassType.W);
                    listOfFares.add(newFare);
                    incorrectFare = true;
                } else if (choice == 4) {
                    FareEntity newFare = new FareEntity(fareBasis, fareAmount, CabinClassType.Y);
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
                System.out.println("-----List of Flight Schedule-----");
                for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                    System.out.println("-----------------------------");
                    System.out.println("Flight Schedule  " + fs.getFlightScheduleId());
                    GregorianCalendar departDateTime = fs.getDepartureDateTime();
                    Date date = departDateTime.getTime();
                    System.out.println("Departure Date: " + format.format(date));
                    System.out.println("Flight Duration: " + fs.getFlightDuration());
                    System.out.println("-----------END---------------");
                    System.out.println();
                }
                System.out.println();
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
            System.out.println("-----List of Flight Schedule-----");
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                System.out.println("-----------------------------");
                System.out.println("Flight Schedule  " + fs.getFlightScheduleId());
                String departureDate = format.format(fs.getDepartureDateTime().getTime());
                System.out.println("Departure Date:  " + departureDate);
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
            System.out.println();
        } catch (FlightSchedulePlanDoesNotExistException | FlightSchedulePlanIsEmptyException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
