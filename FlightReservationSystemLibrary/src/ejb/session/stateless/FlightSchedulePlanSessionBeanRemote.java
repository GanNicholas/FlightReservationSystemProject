/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FareEntity;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightScheduleExistException;

/**
 *
 * @author nickg
 */
@Remote
public interface FlightSchedulePlanSessionBeanRemote {

    public String createNonRecurrentFlightSchedulePlan(String flightNumber, List<GregorianCalendar> listOfDepartureDateTime, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover) throws FlightDoesNotExistException, FlightScheduleExistException;

    public String createRecurrentFlightSchedulePlan(String flightNumber, GregorianCalendar departureDateTime, GregorianCalendar endDate, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover, Integer recurrency) throws FlightDoesNotExistException, FlightScheduleExistException;
}
