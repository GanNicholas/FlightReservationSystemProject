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
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.CabinClassType;
import util.exception.AircraftConfigurationNotExistException;
import util.exception.AirportODPairNotFoundException;
import util.exception.CabinClassExceedMaxCapacity;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightRouteExistInOtherClassException;
import util.exception.FlightRouteODPairExistException;

/**
 *
 * @author sohqi
 */
public class AircraftConfiguration {

    private AircraftSessionBeanRemote aircraftSessionBeanRemote = null;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AircraftConfiguration() {

        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public AircraftConfiguration(AircraftSessionBeanRemote aircraftSessionBeanRemote) {
        this();
        this.aircraftSessionBeanRemote = aircraftSessionBeanRemote;
    }

     public void AircraftConfigurationApp() {
        Scanner sc = new Scanner(System.in);
        System.out.println("**Welcome to creation of air craft configuration **");
        while (true) {

            System.out.println("1. Create aircraft configurations.");
            System.out.println("2. View all aircraft configurations.");
            System.out.println("3. View detail of an aircraft configurations.");
            System.out.println("0. Exit");
            String choice = sc.nextLine();
            if (choice.equals("1")) {
                createAircraftConfiguration();
            } else if (choice.equals("2")) {
                viewAircraftConfiguration();
            } else if (choice.equals("3")) {
                viewDetailOfAircraftConfiguration();
            } else if (choice.equals("0")) {
                break;
            }
        }
    }

    public void createAircraftConfiguration() {
        try{
        Scanner sc = new Scanner(System.in);
        List<CabinClassConfigurationEntity> listOfCabin = new ArrayList<CabinClassConfigurationEntity>();
        AircraftConfigurationEntity aircraftConfigurationEntity = new AircraftConfigurationEntity();
        boolean isValidationPassed = true;
        //aircraftconfig name and aircraft type
        System.out.print("Please enter the aircraft name:  ");
        String aircraftName = sc.nextLine();
        System.out.print("Please enter the aircraft type:  ");
        String aircraftType = sc.nextLine();
        System.out.print("Please enter the maximum number of seatings:  ");
        int maxSeatingCapacity = sc.nextInt();
        String buffer = sc.nextLine();
        aircraftConfigurationEntity.setAircraftName(aircraftName);
        aircraftConfigurationEntity.setMaxSeatingCapacity(maxSeatingCapacity);
        Set<ConstraintViolation<AircraftConfigurationEntity>> constraintViolationsAircraftConfig = validator.validate(aircraftConfigurationEntity);
        if (!constraintViolationsAircraftConfig.isEmpty()) {
            isValidationPassed = false;
            //System.out.println("\nInput data validation error!:");

            for (ConstraintViolation constraintViolation : constraintViolationsAircraftConfig) {
                System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
            }
            // System.out.println("Please try again");
        }
        Set<ConstraintViolation<AircraftTypeEntity>> constraintViolationsAircraftType = validator.validate(new AircraftTypeEntity(aircraftType));
        if (!constraintViolationsAircraftType.isEmpty()) {
            isValidationPassed = false;
            // System.out.println("\nInput data validation error!:");

            for (ConstraintViolation constraintViolation : constraintViolationsAircraftType) {
                System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
            }
            // System.out.println("Please try again");

        }
        if (isValidationPassed) {
            //Cabin class details
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
                    System.out.print("Do you wish to continue ? Yes/No  ");
                    String cabinType_YN = sc.nextLine();
                    if (cabinType_YN.equalsIgnoreCase("Yes")) {
                        System.out.println("Cabin class: ");
                        System.out.println("Please enter the cabin type:");
                        System.out.println("1. First class.");
                        System.out.println("2. Business class");
                        System.out.println("3. Premium economy class");
                        System.out.println("4. Economy class");
                        System.out.print("Please enter the cabin type:  ");
                        cabinType = sc.nextLine();
                    } else if (cabinType_YN.equalsIgnoreCase("No")) {
                        brokenFromCabinType = true;
                        break;
                    }

                }
                if (!brokenFromCabinType) {
                    System.out.print("Please enter the number of aisles:  ");
                    int noOfAisle = sc.nextInt();
                    System.out.print("Please enter the number of row:  ");
                    int numRows = sc.nextInt();
                    buffer = sc.nextLine();
                    System.out.print("Please enter the actual seating configuration per column (i.e. 3-4-3):  ");
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
                    //bean validation
                    Set<ConstraintViolation<CabinClassConfigurationEntity>> constraintViolationsCabin = validator.validate(cabin);
                    if (!constraintViolationsCabin.isEmpty()) {
                        //System.out.println("\nInput data validation error!:");
                        addRecordToDatabase = false;
                        for (ConstraintViolation constraintViolation : constraintViolationsCabin) {
                            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
                            System.out.println("Please try again to add your cabin class");
                        }
                        cabin = null;
                    }
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

                    System.out.print("Do you wish to add more cabin class? Yes/No  ");
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
                Long id = aircraftSessionBeanRemote.createAircraftConfiguration(aircraftConfigurationEntity, new AircraftTypeEntity(aircraftType), listOfCabin);
                System.out.println("You have successfully created aircraft configuration id " + id);
            }
        } else {
            System.out.println("Please try again");
        }
        }catch(InputMismatchException | NumberFormatException exception){
            System.out.println("You have invalid input. Please try again");
        }
    }

