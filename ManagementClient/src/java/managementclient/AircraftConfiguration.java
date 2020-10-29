/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.AircraftSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.AirportEntity;
import entity.CabinClassConfigurationEntity;
import entity.FlightRouteEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.enumeration.CabinClassType;
import util.exception.AircraftConfigurationNotExistException;
import util.exception.CabinClassExceedMaxCapacity;
import util.exception.FlightRouteODPairExistException;

/**
 *
 * @author sohqi
 */
public class AircraftConfiguration {

    AircraftSessionBeanRemote aircraftSessionBeanRemote = null;
    FlightRouteSessionBeanRemote flightRouteSessionBean = null;

    public AircraftConfiguration(AircraftSessionBeanRemote aircraftSessionBeanRemote, FlightRouteSessionBeanRemote flightRouteSessionBean) {
        this.aircraftSessionBeanRemote = aircraftSessionBeanRemote;
        this.flightRouteSessionBean = flightRouteSessionBean;
    }

    public void AircraftConfigurationApp() {
        Scanner sc = new Scanner(System.in);
        System.out.println("**Welcome to creation of air craft configuration **");
        while (true) {

            System.out.println("1. Create aircraft configurations.");
            System.out.println("2. View all aircraft configurations.");
            System.out.println("3. View detail of an aircraft configurations.");
            System.out.println("4. Create flight route.");
            System.out.println("0. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                createAircraftConfiguration();
            } else if (choice == 2) {
                viewAircraftConfiguration();
            } else if (choice == 3) {
                viewDetailOfAircraftConfiguration();
            } else if (choice == 0) {
                break;
            }
        }
    }

    public void createAircraftConfiguration() {
        Scanner sc = new Scanner(System.in);
        List<CabinClassConfigurationEntity> listOfCabin = new ArrayList<CabinClassConfigurationEntity>();

        System.out.println("Please enter the aircraft name:");
        String aircraftName = sc.nextLine();
        System.out.println("Please enter the aircraft type:");
        String aircraftType = sc.nextLine();
        System.out.println("Please enter the maximum number of seatings:");
        int maxSeatingCapacity = sc.nextInt();
        String buffer = sc.nextLine();
        int totalSeat = 0;
        boolean brokenFromCabinType = false;
        boolean addRecordToDatabase = true;
        while (true) {
            CabinClassConfigurationEntity cabin = new CabinClassConfigurationEntity();
            addRecordToDatabase = true;
            System.out.println("Cabin class: ");
            System.out.println("Please enter the cabin type:");
            System.out.println("1. First class.");
            System.out.println("2. Business class");
            System.out.println("3. Premium economy class");
            System.out.println("4. Economy class");
            String cabinType = sc.nextLine();
            while (Integer.parseInt(cabinType) > 4 && Integer.parseInt(cabinType) <= 0) {
                System.out.println("Invalid cabin type!");
                System.out.println("Do you wish to continue ? Yes/No");
                String cabinType_YN = sc.nextLine();
                if (cabinType_YN.equalsIgnoreCase("Yes")) {
                    System.out.println("Cabin class: ");
                    System.out.println("Please enter the cabin type:");
                    System.out.println("1. First class.");
                    System.out.println("2. Business class");
                    System.out.println("3. Premium economy class");
                    System.out.println("4. Economy class");
                    System.out.println("Please enter the cabin type:");
                    cabinType = sc.nextLine();
                } else if (cabinType_YN.equalsIgnoreCase("No")) {
                    brokenFromCabinType = true;
                    break;
                }

            }
            if (!brokenFromCabinType) {
                System.out.println("Please enter the number of aisles:");
                int noOfAisle = sc.nextInt();
                System.out.println("Please enter the number of row:");
                int numRows = sc.nextInt();
                buffer = sc.nextLine();
                System.out.println("Please enter the actual seating configuration per column (i.e. 3-4-3):");
                String seatingConfig = sc.nextLine();

                if (cabinType.equalsIgnoreCase("1")) {
                    cabin.setCabinclassType(CabinClassType.F);
                } else if (cabinType.equalsIgnoreCase("2")) {
                    cabin.setCabinclassType(CabinClassType.J);
                } else if (cabinType.equalsIgnoreCase("3")) {
                    cabin.setCabinclassType(CabinClassType.W);
                } else if (cabinType.equalsIgnoreCase("4")) {
                    cabin.setCabinclassType(CabinClassType.Y);
                }
                cabin.setNumAisles(noOfAisle);
                cabin.setNumRows(numRows);
                cabin.setSeatingConfig(seatingConfig);

                int row = 0;
                String[] maxPplPerRow = seatingConfig.split("-");
                //checking seat configuration input and no of aisle match
                if (maxPplPerRow.length != noOfAisle + 1) {
                    System.out.println("Your number of aisle does not match your seating configuration. Please check your input and add again.");
                    addRecordToDatabase = false;

                }
                if (addRecordToDatabase) {
                    for (int i = 0; i < maxPplPerRow.length; i++) {
                        row += Integer.parseInt(maxPplPerRow[i]);
                    }

                    int totalPplPerClass = row * numRows;
                    totalSeat += totalPplPerClass;

                    cabin.setAvailableSeats(totalSeat);
                    cabin.setBalancedSeats(totalSeat);
                    cabin.setReservedSeats(0);
                    //validating total seat from user input must be lesser than max seat capacity from aircraft configuration
                    if (totalSeat > maxSeatingCapacity) {
                        System.out.println("Please check your capacity. Seating configuration and row for all the cabin classes should not exceed the maximum capacity of aircraft configuration.");
                        addRecordToDatabase = false;
                        break;
                    }
                }

                if (addRecordToDatabase) {
                    listOfCabin.add(cabin);
                }

                System.out.println("Do you wish to add more cabin class? Yes/No");
                String addMoreCabin = sc.nextLine();
                if (!addMoreCabin.equalsIgnoreCase("Yes")) {
                    break;
                }
            } else {
                if (listOfCabin.size() == 0) {
                    System.out.println("You have no cabin record. No record has been added.");
                    addRecordToDatabase = false;
                    break;
                } else {
                    System.out.println("Do you want to insert all the existing record into the database? Yes/No");
                    String insertAirConfigInput = sc.nextLine();
                    if (!insertAirConfigInput.equalsIgnoreCase("Yes")) {
                        addRecordToDatabase = false;
                    }
                }
            }

        }
        if (addRecordToDatabase) {
            AircraftConfigurationEntity aircraftConfigurationEntity = new AircraftConfigurationEntity();
            aircraftConfigurationEntity.setAircraftName(aircraftName);
            //aircraftConfigurationEntity.setAircraftType(new AircraftTypeEntity(aircraftType));
            aircraftConfigurationEntity.setMaxSeatingCapacity(maxSeatingCapacity);
            //aircraftConfigurationEntity.setCabinClasses(listOfCabin);

            Long id = aircraftSessionBeanRemote.createAircraftConfiguration(aircraftConfigurationEntity, new AircraftTypeEntity(aircraftType), listOfCabin);
            System.out.println("You have successfully created aircraft configuration id " + id);
        }

    }

