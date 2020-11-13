/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightBundle;
import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Local;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightScheduleExistException;

/**
 *
 * @author nickg
 */
@Local
public interface FlightScheduleSessionBeanLocal {

    public FlightScheduleEntity createFlightSchedule(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight) throws FlightScheduleExistException;

    public boolean checkFlightScheduleSeats(FlightScheduleEntity fs);

    public FlightScheduleEntity updateFlightSchedule(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight, FlightScheduleEntity currentFs) throws FlightScheduleExistException;

    public FlightScheduleEntity updateReccurentFlightSchedule(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight) throws FlightScheduleExistException;

    public List<FlightBundle> listOfConnectingFlightRecords(GregorianCalendar departureDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException;

    public List<FlightBundle> listOfConnectingFlightRecordsAftThreeDays(GregorianCalendar departureDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException;

    public List<FlightBundle> listOfConnectingFlightRecordsLessThreeDays(GregorianCalendar departureDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException;

    public List<FlightBundle> getDirectFlight(GregorianCalendar actual, GregorianCalendar changeDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException;

    public List<FlightBundle> getDirectFlightUnmanaged(GregorianCalendar gStart, GregorianCalendar gEnd, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException;

    public List<FlightBundle> listOfConnectingFlightRecordsAftThreeDaysUnmanaged(GregorianCalendar actual, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException;

    public List<FlightBundle> listOfConnectingFlightRecordsLessThreeDaysUnmanaged(GregorianCalendar actualDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException;

    public List<FlightBundle> listOfConnectingFlightRecordsUnmanaged(GregorianCalendar departureDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException;
}
