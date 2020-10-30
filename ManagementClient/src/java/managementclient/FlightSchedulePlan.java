/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.FareEntity;
import entity.FlightEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.CabinClassType;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightScheduleExistException;

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
            System.out.println("5. Delte flight schedule plan");
            System.out.println("0. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                createfsp(sc, flightSchedulePlanSessionBean);
            } else if (choice == 2) {
                viewAllFsp(sc,flightSchedulePlanSessionBean );
            } else if (choice == 3) {
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
            System.out.print("Please enter your choice: ");
            String choiceStr = sc.nextLine().trim();
            int choice = Integer.parseInt(choiceStr);

            if (choice == 1) {
                singleMultipleFlightSchedule(sc);
                counter = 0;
            } else if (choice == 2) {
               recurrentFlightSchedule(sc);
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
                GregorianCalendar departDateTime = createDateTime(dateTime);
                listOfDepartDateTime.add(departDateTime);
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

            // run a for loop to take in a list of date and time
            System.out.print("Please enter departure date and time (Please enter in this format (dd/mm/yyyy/hh/mm) : ");
            String dateTime = sc.nextLine().trim();
            GregorianCalendar departDateTime = createDateTime(dateTime);
            
            //POTENTIAL ERROR
            System.out.println("Please enter end date (dd/mm/yyyy/hh/mm) : ");
            String endDateTimeStr = sc.nextLine().trim();
            GregorianCalendar endDateTime = createDateTime(endDateTimeStr);
            

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
                    String message = flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan(flightNumber,departDateTime , endDateTime ,flightDuration, returnFlight, listOfFares, layover, numFrequency);
                    System.out.println(message);
                    break;
                } catch (FlightDoesNotExistException | FlightScheduleExistException ex) {
                    System.out.println(ex.getMessage());
                }
            }

        }
    }

    public List<FareEntity> createFare(Scanner sc, FlightEntity flight) {
        int numCabins = flight.getAircraftConfig().getCabinClasses().size();
        List<FareEntity> listOfFares = new ArrayList<>();
        System.out.println("You have " + numCabins + " in you current flight");
        System.out.println("Please enter " + numCabins + " number of fares");
        for (int i = 0; i < numCabins; i++) {
            System.out.print("Please enter fare basis code: ");
            String fareBasis = sc.nextLine().trim();
            System.out.print("Please enter fare amount: ");
            BigDecimal fareAmount = sc.nextBigDecimal();
            boolean incorrectFare = false;

            while (incorrectFare) {
                System.out.println("--Please enter cabin type--");
                System.out.println("1.First Class");
                System.out.println("2.Busihness Class");
                System.out.println("3.Premium Economy");
                System.out.println("4.Economy");
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

    public GregorianCalendar createDateTime(String dateTime) {
        String[] information = dateTime.split("/");
        List<Integer> informationInteger = new ArrayList<>();
        for (String info : information) {
            informationInteger.add(Integer.parseInt(info));
        }

        //NEED VALIDATE CALENDAR INPUT 
        GregorianCalendar newCalendar = new GregorianCalendar(informationInteger.get(2), informationInteger.get(1), informationInteger.get(0), informationInteger.get(3), informationInteger.get(4));
        return newCalendar;
    }
    
    
    public void viewAllFsp(Scanner sc, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote){
        
    }

}