    public void viewAircraftConfiguration() {
        System.out.println("**View all aircraft configurations.**");
        List<AircraftConfigurationEntity> aircrafttConfigList = aircraftSessionBeanRemote.viewAircraftConfiguration();
        for (AircraftConfigurationEntity aircraftConfig : aircrafttConfigList) {
            System.out.println("Aircraft Configuration Id:" + aircraftConfig.getAircraftConfigId());
            System.out.println("Aircraft Configuration Name:" + aircraftConfig.getAircraftName());
            System.out.println("Aircraft Type Name:" + aircraftConfig.getAircraftType().getAircraftTypeName());
            System.out.println("Aircraft Configuration Maximum Seating Capacity:" + aircraftConfig.getMaxSeatingCapacity());
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
        }
    }

    public void viewDetailOfAircraftConfiguration() {
        Scanner sc = new Scanner(System.in);
        boolean notExitingViewDetailOfAirConfig = true;
        while (notExitingViewDetailOfAirConfig) {
            System.out.println("**View detail of an aircraft configurations.**");
            System.out.println("Please enter the number to view the detail of the aircraft configuration or '0' to exit.");

            List<AircraftConfigurationEntity> aircrafttConfigList = aircraftSessionBeanRemote.viewAircraftConfiguration();
            for (AircraftConfigurationEntity aircraftConfig : aircrafttConfigList) {
                System.out.println("Aircraft Configuration Id:" + aircraftConfig.getAircraftConfigId());
                System.out.println("Aircraft Configuration Name:" + aircraftConfig.getAircraftName());
                System.out.println("Aircraft Type Name:" + aircraftConfig.getAircraftType().getAircraftTypeName());
                System.out.println("Aircraft Configuration Maximum Seating Capacity:" + aircraftConfig.getMaxSeatingCapacity());
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
            }
            Long selectedId = sc.nextLong();
            Long zero = 0L;
            if (selectedId.equals(zero)) {
                notExitingViewDetailOfAirConfig = false;
                break;
            }
            try {
                AircraftConfigurationEntity aircraftConfigDetail = aircraftSessionBeanRemote.viewDetailAircraftConfiguration(selectedId);

                //aircraftConfigDetail.getCabinClasses().size();
                System.out.println("***Aircraft Configuration and Aircraft Type***");
                System.out.println("Aircraft Configuration Id:" + aircraftConfigDetail.getAircraftConfigId());
                System.out.println("Aircraft Configuration Name:" + aircraftConfigDetail.getAircraftName());
                System.out.println("Aircraft Type Name:" + aircraftConfigDetail.getAircraftType().getAircraftTypeName());
                System.out.println("Aircraft Configuration Maximum Seating Capacity:" + aircraftConfigDetail.getMaxSeatingCapacity());

                System.out.println("***Cabin Class Configuration and Aircraft Type***");

                for (int i = 0; i < aircraftConfigDetail.getCabinClasses().size(); i++) {
                    List<CabinClassConfigurationEntity> listOfCabinClass = aircraftConfigDetail.getCabinClasses();
                    System.out.println("Cabin Class Configuration Id:" + listOfCabinClass.get(i).getCabinClassConfigId());
                    System.out.println("Cabin Class Class Type:" + listOfCabinClass.get(i).getCabinclassType());
                    System.out.println("Cabin Class Number of Aisle(s):" + listOfCabinClass.get(i).getNumAisles());
                    System.out.println("Cabin Class Number of Row(s):" + listOfCabinClass.get(i).getNumRows());
                    System.out.println("Cabin Class Available Seat(s):" + listOfCabinClass.get(i).getAvailableSeats());
                    System.out.println("Cabin Class Reserved Seat(s):" + listOfCabinClass.get(i).getReservedSeats());
                    System.out.println("Cabin Class Balance Seat(s):" + listOfCabinClass.get(i).getBalancedSeats());
                    System.out.println("Cabin Class Seating Configuration:" + listOfCabinClass.get(i).getSeatingConfig());
                    System.out.println("****************************************************************************************");
                }
            } catch (AircraftConfigurationNotExistException ex) {
                System.out.println("The number you have entered does not exist in the database");
            }

        }

    }

