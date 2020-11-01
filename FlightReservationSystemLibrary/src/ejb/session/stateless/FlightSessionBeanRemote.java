/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightExistsException;
import util.exception.FlightRecordIsEmptyException;

/**
 *
 * @author nickg
 */
@Remote
public interface FlightSessionBeanRemote {

    public FlightEntity createFlightWithoutReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig) throws FlightExistsException;

    public FlightEntity createFlightWithReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig, FlightEntity returnFlight) throws FlightExistsException;

    public List<FlightEntity> viewAllFlights() throws FlightRecordIsEmptyException;

    public FlightEntity viewFlightDetails(String flightNumber) throws FlightDoesNotExistException;

    public void updateFlight(FlightEntity flight) throws FlightDoesNotExistException;

    public boolean deleteFlight(String flightNumber) throws FlightDoesNotExistException;

    public List<FlightEntity> listOfFlightRecords(String tripType, String departureAirport, String destinationAirport, String departureDate, String returnDate, String passenger);
}
