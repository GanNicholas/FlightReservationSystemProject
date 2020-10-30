/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import java.util.GregorianCalendar;
import javax.ejb.Local;
import util.exception.FlightScheduleExistException;

/**
 *
 * @author nickg
 */
@Local
public interface FlightScheduleSessionBeanLocal {
    
    public FlightScheduleEntity createFlightSchedule(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight) throws FlightScheduleExistException;
    
}
