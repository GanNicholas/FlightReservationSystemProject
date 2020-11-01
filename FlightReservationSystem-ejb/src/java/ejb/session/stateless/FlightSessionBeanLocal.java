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
import javax.ejb.Local;
import util.exception.AircraftConfigurationNotExistException;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightExistsException;
import util.exception.FlightRecordIsEmptyException;
import util.exception.FlightRouteDoesNotExistException;

/**
 *
 * @author nickg
 */
@Local
public interface FlightSessionBeanLocal {

    public FlightEntity createFlightWithoutReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig) throws FlightExistsException, FlightRouteDoesNotExistException, AircraftConfigurationNotExistException;

    public FlightEntity createFlightWithReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig, String mainFlightNumber) throws FlightExistsException, FlightRouteDoesNotExistException, AircraftConfigurationNotExistException, FlightDoesNotExistException;

    public List<FlightEntity> viewAllFlights() throws FlightRecordIsEmptyException;

    public FlightEntity viewFlightDetails(String flightNumber) throws FlightDoesNotExistException;

    public void updateFlight(FlightEntity flight) throws FlightDoesNotExistException;

    public boolean deleteFlight(String flightNumber) throws FlightDoesNotExistException;

    public List<FlightEntity> listOfFlightRecords(String tripType, String departureAirport, String destinationAirport, String departureDate, String returnDate, String passenger);
}
