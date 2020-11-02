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
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import java.util.Scanner;
import javax.ejb.EJB;

/**
 *
 * @author nickg
 */
public class Main {

    @EJB
    private static FlightScheduleSessionBeanRemote flightScheduleSessionBean;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;

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

        Scanner sc = new Scanner(System.in);
        RunApp runApp = new RunApp(flightSessionBean, flightSchedulePlanSessionBean, flightRouteSessionBean, aircraftSessionBean, employeeSessionBean, flightScheduleSessionBean);
        runApp.runApp(sc);

    }

}
