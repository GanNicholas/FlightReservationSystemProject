/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AircraftSessionBeanLocal;
import ejb.session.stateless.FlightSessionBeanLocal;
import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.AirportEntity;
import entity.EmployeeEntity;
import entity.PartnerEntity;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.UserRole;
import util.exception.NoAircraftTypeAvailableException;

/**
 *
 * @author nickg
 */
@Singleton
@LocalBean
@Startup
public class DataInitForTestSessionBean {

    @EJB
    private AircraftSessionBeanLocal aircraftSessionBean;

    @EJB
    private FlightSessionBeanLocal flightSessionBean;

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {

        EmployeeEntity employee = em.find(EmployeeEntity.class, 1L);
        if (employee == null) {
            EmployeeEntity fleetManager = new EmployeeEntity("Fleet Manager", "fleetmanager", "password", UserRole.FLEETMANAGER);
            em.persist(fleetManager);
            EmployeeEntity routePlanner = new EmployeeEntity("Route Planner", "routeplanner", "password", UserRole.ROUTEPLANNER);
            em.persist(routePlanner);
            EmployeeEntity scheduleManager = new EmployeeEntity("Schedule Manager", "schedulemanager", "password", UserRole.SCHEDULEMANAGER);
            em.persist(scheduleManager);
            EmployeeEntity salesManager = new EmployeeEntity("Sales Manager", "salesmanager", "password", UserRole.SALESMANAGER);
            em.persist(salesManager);
        }
        em.flush();

        PartnerEntity partner = em.find(PartnerEntity.class, 1L);
        if (partner == null) {
            PartnerEntity partnerEmployee = new PartnerEntity("holidaydotcom", "password", UserRole.PARTNEREMPLOYEE, "Holiday.com");
            em.persist(partnerEmployee);
        }
        em.flush();

        AirportEntity airport = em.find(AirportEntity.class, 1L);
        if (airport == null) {
            AirportEntity a1 = new AirportEntity("Changi", "SIN", "Singapore", "Singapore", 8, 0, "Singapore");
            em.persist(a1);
            AirportEntity a2 = new AirportEntity("Hong Kong", "HKG", "Hong Kong", "China", 8, 0, "Chek Lap Kok");
            em.persist(a2);
            AirportEntity a3 = new AirportEntity("Taoyuan", "TPE", "Taipei", "Taiwan R.O.C.", 8, 0, "Taoyuan");
            em.persist(a3);
            AirportEntity a4 = new AirportEntity("Narita", "NRT", "Chiba", "Japan", 9, 0, "Narita");
            em.persist(a4);
            AirportEntity a5 = new AirportEntity("Sydney Airport", "SYD", "New South Wales", "Australia", 11, 0, "Sydney");
            em.persist(a5);

        }

        AircraftTypeEntity acType = em.find(AircraftTypeEntity.class, 1L);
        if (acType == null) {
            AircraftTypeEntity ac1 = new AircraftTypeEntity("Boeing 737");
            em.persist(ac1);
            AircraftTypeEntity ac2 = new AircraftTypeEntity("Boeing 747");
            em.persist(ac2);
        }

        em.flush();

//        Integer maxSeatingCapacity, AircraftTypeEntity aircraftType, String aircraftName
//        if (em.find(AircraftConfigurationEntity.class, 1L) == null) {
//            AircraftTypeEntity aircraftType1 = null;
//            AircraftTypeEntity aircraftType2 = null;
//            try {
//                aircraftType1 = aircraftSessionBean.getAircraftType(1L);
//                aircraftType2 = aircraftSessionBean.getAircraftType(2L);
//            } catch (NoAircraftTypeAvailableException ex) {
//                System.out.println(ex.getMessage());
//            }
//
//            AircraftConfigurationEntity ac1 = new AircraftConfigurationEntity(180, aircraftType1, "Boeing 737 All Economy");
//            em.persist(ac1);
//            
//            AircraftConfigurationEntity ac2 = new AircraftConfigurationEntity(180, aircraftType1, "Boeing 737 Three Classes");
//            em.persist(ac2);
//            
//            AircraftConfigurationEntity ac3 = new AircraftConfigurationEntity(380, aircraftType2, "Boeing 747 All Economy");
//            em.persist(ac3);
//            
//            AircraftConfigurationEntity ac4 = new AircraftConfigurationEntity(360, aircraftType2, "Boeing 747 Three Classes");
//            em.persist(ac4);
//            
//
//        }

    }

}
