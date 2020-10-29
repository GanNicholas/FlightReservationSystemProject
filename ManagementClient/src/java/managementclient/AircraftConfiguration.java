/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.AircraftSessionBeanRemote;
import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.CabinClassConfigurationEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.enumeration.CabinClassType;
import util.exception.CabinClassExceedMaxCapacity;

/**
 *
 * @author sohqi
 */
public class AircraftConfiguration {

    AircraftSessionBeanRemote aircraftSessionBeanRemote = null;

    public AircraftConfiguration(AircraftSessionBeanRemote aircraftSessionBeanRemote) {
        this.aircraftSessionBeanRemote = aircraftSessionBeanRemote;
    }

    public void AircraftConfigurationApp() {
        Scanner sc = new Scanner(System.in);
        System.out.println("**Welcome to creation of air craft configuration **");
        while (true) {

            System.out.println("1. Create aircraft configurations.");
            System.out.println("2. View all aircraft configurations.");
            System.out.println("0. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                createAircraftConfiguration();
            } else if (choice == 2) {
                viewAircraftConfiguration();
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
            System.out.println("Aircraft configuration id:" + aircraftConfig.getAircraftConfigId());
            System.out.println("Aircraft configuration:" + aircraftConfig.getAircraftName());
            System.out.println("Aircraft configuration:" + aircraftConfig.getAircraftType().getAircraftTypeName());
            System.out.println("Aircraft configuration:" + aircraftConfig.getMaxSeatingCapacity());

        }
    }
}
