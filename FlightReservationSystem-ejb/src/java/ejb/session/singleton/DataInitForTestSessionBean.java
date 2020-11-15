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
import entity.FlightSchedulePlanEntity;
import entity.PartnerEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import util.exception.FlightSchedulePlanEndDateIsBeforeStartDateException;
import util.exception.IncorrectFormatException;
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
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBean;

    @EJB
    private FlightRouteSessionBeanLocal flightRouteSessionBean;

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
        if (em.find(AircraftConfigurationEntity.class, 1L) == null) {
            AircraftTypeEntity aircraftTypeTemp1 = null;
            AircraftTypeEntity aircraftTypeTemp2 = null;
            try {
                aircraftTypeTemp1 = aircraftSessionBean.getAircraftType(1L);
                System.out.println("Temp 1: " + aircraftTypeTemp1);
                aircraftTypeTemp2 = aircraftSessionBean.getAircraftType(2L);
                System.out.println("Temp 2: " + aircraftTypeTemp2);
            } catch (NoAircraftTypeAvailableException ex) {
                System.out.println(ex.getMessage());
            }

            AircraftTypeEntity aircraftType1 = null;
            AircraftTypeEntity aircraftType2 = null;

            if (aircraftTypeTemp1.getAircraftTypeName().equals("Boeing 737")) {
                aircraftType1 = aircraftTypeTemp1;
                aircraftType2 = aircraftTypeTemp2;
            } else {
                aircraftType1 = aircraftTypeTemp2;
                aircraftType2 = aircraftTypeTemp1;
            }

            List<CabinClassConfigurationEntity> listAc1 = new ArrayList<>();
            AircraftConfigurationEntity ac1 = new AircraftConfigurationEntity(180, aircraftType1, "Boeing 737 All Economy");
            CabinClassConfigurationEntity ac1CabinConfig = new CabinClassConfigurationEntity(CabinClassType.Y, 1, 30, 180, 0, 0, "3-3");
            listAc1.add(ac1CabinConfig);

            aircraftSessionBean.createAircraftConfiguration(ac1, aircraftType1, listAc1);

            List<CabinClassConfigurationEntity> listAc2 = new ArrayList<>();
            AircraftConfigurationEntity ac2 = new AircraftConfigurationEntity(180, aircraftType1, "Boeing 737 Three Classes");
            CabinClassConfigurationEntity ac2CabinConfig = new CabinClassConfigurationEntity(CabinClassType.F, 1, 5, 10, 0, 0, "1-1");
            CabinClassConfigurationEntity ac2CabinConfig2 = new CabinClassConfigurationEntity(CabinClassType.J, 1, 5, 20, 0, 0, "2-2");
            CabinClassConfigurationEntity ac2CabinConfig3 = new CabinClassConfigurationEntity(CabinClassType.Y, 1, 25, 150, 0, 0, "3-3");
            listAc2.add(ac2CabinConfig);
            listAc2.add(ac2CabinConfig2);
            listAc2.add(ac2CabinConfig3);

            aircraftSessionBean.createAircraftConfiguration(ac2, aircraftType1, listAc2);

            List<CabinClassConfigurationEntity> listAc3 = new ArrayList<>();
            AircraftConfigurationEntity ac3 = new AircraftConfigurationEntity(380, aircraftType2, "Boeing 747 All Economy");
            CabinClassConfigurationEntity ac3CabinConfig = new CabinClassConfigurationEntity(CabinClassType.Y, 2, 38, 380, 0, 0, "3-4-3");
            listAc3.add(ac3CabinConfig);
            aircraftSessionBean.createAircraftConfiguration(ac3, aircraftType2, listAc3);

            List<CabinClassConfigurationEntity> listAc4 = new ArrayList<>();
            AircraftConfigurationEntity ac4 = new AircraftConfigurationEntity(360, aircraftType2, "Boeing 747 Three Classes");
            CabinClassConfigurationEntity ac4CabinConfig = new CabinClassConfigurationEntity(CabinClassType.F, 1, 5, 10, 0, 0, "1-1");
            CabinClassConfigurationEntity ac4CabinConfig2 = new CabinClassConfigurationEntity(CabinClassType.J, 2, 5, 30, 0, 0, "2-2-2");
            CabinClassConfigurationEntity ac4CabinConfig3 = new CabinClassConfigurationEntity(CabinClassType.Y, 2, 32, 320, 0, 0, "3-4-3");
            listAc4.add(ac4CabinConfig);
            listAc4.add(ac4CabinConfig2);
            listAc4.add(ac4CabinConfig3);

            aircraftSessionBean.createAircraftConfiguration(ac4, aircraftType2, listAc4);
        }

        if (em.find(FlightRouteEntity.class, 1L) == null) {
            try {
                //String oIATA, String dIATA, String returnFlight - yes
                flightRouteSessionBean.createFlightRoute("SIN", "HKG", "yes");
                flightRouteSessionBean.createFlightRoute("SIN", "TPE", "yes");
                flightRouteSessionBean.createFlightRoute("SIN", "NRT", "yes");
                flightRouteSessionBean.createFlightRoute("HKG", "NRT", "yes");
                flightRouteSessionBean.createFlightRoute("TPE", "NRT", "yes");
                flightRouteSessionBean.createFlightRoute("SIN", "SYD", "yes");
                flightRouteSessionBean.createFlightRoute("SYD", "NRT", "yes");
            } catch (FlightRouteODPairExistException | AirportODPairNotFoundException ex) {
                System.out.println(ex.getMessage());
            }

        }

        if (em.find(FlightEntity.class, 1L) == null) {
            //String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig
            try {
                FlightRouteEntity flightRoute1 = (FlightRouteEntity) em.createNamedQuery("findFlightRouteWithAirportName").setParameter("origin", "SIN").setParameter("destination", "HKG").getSingleResult();
                FlightRouteEntity flightRoute2 = flightRoute1.getReturnRoute();
                AircraftConfigurationEntity ac1 = (AircraftConfigurationEntity) em.createNamedQuery("findAirCraftConfig").setParameter("name", "Boeing 737 Three Classes").getSingleResult();
                flightSessionBean.createFlightWithoutReturnFlight("ML111", flightRoute1, ac1);
                flightSessionBean.createFlightWithReturnFlight("ML112", flightRoute2, ac1, "ML111");

                FlightRouteEntity flightRoute3 = (FlightRouteEntity) em.createNamedQuery("findFlightRouteWithAirportName").setParameter("origin", "SIN").setParameter("destination", "TPE").getSingleResult();
                FlightRouteEntity flightRoute4 = flightRoute3.getReturnRoute();
                AircraftConfigurationEntity ac2 = (AircraftConfigurationEntity) em.createNamedQuery("findAirCraftConfig").setParameter("name", "Boeing 737 Three Classes").getSingleResult();
                flightSessionBean.createFlightWithoutReturnFlight("ML211", flightRoute3, ac2);
                flightSessionBean.createFlightWithReturnFlight("ML212", flightRoute4, ac2, "ML211");

                FlightRouteEntity flightRoute5 = (FlightRouteEntity) em.createNamedQuery("findFlightRouteWithAirportName").setParameter("origin", "SIN").setParameter("destination", "NRT").getSingleResult();
                FlightRouteEntity flightRoute6 = flightRoute5.getReturnRoute();
                AircraftConfigurationEntity ac3 = (AircraftConfigurationEntity) em.createNamedQuery("findAirCraftConfig").setParameter("name", "Boeing 747 Three Classes").getSingleResult();
                flightSessionBean.createFlightWithoutReturnFlight("ML311", flightRoute5, ac3);
                flightSessionBean.createFlightWithReturnFlight("ML312", flightRoute6, ac3, "ML311");

                FlightRouteEntity flightRoute7 = (FlightRouteEntity) em.createNamedQuery("findFlightRouteWithAirportName").setParameter("origin", "HKG").setParameter("destination", "NRT").getSingleResult();
                FlightRouteEntity flightRoute8 = flightRoute7.getReturnRoute();
                AircraftConfigurationEntity ac4 = (AircraftConfigurationEntity) em.createNamedQuery("findAirCraftConfig").setParameter("name", "Boeing 737 Three Classes").getSingleResult();
                flightSessionBean.createFlightWithoutReturnFlight("ML411", flightRoute7, ac4);
                flightSessionBean.createFlightWithReturnFlight("ML412", flightRoute8, ac4, "ML411");

                FlightRouteEntity flightRoute9 = (FlightRouteEntity) em.createNamedQuery("findFlightRouteWithAirportName").setParameter("origin", "TPE").setParameter("destination", "NRT").getSingleResult();
                FlightRouteEntity flightRoute10 = flightRoute9.getReturnRoute();
                AircraftConfigurationEntity ac5 = (AircraftConfigurationEntity) em.createNamedQuery("findAirCraftConfig").setParameter("name", "Boeing 737 Three Classes").getSingleResult();
                flightSessionBean.createFlightWithoutReturnFlight("ML511", flightRoute9, ac5);
                flightSessionBean.createFlightWithReturnFlight("ML512", flightRoute10, ac5, "ML511");

                FlightRouteEntity flightRoute11 = (FlightRouteEntity) em.createNamedQuery("findFlightRouteWithAirportName").setParameter("origin", "SIN").setParameter("destination", "SYD").getSingleResult();
                FlightRouteEntity flightRoute12 = flightRoute11.getReturnRoute();
                AircraftConfigurationEntity ac6 = (AircraftConfigurationEntity) em.createNamedQuery("findAirCraftConfig").setParameter("name", "Boeing 737 Three Classes").getSingleResult();
                flightSessionBean.createFlightWithoutReturnFlight("ML611", flightRoute11, ac6);
                flightSessionBean.createFlightWithReturnFlight("ML612", flightRoute12, ac6, "ML611");

                FlightRouteEntity flightRoute13 = (FlightRouteEntity) em.createNamedQuery("findFlightRouteWithAirportName").setParameter("origin", "SIN").setParameter("destination", "SYD").getSingleResult();
                FlightRouteEntity flightRoute14 = flightRoute13.getReturnRoute();
                AircraftConfigurationEntity ac7 = (AircraftConfigurationEntity) em.createNamedQuery("findAirCraftConfig").setParameter("name", "Boeing 737 All Economy").getSingleResult();
                flightSessionBean.createFlightWithoutReturnFlight("ML621", flightRoute13, ac7);
                flightSessionBean.createFlightWithReturnFlight("ML622", flightRoute14, ac7, "ML621");

                FlightRouteEntity flightRoute15 = (FlightRouteEntity) em.createNamedQuery("findFlightRouteWithAirportName").setParameter("origin", "SYD").setParameter("destination", "NRT").getSingleResult();
                FlightRouteEntity flightRoute16 = flightRoute15.getReturnRoute();
                AircraftConfigurationEntity ac8 = (AircraftConfigurationEntity) em.createNamedQuery("findAirCraftConfig").setParameter("name", "Boeing 747 Three Classes").getSingleResult();
                flightSessionBean.createFlightWithoutReturnFlight("ML711", flightRoute15, ac8);
                flightSessionBean.createFlightWithReturnFlight("ML712", flightRoute16, ac8, "ML711");

            } catch (FlightExistsException | FlightRouteDoesNotExistException | AircraftConfigurationNotExistException | FlightDoesNotExistException ex) {
                System.out.println(ex.getMessage());
            }

        }

        if (em.find(FlightSchedulePlanEntity.class, 1L) == null) {
            //Non recurrent
            //String flightNumber, List<GregorianCalendar> listOfDepartureDateTime, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover

            //reccurrent
            //String flightNumber, GregorianCalendar departureDateTime, GregorianCalendar endDate, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover, Integer recurrency
            //String fareBasisCode, BigDecimal fareAmount, CabinClassType cabinType
            try {
                List<FareEntity> fareFlight711 = new ArrayList<>();
                FareEntity flight711Fare1 = new FareEntity("F001", BigDecimal.valueOf(6500), CabinClassType.F);
                FareEntity flight711Fare2 = new FareEntity("F002", BigDecimal.valueOf(6000), CabinClassType.F);
                FareEntity flight711Fare3 = new FareEntity("J001", BigDecimal.valueOf(3500), CabinClassType.J);
                FareEntity flight711Fare4 = new FareEntity("J002", BigDecimal.valueOf(3000), CabinClassType.J);
                FareEntity flight711Fare5 = new FareEntity("Y001", BigDecimal.valueOf(1500), CabinClassType.Y);
                FareEntity flight711Fare6 = new FareEntity("Y002", BigDecimal.valueOf(1000), CabinClassType.Y);
                fareFlight711.add(flight711Fare1);
                fareFlight711.add(flight711Fare2);
                fareFlight711.add(flight711Fare3);
                fareFlight711.add(flight711Fare4);
                fareFlight711.add(flight711Fare5);
                fareFlight711.add(flight711Fare6);

                GregorianCalendar departDate711 = createDateTime("07/12/2020/09/00");
                GregorianCalendar endDate711 = createDateTime("31/12/2020/09/00");
                flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML711", departDate711, endDate711, 840, true, fareFlight711, 120, 7);

                List<FareEntity> fareFlight611 = new ArrayList<>();
                FareEntity flight611Fare1 = new FareEntity("F001", BigDecimal.valueOf(3250), CabinClassType.F);
                FareEntity flight611Fare2 = new FareEntity("F002", BigDecimal.valueOf(3000), CabinClassType.F);
                FareEntity flight611Fare3 = new FareEntity("J001", BigDecimal.valueOf(1750), CabinClassType.J);
                FareEntity flight611Fare4 = new FareEntity("J002", BigDecimal.valueOf(1500), CabinClassType.J);
                FareEntity flight611Fare5 = new FareEntity("Y001", BigDecimal.valueOf(750), CabinClassType.Y);
                FareEntity flight611Fare6 = new FareEntity("Y002", BigDecimal.valueOf(500), CabinClassType.Y);
                fareFlight611.add(flight611Fare1);
                fareFlight611.add(flight611Fare2);
                fareFlight611.add(flight611Fare3);
                fareFlight611.add(flight611Fare4);
                fareFlight611.add(flight611Fare5);
                fareFlight611.add(flight611Fare6);

                GregorianCalendar departDate611 = createDateTime("06/12/2020/12/00");
                GregorianCalendar endDate611 = createDateTime("31/12/2020/12/00");
                flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML611", departDate611, endDate611, 480, true, fareFlight611, 120, 7);

                List<FareEntity> fareFlight621 = new ArrayList<>();

                FareEntity flight621Fare1 = new FareEntity("Y001", BigDecimal.valueOf(700), CabinClassType.Y);
                FareEntity flight621Fare2 = new FareEntity("Y002", BigDecimal.valueOf(400), CabinClassType.Y);
                fareFlight621.add(flight621Fare1);
                fareFlight621.add(flight621Fare2);

                GregorianCalendar departDate621 = createDateTime("01/12/2020/10/00");
                GregorianCalendar endDate621 = createDateTime("31/12/2020/10/00");
                flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML621", departDate621, endDate621, 480, true, fareFlight621, 120, 7);

                List<FareEntity> fareFlight311 = new ArrayList<>();
                FareEntity flight311Fare1 = new FareEntity("F001", BigDecimal.valueOf(3350), CabinClassType.F);
                FareEntity flight311Fare2 = new FareEntity("F002", BigDecimal.valueOf(3100), CabinClassType.F);
                FareEntity flight311Fare3 = new FareEntity("J001", BigDecimal.valueOf(1850), CabinClassType.J);
                FareEntity flight311Fare4 = new FareEntity("J002", BigDecimal.valueOf(1600), CabinClassType.J);
                FareEntity flight311Fare5 = new FareEntity("Y001", BigDecimal.valueOf(850), CabinClassType.Y);
                FareEntity flight311Fare6 = new FareEntity("Y002", BigDecimal.valueOf(600), CabinClassType.Y);
                fareFlight311.add(flight311Fare1);
                fareFlight311.add(flight311Fare2);
                fareFlight311.add(flight311Fare3);
                fareFlight311.add(flight311Fare4);
                fareFlight311.add(flight311Fare5);
                fareFlight311.add(flight311Fare6);

                GregorianCalendar departDate311 = createDateTime("07/12/2020/10/00");
                GregorianCalendar endDate311 = createDateTime("31/12/2020/10/00");
                flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML311", departDate311, endDate311, 390, true, fareFlight311, 180, 7);

                List<FareEntity> fareFlight411 = new ArrayList<>();
                FareEntity flight411Fare1 = new FareEntity("F001", BigDecimal.valueOf(3150), CabinClassType.F);
                FareEntity flight411Fare2 = new FareEntity("F002", BigDecimal.valueOf(2900), CabinClassType.F);
                FareEntity flight411Fare3 = new FareEntity("J001", BigDecimal.valueOf(1650), CabinClassType.J);
                FareEntity flight411Fare4 = new FareEntity("J002", BigDecimal.valueOf(1400), CabinClassType.J);
                FareEntity flight411Fare5 = new FareEntity("Y001", BigDecimal.valueOf(650), CabinClassType.Y);
                FareEntity flight411Fare6 = new FareEntity("Y002", BigDecimal.valueOf(400), CabinClassType.Y);
                fareFlight411.add(flight411Fare1);
                fareFlight411.add(flight411Fare2);
                fareFlight411.add(flight411Fare3);
                fareFlight411.add(flight411Fare4);
                fareFlight411.add(flight411Fare5);
                fareFlight411.add(flight411Fare6);

                GregorianCalendar departDate411 = createDateTime("01/12/2020/13/00");
                GregorianCalendar endDate411 = createDateTime("31/12/2020/13/00");
                flightSchedulePlanSessionBean.createRecurrentFlightSchedulePlan("ML411", departDate411, endDate411, 240, true, fareFlight411, 240, 2);

                List<FareEntity> fareFlight511 = new ArrayList<>();
                FareEntity flight511Fare1 = new FareEntity("F001", BigDecimal.valueOf(3100), CabinClassType.F);
                FareEntity flight511Fare2 = new FareEntity("F002", BigDecimal.valueOf(2850), CabinClassType.F);
                FareEntity flight511Fare3 = new FareEntity("J001", BigDecimal.valueOf(1600), CabinClassType.J);
                FareEntity flight511Fare4 = new FareEntity("J002", BigDecimal.valueOf(1350), CabinClassType.J);
                FareEntity flight511Fare5 = new FareEntity("Y001", BigDecimal.valueOf(600), CabinClassType.Y);
                FareEntity flight511Fare6 = new FareEntity("Y002", BigDecimal.valueOf(350), CabinClassType.Y);
                fareFlight511.add(flight511Fare1);
                fareFlight511.add(flight511Fare2);
                fareFlight511.add(flight511Fare3);
                fareFlight511.add(flight511Fare4);
                fareFlight511.add(flight511Fare5);
                fareFlight511.add(flight511Fare6);

                //String flightNumber, List<GregorianCalendar> listOfDepartureDateTime, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover
                List<GregorianCalendar> listOfDates = new ArrayList<>();
                GregorianCalendar departDate1 = createDateTime("07/12/2020/17/00");
                GregorianCalendar departDate2 = createDateTime("08/12/2020/17/00");
                GregorianCalendar departDate3 = createDateTime("09/12/2020/17/00");
                listOfDates.add(departDate1);
                listOfDates.add(departDate2);
                listOfDates.add(departDate3);
                flightSchedulePlanSessionBean.createNonRecurrentFlightSchedulePlan("ML511", listOfDates, 180, true, fareFlight511, 120);

            } catch (FlightDoesNotExistException | FlightSchedulePlanEndDateIsBeforeStartDateException | FlightScheduleExistException ex) {
                System.out.println(ex.getMessage());
            }

        }

    }

    public GregorianCalendar createDateTime(String dateTime) {
        String[] information = dateTime.split("/");
        List<Integer> informationInteger = new ArrayList<>();
        for (String info : information) {
            informationInteger.add(Integer.parseInt(info));
        }

        //NEED VALIDATE CALENDAR INPUT 
        GregorianCalendar newCalendar = new GregorianCalendar(informationInteger.get(2), (informationInteger.get(1) - 1), informationInteger.get(0), informationInteger.get(3), informationInteger.get(4));
        return newCalendar;
    }

}
