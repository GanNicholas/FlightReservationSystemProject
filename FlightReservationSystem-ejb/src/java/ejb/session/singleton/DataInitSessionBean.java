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
            PartnerEntity partnerEmployee = new PartnerEntity("PartnerEmployee", "password", UserRole.PARTNEREMPLOYEE, "Holiday Reservation System");
            PartnerEntity partnerManager = new PartnerEntity("PartnerManager", "password", UserRole.PARTNERRESERVATIONMANAGER, "Holiday Reservation System");
            em.persist(partnerEmployee);
            em.persist(partnerManager);
        }
        em.flush();

        AirportEntity airport = em.find(AirportEntity.class, 1L);
        if (airport == null) {
//            String airportName, String iataAirportCode, String state, String country, Integer timeZoneHour, Integer timezoneMin, String city
            AirportEntity a1 = new AirportEntity("Singapore Changi Airport", "SIN", "Singapore", "Singapore", 8, 0, "Singapore");
            em.persist(a1);
            AirportEntity a2 = new AirportEntity("Narita Airport", "NRT", "Chiba", "Japan", 9, 0, "Narita");
            em.persist(a2);
            //check if minutes is displayd how
            AirportEntity a3 = new AirportEntity("Darwin International Airport", "DRW", "Northern Territory", "Australia", 9, 30, "Eaton");
            em.persist(a3);
            AirportEntity a4 = new AirportEntity("Perth Airport", "PER", "Western Australia", "Australia", 8, 0 , "Perth");
            em.persist(a4);
            AirportEntity a5 = new AirportEntity("Kuala Lumpur International Airport", "KUL", "Selangor", "Malaysia", 8, 0, "Sepang");
            em.persist(a5);
            AirportEntity a6 = new AirportEntity("Shanghai Pudong International Airport", "PVG", "Shanghai", "China", 8, 0, "Pudong");
            em.persist(a6);
            AirportEntity a7 = new AirportEntity("Taiwan Taoyuan International Airport", "TPE", "Taoyuan", "Taiwan", 8, 0, "Dayuan District");
            em.persist(a7);
            AirportEntity a8 = new AirportEntity("Incheon International Airport", "ICN", "Incheon", "South Korea", 9, 0, "Jung-gu");
            em.persist(a8);
            AirportEntity a9 = new AirportEntity("Jeju International Airport", "CJU", "Jeju-do", "South Korea", 9, 0, "Jeju-si");
            em.persist(a9);
            AirportEntity a10 = new AirportEntity("Soekarno-Hatta International Airport", "CGK", "Banten", "Indonesia", 7, 0, "Tangerang City");
            em.persist(a10);
            AirportEntity a11 = new AirportEntity("Auckland Airport", "AKL", "Auckland", "New Zealand", 13, 0, "Mangere");
            em.persist(a11);
            AirportEntity a12 = new AirportEntity("Tribhuvan International Airport", "KTM", "Bagmati Pradesh", "Nepal", 5, 45, "Kathmandu");
            em.persist(a12);
            AirportEntity a13 = new AirportEntity("Indira Gandhi International Airport", "DEL", "Delhi", "India", 5, 30, "New Delhi");
            em.persist(a13);
            AirportEntity a14 = new AirportEntity("John F. Kennedy International Airport", "JFK", "New York", "United States Of America", -4, 0, "Queens");
            em.persist(a14);
            AirportEntity a15 = new AirportEntity("Sheremetyevo International Airport", "SVO", "Moscow Oblast", "Russia", 3, 0, "Khimki");
            em.persist(a15);
            AirportEntity a16 = new AirportEntity("Hong Kong International Airport", "HKG", "Chek Lap Kok", "Hong Kong", 8, 0, "Hong Kong");
            em.persist(a16);
            AirportEntity a17 = new AirportEntity("Ted Stevens Anchorage International Airport", "ANC", "Alaska", "United States of America", -8, 0, "Anchorage");
            em.persist(a17);
            AirportEntity a18 = new AirportEntity("Galeão International Airport", "GIG", "Rio de Janeiro", "Brazil", -3, 0, "Rio de Janeiro");
            em.persist(a18);
            AirportEntity a19 = new AirportEntity("Toronto Pearson International Airport", "YYZ", "Ontario", "Canada", -4, 0, "Toronto");
            em.persist(a19);
            AirportEntity a20 = new AirportEntity("Vancouver International Airport", "YVR", "Richmond", "Canada", -7, 0, "Vancouver");
            em.persist(a20);
            AirportEntity a21 = new AirportEntity("Tan Son Nhat International Airport", "SGN", "Ho Chi Minh", "Vietnam", 7, 0, "Ho Chi Minh");
            em.persist(a21);
            AirportEntity a22 = new AirportEntity("Phnom Penh International Airport", "PNH", "Phnom Penh", "Cambodia", 7, 0, "Phnom Penh");
            em.persist(a22);
            AirportEntity a23 = new AirportEntity("Kansai International Airport", "KIX", "Osaka", "Japan", 9, 0, "Izumisano-shi");
            em.persist(a23);
            AirportEntity a24 = new AirportEntity("Dublin Airport", "DUB", "Dublin", "Republic of Ireland", 0, 0, "Dublin");
            em.persist(a24);
            AirportEntity a25 = new AirportEntity("London City Airport", "LCY", "London", "England", 0, 0, "Newham");
            em.persist(a25);
            AirportEntity a26 = new AirportEntity("Ninoy Aquino International Airport", "MNL", "Philippines", "Philippines", 8, 0, "Manila");
            em.persist(a26);
            AirportEntity a27 = new AirportEntity("Zurich Airport", "ZRH", "Kloten", "Switzerland", 1, 0, "Zürich");
            em.persist(a27);
            AirportEntity a28 = new AirportEntity("Suvarnabhumi Airport", "BKK", "Samut Prakan", "Thailand", 7, 0, "Bangkok");
            em.persist(a28);
            AirportEntity a29 = new AirportEntity("Cape Town International Airport", "CPT", "Western Cape", "South Africa", 2, 0, "Cape Town");
            em.persist(a29);
            AirportEntity a30 = new AirportEntity("Dubai International Airport", "DXB", "Dubai", "United Arab Emirates", 4, 0, "Dubai");
            em.persist(a30);
        }
        em.flush();
        
        AircraftTypeEntity acType = em.find(AircraftTypeEntity.class, 1L);
        if(acType == null){
            AircraftTypeEntity ac1 = new AircraftTypeEntity("Boeing 737 Narrow body short range");
            em.persist(ac1);
            AircraftTypeEntity ac2 = new AircraftTypeEntity("Boeing 737 Wide body long range");
            em.persist(ac2);
        }   
        
        em.flush();
    }

}
