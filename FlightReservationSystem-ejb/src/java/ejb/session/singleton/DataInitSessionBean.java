/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AircraftSessionBeanLocal;
import ejb.session.stateless.FlightRouteSessionBeanLocal;
import ejb.session.stateless.FlightSchedulePlanSessionBeanLocal;
import ejb.session.stateless.FlightSessionBeanLocal;
import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.AirportEntity;
import entity.CabinClassConfigurationEntity;
import entity.EmployeeEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.PartnerEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.CabinClassType;
import util.enumeration.UserRole;
import util.exception.AircraftConfigurationNotExistException;
import util.exception.AirportODPairNotFoundException;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightExistsException;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightRouteODPairExistException;
import util.exception.FlightScheduleExistException;

/**
 *
 * @author nickg
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBean;

    @EJB
    private FlightSessionBeanLocal flightSessionBean;

    @EJB
    private FlightRouteSessionBeanLocal flightRouteSessionBean;

    @EJB
    private AircraftSessionBeanLocal aircraftSessionBean;

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
            AirportEntity a4 = new AirportEntity("Perth Airport", "PER", "Western Australia", "Australia", 8, 0, "Perth");
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
            AirportEntity a12 = new AirportEntity("Tokyo International Airport", "HND", "Tokyo", "Japan", 9, 0, "Ota");
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
        if (acType == null) {
            AircraftTypeEntity ac1 = new AircraftTypeEntity("Boeing 737 Narrow body short range");
            em.persist(ac1);
            AircraftTypeEntity ac2 = new AircraftTypeEntity("Boeing 737 Wide body long range");
            em.persist(ac2);
        }

        em.flush();

        //To test search flight
        for (int i = 0; i < 15; i++) { //aircraft config and aircraft type
            AircraftConfigurationEntity ac1 = new AircraftConfigurationEntity();
            String type = "type" + i;
            AircraftTypeEntity at1 = new AircraftTypeEntity(type);
            em.persist(at1);
            ac1.setAircraftName("name" + i);
            ac1.setMaxSeatingCapacity(250);

            List<CabinClassConfigurationEntity> cabinList = new ArrayList<CabinClassConfigurationEntity>();
            CabinClassConfigurationEntity ccc1 = new CabinClassConfigurationEntity();
            ccc1.setNumAisles(2);
            ccc1.setSeatingConfig("3-4-3");
            ccc1.setCabinclassType(CabinClassType.Y);
            ccc1.setNumRows(10);
            cabinList.add(ccc1);
            ccc1 = new CabinClassConfigurationEntity();
            ccc1.setNumAisles(1);
            ccc1.setSeatingConfig("5-5");
            ccc1.setCabinclassType(CabinClassType.W);
            ccc1.setNumRows(5);
            cabinList.add(ccc1);
            aircraftSessionBean.createAircraftConfiguration(ac1, at1, cabinList);

        }
        try { //flight route
            flightRouteSessionBean.createFlightRoute("SIN", "KUL", "Yes");
            flightRouteSessionBean.createFlightRoute("SIN", "BNE", "Yes");
            flightRouteSessionBean.createFlightRoute("SIN", "ASP", "Yes");
            flightRouteSessionBean.createFlightRoute("ASP", "DRW", "Yes");
            flightRouteSessionBean.createFlightRoute("BNE", "DRW", "Yes");
            flightRouteSessionBean.createFlightRoute("DRW", "CAN", "Yes");
            flightRouteSessionBean.createFlightRoute("ITM", "SGN", "Yes");
            flightRouteSessionBean.createFlightRoute("SIN", "USN", "Yes");
            flightRouteSessionBean.createFlightRoute("USN", "DRW", "Yes");
            flightRouteSessionBean.createFlightRoute("SIN", "TPE", "Yes");

        } catch (AirportODPairNotFoundException ex) {
            System.out.println("AirportODPairNotFoundException at flight route record");
        } catch (FlightRouteODPairExistException ex) {
            System.out.println("FlightRouteODPairExistException at flight route record");
        }

        AircraftConfigurationEntity a1 = em.find(AircraftConfigurationEntity.class, 1l); // create FS

        FlightRouteEntity r1 = em.find(FlightRouteEntity.class, 1l);
        try {
            r1 = em.find(FlightRouteEntity.class, 1l);
            flightSessionBean.createFlightWithoutReturnFlight("ML001", r1, a1);
            flightSessionBean.createFlightWithReturnFlight("ML011", r1, a1, "ML001");

            r1 = em.find(FlightRouteEntity.class, 3l);
            flightSessionBean.createFlightWithoutReturnFlight("ML002", r1, a1);
            flightSessionBean.createFlightWithReturnFlight("ML012", r1, a1, "ML002");

            r1 = em.find(FlightRouteEntity.class, 5l);
            flightSessionBean.createFlightWithoutReturnFlight("ML003", r1, a1);
            flightSessionBean.createFlightWithReturnFlight("ML013", r1, a1, "ML003");

            r1 = em.find(FlightRouteEntity.class, 7l);
            flightSessionBean.createFlightWithoutReturnFlight("ML004", r1, a1);
            flightSessionBean.createFlightWithReturnFlight("ML014", r1, a1, "ML004");

            r1 = em.find(FlightRouteEntity.class, 9l);
            flightSessionBean.createFlightWithoutReturnFlight("ML005", r1, a1);
            flightSessionBean.createFlightWithReturnFlight("ML015", r1, a1, "ML005");

            r1 = em.find(FlightRouteEntity.class, 11l);
            flightSessionBean.createFlightWithoutReturnFlight("ML006", r1, a1);
            flightSessionBean.createFlightWithReturnFlight("ML016", r1, a1, "ML006");

            r1 = em.find(FlightRouteEntity.class, 13l);
            flightSessionBean.createFlightWithoutReturnFlight("ML007", r1, a1);
            flightSessionBean.createFlightWithReturnFlight("ML017", r1, a1, "ML007");

            r1 = em.find(FlightRouteEntity.class, 15l);
            flightSessionBean.createFlightWithoutReturnFlight("ML008", r1, a1);
            flightSessionBean.createFlightWithReturnFlight("ML018", r1, a1, "ML008");

        } catch (FlightExistsException ex) {
            System.out.println("FlightExistsException went wrong at flight record");
        } catch (FlightDoesNotExistException ex) {
            System.out.println("FlightDoesNotExistException went wrong at flight record");
        } catch (FlightRouteDoesNotExistException ex) {
            System.out.println("FlightRouteDoesNotExistException went wrong at flight record");
        } catch (AircraftConfigurationNotExistException ex) {
            System.out.println("AircraftConfigurationNotExistException went wrong at flight record");
        }
        System.out.println("------------------FlightRecord: " + r1.getFlightRouteId());
        // public String createRecurrentFlightSchedulePlan(String flightNumber, GregorianCalendar departureDateTime, GregorianCalendar endDate, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover, Integer recurrency) throws FlightDoesNotExistException, FlightScheduleExistException {

        try {
            int counterForFareBasis = 0;
            //1
            GregorianCalendar departureDateTime = new GregorianCalendar();
            departureDateTime.set(2021, 04, 02, 00, 10, 0);
            GregorianCalendar endDate = new GregorianCalendar();
            endDate.set(2021, 05, 16, 00, 10, 0);
            List<FareEntity> listOfFare = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FareEntity fe = new FareEntity();
                fe.setCabinType(CabinClassType.Y);
                fe.setFareBasisCode("F001" + counterForFareBasis);
                fe.setFareAmount(new BigDecimal(1500));
                listOfFare.add(fe);
                counterForFareBasis++;
            }
            flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML001", departureDateTime, endDate, 100, false, listOfFare, 600, 2);
            //2
            departureDateTime = new GregorianCalendar();
            departureDateTime.set(2021, 4, 01, 00, 10, 0);
            endDate = new GregorianCalendar();
            endDate.set(2021, 6, 02, 00, 10, 0);
            listOfFare = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FareEntity fe = new FareEntity();
                fe.setCabinType(CabinClassType.Y);
                fe.setFareBasisCode("F001" + counterForFareBasis);
                fe.setFareAmount(new BigDecimal(1500));
                listOfFare.add(fe);
                counterForFareBasis++;
            }
            flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML002", departureDateTime, endDate, 400, false, listOfFare, 120, 3);
            //3
            departureDateTime = new GregorianCalendar();
            departureDateTime.set(2021, 04, 01, 00, 10, 0);
            endDate = new GregorianCalendar();
            endDate.set(2021, 06, 02, 00, 10, 0);
            listOfFare = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FareEntity fe = new FareEntity();
                fe.setCabinType(CabinClassType.Y);
                fe.setFareBasisCode("F001" + counterForFareBasis);
                fe.setFareAmount(new BigDecimal(1500));
                listOfFare.add(fe);
                counterForFareBasis++;
            }
            flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML003", departureDateTime, endDate, 420, false, listOfFare, 600, 5);

            //4
            departureDateTime = new GregorianCalendar();
            departureDateTime.set(2021, 04, 02, 00, 10, 0);
            endDate = new GregorianCalendar();
            endDate.set(2021, 04, 03, 16, 00, 0);
            listOfFare = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FareEntity fe = new FareEntity();
                fe.setCabinType(CabinClassType.Y);
                fe.setFareBasisCode("F001" + counterForFareBasis);
                fe.setFareAmount(new BigDecimal(1500));
                listOfFare.add(fe);
                counterForFareBasis++;
            }
            flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML004", departureDateTime, endDate, 450, false, listOfFare, 240, 1);

            //5
            departureDateTime = new GregorianCalendar();
            departureDateTime.set(2021, 4, 01, 00, 10, 0);
            endDate = new GregorianCalendar();
            endDate.set(2021, 7, 01, 00, 10, 0);
            listOfFare = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FareEntity fe = new FareEntity();
                fe.setCabinType(CabinClassType.Y);
                fe.setFareBasisCode("F001" + counterForFareBasis);
                fe.setFareAmount(new BigDecimal(1500));
                listOfFare.add(fe);
                counterForFareBasis++;
            }
            flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML005", departureDateTime, endDate, 120, false, listOfFare, 700, 3);

            //6
            departureDateTime = new GregorianCalendar();
            departureDateTime.set(2021, 04, 01, 00, 10, 0);
            endDate = new GregorianCalendar();
            endDate.set(2021, 04, 14, 00, 10, 0);
            listOfFare = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FareEntity fe = new FareEntity();
                fe.setCabinType(CabinClassType.Y);
                fe.setFareBasisCode("F001" + counterForFareBasis);
                fe.setFareAmount(new BigDecimal(1500));
                listOfFare.add(fe);
                counterForFareBasis++;
            }
            flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML006", departureDateTime, endDate, 470, false, listOfFare, 700, 1);

            //7
            departureDateTime = new GregorianCalendar();
            departureDateTime.set(2021, 04, 05, 00, 10, 0);
            endDate = new GregorianCalendar();
            endDate.set(2021, 04, 19, 00, 10, 0);
            listOfFare = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FareEntity fe = new FareEntity();
                fe.setCabinType(CabinClassType.Y);
                fe.setFareBasisCode("F001" + counterForFareBasis);
                fe.setFareAmount(new BigDecimal(1500));
                listOfFare.add(fe);
                counterForFareBasis++;
            }
            flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML007", departureDateTime, endDate, 300, false, listOfFare, 200, 1);

            //8
            departureDateTime = new GregorianCalendar();
            departureDateTime.set(2021, 04, 06, 19, 10, 0);
            endDate = new GregorianCalendar();
            endDate.set(2021, 04, 19, 00, 19, 0);
            listOfFare = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FareEntity fe = new FareEntity();
                fe.setCabinType(CabinClassType.Y);
                fe.setFareBasisCode("F001" + counterForFareBasis);
                fe.setFareAmount(new BigDecimal(1500));
                listOfFare.add(fe);
                counterForFareBasis++;
            }
            flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML008", departureDateTime, endDate, 420, false, listOfFare, 200, 3);

            //9
            departureDateTime = new GregorianCalendar();
            departureDateTime.set(2021, 04, 07, 8, 10, 0);
            endDate = new GregorianCalendar();
            endDate.set(2021, 04, 21, 00, 19, 0);
            listOfFare = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                FareEntity fe = new FareEntity();
                fe.setCabinType(CabinClassType.Y);
                fe.setFareBasisCode("F001" + counterForFareBasis);
                fe.setFareAmount(new BigDecimal(1500));
                listOfFare.add(fe);
                counterForFareBasis++;
            }
            flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML009", departureDateTime, endDate, 300, true, listOfFare, 200, 3);
        } catch (FlightScheduleExistException ex) {
            System.out.println("FlightScheduleExistException went wrong at fsp");
        } catch (FlightDoesNotExistException ex) {
            System.out.println("FlightDoesNotExistExceptions went wrong at fsp");
        }
        //createRecurrentFlightSchedulePlan

    }

}
