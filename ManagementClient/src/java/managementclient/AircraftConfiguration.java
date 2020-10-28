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
        String aircraftName = sc.nextLine().trim();
        System.out.println("Please enter the aircraft type:");
        String aircraftType = sc.nextLine().trim();
        System.out.println("Please enter the maximum number of seatings:");
        int maxSeatingCapacity = sc.nextInt();
        String buffer = sc.nextLine();

        while (true) {
            System.out.println("Cabin class: ");
            System.out.println("Please enter the cabin type:");
            String cabinType = sc.nextLine();
            System.out.println("Please enter the number of aisles:");
            int noOfAisle = sc.nextInt();
            System.out.println("Please enter the number of row:");
            int numRows = sc.nextInt();
            System.out.println("Please enter the number of seats abreast:");
            int numSeatsInAColumn = sc.nextInt();
            buffer = sc.nextLine();
            System.out.println("Please enter the actual seating configuration per column (i.e. 3-4-3):");
            String seatingConfig = sc.nextLine().trim();

            CabinClassConfigurationEntity cabin = new CabinClassConfigurationEntity();
            cabin.setNumAisles(noOfAisle);
            cabin.setNumRows(numRows);
            cabin.setNumSeatsInAColumn(numSeatsInAColumn);
            cabin.setSeatingConfig(seatingConfig);
            if (cabinType.equals("F")) {
                cabin.setCabinclassType(CabinClassType.F);
            } else if (cabinType.equals("J")) {
                cabin.setCabinclassType(CabinClassType.J);
            } else if (cabinType.equals("W")) {
                cabin.setCabinclassType(CabinClassType.W);
            } else if (cabinType.equals("Y")) {
                cabin.setCabinclassType(CabinClassType.Y);
            }
            listOfCabin.add(cabin);

            System.out.println("Do you wish to add more cabin class? Y/N");
            String addMoreCabin = sc.nextLine();
            if (!addMoreCabin.equalsIgnoreCase("Y")) {
                break;
            }
        }
        AircraftConfigurationEntity aircraftConfigurationEntity = new AircraftConfigurationEntity();
        aircraftConfigurationEntity.setAircraftName(aircraftName);
        //aircraftConfigurationEntity.setAircraftType(new AircraftTypeEntity(aircraftType));
        aircraftConfigurationEntity.setMaxSeatingCapacity(maxSeatingCapacity);
        //aircraftConfigurationEntity.setCabinClasses(listOfCabin);

        Long id = aircraftSessionBeanRemote.createAircraftConfiguration(aircraftConfigurationEntity, new AircraftTypeEntity(aircraftType), listOfCabin);
        System.out.println("You have successfully created aircraft configuration id " + id);

    }

    public void viewAircraftConfiguration() {
        System.out.println("**View all aircraft configurations.**");
        List<AircraftConfigurationEntity> aircrafttConfigList = aircraftSessionBeanRemote.viewAircraftConfiguration();
        for (AircraftConfigurationEntity aircraftConfig : aircrafttConfigList) {
            System.out.println("Aircraft configuration id:" + aircraftConfig.getAircraftConfigId());
            System.out.println("Aircraft configuration id:" + aircraftConfig.getAircraftName());
            System.out.println("Aircraft configuration id:" + aircraftConfig.getAircraftType().getAircraftTypeName());
            System.out.println("Aircraft configuration id:" + aircraftConfig.getMaxSeatingCapacity());

        }
    }
}
