/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.AircraftSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author nickg
 */
public class Main {

    @EJB
    private static AircraftSessionBeanRemote aircraftSessionBean;

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        AircraftConfiguration aircraft = new AircraftConfiguration(aircraftSessionBean);
        aircraft.AircraftConfigurationApp();
    }
    
}