    public void createFlightRoute() {
        Scanner sc = new Scanner(System.in);
        System.out.println("**Create flight route.**");

        System.out.println("Please enter the original location of the airport.");
        String originalLocation = sc.nextLine();
        System.out.println("Please enter the original location of IATA airport code.");
        String oIATAAirport = sc.nextLine();
        System.out.println("Please enter the original location of the airport city.");
        String oCity = sc.nextLine();
        System.out.println("Please enter the original location of the airport state.");
        String oState = sc.nextLine();
        System.out.println("Please enter the original location of the airport country.");
        String oCountry = sc.nextLine();
        System.out.println("Please enter the original location of time zone(hour).");
        int oTimezoneHr = sc.nextInt();
        System.out.println("Please enter the original location of time zone(minute).");
        int oTimezoneMin = sc.nextInt();
        sc.nextLine();
        System.out.println("Please enter the Destination location of the airport.");
        String destinationLocation = sc.nextLine();
        System.out.println("Please enter the Destination location of IATA airport code.");
        String dIATAAirport = sc.nextLine();
        System.out.println("Please enter the Destination location of the airport city.");
        String dCity = sc.nextLine();
        System.out.println("Please enter the Destination location of the airport state.");
        String dState = sc.nextLine();
        System.out.println("Please enter the Destination location of the airport country.");
        String dCountry = sc.nextLine();
        System.out.println("Please enter the Destination location of time zone(hour).");
        int dTimezoneHr = sc.nextInt();
        System.out.println("Please enter the original location of time zone(minute).");
        int dTimezoneMin = sc.nextInt();
        sc.nextLine();
        System.out.println("Is there a return route? Yes/No");
        String returnRoute = sc.nextLine();
        while (!returnRoute.equalsIgnoreCase("Yes") && !returnRoute.equalsIgnoreCase("No")) {
            System.out.println("Invalid return route.");
            System.out.println("Is there a return route? Yes/No");
            returnRoute = sc.nextLine();
        }

        FlightRouteEntity fr = new FlightRouteEntity();

        AirportEntity airportOriginal = new AirportEntity();
        airportOriginal.setAirportName(originalLocation);
        airportOriginal.setIataAirportCode(oIATAAirport);
        airportOriginal.setCity(oCity);
        airportOriginal.setState(oState);
        airportOriginal.setCountry(oCountry);
        airportOriginal.setTimeZoneHour(oTimezoneHr);
        airportOriginal.setTimeZoneMin(oTimezoneMin);

        AirportEntity airportDestination = new AirportEntity();
        airportDestination.setAirportName(destinationLocation);
        airportDestination.setIataAirportCode(dIATAAirport);
        airportDestination.setCity(dCity);
        airportDestination.setState(dState);
        airportDestination.setCountry(dCountry);
        airportDestination.setTimeZoneHour(dTimezoneHr);
        airportDestination.setTimeZoneMin(dTimezoneMin);

        fr.setDestinationLocation(airportDestination);
        fr.setOriginLocation(airportOriginal);
        fr.setIsDeleted(false);

        FlightRouteEntity returnRouteEntity = null;
        if (returnRoute.equalsIgnoreCase("Yes")) {
            returnRouteEntity = new FlightRouteEntity();
            returnRouteEntity.setOriginLocation(airportDestination);
            returnRouteEntity.setDestinationLocation(airportOriginal);
            returnRouteEntity.setIsDeleted(false);
            returnRouteEntity.setReturnRoute(fr);
        }
        fr.setReturnRoute(returnRouteEntity);
        try {
            Long id = flightRouteSessionBean.createFlightRoute(fr);
            System.out.println("You have successfully create a flight route with the id of" + id);
        } catch (FlightRouteODPairExistException ex) {
            System.out.println("Flight route origin-destination already exist in the database");
        }

    }

