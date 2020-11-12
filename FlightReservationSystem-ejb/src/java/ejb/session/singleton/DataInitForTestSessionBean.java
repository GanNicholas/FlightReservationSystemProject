/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.AircraftTypeEntity;
import entity.AirportEntity;
import entity.EmployeeEntity;
import entity.PartnerEntity;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.UserRole;

/**
 *
 * @author nickg
 */
@Singleton
@LocalBean
//@Startup
public class DataInitForTestSessionBean {

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
    }

}
