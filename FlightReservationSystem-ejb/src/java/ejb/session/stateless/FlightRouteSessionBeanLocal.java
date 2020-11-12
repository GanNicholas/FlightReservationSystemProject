/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AirportEntity;
import entity.FlightRouteEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.AirportODPairNotFoundException;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightRouteExistInOtherClassException;
import util.exception.FlightRouteIsNotMainRouteException;
import util.exception.FlightRouteODPairExistException;

/**
 *
 * @author sohqi
 */
@Local
public interface FlightRouteSessionBeanLocal {

    public Long createFlightRoute(String oIATA, String dIATA, String returnFlight) throws FlightRouteODPairExistException, AirportODPairNotFoundException;

    public boolean checkFlightRouteOD(String oIATA, String dIATA) throws FlightRouteODPairExistException;

    public List<FlightRouteEntity> viewListOfFlightRoute();

    public void DeleteFlightRoute(Long id) throws FlightRouteDoesNotExistException;

    public List<AirportEntity> getListOfAirportEntity();

    public FlightRouteEntity getFlightRoute(Long id) throws FlightRouteDoesNotExistException;

    public FlightRouteEntity getMainFlightRoute(Long id) throws FlightRouteDoesNotExistException, FlightRouteIsNotMainRouteException;

    public List<FlightRouteEntity> viewListOfAllFlightRoute();

    public void retrieveOD(String oIataCode) throws FlightRouteDoesNotExistException;

}
