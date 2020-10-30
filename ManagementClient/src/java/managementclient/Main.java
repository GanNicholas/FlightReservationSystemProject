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
import java.util.Scanner;
import javax.ejb.EJB;

/**
 *
 * @author nickg
 */
public class Main {

    @EJB
    private static FlightSessionBeanRemote flightSessionBean;

    @EJB
    private static FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean;

    @EJB
    private static FlightRouteSessionBeanRemote flightRouteSessionBean;

    @EJB
    private static AircraftSessionBeanRemote aircraftSessionBean;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("-----Welcome to Flight Reservation System-----");
            System.out.println("1. Flight Schedule Plan");
            System.out.println("2. Aircraft configuration");
            System.out.println("3. Exit");
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            if (input.equals("1")) {
                FlightSchedulePlan fsp = new FlightSchedulePlan(flightSchedulePlanSessionBean, flightSessionBean);
                fsp.runFSP();
            } else if (input.equals("2")) {
                AircraftConfiguration aircraft = new AircraftConfiguration(aircraftSessionBean, flightRouteSessionBean);
                aircraft.AircraftConfigurationApp();
            } else if (input.equals("3")) {
                System.out.println("Goodbye!");
                break;
            }
        }

    }

}
