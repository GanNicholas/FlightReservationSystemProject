/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.AircraftSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import java.util.InputMismatchException;
import java.util.Scanner;
import util.enumeration.UserRole;
import util.exception.CurrentlyLoggedInException;
import util.exception.EmployeeDoesNotExistException;
import util.exception.WrongPasswordException;

/**
 *
 * @author nickg
 */
public class RunApp {

    private FlightSessionBeanRemote flightSessionBean;

    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean;

    private FlightRouteSessionBeanRemote flightRouteSessionBean;

    private AircraftSessionBeanRemote aircraftSessionBean;

    private EmployeeSessionBeanRemote employeeSessionBean;

    private boolean loggedIn;

    private String userId;

    public RunApp(FlightSessionBeanRemote flightSessionBean, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBean, AircraftSessionBeanRemote aircraftSessionBean, EmployeeSessionBeanRemote employeeSessionBean) {
        this.flightSessionBean = flightSessionBean;
        this.flightSchedulePlanSessionBean = flightSchedulePlanSessionBean;
        this.flightRouteSessionBean = flightRouteSessionBean;
        this.aircraftSessionBean = aircraftSessionBean;
        this.employeeSessionBean = employeeSessionBean;
        this.loggedIn = false;
        this.userId = "";
    }

    public void runApp(Scanner sc) {
        int counter = 0;
        while (counter < 3) {
            System.out.println("-----Welcome to Flight Reservation System-----");
            System.out.println("------------------Login-----------------------");
            System.out.println("(enter 'bye' to exit)");
            System.out.print("ID        : ");
            String iD = sc.nextLine().trim();
            if (iD.toLowerCase().equals("bye")) {
                break;
            }
            this.userId = iD;
            System.out.print("Password  : ");
            String pw = sc.nextLine().trim();

            try {
                loggedIn = employeeSessionBean.employeeLogin(iD, pw);
                counter = 0;
                menu(sc);
            } catch (EmployeeDoesNotExistException | WrongPasswordException | CurrentlyLoggedInException ex) {
                System.out.println(ex.getMessage());
                counter++;
            }

        }

    }

    public void menu(Scanner sc) {
        int counter = 0;
        try {
            while (counter < 3) {
                System.out.println("-----Welcome to Flight Reservation System-----");
                System.out.println("1. Flight Planning");
                System.out.println("2. Flight Operation");
                System.out.println("3. Sales Management");
                System.out.println("4. Log out");
                System.out.print("Please enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    try {
                        if (employeeSessionBean.getEmployeeRole(userId).equals(UserRole.FLEETMANAGER) || employeeSessionBean.getEmployeeRole(userId).equals(UserRole.ROUTEPLANNER)) {
                            System.out.println("1. Aircraft Configuration");
                            System.out.println("2. Flight Route");
                            String subInput = sc.nextLine().trim();
                            int subChoice = Integer.parseInt(subInput);
                            if (subChoice == 1) {
                                AircraftConfiguration aircraft = new AircraftConfiguration(aircraftSessionBean);
                                aircraft.AircraftConfigurationApp();
                            } else if (subChoice == 2) {
                                FlightRoute fr = new FlightRoute(flightRouteSessionBean);
                                fr.flightRouteApp();
                            }

                            counter = 0;
                        } else {
                            System.out.println("Sorry, you do not have the access right. Please try again!");
                            counter++;
                        }
                    } catch (EmployeeDoesNotExistException ex) {
                        System.out.println(ex.getMessage());
                        break;
                    }
                } else if (choice == 2) {
                    try {
                        if (employeeSessionBean.getEmployeeRole(userId).equals(UserRole.SCHEDULEMANAGER)) {
                            FlightSchedulePlan fsp = new FlightSchedulePlan(flightSchedulePlanSessionBean, flightSessionBean, aircraftSessionBean, flightRouteSessionBean);
                            fsp.runFSP();
                        } else {
                            System.out.println("Sorry, you do not have the access right. Please try again!");
                            counter++;
                        }
                    } catch (EmployeeDoesNotExistException ex) {
                        System.out.println(ex.getMessage());
                        break;
                    }
                } else if (choice == 3) {
                    try {
                        if (employeeSessionBean.getEmployeeRole(userId).equals(UserRole.SALESMANAGER)) {
//                        FlightSchedulePlan fsp = new FlightSchedulePlan(flightSchedulePlanSessionBean, flightSessionBean);
//                        fsp.runFSP();
                        } else {
                            System.out.println("Sorry, you do not have the access right. Please try again!");
                            counter++;
                        }
                    } catch (EmployeeDoesNotExistException ex) {
                        System.out.println(ex.getMessage());
                        break;
                    }
                } else if (choice == 4) {
                    loggedIn = false;
                    System.out.println("Goodbye!");
                    break;
                }
            }
        } catch (NumberFormatException | InputMismatchException ex) {
            if (ex instanceof NumberFormatException) {
                System.out.println("You have invalid input.");
            } else {
                System.out.println(ex.getMessage());
            }
        }
    }
}