    public void viewFlightRoute() {
        List<FlightRouteEntity> listOfFlightRoute = flightRouteSessionBean.viewListOfFlightRoute();
        for (int i = 0; i < listOfFlightRoute.size(); i++) {
            FlightRouteEntity fr = listOfFlightRoute.get(i);
            System.out.println("***Flight Route***");
            System.out.println(String.format("Origin Location(IATA airport Code: %s (%s)", fr.getOriginLocation().getAirportName(), fr.getOriginLocation().getIataAirportCode()));
            System.out.println("City: " + fr.getOriginLocation().getCity());
            System.out.println("State: " + fr.getOriginLocation().getCity());
            System.out.println("Country: " + fr.getOriginLocation().getCity());
            System.out.println(String.format("Time Zone: %d hour(s) : %d minute(s)  ", fr.getOriginLocation().getTimeZoneHour(), fr.getOriginLocation().getTimeZoneMin()));

            System.out.println(String.format("Destination Location(IATA airport Code: %s (%s)", fr.getDestinationLocation().getAirportName(), fr.getDestinationLocation().getIataAirportCode()));
            System.out.println("City: " + fr.getDestinationLocation().getCity());
            System.out.println("State: " + fr.getDestinationLocation().getCity());
            System.out.println("Country: " + fr.getDestinationLocation().getCity());
            System.out.println(String.format("Time Zone: %d hour(s) : %d minute(s)  ", fr.getDestinationLocation().getTimeZoneHour(), fr.getDestinationLocation().getTimeZoneMin()));

            if (fr.getReturnRoute() != null) {
                System.out.println("*** Return Flight Route***");
                System.out.println(String.format("Origin Location(IATA airport Code: %s (%s)", fr.getReturnRoute().getOriginLocation().getAirportName(), fr.getReturnRoute().getOriginLocation().getIataAirportCode()));
                System.out.println("City: " + fr.getReturnRoute().getOriginLocation().getCity());
                System.out.println("State: " + fr.getReturnRoute().getOriginLocation().getCity());
                System.out.println("Country: " + fr.getReturnRoute().getOriginLocation().getCity());
                System.out.println(String.format("Time Zone: %d hour(s) : %d minute(s)  ", fr.getReturnRoute().getOriginLocation().getTimeZoneHour(), fr.getReturnRoute().getOriginLocation().getTimeZoneMin()));

                System.out.println(String.format("Destination Location(IATA airport Code: %s (%s)", fr.getReturnRoute().getDestinationLocation().getAirportName(), fr.getReturnRoute().getDestinationLocation().getIataAirportCode()));
                System.out.println("City: " + fr.getReturnRoute().getDestinationLocation().getCity());
                System.out.println("State: " + fr.getReturnRoute().getDestinationLocation().getCity());
                System.out.println("Country: " + fr.getReturnRoute().getDestinationLocation().getCity());
                System.out.println(String.format("Time Zone: %d hour(s) : %d minute(s)  ", fr.getReturnRoute().getDestinationLocation().getTimeZoneHour(), fr.getReturnRoute().getDestinationLocation().getTimeZoneMin()));
            }
            System.out.println("--------------------------------------------------------------------------------------------------------------------------");
        }

    }
}
