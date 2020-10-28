/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import javax.ejb.Remote;
import util.exception.FlightExistsException;

/**
 *
 * @author nickg
 */
@Remote
public interface FlightSessionBeanRemote {

    public FlightEntity createFlightWithoutReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig) throws FlightExistsException;

    public FlightEntity createFlightWithReturnFlight(String flightNumber, FlightRouteEntity flightRoute, AircraftConfigurationEntity aircraftConfig, FlightEntity returnFlight) throws FlightExistsException;

}