      public void viewAircraftConfiguration() {
        System.out.printf("%90s", "**View all aircraft configurations.**");
        System.out.println();
        List<AircraftConfigurationEntity> aircrafttConfigList = aircraftSessionBeanRemote.viewAircraftConfiguration();
        System.out.printf("%-30s %-40s %-40s %-51s", "Aircraft Configuration Id", "Aircraft Configuration Name", "Aircraft Type Name", "Aircraft Configuration Maximum Seating Capacity");
        System.out.println("");
        for (AircraftConfigurationEntity aircraftConfig : aircrafttConfigList) {

            System.out.printf("%-30s %-40s %-40s %-51d", aircraftConfig.getAircraftConfigId(), aircraftConfig.getAircraftName(), aircraftConfig.getAircraftType().getAircraftTypeName(), aircraftConfig.getMaxSeatingCapacity());
            System.out.println("");
            // System.out.println("Aircraft Configuration Name:" + aircraftConfig.getAircraftName());
            //System.out.println("Aircraft Type Name:" + aircraftConfig.getAircraftType().getAircraftTypeName());
            //System.out.println("Aircraft Configuration Maximum Seating Capacity:" + aircraftConfig.getMaxSeatingCapacity());
        }

    }

    public void viewDetailOfAircraftConfiguration() {
        Scanner sc = new Scanner(System.in);
        boolean notExitingViewDetailOfAirConfig = true;
        while (notExitingViewDetailOfAirConfig) {
            System.out.printf("%90s", "**View all aircraft configurations.**");
            System.out.println();

            List<AircraftConfigurationEntity> aircrafttConfigList = aircraftSessionBeanRemote.viewAircraftConfiguration();
            System.out.printf("%-30s %-40s %-40s %-51s ", "Aircraft Configuration Id", "Aircraft Configuration Name", "Aircraft Type Name", "Aircraft Configuration Maximum Seating Capacity");
            System.out.println();
            for (AircraftConfigurationEntity aircraftConfig : aircrafttConfigList) {

                System.out.printf("%-30s %-40s %-40s %-51d", aircraftConfig.getAircraftConfigId(), aircraftConfig.getAircraftName(), aircraftConfig.getAircraftType().getAircraftTypeName(), aircraftConfig.getMaxSeatingCapacity());
                System.out.println();
                /* System.out.println("Aircraft Configuration Id:" + aircraftConfig.getAircraftConfigId());
                System.out.println("Aircraft Configuration Name:" + aircraftConfig.getAircraftName());
                System.out.println("Aircraft Type Name:" + aircraftConfig.getAircraftType().getAircraftTypeName());
                System.out.println("Aircraft Configuration Maximum Seating Capacity:" + aircraftConfig.getMaxSeatingCapacity());
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");*/
            }
            System.out.println();
            System.out.println("Please enter the number to view the detail of the aircraft configuration or '0' to exit.");

            Long selectedId = sc.nextLong();
            Long zero = 0L;
            if (selectedId.equals(zero)) {
                notExitingViewDetailOfAirConfig = false;
                break;
            }
            try {
                AircraftConfigurationEntity aircraftConfigDetail = aircraftSessionBeanRemote.viewDetailAircraftConfiguration(selectedId);
                System.out.printf("%-30s %-40s %-40s %-51s", "Aircraft Configuration Id", "Aircraft Configuration Name", "Aircraft Type Name", "Aircraft Configuration Maximum Seating Capacity");
                System.out.println();
                System.out.printf("%-30s %-40s %-40s %-51d", aircraftConfigDetail.getAircraftConfigId(), aircraftConfigDetail.getAircraftName(), aircraftConfigDetail.getAircraftType().getAircraftTypeName(), aircraftConfigDetail.getMaxSeatingCapacity());

                //aircraftConfigDetail.getCabinClasses().size();
                /* System.out.println("***Aircraft Configuration and Aircraft Type***");
                System.out.println("Aircraft Configuration Id:" + aircraftConfigDetail.getAircraftConfigId());
                System.out.println("Aircraft Type Name:" + aircraftConfigDetail.getAircraftType().getAircraftTypeName());
                System.out.println("Aircraft Configuration Name:" + aircraftConfigDetail.getAircraftName());
                System.out.println("Aircraft Configuration Maximum Seating Capacity:" + aircraftConfigDetail.getMaxSeatingCapacity());*/
                System.out.println();
                System.out.printf("%110s", "***Cabin Class Configuration and Aircraft Type***");
                System.out.println("");
                System.out.println("");
                System.out.printf("%-30s %-14s %-32s %-30s %-30s %-30s %-30s %-30s", "Cabin Class Configuration Id",  "Class Type", "Cabin Class Number of Aisle(s)", "Cabin Class Number of Row(s)", "Cabin Class Available Seat(s)", "Cabin Class Reserved Seat(s)", "Cabin Class Balance Seat(s)", "Cabin Class Seating Configuration");
                System.out.println();
                for (int i = 0; i < aircraftConfigDetail.getCabinClasses().size(); i++) {
                    List<CabinClassConfigurationEntity> listOfCabinClass = aircraftConfigDetail.getCabinClasses();
                    System.out.printf("%-30s %-14s %-32s %-30s %-30s %-30s %-30s %-30s", listOfCabinClass.get(i).getCabinClassConfigId(),  listOfCabinClass.get(i).getCabinclassType(),
                            listOfCabinClass.get(i).getNumAisles(), listOfCabinClass.get(i).getNumRows(), listOfCabinClass.get(i).getAvailableSeats(),
                            listOfCabinClass.get(i).getReservedSeats(), listOfCabinClass.get(i).getBalancedSeats(), listOfCabinClass.get(i).getSeatingConfig());
                    System.out.println();
                    /* System.out.println("Cabin Class Configuration Id:" + listOfCabinClass.get(i).getCabinClassConfigId());
                    System.out.println("Cabin Class Class Type:" + listOfCabinClass.get(i).getCabinclassType());
                    System.out.println("Cabin Class Number of Aisle(s):" + listOfCabinClass.get(i).getNumAisles());
                    System.out.println("Cabin Class Number of Row(s):" + listOfCabinClass.get(i).getNumRows());
                    System.out.println("Cabin Class Available Seat(s):" + listOfCabinClass.get(i).getAvailableSeats());
                    System.out.println("Cabin Class Reserved Seat(s):" + listOfCabinClass.get(i).getReservedSeats());
                    System.out.println("Cabin Class Balance Seat(s):" + listOfCabinClass.get(i).getBalancedSeats());
                    System.out.println("Cabin Class Seating Configuration:" + listOfCabinClass.get(i).getSeatingConfig());
                    System.out.println("****************************************************************************************");*/
                }
                System.out.println();
            } catch (AircraftConfigurationNotExistException ex) {
                System.out.println("The number you have entered does not exist in the database");
            }

        }

    }

}
