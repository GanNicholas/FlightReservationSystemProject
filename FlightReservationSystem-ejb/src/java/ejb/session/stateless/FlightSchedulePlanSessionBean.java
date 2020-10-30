/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AirportEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.MultipleFlightScheduleEntity;
import entity.RecurringScheduleEntity;
import entity.RecurringWeeklyScheduleEntity;
import entity.SeatEntity;
import entity.SingleFlightScheduleEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightScheduleExistException;
import util.exception.FlightSchedulePlanDoesNotExistException;
import util.exception.FlightSchedulePlanIsEmptyException;

/**
 *
 * @author nickg
 */
@Stateless
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanSessionBeanRemote, FlightSchedulePlanSessionBeanLocal {

    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBean;

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    //for single/multiple flight schedule plan
    @Override
    public String createNonRecurrentFlightSchedulePlan(String flightNumber, List<GregorianCalendar> listOfDepartureDateTime, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover) throws FlightDoesNotExistException, FlightScheduleExistException {
        FlightEntity flight;
        try {
            flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight with flight number does not exist!");
        }

        FlightSchedulePlanEntity fsp;
        if (listOfDepartureDateTime.size() == 1) {
            fsp = new SingleFlightScheduleEntity(flightNumber, false, flight);
        } else {
            fsp = new MultipleFlightScheduleEntity(flightNumber, false, flight);
        }

        for (FareEntity fare : listOfFares) {
            fsp.getListOfFare().add(fare);
        }

        for (int i = 0; i < listOfDepartureDateTime.size(); i++) {
            GregorianCalendar departureDateTime = listOfDepartureDateTime.get(i);
            try {
                FlightScheduleEntity fe1 = flightScheduleSessionBean.createFlightSchedule(departureDateTime, flightDuration, fsp, flight);
                //link flight schedule to FSP
                fsp.getListOfFlightSchedule().add(fe1);
            } catch (FlightScheduleExistException ex) {
                throw new FlightScheduleExistException(ex.getMessage());
            }
        }

        for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
            em.persist(fs);
        }

        em.persist(fsp);
        em.flush();

        //return Flight entity
        FlightEntity returnFlight = null;
        if (createReturnFlightSchedule) {

            AirportEntity origin = flight.getFlightRoute().getDestinationLocation();
            AirportEntity destination = flight.getFlightRoute().getOriginLocation();
            try {
                FlightRouteEntity returnFlightRoute = (FlightRouteEntity) em.createNamedQuery("findFlightRoute").setParameter("origin", origin).setParameter("destination", destination).getSingleResult();
                returnFlight = (FlightEntity) em.createQuery("retrieveFlightUsingFlightRoute").setParameter("flightRoute", returnFlightRoute);
            } catch (NoResultException ex) {
                throw new FlightDoesNotExistException("Flight does not exist!");
            }

            //create return FSP
            FlightSchedulePlanEntity returnFSP;
            if (listOfDepartureDateTime.size() == 1) {
                returnFSP = new SingleFlightScheduleEntity(flightNumber, false, flight);
            } else {
                returnFSP = new MultipleFlightScheduleEntity(flightNumber, false, flight);
            }

            for (FareEntity fare : listOfFares) {
                returnFSP.getListOfFare().add(fare);
            }

            // GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                GregorianCalendar returnFlightDeparture = fs.getArrivalDateTime();
                returnFlightDeparture.add(GregorianCalendar.MINUTE, layover);
                try {
                    FlightScheduleEntity fe1 = flightScheduleSessionBean.createFlightSchedule(returnFlightDeparture, flightDuration, returnFSP, returnFlight);
                    //link flight schedule to FSP
                    returnFSP.getListOfFlightSchedule().add(fe1);
                    //get seatingplan - need get seating plan from aircraft config, add to flight schedule
                    List<SeatEntity> returnSeatingPlan = returnFlight.getAircraftConfig().getSeatingPlan();
                    for (SeatEntity seat : returnSeatingPlan) {
                        fe1.getSeatingPlan().add(seat);
                    }

                } catch (FlightScheduleExistException ex) {
                    throw new FlightScheduleExistException(ex.getMessage());
                }

            }

            for (FlightScheduleEntity fs : returnFSP.getListOfFlightSchedule()) {
                em.persist(fs);
            }

            em.persist(returnFSP);
            em.flush();

        }

        if (createReturnFlightSchedule && returnFlight != null) {
            return "Flight Schedule Plan created for " + flight.getFlightNumber() + " \n Return Flight Schedule Plan created for " + returnFlight.getFlightNumber();
        } else {
            return "Flight Schedule Plan created for " + flight.getFlightNumber();
        }
    }

    @Override
    public String createRecurrentFlightSchedulePlan(String flightNumber, GregorianCalendar departureDateTime, GregorianCalendar endDate, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover, Integer recurrency) throws FlightDoesNotExistException, FlightScheduleExistException {
        FlightEntity flight;
        try {
            flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber);
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight with flight number does not exist!");
        }

        FlightSchedulePlanEntity fsp;
        if (recurrency <= 7) {
            fsp = new RecurringScheduleEntity(flightNumber, false, flight, endDate, recurrency);
        } else { //MUST CHECK RECURRENCY 7 OR LESSER
            fsp = new RecurringWeeklyScheduleEntity(flightNumber, false, flight, endDate);
        }

        for (FareEntity fare : listOfFares) {
            fsp.getListOfFare().add(fare);
        }

        int dateCompare = departureDateTime.compareTo(endDate);

        while (dateCompare == -1) {
            try {
                FlightScheduleEntity fe1 = flightScheduleSessionBean.createFlightSchedule(departureDateTime, flightDuration, fsp, flight);
                //link flight schedule to FSP
                fsp.getListOfFlightSchedule().add(fe1);
            } catch (FlightScheduleExistException ex) {
                throw new FlightScheduleExistException(ex.getMessage());
            }

            departureDateTime.add(GregorianCalendar.DAY_OF_MONTH, recurrency);
            dateCompare = departureDateTime.compareTo(endDate);
        }

        for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
            em.persist(fs);
        }

        em.persist(fsp);
        em.flush();

        //return Flight entity
        FlightEntity returnFlight = null;
        if (createReturnFlightSchedule) {

            AirportEntity origin = flight.getFlightRoute().getDestinationLocation();
            AirportEntity destination = flight.getFlightRoute().getOriginLocation();
            try {
                FlightRouteEntity returnFlightRoute = (FlightRouteEntity) em.createNamedQuery("findFlightRoute").setParameter("origin", origin).setParameter("destination", destination).getSingleResult();
                returnFlight = (FlightEntity) em.createQuery("retrieveFlightUsingFlightRoute").setParameter("flightRoute", returnFlightRoute);
            } catch (NoResultException ex) {
                throw new FlightDoesNotExistException("Flight does not exist!");
            }

            //create return FSP
            FlightSchedulePlanEntity returnFSP;
            if (recurrency <= 7) {
                returnFSP = new RecurringScheduleEntity(flightNumber, false, flight, endDate, recurrency);
            } else { //MUST CHECK RECURRENCY 7 OR LESSER
                returnFSP = new RecurringWeeklyScheduleEntity(flightNumber, false, flight, endDate);
            }
            for (FareEntity fare : listOfFares) {
                returnFSP.getListOfFare().add(fare);
            }

            // GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                GregorianCalendar returnFlightDeparture = fs.getArrivalDateTime();
                returnFlightDeparture.add(GregorianCalendar.MINUTE, layover);
                try {
                    FlightScheduleEntity fe1 = flightScheduleSessionBean.createFlightSchedule(returnFlightDeparture, flightDuration, returnFSP, returnFlight);
                    //link flight schedule to FSP
                    returnFSP.getListOfFlightSchedule().add(fe1);
                    //get seatingplan - need get seating plan from aircraft config, add to flight schedule
                    List<SeatEntity> returnSeatingPlan = returnFlight.getAircraftConfig().getSeatingPlan();

                    for (SeatEntity seat : returnSeatingPlan) {
                        fe1.getSeatingPlan().add(seat);
                    }
                } catch (FlightScheduleExistException ex) {
                    throw new FlightScheduleExistException(ex.getMessage());
                }

            }

            for (FlightScheduleEntity fs : returnFSP.getListOfFlightSchedule()) {
                em.persist(fs);
            }

            returnFSP.setListOfFare(listOfFares);

            em.persist(returnFSP);
            em.flush();

        }

        if (createReturnFlightSchedule && returnFlight != null) {
            return "Flight Schedule Plan created for " + flight.getFlightNumber() + " \n Return Flight Schedule Plan created for " + returnFlight.getFlightNumber();
        } else {
            return "Flight Schedule Plan created for " + flight.getFlightNumber();
        }
    }

    @Override
    public List<FlightSchedulePlanEntity> viewAllFlightSchedulePlan() throws FlightSchedulePlanIsEmptyException {
        List<FlightSchedulePlanEntity> listOfFsp = em.createQuery("SELECT c FROM FlightSchedulePlanEntity c WHERE c.isDeleted = FALSE ORDER BY c.flightNumber ASC").getResultList();
        if (listOfFsp.isEmpty()) {
            throw new FlightSchedulePlanIsEmptyException("No flight schedule plan exists");
        }

        List<FlightSchedulePlanEntity> listOfAllFsp = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < listOfFsp.size(); i++) {
            if (listOfFsp.get(i).getReturnFlightSchedulePlan() != null && !listOfAllFsp.contains(listOfFsp.get(i).getReturnFlightSchedulePlan())) {

                listOfAllFsp.add(counter, listOfFsp.get(i));
                listOfAllFsp.add(counter + 1, listOfFsp.get(i).getReturnFlightSchedulePlan());
                counter += 2;
            } else if (!listOfAllFsp.contains(listOfFsp.get(i))) {
                listOfAllFsp.add(counter, listOfFsp.get(i));
                counter++;
            }
        }

        for (FlightSchedulePlanEntity fsp : listOfAllFsp) {
            sortFlightSchedule(fsp);
        }

        return listOfAllFsp;

    }

    private void sortFlightSchedule(FlightSchedulePlanEntity fsp) {
        List<FlightScheduleEntity> listOfFlightSchedule = fsp.getListOfFlightSchedule();

//        Collections.sort(listOfFlightSchedule, new Comparator<FlightScheduleEntity>(){
//        @Override
//        public int compare(GregorianCalendar d1, GregorianCalendar d2){
//            return d1.compareTo(d2);
//        }
//        });
//        
        for (int i = 0; i < fsp.getListOfFlightSchedule().size(); i++) {
            for (int j = i + 1; j < fsp.getListOfFlightSchedule().size(); j++) {
                FlightScheduleEntity fs1 = fsp.getListOfFlightSchedule().get(i);
                FlightScheduleEntity fs2 = fsp.getListOfFlightSchedule().get(j);
                if (fs1.getDepartureDateTime().compareTo(fs2.getDepartureDateTime()) == -1) {
                    FlightScheduleEntity temp = fsp.getListOfFlightSchedule().get(i);
                    fsp.getListOfFlightSchedule().set(i, fs2);
                    fsp.getListOfFlightSchedule().set(j, temp);
                }

            }
        }

    }

    @Override
    public FlightSchedulePlanEntity viewFlightSchedulePlan(String flightNumber) throws FlightSchedulePlanDoesNotExistException {
        try {
            FlightSchedulePlanEntity fsp = (FlightSchedulePlanEntity) em.createNamedQuery("queryFSPwithFlightNumber").setParameter("flightNum", flightNumber);
            fsp.getListOfFlightSchedule().size();
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                fs.getSeatingPlan().size();
            }

            FlightSchedulePlanEntity returnFsp = fsp.getReturnFlightSchedulePlan();
            returnFsp.getListOfFlightSchedule().size();
            for (FlightScheduleEntity fs : returnFsp.getListOfFlightSchedule()) {
                fs.getSeatingPlan().size();
            }

            fsp.getFlightEntity();
            fsp.getFlightEntity().getFlightRoute().getOriginLocation();
            fsp.getFlightEntity().getFlightRoute().getDestinationLocation();

            return fsp;
        } catch (NoResultException ex) {
            throw new FlightSchedulePlanDoesNotExistException("Flight schedule plan does not exist!");
        }

    }

}
