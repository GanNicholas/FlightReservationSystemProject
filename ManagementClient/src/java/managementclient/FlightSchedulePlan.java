/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.AircraftSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.AircraftConfigurationEntity;
import entity.CabinClassConfigurationEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.MultipleFlightScheduleEntity;
import entity.RecurringScheduleEntity;
import entity.RecurringWeeklyScheduleEntity;
import entity.SingleFlightScheduleEntity;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.CabinClassType;
import util.exception.AircraftConfigurationNotExistException;
import util.exception.FareCannotBeDeletedException;
import util.exception.FareDoesNotExistException;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightExistsException;
import util.exception.FlightHasFlightSchedulePlanException;
import util.exception.FlightIsDeletedException;
import util.exception.FlightRecordIsEmptyException;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightRouteIsNotMainRouteException;
import util.exception.FlightScheduleDoesNotExistException;
import util.exception.FlightScheduleExistException;
import util.exception.FlightSchedulePlanDoesNotExistException;
import util.exception.FlightSchedulePlanEndDateIsBeforeStartDateException;
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

    private FlightScheduleSessionBeanRemote flightScheduleSessionBean;

    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public FlightSchedulePlan(FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean, FlightSessionBeanRemote flightSessionBean, AircraftSessionBeanRemote aircraftSessionBeanRemote, FlightRouteSessionBeanRemote flightRouteSessionBean, FlightScheduleSessionBeanRemote flightScheduleSessionBean) {
        this.flightSchedulePlanSessionBean = flightSchedulePlanSessionBean;
        this.flightSessionBean = flightSessionBean;
        this.aircraftSessionBeanRemote = aircraftSessionBeanRemote;
        this.flightRouteSessionBean = flightRouteSessionBean;
        this.flightScheduleSessionBean = flightScheduleSessionBean;
    }

    public void runFSP() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("**Welcome to Flight Schedule Plan Management **");
                System.out.println("What would you like to do? ");
                System.out.println("1.  Create Flight.");
                System.out.println("2.  Update Flight.");
                System.out.println("3.  View All Flights.");
                System.out.println("4.  View Flight Details.");
                System.out.println("5.  Delete Flight.");
                System.out.println("6.  Create Flight Schedule Plan.");
                System.out.println("7.  View all flight schedule plan");
                System.out.println("8.  View detail of a flight schedule plan.");
                System.out.println("9.  Update flight schedule plan.");
                System.out.println("10. Delete flight schedule plan");
                System.out.println("0.  Exit");
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
                    viewSpecificFlight(sc);
                } else if (choice == 5) {
                    deleteFlight(sc);
                } else if (choice == 6) {
                    createfsp(sc);
                } else if (choice == 7) {
                    viewAllFsp();
                } else if (choice == 8) {
                    viewSpecificFsp(sc);
                } else if (choice == 9) {
                    updateFsp(sc);
                } else if (choice == 10) {
                    deleteFsp(sc);
                } else if (choice == 0) {
                    System.out.println("Goodbye!");
                    break;
                }
            } catch (InputMismatchException ex) {
                System.out.println(ex.getMessage());
                sc.next();;
            }
        }

    }

    public void createfsp(Scanner sc) {
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
                counter = 0;
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
            try {
                reenter = false;
                listOfDepartDateTime.clear();
                listOfFares.clear();
                layover = 0;
                System.out.print("Please enter flight number: ");
                String flightNumber = sc.nextLine().trim();
                System.out.print("Please enter number of flight schedule you wish to create: ");
                int numSchedule = sc.nextInt();
                sc.nextLine();

                // run a for loop to take in a list of date and time
                for (int i = 0; i < numSchedule; i++) {
                    System.out.print("Please enter departure date and time (Please enter in this format: dd/mm/yyyy/hh/mm) ");
                    String dateTime = sc.nextLine().trim();
                    try {
                        GregorianCalendar departDateTime = createDateTime(dateTime);
                        Date date = departDateTime.getTime();
//                        System.out.println(format.format(date));

                        listOfDepartDateTime.add(departDateTime);
                    } catch (IncorrectFormatException ex) {
                        System.out.println(ex.getMessage());
                        i--;
                    }

                }

                System.out.print("Please enter flight duration (in minutes) : ");
                String duration = sc.nextLine().trim();
                Integer flightDuration = Integer.parseInt(duration);
                FlightEntity flight = null;
                boolean returnFlight = false;
                try {
                    flight = flightSessionBean.viewFlightDetails(flightNumber);
                    if (flight.getReturnFlight() != null && flight.isIsDeleted() == false && flight.isIsMainRoute()) {
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
                    } else {
                        reenter = false;
                    }
                } catch (FlightDoesNotExistException | InputMismatchException ex) {
                    System.out.println(ex.getMessage());
                    sc.next();;
                    reenter = true;
                }

                if (reenter == false) {
                    listOfFares = createFare(sc, flight);

                    for (FareEntity fare : listOfFares) {
                        Set<ConstraintViolation<FareEntity>> violations = validator.validate(fare);
                        if (!violations.isEmpty()) {
                            for (ConstraintViolation constraintViolation : violations) {
                                System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
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
                        System.out.println();
                        break;
                    } catch (FlightDoesNotExistException | FlightScheduleExistException ex) {
                        System.out.println(ex.getMessage());
                        break;
                    }
                }
            } catch (InputMismatchException ex) {
                System.out.println(ex.getMessage());
                sc.next();;
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
            layover = 0;
            int day = 0;
            System.out.print("Please enter flight number: ");
            String flightNumber = sc.nextLine().trim();
            System.out.print("Please enter frequency of flight schedule you wish to create: ");
            String numFrequencyString = sc.nextLine().trim();
            int numFrequency = Integer.parseInt(numFrequencyString);

            if (numFrequency == 7) {
                System.out.print("Please enter day of week for schedule to start: ");
                String dayOfWeek = sc.nextLine().trim();

                if (dayOfWeek.toLowerCase().equals("monday")) {
                    day = 1;
                } else if (dayOfWeek.toLowerCase().equals("tuesday")) {
                    day = 2;
                } else if (dayOfWeek.toLowerCase().equals("wednesday")) {
                    day = 3;
                } else if (dayOfWeek.toLowerCase().equals("thursday")) {
                    day = 4;
                } else if (dayOfWeek.toLowerCase().equals("friday")) {
                    day = 5;
                } else if (dayOfWeek.toLowerCase().equals("saturday")) {
                    day = 6;
                } else if (dayOfWeek.toLowerCase().equals("sunday")) {
                    day = 7;
                }
            }

            System.out.print("Please enter departure date and time (Please enter in this format (dd/mm/yyyy/hh/mm) : ");
            String dateTime = sc.nextLine().trim();
            GregorianCalendar departDateTime = null;
            try {
                departDateTime = createDateTime(dateTime);

                if (numFrequency == 7) {
                    int dayOfWeek = departDateTime.get(GregorianCalendar.DAY_OF_WEEK);
                    dayOfWeek--;
//                System.out.println("Day of week: " + dayOfWeek);
                    int diff = 0;
                    if (day < dayOfWeek) {
                        diff = 7 - dayOfWeek;
                        diff += day;
                    } else if (day > dayOfWeek) {
                        diff = day - dayOfWeek;
                    }

                    departDateTime.add(GregorianCalendar.DATE, diff);
                }
//                System.out.println(format.format(departDateTime.getTime()));
            } catch (IncorrectFormatException ex) {
                System.out.println(ex.getMessage());
                reenter = true;
            }
            //maybe can add if statement for reenter
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
                if (flight.getReturnFlight() != null && flight.isIsMainRoute() && !flight.isIsDeleted()) {
                    System.out.println("Please enter if you would like to create a return flight schedule plan for your existing flight? (1 for yes)");
                    System.out.print("Please enter your choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();
                    if (choice == 1) {
                        returnFlight = true;
                        System.out.print("Please enter layover duration: ");
                        layover = sc.nextInt();
                        sc.nextLine();
                    } else {
                        returnFlight = false;
                    }
                }
            } catch (FlightDoesNotExistException | InputMismatchException ex) {
                System.out.println(ex.getMessage());
                sc.next();;
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
                    System.out.println();
                    break;
                } catch (FlightDoesNotExistException | FlightScheduleExistException | FlightSchedulePlanEndDateIsBeforeStartDateException ex) {
                    System.out.println(ex.getMessage());
                    break;
                }
            }

        }
    }

    public List<FareEntity> createFare(Scanner sc, FlightEntity flight) {
        boolean firstClass = false;
        boolean businessClass = false;
        boolean premiumEconomyClass = false;
        boolean economyClass = false;

        List<CabinClassType> listOfCabinClassTypes = new ArrayList<>();

        while (true) {
            try {
                listOfCabinClassTypes.clear();
                firstClass = false;
                businessClass = false;
                premiumEconomyClass = false;
                economyClass = false;
                List<CabinClassConfigurationEntity> listOfCabin = flight.getAircraftConfig().getCabinClasses();
                List<FareEntity> listOfFares = new ArrayList<>();
                System.out.println("You have " + listOfCabin.size() + " cabin configurations in you current flight");
                System.out.println();
                System.out.println("==============================LIST OF CABIN TYPES FOR FLIGHT========================================");
                for (CabinClassConfigurationEntity cabin : listOfCabin) {
                    System.out.printf("%-20s%-25s%-30s%-30s", "ID Number", "Cabin class type", "Avaialable Seats", "Seating Configuration");
                    System.out.println();
                    System.out.printf("%-20d%-25s%-30s%-30s", cabin.getCabinClassConfigId(), cabin.getCabinclassType(), cabin.getAvailableSeats(), cabin.getSeatingConfig());
                    System.out.println();
                    listOfCabinClassTypes.add(cabin.getCabinclassType());
                }
                System.out.println();
                for (CabinClassType cabinType : listOfCabinClassTypes) {
                    boolean stillHaveFare = true;

                    while (stillHaveFare) {

                        if (cabinType.equals(CabinClassType.F) && !firstClass) {
                            System.out.print("Please enter your fare basis code for First Class Cabin : ");
                            String fbc = "F" + sc.nextLine().trim();
                            System.out.print("Please enter fare amount: $");
                            BigDecimal fareAmount = sc.nextBigDecimal();
                            sc.nextLine();
                            FareEntity newFare = new FareEntity(fbc, fareAmount, CabinClassType.F);
                            listOfFares.add(newFare);
                            System.out.print("Do you still wish to add fares for First Class Cabin (y/n) : ");
                            String choice = sc.nextLine().trim();
                            if (choice.toLowerCase().equals("n")) {
                                stillHaveFare = false;
                                firstClass = true;
                            }
                        } else if (cabinType.equals(CabinClassType.J) && !businessClass) {
                            System.out.print("Please enter your fare basis code for Business Class Cabin : ");
                            String fbc = "J" + sc.nextLine().trim();
                            System.out.print("Please enter fare amount: $");
                            BigDecimal fareAmount = sc.nextBigDecimal();
                            sc.nextLine();
                            FareEntity newFare = new FareEntity(fbc, fareAmount, CabinClassType.J);
                            listOfFares.add(newFare);
                            System.out.print("Do you still wish to add fares for Business Class Cabin (y/n) : ");
                            String choice = sc.nextLine().trim();
                            if (choice.toLowerCase().equals("n")) {
                                stillHaveFare = false;
                                businessClass = true;
                            }
                        } else if (cabinType.equals(CabinClassType.W) && !premiumEconomyClass) {
                            System.out.print("Please enter your fare basis code for Premium Economy Class : ");
                            String fbc = "W" + sc.nextLine().trim();
                            System.out.print("Please enter fare amount: $");
                            BigDecimal fareAmount = sc.nextBigDecimal();
                            sc.nextLine();
                            FareEntity newFare = new FareEntity(fbc, fareAmount, CabinClassType.W);
                            listOfFares.add(newFare);
                            System.out.print("Do you still wish to add fares for Premium Economy Class Cabin (y/n) : ");
                            String choice = sc.nextLine().trim();
                            if (choice.toLowerCase().equals("n")) {
                                stillHaveFare = false;
                                premiumEconomyClass = true;
                            }
                        } else if (cabinType.equals(CabinClassType.Y) && !economyClass) {
                            System.out.print("Please enter your fare basis code for Economy Class : ");
                            String fbc = "Y" + sc.nextLine().trim();
                            System.out.print("Please enter fare amount: $");
                            BigDecimal fareAmount = sc.nextBigDecimal();
                            sc.nextLine();
                            FareEntity newFare = new FareEntity(fbc, fareAmount, CabinClassType.Y);
                            listOfFares.add(newFare);
                            System.out.print("Do you still wish to add fares for Economy Class Cabin (y/n) : ");
                            String choice = sc.nextLine().trim();
                            if (choice.toLowerCase().equals("n")) {
                                stillHaveFare = false;
                                economyClass = true;
                            }
                        } else {
                            break;
                        }
                    }

                }

                return listOfFares;
            } catch (InputMismatchException ex) {
                System.out.println(ex.getMessage());
                sc.next();;
            }
        }

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

    public void viewAllFsp() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            List<FlightSchedulePlanEntity> listOfFsp = flightSchedulePlanSessionBean.viewAllFlightSchedulePlan();
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
//                
            }

            System.out.println();
        } catch (FlightSchedulePlanIsEmptyException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void viewSpecificFsp(Scanner sc) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            List<FlightSchedulePlanEntity> listOfFsp = flightSchedulePlanSessionBean.viewAllFlightSchedulePlan();
            System.out.println("=========FLIGHT SCHEDULE PLAN==========");
            System.out.printf("%-10s%-20s%-70s%-70s%-30s", "FSP ID", "Flight Number", "Origin Airport", "Destination Airport", "Flight Schedule Plan Type");
            System.out.println();
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

            System.out.print("Please enter ID of FSP you wish to view (eg 1): ");
            Long id = sc.nextLong();
            sc.nextLine();

            FlightSchedulePlanEntity fsp = flightSchedulePlanSessionBean.viewFlightSchedulePlan(id);

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
            System.out.printf("%-30s%-30s%-30s", "Departure Date/Time", "Flight Duration", "Arrival Date/Time");
            System.out.println();
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                int flightMins = fs.getFlightDuration();
                int flightHour = flightMins / 60;
                flightMins %= 60;
                String flightduration = flightHour + "hr " + flightMins + " mins";
                System.out.printf("%-30s%-30s%-30s", format.format(fs.getDepartureDateTime().getTime()), flightduration, format.format(fs.getArrivalDateTime().getTime()));
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
            System.out.println();
            System.out.println();
        } catch (FlightSchedulePlanDoesNotExistException | FlightSchedulePlanIsEmptyException | InputMismatchException ex) {
            System.out.println(ex.getMessage());
            sc.next();
        }
    }

    public void viewSpecificFspForAllFlight(Scanner sc) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            List<FlightSchedulePlanEntity> listOfFsp = flightSchedulePlanSessionBean.viewAllFlightSchedulePlanForAllFlights();
            System.out.println("=========FLIGHT SCHEDULE PLAN==========");
            System.out.printf("%-10s%-20s%-70s%-70s%-30s", "FSP ID", "Flight Number", "Origin Airport", "Destination Airport", "Flight Schedule Plan Type");
            System.out.println();
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

            System.out.print("Please enter ID of FSP you wish to view (eg 1): ");
            Long id = sc.nextLong();
            sc.nextLine();

            FlightSchedulePlanEntity fsp = flightSchedulePlanSessionBean.viewFlightSchedulePlan(id);

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
            System.out.printf("%-30s%-30s%-30s", "Departure Date/Time", "Flight Duration", "Arrival Date/Time");
            System.out.println();
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                int flightMins = fs.getFlightDuration();
                int flightHour = flightMins / 60;
                flightMins %= 60;
                String flightduration = flightHour + "hr " + flightMins + " mins";
                System.out.printf("%-30s%-30s%-30s", format.format(fs.getDepartureDateTime().getTime()), flightduration, format.format(fs.getArrivalDateTime().getTime()));
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
            System.out.println();
            System.out.println();
        } catch (FlightSchedulePlanDoesNotExistException | FlightSchedulePlanIsEmptyException | InputMismatchException ex) {
            System.out.println(ex.getMessage());
            sc.next();
        }
    }

    public void createFlight(Scanner sc) {
        String choice = "";
        while (true) {
            System.out.print("Please enter Flight Number: ");
            String returnRouteStr = "";
            String flightNumber = sc.nextLine().trim();
            List<FlightRouteEntity> listOfRoute = flightRouteSessionBean.viewListOfAllFlightRoute();
            for (FlightRouteEntity fr : listOfRoute) {
                if (!fr.isIsDeleted()) {
                    System.out.println("=======Flight Route======");
                    System.out.printf("%-20s%-45s%-45s%-30s", "Flight Route ID", "Origin Location", "Destination Location", "Return Route");
                    System.out.println();
                    if (fr.isMainRoute() && fr.getReturnRoute() != null) {
                        returnRouteStr = "Return route exists";
                    } else {
                        returnRouteStr = "Return route does not exists";
                    }
                    System.out.printf("%-20d%-45s%-45s%-30s", fr.getFlightRouteId(), fr.getOriginLocation().getIataAirportCode(), fr.getDestinationLocation().getIataAirportCode(), returnRouteStr);
                    System.out.println();
                }
            }
            System.out.print("Please select flight route ID: ");
            Long flightRouteId = sc.nextLong();
            sc.nextLine();
            System.out.println();
            try {
                FlightRouteEntity flightRoute = flightRouteSessionBean.getFlightRoute(flightRouteId);
                List<AircraftConfigurationEntity> listOfAircraftConfig = aircraftSessionBeanRemote.viewAircraftConfiguration();
                System.out.printf("%-35s %-35s %-35s %-51s", "Aircraft Configuration Id", "Aircraft Configuration Name", "Aircraft Type Name", "Aircraft Configuration Maximum Seating Capacity");
                System.out.println();
                for (AircraftConfigurationEntity aircraftConfig : listOfAircraftConfig) {

                    System.out.printf("%-35s %-35s %-35s %-51s", aircraftConfig.getAircraftConfigId(), aircraftConfig.getAircraftName(), aircraftConfig.getAircraftType().getAircraftTypeName(), aircraftConfig.getMaxSeatingCapacity());
                    System.out.println();
                }
                System.out.println();
                System.out.print("Please select aircraft configuration ID: ");
                Long airConfigId = sc.nextLong();
                sc.nextLine();
                AircraftConfigurationEntity aircraftConfig = aircraftSessionBeanRemote.viewDetailAircraftConfiguration(airConfigId);

                FlightEntity flight = flightSessionBean.createFlightWithoutReturnFlight(flightNumber, flightRoute, aircraftConfig);
                System.out.println("A new flight " + flight.getFlightNumber() + " has been created!");
                System.out.println();

                if (flightRoute.getReturnRoute() != null && flightRoute.isMainRoute()) {
                    System.out.println("There is a return route for this flight route.");
                    System.out.println("Woud you like to create a return flight? (y/n)");
                    System.out.print("Please enter choice: ");
                    choice = sc.nextLine().trim();
                    if (choice.toLowerCase().equals("y")) {
                        System.out.println("Please enter the return flight number: ");
                        String returnFlightNumber = sc.nextLine().trim();
                        System.out.println();
                        FlightRouteEntity returnRoute = flightRoute.getReturnRoute();
                        FlightEntity returnFlight = flightSessionBean.createFlightWithReturnFlight(returnFlightNumber, returnRoute, aircraftConfig, flightNumber);
                        System.out.println("A return flight " + returnFlight.getFlightNumber() + " for flight " + flight.getFlightNumber() + " has been created!");
                        break;
                    }
                } else {
                    break;
                }
                break;
            } catch (FlightRouteDoesNotExistException | AircraftConfigurationNotExistException | FlightExistsException | FlightDoesNotExistException | InputMismatchException ex) {
                System.out.println(ex.getMessage());
                sc.next();
                break;
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
                int choice = sc.nextInt();
                sc.nextLine();
                System.out.println();
                if (choice == 1) {
                    counter = 0;
                    List<FlightRouteEntity> listOfRoute = flightRouteSessionBean.viewListOfAllFlightRoute();
                    for (FlightRouteEntity fr : listOfRoute) {
                        if (!fr.isIsDeleted()) {
                            System.out.println("=======Flight Route======");
                            System.out.printf("%-20s%-45s%-45s%-30s", "Flight Route ID", "Origin Location", "Destination Location", "Return Route");
                            System.out.println();
                            System.out.printf("%-20d%-45s%-45s%-30b", fr.getFlightRouteId(), fr.getOriginLocation().getAirportName(), fr.getDestinationLocation().getAirportName(), fr.getReturnRoute());
                            System.out.println();
                        }
                    }

                    System.out.print("Please enter ID of flight route you wish to choose (press 0 to return) : ");
                    Long flightRouteId = sc.nextLong();
                    sc.nextLine();
                    if (flightRouteId == 0) {
                        break;
                    }
                    FlightRouteEntity flightRoute = flightRouteSessionBean.getFlightRoute(flightRouteId);
                    if (flightRoute.isIsDeleted()) {
                        System.out.println("Unable to update flight! Flight route chosen is deleted!");
                        System.out.println();
                    } else {
                        if (flight.getReturnFlight() != null && !Objects.equals(flightRouteId, flight.getFlightRoute().getFlightRouteId()) && !Objects.equals(flightRouteId, flight.getReturnFlight().getFlightRoute().getFlightRouteId())) {
                            System.out.println("Return flight detected. Would you like to change the flight route for the return flight as well? ");
                            System.out.print("Please enter choice (y/n) : ");
                            String decision = sc.nextLine().trim();

                            if (decision.toLowerCase().equals("y")) {
                                FlightEntity returnFlight = flight.getReturnFlight();
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

                        } else if (!Objects.equals(flightRouteId, flight.getFlightRoute().getFlightRouteId())) {
                            flight.setFlightRoute(flightRoute);
                            flightSessionBean.updateFlight(flight);
                            System.out.println("Flight routes have been updated for flight " + flight.getFlightNumber());
                            System.out.println();
                            break;
                        } else {
//                            if (!flight.isIsMainRoute()) {
//                                System.out.println("Flight is not the main flight! Please select the main flight to update!");
//                                System.out.println();
//                            } else if (flight.isIsDeleted()) {
//                                System.out.println("Flight is no longer active! Please select another flight!");
//                            }
                            System.out.println("You cannot pick the same flight route! Please select a different flight route!");
                            System.out.println();
                        }
                    }
                } else if (choice == 2) {
                    counter = 0;
                    List<AircraftConfigurationEntity> listOfAircraftConfig = aircraftSessionBeanRemote.viewAircraftConfiguration();
                    System.out.printf("%-35s %-35s %-35s %-51s", "Aircraft Configuration Id", "Aircraft Configuration Name", "Aircraft Type Name", "Aircraft Configuration Maximum Seating Capacity");
                    System.out.println();
                    for (AircraftConfigurationEntity aircraftConfig : listOfAircraftConfig) {

                        System.out.printf("%-35s %-35s %-35s %-51s", aircraftConfig.getAircraftConfigId(), aircraftConfig.getAircraftName(), aircraftConfig.getAircraftType().getAircraftTypeName(), aircraftConfig.getMaxSeatingCapacity());
                        System.out.println();
                    }
                    System.out.println();
                    System.out.print("Please select aircraft configuration ID (press 0 to return) : ");
                    Long airConfigId = sc.nextLong();
                    sc.nextLine();

                    if (airConfigId == 0) {
                        break;
                    }
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
            } catch (FlightDoesNotExistException | FlightRouteDoesNotExistException | AircraftConfigurationNotExistException | InputMismatchException | FlightHasFlightSchedulePlanException ex) {
                System.out.println(ex.getMessage());
                if (ex instanceof InputMismatchException) {
                    sc.next();
                }
                System.out.println();
                counter++;
            }

        }
    }

    public void viewAllFlights(Scanner sc) {
        try {
            List<FlightEntity> listOfFlights = flightSessionBean.viewAllFlights();
            System.out.printf("%-20s%-70s%-70s%-30s", "Flight Number", "Origin", "Destination", "Return Flight Number");
            System.out.println();
            for (FlightEntity flight : listOfFlights) {
                if (flight.getReturnFlight() != null) {
                    String originLocation = flight.getFlightRoute().getOriginLocation().getAirportName() + " in " + flight.getFlightRoute().getOriginLocation().getCountry() + ", " + flight.getFlightRoute().getOriginLocation().getCity();
                    String destinationLocation = flight.getFlightRoute().getDestinationLocation().getAirportName() + " in " + flight.getFlightRoute().getDestinationLocation().getCountry() + ", " + flight.getFlightRoute().getDestinationLocation().getCity();

                    System.out.printf("%-20s%-70s%-70s%-30s", flight.getFlightNumber(), originLocation, destinationLocation, flight.getReturnFlight().getFlightNumber());
                    System.out.println();
                } else {
                    String originLocation = flight.getFlightRoute().getOriginLocation().getAirportName() + " in " + flight.getFlightRoute().getOriginLocation().getCountry() + ", " + flight.getFlightRoute().getOriginLocation().getCity();
                    String destinationLocation = flight.getFlightRoute().getDestinationLocation().getAirportName() + " in " + flight.getFlightRoute().getDestinationLocation().getCountry() + ", " + flight.getFlightRoute().getDestinationLocation().getCity();
                    System.out.printf("%-20s%-70s%-70s%-30s", flight.getFlightNumber(), originLocation, destinationLocation, "No return flight");
                    System.out.println();
                }
            }
            System.out.println();
        } catch (FlightRecordIsEmptyException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void viewSpecificFlight(Scanner sc) {
        String returnFlight = "";

        System.out.print("Please enter the flight number : ");
        String flightNumber = sc.nextLine().trim();
        try {
            FlightEntity flight = flightSessionBean.viewFlightDetails(flightNumber);
            System.out.println("Flight number: " + flight.getFlightNumber());
            System.out.println();
            FlightRouteEntity fr = flight.getFlightRoute();
            String originLocation = flight.getFlightRoute().getOriginLocation().getAirportName() + " in " + flight.getFlightRoute().getOriginLocation().getCountry() + ", " + flight.getFlightRoute().getOriginLocation().getCity();
            String destinationLocation = flight.getFlightRoute().getDestinationLocation().getAirportName() + " in " + flight.getFlightRoute().getDestinationLocation().getCountry() + ", " + flight.getFlightRoute().getDestinationLocation().getCity();
            System.out.printf("%-20s%-70s%-70s%-30s", "Flight Route ID", "Origin Location", "Destination Location", "Return Flight");
            System.out.println();
            if (flight.getReturnFlight() != null) {
                returnFlight = "Return flight exists";
            } else {
                returnFlight = "No return flight exists";
            }
            System.out.printf("%-20d%-70s%-70s%-30s", fr.getFlightRouteId(), originLocation, destinationLocation, returnFlight);
            System.out.println();
            System.out.println();
            List<CabinClassConfigurationEntity> listOfCabinClass = flight.getAircraftConfig().getCabinClasses();
//            List<SeatEntity> listOfSeats = flight.getAircraftConfig().getSeatingPlan();
            System.out.printf("%-30s%-30s%-30s%-40s%-39s%-40s", "Cabin Class", "Number of Aisles", "Number of Rows", "Seating Configuration", "Number of available seats", " Number of reserved seats");
            System.out.println();
            for (CabinClassConfigurationEntity cabin : listOfCabinClass) {

                System.out.printf("%-30s%-30d%-30d%-40s%-40d%-40d", cabin.getCabinclassType(), cabin.getNumAisles(), cabin.getNumRows(), cabin.getSeatingConfig(), cabin.getAvailableSeats(), cabin.getReservedSeats());
                System.out.println();
            }
            System.out.println();

        } catch (FlightDoesNotExistException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void deleteFlight(Scanner sc) {
        System.out.println("");
        System.out.println("Please enter flight number: ");
        String flightNumber = sc.nextLine().trim();
        try {
            boolean deleted = flightSessionBean.deleteFlight(flightNumber);
            if (deleted) {
                System.out.println("Flight " + flightNumber + " has been deleted!");
            } else {
                System.out.println("Flight " + flightNumber + " is still in use, but is no longer taking in new schedules!");
            }

            System.out.println();
        } catch (FlightDoesNotExistException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updateFsp(Scanner sc) {
        boolean canEdit = false;

        viewAllFsp();
        try {
            System.out.println("Which Flight Schedule Plan would you like to edit (enter FSP ID) : ");
            Long fspId = sc.nextLong();
            sc.nextLine();

            FlightSchedulePlanEntity fsp = flightSchedulePlanSessionBean.viewFlightSchedulePlan(fspId);

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
            System.out.printf("%-30s%-30s%-30s", "Departure Date/Time", "Flight Duration", "Arrival Date/Time");
            System.out.println();
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                int flightMins = fs.getFlightDuration();
                int flightHour = flightMins / 60;
                flightMins %= 60;
                String flightduration = flightHour + "hr " + flightMins + " mins";
                System.out.printf("%-30s%-30s%-30s", format.format(fs.getDepartureDateTime().getTime()), flightduration, format.format(fs.getArrivalDateTime().getTime()));
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
            System.out.println();
            System.out.println();
            System.out.println();

            System.out.println("What would you like to edit? ");

            System.out.println("1. Edit Flight Schedule");
            System.out.println("2. Edit Fares");
            int mainChoice = sc.nextInt();

            if (mainChoice == 1 && fsp instanceof SingleFlightScheduleEntity) {

                FlightScheduleEntity fs = fsp.getListOfFlightSchedule().get(0);
                FlightScheduleEntity returnFs = null;
                boolean returnFlight = false;

                canEdit = flightScheduleSessionBean.checkFlightScheduleSeats(fs);

                //means flight has existing reservations and cannot be edited
                if (canEdit == false) {
                    System.out.println("Flight Schedule Plan has a flight schedule with reservations made! Changes can not longer be made!");
                } else {

                    int fsChoice = 0;
                    //means flight has no reservation, so can change
                    while (fsChoice != 2) {
                        System.out.println("What would you like to edit? ");
                        System.out.println("1. Flight departure date/time and duration");
                        System.out.println("2. Exit");
                        System.out.print("Please enter choice: ");
                        fsChoice = sc.nextInt();
                        sc.nextLine();

                        if (fsChoice == 1) {
                            System.out.println();
                            System.out.print("Please enter departure date and time (Please enter in this format: dd/mm/yyyy/hh/mm) ");
                            String dateTime = sc.nextLine().trim();
                            try {
                                GregorianCalendar departDateTime = createDateTime(dateTime);
                                Date date = departDateTime.getTime();
                                System.out.println(format.format(date));

                                System.out.println("Would you like to change flight duration as well? (press 'y' for yes)");
                                String choice = sc.nextLine().trim();

                                if (choice.toLowerCase().equals("y")) {

                                    System.out.println("What is the new flight duration: ");
                                    int newFlightDuration = sc.nextInt();
                                    sc.nextLine();

                                    flightSchedulePlanSessionBean.mergeFSPWithNewFlightDuration(newFlightDuration, fsp, departDateTime, fs);
                                    System.out.println("Update for date and duration successful!");
                                    System.out.println();
                                } else {
                                    //call fsp update method - if got return flight, run code to replace flight schedule
                                    flightSchedulePlanSessionBean.updateSingleFspDate(fsp.getFlightEntity(), departDateTime, fsp, fs);
                                    System.out.println("Update for date successful!");
                                    System.out.println();
                                }
                            } catch (FlightScheduleExistException | FlightDoesNotExistException | IncorrectFormatException | FlightSchedulePlanDoesNotExistException ex) {
                                System.out.println(ex.getMessage());
                            }

                        } else if (fsChoice == 3) { // update flight duration
                            return;

                        } else if (fsChoice != 2) {
                            System.out.println("Invalid choice selected, please enter again!");
                        }
                    }
                }

            } else if (mainChoice == 1 && fsp instanceof MultipleFlightScheduleEntity) {
                for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                    canEdit = flightScheduleSessionBean.checkFlightScheduleSeats(fs);

                    //means flight has existing reservations and cannot be edited
                    if (canEdit == false) {
                        System.out.println("Flight Schedule Plan has a flight schedule with reservations made! Changes can not longer be made!");
                        break;
                    }
                }

                //means flight has existing reservations and cannot be edited
                if (canEdit) {
                    System.out.println("What would you like to do?");
                    System.out.println("1. Add a flight schedule");
                    System.out.println("2. Update a flight schedule");
                    System.out.println("3. Delete a flight schedule");
                    System.out.println("4. Exit");
                    int subchoice = sc.nextInt();
                    sc.nextLine();

                    if (subchoice == 1) {

                        try {
                            System.out.print("Please enter departure date and time (Please enter in this format: dd/mm/yyyy/hh/mm) ");
                            String dateTime = sc.nextLine().trim();
                            GregorianCalendar departDateTime = createDateTime(dateTime);
                            Date date = departDateTime.getTime();
                            System.out.println(format.format(date));

                            flightSchedulePlanSessionBean.addNewFlightSchedule(departDateTime, fsp);
                            System.out.println("Flight Schedule has been successfully added to Flight " + fsp.getFlightNumber());
                        } catch (IncorrectFormatException | FlightScheduleExistException | FlightSchedulePlanDoesNotExistException ex) {
                            System.out.println(ex.getMessage());
                        }
                        System.out.println();

                    } else if (subchoice == 2) {
                        int fsChoice = 0;
                        while (fsChoice != 2) {
                            System.out.println("What would you like to edit? ");
                            System.out.println("1. Flight departure date/time and duration");
                            System.out.println("2. Exit");
                            System.out.print("Please enter choice: ");
                            fsChoice = sc.nextInt();
                            sc.nextLine();

                            if (fsChoice == 1) {
                                for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                                    System.out.println("Would you like to edit Flight Schedule " + fs.getFlightScheduleId() + " for flight " + fsp.getFlightNumber() + " (1 for yes, 2 for no)");
                                    int decision = sc.nextInt();
                                    sc.nextLine();
                                    System.out.println();
                                    if (decision == 1) {
                                        System.out.print("Please enter departure date and time (Please enter in this format: dd/mm/yyyy/hh/mm) ");
                                        String dateTime = sc.nextLine().trim();
                                        try {
                                            GregorianCalendar departDateTime = createDateTime(dateTime);
                                            Date date = departDateTime.getTime();
                                            System.out.println(format.format(date));

                                            System.out.println("Would you like to change flight duration as well? (press 'y' for yes)");
                                            String choice = sc.nextLine().trim();

                                            if (choice.toLowerCase().equals("y")) {

                                                System.out.println("What is the new flight duration: ");
                                                int newFlightDuration = sc.nextInt();
                                                sc.nextLine();

                                                flightSchedulePlanSessionBean.mergeFSPWithNewFlightDuration(newFlightDuration, fsp, departDateTime, fs);
                                                System.out.println("Update for date and duration successful!");
                                                System.out.println();
                                            } else {
                                                //call fsp update method - if got return flight, run code to replace flight schedule
                                                FlightEntity flight = fsp.getFlightEntity();
                                                flightSchedulePlanSessionBean.updateSingleFspDate(flight, departDateTime, fsp, fs);
                                                System.out.println("Update for date successful!");
                                                System.out.println();
                                            }
                                        } catch (FlightScheduleExistException | FlightDoesNotExistException | IncorrectFormatException | FlightSchedulePlanDoesNotExistException | InputMismatchException ex) {
                                            System.out.println(ex.getMessage());
                                            sc.next();;
                                        }
                                    } else { // NEW ADDITION TO CHECK
                                        System.out.println("Do you want to edit anymore flight schedules for flight schedule plan? (1 for yes, 2 for no)");
                                        int decision2 = sc.nextInt();
                                        sc.nextLine();

                                        if (decision2 == 2) {
                                            break;
                                        }
                                    }
                                }
                            } else if (fsChoice == 3) {
                                return;

                            } else if (fsChoice != 2) {
                                System.out.println("Invalid choice selected, please enter again!");
                            }
                        }
                    } else if (subchoice == 3) {

                        System.out.println("============FLIGHT SCHEDULE=============");
                        System.out.println();
                        System.out.printf("%-30s%-30s%-30s%-30s", "Flight Schedule ID", "Departure Date/Time", "Flight Duration", "Arrival Date/Time");
                        System.out.println();
                        for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                            int flightMins = fs.getFlightDuration();
                            int flightHour = flightMins / 60;
                            flightMins %= 60;
                            String flightduration = flightHour + "hr " + flightMins + " mins";
                            System.out.printf("%-30s%-30s%-30s%-30s", fs.getFlightScheduleId(), format.format(fs.getDepartureDateTime().getTime()), flightduration, format.format(fs.getArrivalDateTime().getTime()));
                            System.out.println();
                        }

                        System.out.print("Please key in Flight Schedule ID you wish to delete: ");
                        Long fsId = sc.nextLong();
                        sc.nextLine();

                        //checks if any of the seats are taken
                        FlightScheduleEntity fs = flightSchedulePlanSessionBean.getFlightScheduleUsingID(fsId);
                        if (fs.getFlightSchedulePlan().getReturnFlightSchedulePlan() == null) { // one way flight
                            canEdit = flightScheduleSessionBean.checkFlightScheduleSeats(fs);
                        }
                        //means flight has existing reservations and cannot be edited
                        if (canEdit == false) {
                            System.out.println("Flight Schedule Plan has a flight schedule with reservations made and can not longer be deleted!");
                        } else {
                            flightSchedulePlanSessionBean.deleteFlightSchedule(fsId);
                            System.out.println("Flight schedule " + fsId + " has been successfully deleted!");
                        }

                    } else if (subchoice == 4) {
                        return;
                    }
                }

            } else if ((mainChoice == 1 && fsp instanceof RecurringScheduleEntity) || (mainChoice == 1 && fsp instanceof RecurringWeeklyScheduleEntity)) {
                System.out.println("Do you want to update all flight Schedule? (1 for yes, 2 for no)");
                int choice = sc.nextInt();
                sc.nextLine();

                int day = 0;

                if (choice == 1) {
                    boolean reenter = false;
                    int layover = 0;

                    String flightNumber = fsp.getFlightNumber();
                    System.out.print("Please enter frequency of flight schedule you wish to create: ");
                    String numFrequencyString = sc.nextLine().trim();
                    int numFrequency = Integer.parseInt(numFrequencyString);

                    if (numFrequency == 7) {
                        System.out.print("Please enter day of week for schedule to start: ");
                        String dayOfWeek = sc.nextLine().trim();

                        if (dayOfWeek.toLowerCase().equals("monday")) {
                            day = 1;
                        } else if (dayOfWeek.toLowerCase().equals("tuesday")) {
                            day = 2;
                        } else if (dayOfWeek.toLowerCase().equals("wednesday")) {
                            day = 3;
                        } else if (dayOfWeek.toLowerCase().equals("thursday")) {
                            day = 4;
                        } else if (dayOfWeek.toLowerCase().equals("friday")) {
                            day = 5;
                        } else if (dayOfWeek.toLowerCase().equals("saturday")) {
                            day = 6;
                        } else if (dayOfWeek.toLowerCase().equals("sunday")) {
                            day = 7;
                        }
                    }

                    System.out.print("Please enter departure date and time (Please enter in this format (dd/mm/yyyy/hh/mm) : ");
                    String dateTime = sc.nextLine().trim();
                    GregorianCalendar departDateTime = null;
                    try {
                        departDateTime = createDateTime(dateTime);

                        if (numFrequency == 7) {
                            int dayOfWeek = departDateTime.get(GregorianCalendar.DAY_OF_WEEK);
                            dayOfWeek--;
                            int diff = 0;
                            if (day < dayOfWeek) {
                                diff = 7 - dayOfWeek;
                                diff += day;
                            } else if (day > dayOfWeek) {
                                diff = day - dayOfWeek;
                            }

                            departDateTime.add(GregorianCalendar.DATE, diff);
                        }

                    } catch (IncorrectFormatException ex) {
                        System.out.println(ex.getMessage());
                        reenter = true;
                    }
                    //maybe can add if statement for reenter
                    System.out.print("Please enter end date (dd/mm/yyyy/hh/mm) : ");
                    String endDateTimeStr = sc.nextLine().trim();
                    GregorianCalendar endDateTime = null;
                    try {
                        endDateTime = createDateTime(endDateTimeStr);
                    } catch (IncorrectFormatException ex) {
                        System.out.println(ex.getMessage());
                        reenter = true;
                    }

                    if (reenter == false) {
                        System.out.print("Please enter flight duration (in minutes) : ");
                        String duration = sc.nextLine().trim();
                        Integer flightDuration = Integer.parseInt(duration);

                        //call sessionbean method to update flightschedule plan
                        flightSchedulePlanSessionBean.updateRecurrentFSP(flightNumber, departDateTime, endDateTime, flightDuration, numFrequency, fsp);
                        System.out.println("Flight Schedule Plan for Flight " + fsp.getFlightNumber() + " has been updated!");

//                        FlightEntity flight = null;
//                        boolean returnFlight = false;
//                        try {
//                            flight = flightSessionBean.viewFlightDetails(flightNumber);
//                            if (flight.getReturnFlight() != null) {
//                                System.out.println("Please enter if you would like to create a return flight schedule plan for your existing flight? (1 for yes)");
//                                System.out.print("Please enter your choice: ");
//                                int choice3 = sc.nextInt();
//                                sc.nextLine();
//                                if (choice3 == 1) {
//                                    returnFlight = true;
//                                    System.out.print("Please enter layover duration: ");
//                                    layover = sc.nextInt();
//                                    sc.nextLine();
//                                } else {
//                                    returnFlight = false;
//                                }
//                            }
//                        } catch (FlightDoesNotExistException ex) {
//                            System.out.println(ex.getMessage());
//                        }
                    }
                } else {
                    System.out.println("Unable to edit Recurring Flight Schedule Plan!");
                }
            }  else if (mainChoice == 2) {

                int choice = 0;
                while (choice != 4) {
                    System.out.println("What woud you like to do?");
                    System.out.println("1. Add new fare");
                    System.out.println("2. Update one fare");
                    System.out.println("3. Delete one fare");
                    System.out.println("4. Exit");
                    System.out.print("Please enter choice: ");
                    choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 1) {
                        boolean topersist = false;
                        System.out.println();
                        System.out.println("Which Cabin type would you like to add the fare for?");
                        System.out.println("1. First Class");
                        System.out.println("2. Business Class");
                        System.out.println("3. Premium Economy");
                        System.out.println("4. Economy");
                        System.out.print("Please enter choice: ");
                        int fareChoice = sc.nextInt();
                        sc.nextLine();

                        if (fareChoice == 1) {
                            System.out.print("Please enter your fare basis code for First Class Cabin : ");
                            String fbc = "F" + sc.nextLine().trim();
                            System.out.print("Please enter fare amount: $");
                            BigDecimal fareAmount = sc.nextBigDecimal();
                            sc.nextLine();
                            FareEntity newFare = new FareEntity(fbc, fareAmount, CabinClassType.F);
                            fsp.getListOfFare().add(newFare);
                            topersist = true;
                        } else if (fareChoice == 2) {
                            System.out.print("Please enter your fare basis code for Business Class Cabin : ");
                            String fbc = "J" + sc.nextLine().trim();
                            System.out.print("Please enter fare amount: $");
                            BigDecimal fareAmount = sc.nextBigDecimal();
                            sc.nextLine();
                            FareEntity newFare = new FareEntity(fbc, fareAmount, CabinClassType.J);
                            fsp.getListOfFare().add(newFare);
                            topersist = true;
                        } else if (fareChoice == 3) {
                            System.out.print("Please enter your fare basis code for Premium Economy Class : ");
                            String fbc = "W" + sc.nextLine().trim();
                            System.out.print("Please enter fare amount: $");
                            BigDecimal fareAmount = sc.nextBigDecimal();
                            sc.nextLine();
                            FareEntity newFare = new FareEntity(fbc, fareAmount, CabinClassType.W);
                            fsp.getListOfFare().add(newFare);
                            topersist = true;
                        } else if (fareChoice == 4) {
                            System.out.print("Please enter your fare basis code for Economy Class : ");
                            String fbc = "Y" + sc.nextLine().trim();
                            System.out.print("Please enter fare amount: $");
                            BigDecimal fareAmount = sc.nextBigDecimal();
                            sc.nextLine();
                            FareEntity newFare = new FareEntity(fbc, fareAmount, CabinClassType.Y);
                            fsp.getListOfFare().add(newFare);
                            topersist = true;
                        } else {
                            System.out.println("Invalid choice for adding new fare!!");
                            topersist = false;
                        }

                        if (topersist) {
                            flightSchedulePlanSessionBean.mergeFSPForFare(fsp);
                            System.out.println("New Fare for flight " + fsp.getFlightNumber() + " has been added!");
                        }
                    } else if (choice == 2) {
                        fsp = flightSchedulePlanSessionBean.viewFlightSchedulePlan(fsp.getFlightSchedulePlanId());
                        List<FareEntity> listOfFare = fsp.getListOfFare();
                        System.out.println("-----Fares for Flight Schedule Plan-----");
                        for (FareEntity fare : fsp.getListOfFare()) {
                            System.out.printf("%-20s%-20s%-15s%-15s", "Fare ID", "Fare basis code", "Fare amount", "Fare cabin type");
                            System.out.println();
                            System.out.printf("%-20s%-20s%-15.2f%-15s", fare.getFareId(), fare.getFareBasisCode(), fare.getFareAmount(), fare.getCabinType());
                            System.out.println();
                        }
                        System.out.println();
                        System.out.println("Enter ID of fare you wish to update: ");
                        Long fareId = sc.nextLong();
                        sc.nextLine();

                        //retrieve fare entity
                        FareEntity fare = flightSchedulePlanSessionBean.retrieveFare(fareId);
                        System.out.println("What would you like to change? ");
                        System.out.println("1. Fare Basis Code");
                        System.out.println("2. Price for fare");
                        int fareChoice = sc.nextInt();
                        sc.nextLine();
                        if (fareChoice == 1) {
                            System.out.print("Please enter the new fare basis code: ");
                            String fareBasis = fare.getCabinType() + sc.nextLine().trim();
                            fare.setFareBasisCode(fareBasis);
                            flightSchedulePlanSessionBean.mergeFare(fare);
                            System.out.println("Fares for flight " + fsp.getFlightNumber() + " has been updated!");
                        } else if (fareChoice == 2) {
                            System.out.println("Please enter the price for the fare: ");
                            BigDecimal newFare = sc.nextBigDecimal();
                            sc.nextLine();
                            fare.setFareAmount(newFare);
                            flightSchedulePlanSessionBean.mergeFare(fare);
                            System.out.println("Fares for flight " + fsp.getFlightNumber() + " has been updated!");
                        } else {
                            System.out.println("Invalid choice for updating fare!");
                        }

                    } else if (choice == 3) {
                        List<FareEntity> listOfFare = fsp.getListOfFare();
                        System.out.println("-----Fares for Flight Schedule Plan-----");
                        for (FareEntity fare : fsp.getListOfFare()) {
                            System.out.printf("%-20s%-20s%-15s%-15s", "Fare ID", "Fare basis code", "Fare amount", "Fare cabin type");
                            System.out.println();
                            System.out.printf("%-20s%-20s%-15.2f%-15s", fare.getFareId(), fare.getFareBasisCode(), fare.getFareAmount(), fare.getCabinType());
                            System.out.println();
                        }
                        System.out.println();
                        System.out.println("Enter ID of fare you wish to delete: ");
                        Long fareId = sc.nextLong();
                        sc.nextLine();

                        flightSchedulePlanSessionBean.deleteFare(fareId, fsp);
                        System.out.println("Fare " + fareId + " has been deleted for flight " + fsp.getFlightNumber());

                    }
                }

            } else {
                System.out.println("Invalid choice!");
            }

        } catch (InputMismatchException ex) {
            System.out.println("Wrong input for FSP ID!");
            sc.next();;
        } catch (FlightSchedulePlanDoesNotExistException | FlightScheduleDoesNotExistException | FareDoesNotExistException | FareCannotBeDeletedException | FlightDoesNotExistException | FlightScheduleExistException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void deleteFsp(Scanner sc) {

        viewSpecificFspForAllFlight(sc);
        System.out.print("Please enter ID of Flight Schedule Plan you wish to delete: ");
        Long fspId = sc.nextLong();
        sc.nextLine();
        try {
            FlightSchedulePlanEntity fsp = flightSchedulePlanSessionBean.viewFlightSchedulePlan(fspId);
            System.out.println("Are you sure you wish to delete Flight Schedule Plan for flight " + fsp.getFlightNumber() + " (1 for yes, 2 for no)");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                String message = flightSchedulePlanSessionBean.deleteFsp(fspId);
                System.out.println(message);
            } else if (choice == 2) {
                return;
            } else {
                System.out.println("Invalid choice for deleting Flight Schedule Plan");
            }

        } catch (FlightSchedulePlanDoesNotExistException | FareCannotBeDeletedException | FareDoesNotExistException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
