/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightRouteEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.FlightRouteODPairExistException;

/**
 *
 * @author sohqi
 */
@Local
public interface FlightRouteSessionBeanLocal {

    public Long createFlightRoute(FlightRouteEntity frEntity) throws FlightRouteODPairExistException;

    public boolean checkFlightRouteOD(FlightRouteEntity frEntity) throws FlightRouteODPairExistException;

    public List<FlightRouteEntity> viewListOfFlightRoute();

}
