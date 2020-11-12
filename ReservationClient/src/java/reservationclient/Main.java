/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightReservationSessionBeanRemote;
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
    private static FlightReservationSessionBeanRemote flightReservationSessionBean;

    @EJB
    private static FlightScheduleSessionBeanRemote flightScheduleSessionBean;


    @EJB
    private static CustomerSessionBeanRemote customerSessionBean;
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("1. Customer");
        System.out.println("0. Exit");
        String input = sc.nextLine();
        while (true) {
            if (input.equals("1")) {
                Customer c = new Customer(customerSessionBean, flightScheduleSessionBean, flightReservationSessionBean);
                c.runApp();
            }
        }
    }

}
