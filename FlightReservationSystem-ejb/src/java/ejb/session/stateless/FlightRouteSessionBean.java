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

    @Override
    public Long createFlightRoute(String oIATA, String dIATA, String returnFlight) throws FlightRouteODPairExistException, AirportODPairNotFoundException {
        FlightRouteEntity flightRoute = new FlightRouteEntity();
        System.out.println("-------------------------O:" + oIATA + "---------------dIATA  : " + dIATA);
        try {
            boolean frExistInDB = checkFlightRouteOD(oIATA, dIATA);
            //check origin exist in airportentity
            if (!frExistInDB) {

                Query oQuery = em.createQuery("SELECT a FROM AirportEntity a where a.iataAirportCode=:origin").setParameter("origin", oIATA);
                AirportEntity oAirport = (AirportEntity) oQuery.getSingleResult();
                System.out.println("-----------------OAir:" + oAirport.getAirportId());
                Query dQuery = em.createQuery("SELECT a FROM AirportEntity a where a.iataAirportCode=:origin").setParameter("origin", dIATA);
                AirportEntity dAirport = (AirportEntity) dQuery.getSingleResult();
                System.out.println("-----------------OAir:" + dAirport.getAirportId());
                flightRoute.setOriginLocation(oAirport);
                flightRoute.setDestinationLocation(dAirport);
                flightRoute.setIsDeleted(false);
                flightRoute.setMainRoute(true);
                em.persist(flightRoute);
                em.flush();
                if (returnFlight.equalsIgnoreCase("Yes")) {
                    FlightRouteEntity tempFr = new FlightRouteEntity();
                    tempFr.setOriginLocation(dAirport);
                    tempFr.setDestinationLocation(oAirport);
                    tempFr.setIsDeleted(false);
                    tempFr.setReturnRoute(flightRoute);
                    tempFr.setMainRoute(false);
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

    @Override
    public boolean checkFlightRouteOD(String oIATA, String dIATA) throws FlightRouteODPairExistException {
        try {

            Query query = em.createQuery("SELECT a FROM FlightRouteEntity a WHERE a.originLocation.iataAirportCode = :original AND a.destinationLocation.iataAirportCode=:destination").setParameter("original", oIATA).setParameter("destination", dIATA);
            FlightRouteEntity tempFrEntity = (FlightRouteEntity) query.getSingleResult();
            throw new FlightRouteODPairExistException("Flight route O-D already exist");
        } catch (NoResultException ex) {
            return false;
        }

    }

    @Override
    public List<FlightRouteEntity> viewListOfFlightRoute() {

        Query query = em.createQuery("SELECT f FROM FlightRouteEntity f WHERE f.mainRoute=:mainRoute AND f.isDeleted =:isDeleted").setParameter("mainRoute", true).setParameter("isDeleted", false);
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

    @Override
    public void DeleteFlightRoute(Long id) throws FlightRouteDoesNotExistException {

        try {
            Query query = em.createQuery("SELECT f FROM FlightRouteEntity f where f.flightRouteId = :flightRouteId and f.isDeleted=:isDeleted").setParameter("flightRouteId", id).setParameter("isDeleted", false);
            FlightRouteEntity flightRoute = (FlightRouteEntity) query.getSingleResult();
            FlightRouteEntity returnFlightRoute = null;
            if (flightRoute.getReturnRoute() != null) {
                Query queryReturnFlight = em.createQuery("SELECT f FROM FlightRouteEntity f where f.flightRouteId = :flightRouteId and f.isDeleted=:isDeleted").setParameter("flightRouteId", flightRoute.getReturnRoute().getFlightRouteId()).setParameter("isDeleted", false);
                returnFlightRoute = (FlightRouteEntity) queryReturnFlight.getSingleResult();
            }

            if (checkFlightRouteUsedByOthers(id)) {
                if (flightRoute.getReturnRoute() != null) {
                    returnFlightRoute.setIsDeleted(true);
                }
                flightRoute.setIsDeleted(true);
            } else {
                if (flightRoute.getReturnRoute() != null) {
                    em.remove(returnFlightRoute);
                }
                em.remove(flightRoute);
            }
        } catch (NoResultException ex) {
            throw new FlightRouteDoesNotExistException("Invalid flight route id");
        }

    }

    @Override
    public List<AirportEntity> getListOfAirportEntity() {

        Query query = em.createQuery("SELECT a FROM AirportEntity a GROUP BY a.airportName ORDER BY a.country ASC");
        List<AirportEntity> listOfAirport = query.getResultList();
        return listOfAirport;
    }

    @Override
    public FlightRouteEntity getFlightRoute(Long id) throws FlightRouteDoesNotExistException {
        FlightRouteEntity fr = em.find(FlightRouteEntity.class, id);
        if (fr == null) {
            throw new FlightRouteDoesNotExistException("Flight route does not exist!");
        } else {
            fr.getDestinationLocation();
            fr.getOriginLocation();
            fr.getReturnRoute();
            return fr;
        }
    }

}
