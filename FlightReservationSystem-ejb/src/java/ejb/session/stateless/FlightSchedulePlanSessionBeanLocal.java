/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Local;
import util.exception.FareCannotBeDeletedException;
import util.exception.FareDoesNotExistException;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightScheduleDoesNotExistException;
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

    public FlightSchedulePlanEntity viewFlightSchedulePlan(Long fspId) throws FlightSchedulePlanDoesNotExistException;

    public void updateSingleFspDate(FlightEntity flight, GregorianCalendar newDepartureDateTime, FlightSchedulePlanEntity specificFsp, FlightScheduleEntity fs) throws FlightSchedulePlanDoesNotExistException, FlightScheduleExistException, FlightDoesNotExistException;

    public void mergeFSPForFare(FlightSchedulePlanEntity fsp) throws FlightSchedulePlanDoesNotExistException;

    public void mergeFSPWithNewFlightDuration(int newFlightDuration, FlightSchedulePlanEntity fsp, GregorianCalendar updatedDepartureDateTime, FlightScheduleEntity fs) throws FlightSchedulePlanDoesNotExistException, FlightScheduleExistException, FlightDoesNotExistException;

    public void addNewFlightSchedule(GregorianCalendar departureDateTime, FlightSchedulePlanEntity fsp) throws FlightSchedulePlanDoesNotExistException, FlightScheduleExistException;

    public FlightScheduleEntity getFlightScheduleUsingID(Long fsId) throws FlightScheduleDoesNotExistException;

    public void deleteFlightSchedule(Long fsId) throws FlightScheduleDoesNotExistException;

    public FareEntity retrieveFare(Long fareId) throws FareDoesNotExistException;

    public void mergeFare(FareEntity fare) throws FareDoesNotExistException;

    public void deleteFare(Long fareId, FlightSchedulePlanEntity fsp) throws FareDoesNotExistException, FlightSchedulePlanDoesNotExistException, FareCannotBeDeletedException;

    public void updateRecurrentFSP(String flightNumber, GregorianCalendar departureDateTime, GregorianCalendar endDate, Integer flightDuration, Integer recurrency, FlightSchedulePlanEntity currentFsp) throws FlightDoesNotExistException, FlightScheduleExistException, FlightSchedulePlanDoesNotExistException;

    public String deleteFsp(Long fspId) throws FlightSchedulePlanDoesNotExistException, FareDoesNotExistException, FareCannotBeDeletedException;

    public List<FlightSchedulePlanEntity> getFlightSchedulePlanForFlight(String flightNumber) throws FlightSchedulePlanIsEmptyException;

}
