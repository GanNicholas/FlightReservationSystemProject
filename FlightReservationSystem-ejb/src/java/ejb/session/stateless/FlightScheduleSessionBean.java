/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AirportEntity;
import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.SeatEntity;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.FlightScheduleExistException;

/**
 *
 * @author nickg
 */
@Stateless
public class FlightScheduleSessionBean implements FlightScheduleSessionBeanRemote, FlightScheduleSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public FlightScheduleEntity createFlightSchedule(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight) throws FlightScheduleExistException {
        String flightNumber = flight.getFlightNumber();

        AirportEntity originAirport = flight.getFlightRoute().getOriginLocation();
        AirportEntity destinationAirport = flight.getFlightRoute().getDestinationLocation();

        int timeDiffHour = destinationAirport.getTimeZoneHour() - originAirport.getTimeZoneHour();
        int timeDiffMin = destinationAirport.getTimeZoneMin() - originAirport.getTimeZoneMin();

        GregorianCalendar arrivalDateTime = (GregorianCalendar) departureDateTime.clone();
        arrivalDateTime.add(GregorianCalendar.MINUTE, flightDuration);
        arrivalDateTime.add(GregorianCalendar.HOUR, timeDiffHour);
        arrivalDateTime.add(GregorianCalendar.MINUTE, timeDiffMin);

        this.checkSchedules(arrivalDateTime, departureDateTime, flightNumber);

        //create flight schedule
        FlightScheduleEntity flightSchedule = new FlightScheduleEntity(departureDateTime, flightDuration, fsp);
        //get seatingplan - need get seating plan from aircraft config, add to flight schedule
        List<SeatEntity> seatingPlan = flight.getAircraftConfig().getSeatingPlan();
        
        for(SeatEntity seat : flight.getAircraftConfig().getSeatingPlan()){
            flightSchedule.getSeatingPlan().add(seat);
        }

        return flightSchedule;
    }

    private void checkSchedules(Calendar arrivalDateTime, Calendar departureDateTime, String flightNumber) throws FlightScheduleExistException {

        List<FlightSchedulePlanEntity> listOfFlightSchedulePlan = em.createQuery("SELECT c FROM FlightSchedulePlanEntity c WHERE c.flightNumber =:flightNum AND c.isDeleted = TRUE").setParameter("flightNum", flightNumber).getResultList();
        for (FlightSchedulePlanEntity tempFSP : listOfFlightSchedulePlan) {
            List<FlightScheduleEntity> tempFlight = tempFSP.getListOfFlightSchedule();
            for (FlightScheduleEntity tempFlightSchedule : tempFlight) {
                GregorianCalendar tempFSPDepartureDate = tempFlightSchedule.getDepartureDateTime();
                GregorianCalendar tempFSPArrivalDate = (GregorianCalendar) tempFSPDepartureDate.clone();
                tempFSPArrivalDate.add(GregorianCalendar.MINUTE, tempFlightSchedule.getFlightDuration());

                int departValue1 = tempFSPDepartureDate.compareTo(departureDateTime);
                int arriveValue1 = tempFSPArrivalDate.compareTo(arrivalDateTime);

                //>-1 for both = the new schedule is within an existing schedule
                //=-1 for depart and <=-1 for arrive, new schedule either overlaps current schedule, or arrives in middle of current schedule
                //=1 for depart and <=-1 for arrive, new schedule starts within current schedule, and ends either before or after current arriveDate
                if ((departValue1 > -1 && arriveValue1 > -1) || (departValue1 == -1 && arriveValue1 <= -1) || (departValue1 == 1 && arriveValue1 <= -1)) {
                    throw new FlightScheduleExistException("Flight Schedule has conflict with existing flight schedule!");
                }

            }
        }

    }

}
