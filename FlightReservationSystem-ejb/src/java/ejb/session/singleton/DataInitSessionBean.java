/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

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
@Startup
public class DataInitSessionBean {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void initialise() {
        EmployeeEntity employee = em.find(EmployeeEntity.class, 1L);
        if (employee == null) {
            EmployeeEntity fleetManager = new EmployeeEntity("Fleet Manager", "FleetManager", "password", UserRole.FLEETMANAGER);
            em.persist(fleetManager);
            EmployeeEntity routePlanner = new EmployeeEntity("Route Planner", "RoutePlanner", "password", UserRole.ROUTEPLANNER);
            em.persist(routePlanner);
            EmployeeEntity scheduleManager = new EmployeeEntity("Schedule Manager", "ScheduleManager", "password", UserRole.SCHEDULEMANAGER);
            em.persist(scheduleManager);
            EmployeeEntity salesManager = new EmployeeEntity("Sales Manager", "SalesManager", "password", UserRole.SALESMANAGER);
            em.persist(salesManager);
        }
        em.flush();

        PartnerEntity partner = em.find(PartnerEntity.class, 1L);
        if (partner == null) {
            PartnerEntity partnerEmployee = new PartnerEntity("HrsPartnerEmployee", "password", UserRole.PARTNEREMPLOYEE, "Holiday Reservation System");
            PartnerEntity partnerManager = new PartnerEntity("HrsPartnerManager", "password", UserRole.PARTNERRESERVATIONMANAGER, "Holiday Reservation System");
            em.persist(partnerEmployee);
            em.persist(partnerManager);
        }

        AirportEntity airport = em.find(AirportEntity.class, 1L);
        if (airport == null) {
//            String airportName, String iataAirPortCode, String state, String country, Integer timeZoneHour, Integer timezoneMin
            AirportEntity a1 = new AirportEntity("Changi Airport", "SIN", "Singapore", "Singapore", 8, 0);
            em.persist(a1);
            AirportEntity a2 = new AirportEntity("Narita Airport", "NRT", "Tokyo", "Japan", 9, 0);
            em.persist(a2);
            //check if minutes is displayd how
            AirportEntity a3 = new AirportEntity("Dawrin International Airport", "DRW", "Eaton", "Australia", 9, 30);
            em.persist(a3);
            AirportEntity a4 = new AirportEntity("Perth Airport", "PER", "Perth", "Australia", 8, 0);
            em.persist(a4);
            AirportEntity a5 = new AirportEntity("Kuala Lumpur International Airport", "KUL", "Kuala Lumpur", "Malaysia", 8, 0);
            em.persist(a5);
            AirportEntity a6 = new AirportEntity("Shanghai Pudong International Airport", "PVG", "Shanghai", "China", 8, 0);
            em.persist(a6);
            AirportEntity a7 = new AirportEntity("Taiwan Taoyuan International Airport", "TPE", "Taipei", "Taiwan", 8, 0);
            em.persist(a7);
            AirportEntity a8 = new AirportEntity("Incheon International Airport", "ICN", "Incheon", "South Korea", 9, 0);
            em.persist(a8);
            AirportEntity a9 = new AirportEntity("Jeju International Airport", "CJU", "Jeju-do", "South Korea", 9, 0);
            em.persist(a9);
            AirportEntity a10 = new AirportEntity("Soekarno-Hatta International Airport", "CGK", "Banten", "Indonesia", 7, 0);
            em.persist(a10);
            AirportEntity a11 = new AirportEntity("Auckland Airport", "AKL", "Auckland", "New Zealand", 13, 0);
            em.persist(a11);
        }
    }

}
