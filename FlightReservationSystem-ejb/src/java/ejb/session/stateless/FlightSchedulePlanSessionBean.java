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
import static java.util.Calendar.MINUTE;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.FlightDoesNotExistException;

/**
 *
 * @author nickg
 */
@Stateless
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanSessionBeanRemote, FlightSchedulePlanSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    //for single/multiple flight schedule plan
    public String createSingleFlightSchedulePlan(String flightNumber, Integer numSchedule, GregorianCalendar departureDateTime, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares) throws FlightDoesNotExistException {
        FlightEntity flight;
        try {
            flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber);
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight with flight number does not exist!");
        }

        if (numSchedule == 1) {
            FlightSchedulePlanEntity fsp = new SingleFlightScheduleEntity(flightNumber, false, flight);
            em.persist(fsp);
            em.flush();
            fsp.setListOfFare(listOfFares);
            GregorianCalendar arrivalDateTime = (GregorianCalendar) departureDateTime.clone();
            
            //check if new flightschedule exists in database
            List<FlightSchedulePlanEntity> listOfFlightSchedulePlan = em.createQuery("SELECT c FROM FlightSchedulePlanEntity c WHERE c.flightNumber =: flightNum").setParameter("flightNum", flightNumber).getResultList();
            for(FlightSchedulePlanEntity tempFSP : listOfFlightSchedulePlan){
                
            }
            
            //get seatingplan - need get seating plan from aircraft config, add to flight schedule
            List<SeatEntity> seatingPlan = flight.getAircraftConfig().getSeatingPlan();
            
            //create flight schedule
            FlightScheduleEntity flightSchedule = new FlightScheduleEntity(departureDateTime, flightDuration, fsp);
            //link flight schedule

            arrivalDateTime.add(MINUTE, flightDuration);

            if (createReturnFlightSchedule) {
                Integer layover = 60; //1 hour
                GregorianCalendar returnFlightDeparture = (GregorianCalendar) arrivalDateTime.clone();
                returnFlightDeparture.add(MINUTE , layover);
                
                
            } else { // means create one flight schedule only

            }

        } else {

        }
    }
}
