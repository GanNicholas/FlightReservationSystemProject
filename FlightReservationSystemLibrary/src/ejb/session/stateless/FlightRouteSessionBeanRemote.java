/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AirportEntity;
import entity.FlightRouteEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AirportODPairNotFoundException;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightRouteExistInOtherClassException;
import util.exception.FlightRouteODPairExistException;

/**
 *
 * @author sohqi
 */
@Remote
public interface FlightRouteSessionBeanRemote {

    public Long createFlightRoute(String oIATA, String dIATA, String returnFlight) throws FlightRouteODPairExistException, AirportODPairNotFoundException;

    public boolean checkFlightRouteOD(String oIATA, String dIATA) throws FlightRouteODPairExistException;

    public List<FlightRouteEntity> viewListOfFlightRoute();

    public boolean DeleteFlightRoute(Long id) throws FlightRouteDoesNotExistException, FlightRouteExistInOtherClassException;

    public List<AirportEntity> getListOfAirportEntity();
}