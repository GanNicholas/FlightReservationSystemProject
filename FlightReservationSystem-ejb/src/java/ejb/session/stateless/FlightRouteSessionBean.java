/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.AirportEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightRouteExistInOtherClassException;
import util.exception.FlightRouteODPairExistException;

/**
 *
 * @author sohqi
 */
@Stateless
public class FlightRouteSessionBean implements FlightRouteSessionBeanRemote, FlightRouteSessionBeanLocal {
    
    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    public Long createFlightRoute(FlightRouteEntity frEntity) throws FlightRouteODPairExistException {
        FlightRouteEntity flightRoute = new FlightRouteEntity();
        try {
            boolean frExistInDB = checkFlightRouteOD(frEntity);
            FlightRouteEntity returnFlight = frEntity.getReturnRoute();
            
            AirportEntity origin = frEntity.getOriginLocation();
            AirportEntity destination = frEntity.getDestinationLocation();
            AirportEntity tempOairportEntity = null;
            AirportEntity tempDairportEntity = null;
            //check origin exist in airportentity
            System.out.println("Origin : " + origin.getIataAirportCode() + "|| " + "destination: " + destination.getIataAirportCode());
            if (!frExistInDB) {
                Query airportOriginQuery = em.createQuery("SELECT a FROM AirportEntity a WHERE a.iataAirportCode = :original").setParameter("original", origin.getIataAirportCode());
                List<AirportEntity> originAirportEntity = airportOriginQuery.getResultList();
                if (originAirportEntity.isEmpty()) {
                    em.persist(origin);
                } else {
                    origin = originAirportEntity.get(0);
                }

                //check destination exist in airport entity
                Query airportDesQuery = em.createQuery("SELECT a from AirportEntity a WHERE a.iataAirportCode = :destination").setParameter("destination", destination.getIataAirportCode());
                List<AirportEntity> desAirportEntity = airportDesQuery.getResultList();
                if (desAirportEntity.isEmpty()) {
                    em.persist(destination);
                } else {
                    destination = desAirportEntity.get(0);
                }
                
                if (returnFlight != null) {
                    FlightRouteEntity tempReturnFlight = new FlightRouteEntity();
                    tempReturnFlight.setDestinationLocation(origin);
                    tempReturnFlight.setOriginLocation(destination);
                    tempReturnFlight.setIsDeleted(false);
                    tempReturnFlight.setReturnRoute(null);
                    em.persist(tempReturnFlight);
                    em.flush();
                    flightRoute.setReturnRoute(tempReturnFlight);
                }
                em.flush();
                flightRoute.setIsDeleted(false);
                flightRoute.setDestinationLocation(destination);
                flightRoute.setOriginLocation(origin);
                flightRoute.setReturnRoute(flightRoute);
                em.persist(flightRoute);
                em.flush();
            }
            
        } catch (FlightRouteODPairExistException ex) {
            throw new FlightRouteODPairExistException("Flight route O-D already exist");
        }
        
        return flightRoute.getFlightRouteId();
    }
    
    public boolean checkFlightRouteOD(FlightRouteEntity frEntity) throws FlightRouteODPairExistException {
        try {
            Query query = em.createQuery("SELECT a FROM FlightRouteEntity a WHERE a.originLocation.iataAirportCode = :original AND a.destinationLocation.iataAirportCode=:destination").setParameter("original", frEntity.getOriginLocation().getIataAirportCode()).setParameter("destination", frEntity.getDestinationLocation().getIataAirportCode());
            FlightRouteEntity tempFrEntity = (FlightRouteEntity) query.getSingleResult();
            throw new FlightRouteODPairExistException("Flight route O-D already exist");
        } catch (NoResultException ex) {
            return false;
        }
        
    }
    
    public List<FlightRouteEntity> viewListOfFlightRoute() {
        
        Query query = em.createQuery("SELECT f FROM FlightRouteEntity f");
        List<FlightRouteEntity> listOfFlightRoute = query.getResultList();
        listOfFlightRoute.size();
        return listOfFlightRoute;
    }
    
    public boolean checkFlightRouteUsedByOthers(Long id) {
        
        try {
            Query query = em.createQuery("SELECT f FROM FlightEntity f where f.flightRoute.flightRouteId = :flightRouteId").setParameter("flightRouteId", id);
            FlightEntity flight = (FlightEntity) query.getSingleResult();
            return true;
        } catch (NoResultException ex) {
            return false;
        }
        
    }
    
    public boolean DeleteFlightRoute(Long id) throws FlightRouteDoesNotExistException, FlightRouteExistInOtherClassException {
        
        try {
            Query query = em.createQuery("SELECT f FROM FlightRouteEntity f where f.flightRouteId = :flightRouteId").setParameter("flightRouteId", id);
            FlightRouteEntity flightRoute = (FlightRouteEntity) query.getSingleResult();
            if (checkFlightRouteUsedByOthers(id)) {
                throw new FlightRouteExistInOtherClassException("Flight route is used by other flight record");
            } else {
                em.remove(flightRoute);
                return true;
            }
        } catch (NoResultException ex) {
            throw new FlightRouteDoesNotExistException("Invalid flight route id");
        }
        
    }
    
}
