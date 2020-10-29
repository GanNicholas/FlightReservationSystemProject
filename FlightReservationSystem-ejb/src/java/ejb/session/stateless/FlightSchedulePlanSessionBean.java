/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.SeatEntity;
import entity.SingleFlightScheduleEntity;
import java.util.Calendar;
import static java.util.Calendar.MINUTE;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightScheduleExistException;

/**
 *
 * @author nickg
 */
@Stateless
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanSessionBeanRemote, FlightSchedulePlanSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    //for single flight schedule plan
    public String createSingleFlightSchedulePlan(String flightNumber, GregorianCalendar departureDateTime, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares) throws FlightDoesNotExistException, FlightScheduleExistException {
        FlightEntity flight;
        try {
            flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber);
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight with flight number does not exist!");
        }

        GregorianCalendar arrivalDateTime = (GregorianCalendar) departureDateTime.clone();
        arrivalDateTime.add(GregorianCalendar.MINUTE, flightDuration);
        try {
            this.checkSchedules(arrivalDateTime, departureDateTime, flightNumber);
        } catch (FlightScheduleExistException ex) {
            throw new FlightScheduleExistException(ex.getMessage());
        }

        // Create FSP
        FlightSchedulePlanEntity fsp = new SingleFlightScheduleEntity(flightNumber, false, flight);
        em.persist(fsp);
        em.flush();
        fsp.setListOfFare(listOfFares);

        //get seatingplan - need get seating plan from aircraft config, add to flight schedule
        List<SeatEntity> seatingPlan = flight.getAircraftConfig().getSeatingPlan();

        //create flight schedule
        FlightScheduleEntity flightSchedule = new FlightScheduleEntity(departureDateTime, flightDuration, fsp);
        flightSchedule.setSeatingPlan(seatingPlan);
        //link flight schedule to FSP
        fsp.getListOfFlightSchedule().add(flightSchedule);

        if (createReturnFlightSchedule) {
            Integer layover = 60; //1 hour

            //getting dates and time
            GregorianCalendar returnFlightDeparture = (GregorianCalendar) arrivalDateTime.clone();
            returnFlightDeparture.add(GregorianCalendar.MINUTE, layover);
            GregorianCalendar returnFlightArrive = (GregorianCalendar) returnFlightDeparture.clone();
            returnFlightArrive.add(GregorianCalendar.MINUTE, flightDuration);

            //return Flight entity
            FlightEntity returnFlight = flight.getReturnFlight();

            checkSchedules(returnFlightArrive, returnFlightDeparture, returnFlight.getFlightNumber());

            FlightSchedulePlanEntity returnFSP = new SingleFlightScheduleEntity(flightNumber, false, returnFlight);
            em.persist(returnFSP);
            em.flush();
            fsp.setListOfFare(listOfFares);

            //get seatingplan - need get seating plan from aircraft config, add to flight schedule
            List<SeatEntity> returnSeatingPlan = returnFlight.getAircraftConfig().getSeatingPlan();

            //create flight schedule
            FlightScheduleEntity returnFlightSchedule = new FlightScheduleEntity(returnFlightDeparture, flightDuration, returnFSP);
            returnFlightSchedule.setSeatingPlan(returnSeatingPlan);
            //link flight schedule to FSP
            returnFSP.getListOfFlightSchedule().add(returnFlightSchedule);
        }
        return "Flight Schedule Plan created for" + flight.getFlightNumber();
    }

    private void checkSchedules(Calendar arrivalDateTime, Calendar departureDateTime, String flightNumber) throws FlightScheduleExistException {

        List<FlightSchedulePlanEntity> listOfFlightSchedulePlan = em.createQuery("SELECT c FROM FlightSchedulePlanEntity c WHERE c.flightNumber =:flightNum").setParameter("flightNum", flightNumber).getResultList();
        for (FlightSchedulePlanEntity tempFSP : listOfFlightSchedulePlan) {
            List<FlightScheduleEntity> tempFlight = tempFSP.getListOfFlightSchedule();
            for (FlightScheduleEntity tempFlightSchedule : tempFlight) {
                GregorianCalendar tempFSPDepartureDate = tempFlightSchedule.getDepartureDateTime();
                GregorianCalendar tempFSPArrivalDate = (GregorianCalendar) tempFSPDepartureDate.clone();
                tempFSPArrivalDate.add(GregorianCalendar.MINUTE, tempFlightSchedule.getFlightDuration());

                int departValue1 = tempFSPDepartureDate.compareTo(departureDateTime);
                int arriveValue1 = tempFSPArrivalDate.compareTo(arrivalDateTime);
                if ((departValue1 > -1 && arriveValue1 > -1) || (departValue1 == -1 && arriveValue1 >= -1) || (departValue1 == -1 && arriveValue1 == -1)) {
                    throw new FlightScheduleExistException("Flight Schedule exists!");
                }

            }
        }

    }
}
