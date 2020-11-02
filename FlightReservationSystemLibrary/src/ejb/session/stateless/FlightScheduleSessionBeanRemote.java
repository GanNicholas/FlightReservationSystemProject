/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightScheduleExistException;

/**
 *
 * @author nickg
 */
@Remote
public interface FlightScheduleSessionBeanRemote {

    public FlightScheduleEntity createFlightSchedule(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight) throws FlightScheduleExistException;

    public List<FlightScheduleEntity> listOfODQuery(String origin, String destination, Date departureDate, Date endDate);

    public List<FlightScheduleEntity> listOfConnectingFlightRecords(Date departureDate, Date endDate);
}
