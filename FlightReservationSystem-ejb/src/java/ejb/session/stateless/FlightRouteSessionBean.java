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
import util.exception.AirportODPairNotFoundException;
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
    
    public Long createFlightRoute(String oIATA, String dIATA, String returnFlight) throws FlightRouteODPairExistException, AirportODPairNotFoundException {
        FlightRouteEntity flightRoute = new FlightRouteEntity();
        try {
            boolean frExistInDB = checkFlightRouteOD(oIATA, dIATA);
            //check origin exist in airportentity
            if (!frExistInDB) {
                
                Query oQuery = em.createQuery("SELECT a FROM AirportEntity a where a.iataAirportCode=:origin").setParameter("origin", oIATA);
                AirportEntity oAirport = (AirportEntity) oQuery.getSingleResult();
                Query dQuery = em.createQuery("SELECT a FROM AirportEntity a where a.iataAirportCode=:origin").setParameter("origin", dIATA);
                AirportEntity dAirport = (AirportEntity) dQuery.getSingleResult();
                flightRoute.setOriginLocation(oAirport);
                flightRoute.setDestinationLocation(dAirport);
                flightRoute.setIsDeleted(false);
                em.persist(flightRoute);
                em.flush();
                if (returnFlight.equalsIgnoreCase("Yes")) {
                    FlightRouteEntity tempFr = new FlightRouteEntity();
                    tempFr.setOriginLocation(dAirport);
                    tempFr.setDestinationLocation(oAirport);
                    tempFr.setIsDeleted(false);
                    tempFr.setReturnRoute(flightRoute);
                    em.persist(tempFr);
                    em.flush();
                    flightRoute.setReturnRoute(tempFr);
                }
                
            }
        } catch (FlightRouteODPairExistException ex) {
            throw new FlightRouteODPairExistException("Flight route O-D already exist");
        } catch (NoResultException ex) {
            throw new AirportODPairNotFoundException("Invalid O-D pair");
        }
        
        return flightRoute.getFlightRouteId();
    }
    
    public boolean checkFlightRouteOD(String oIATA, String dIATA) throws FlightRouteODPairExistException {
        try {
            
            Query query = em.createQuery("SELECT a FROM FlightRouteEntity a WHERE a.originLocation.iataAirportCode = :original AND a.destinationLocation.iataAirportCode=:destination").setParameter("original", oIATA).setParameter("destination", dIATA);
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
    
    public List<AirportEntity> getListOfAirportEntity() {
        
        Query query = em.createQuery("SELECT a FROM AirportEntity a ");
        List<AirportEntity> listOfAirport = query.getResultList();
        return listOfAirport;
    }
    
}
