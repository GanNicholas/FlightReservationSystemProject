/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FareEntity;
import entity.FlightSchedulePlanEntity;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Local;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightScheduleExistException;
import util.exception.FlightSchedulePlanDoesNotExistException;
import util.exception.FlightSchedulePlanIsEmptyException;

/**
 *
 * @author nickg
 */
@Local
public interface FlightSchedulePlanSessionBeanLocal {

    public String createNonRecurrentFlightSchedulePlan(String flightNumber, List<GregorianCalendar> listOfDepartureDateTime, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover) throws FlightDoesNotExistException, FlightScheduleExistException;

    public String createRecurrentFlightSchedulePlan(String flightNumber, GregorianCalendar departureDateTime, GregorianCalendar endDate, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover, Integer recurrency) throws FlightDoesNotExistException, FlightScheduleExistException;

    public List<FlightSchedulePlanEntity> viewAllFlightSchedulePlan() throws FlightSchedulePlanIsEmptyException;

    public FlightSchedulePlanEntity viewFlightSchedulePlan(String flightNumber, Long fspId) throws FlightSchedulePlanDoesNotExistException;

}
