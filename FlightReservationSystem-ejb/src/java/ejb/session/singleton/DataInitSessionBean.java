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
            AirportEntity a11 = new AirportEntity("Osaka International Airport", "ITM", "Osaka", "Japan", 9, 0, "Itami");
            em.persist(a11);
            AirportEntity a12 = new AirportEntity("Tokyo International Airport", "HND", "Tokyo", "Japan", 9, 0, "Ōta");
            em.persist(a12);
            AirportEntity a13 = new AirportEntity("Beijing Capital International Airport", "PEK", "Beijing", "China", 8, 0, "Shunyi District");
            em.persist(a13);
            AirportEntity a14 = new AirportEntity("Guangzhou Baiyun International Airport", "CAN", "Guangdong", "China", 8, 0, "Guangzhou");
            em.persist(a14);
            AirportEntity a15 = new AirportEntity("Chengdu Shuangliu International Airport", "CTU", "Sichuan", "China", 8, 0, "Chengdu");
            em.persist(a15);
            AirportEntity a16 = new AirportEntity("Hong Kong International Airport", "HKG", "Chek Lap Kok", "Hong Kong", 8, 0, "Hong Kong");
            em.persist(a16);
            AirportEntity a17 = new AirportEntity("Hangzhou Xiaoshan International Airport", "HGH", "Zhejiang", "China", 8, 0, "Hangzhou");
            em.persist(a17);
            AirportEntity a18 = new AirportEntity("Kaohsiung International Airport", "KHH", "Kaohsiung", "Taiwan", 8, 0, "Xiaogang District");
            em.persist(a18);
            AirportEntity a19 = new AirportEntity("Gimpo International Airport", "GMP", "Seoul", "South Korea", 9, 0, "Gangseo-gu");
            em.persist(a19);
            AirportEntity a20 = new AirportEntity("Ulsan Airport", "USN", "Ulsan", "South Korean", 9, 0, "Buk-gu");
            em.persist(a20);
            AirportEntity a21 = new AirportEntity("Tan Son Nhat International Airport", "SGN", "Ho Chi Minh", "Vietnam", 7, 0, "Ho Chi Minh");
            em.persist(a21);
            AirportEntity a22 = new AirportEntity("Phnom Penh International Airport", "PNH", "Phnom Penh", "Cambodia", 7, 0, "Phnom Penh");
            em.persist(a22);
            AirportEntity a23 = new AirportEntity("Shenzhen Bao'an International Airport", "SZX", "Guangdong", "China", 8, 0, "Shengzhen");
            em.persist(a23);
            AirportEntity a24 = new AirportEntity("Halim Perdanakusuma International Airport", "HLP", "Jarkata", "Indonesia", 7, 0, "East Jarkata");
            em.persist(a24);
            AirportEntity a25 = new AirportEntity("Dewadaru Airport", "KWB", "Central Java", "Indonesia", 7, 0, "Karimunjawa");
            em.persist(a25);
            AirportEntity a26 = new AirportEntity("Albury Airport", "ABX", "New South Wales", "Australia", 11, 0, "Albury");
            em.persist(a26);
            AirportEntity a27 = new AirportEntity("Sydney Airport", "SYD", "New South Wales", "Australia", 11, 0, "Sydney");
            em.persist(a27);
            AirportEntity a28 = new AirportEntity("Alice Springs Airport", "ASP", "Northern Territory", "Australia", 9, 30, "Alice Springs");
            em.persist(a28);
            AirportEntity a29 = new AirportEntity("Brisbane Airport", "BNE", "Queensland", "Australia", 10, 0, "Brisbane");
            em.persist(a29);
            AirportEntity a30 = new AirportEntity("Rockhampton Airport", "ROK", "Queensland", "Australia", 10, 0, "Rockhampton");
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
