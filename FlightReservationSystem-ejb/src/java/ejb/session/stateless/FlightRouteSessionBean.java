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
import util.exception.FlightRouteIsNotMainRouteException;
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

        try {
            boolean frExistInDB = checkFlightRouteOD(oIATA, dIATA);
            //check origin exist in airportentity
            if (!frExistInDB) {

                Query oQuery = em.createQuery("SELECT a FROM AirportEntity a where a.iataAirportCode=:origin").setParameter("origin", oIATA);
                AirportEntity oAirport = (AirportEntity) oQuery.getSingleResult();
                //System.out.println("-----------------OAir:" + oAirport.getAirportId());
                Query dQuery = em.createQuery("SELECT a FROM AirportEntity a where a.iataAirportCode=:origin").setParameter("origin", dIATA);
                AirportEntity dAirport = (AirportEntity) dQuery.getSingleResult();
                //System.out.println("-----------------OAir:" + dAirport.getAirportId());
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

        Query query = em.createQuery("SELECT f FROM FlightRouteEntity f WHERE f.isDeleted =:isDeleted").setParameter("isDeleted", false);
        List<FlightRouteEntity> listOfFlightRoute = query.getResultList();
        listOfFlightRoute.size();
        return listOfFlightRoute;
    }

    public boolean checkFlightRouteUsedByOthers(Long id) {

        Query query = em.createQuery("SELECT f FROM FlightEntity f where f.flightRoute.flightRouteId = :flightRouteId").setParameter("flightRouteId", id);
        List<FlightEntity> flight = query.getResultList();
        if (!flight.isEmpty()) {
            return true;
        } else {
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
            Query usedByOther = em.createQuery("SELECT s FROM FlightEntity s WHERE s.flightRoute=:flightRoute").setParameter("flightRoute", flightRoute);
            List<FlightEntity> f = usedByOther.getResultList();
            if (f.isEmpty() || f == null) {
                if (flightRoute.getReturnRoute() != null) {
                    returnFlightRoute.setMainRoute(true);
                    returnFlightRoute.setReturnRoute(null);
                    flightRoute.setReturnRoute(null);
                }
                em.remove(flightRoute);
            } else {
                if (flightRoute.getReturnRoute() != null) {
                    returnFlightRoute.setMainRoute(true);
                    returnFlightRoute.setReturnRoute(null);
                    flightRoute.setReturnRoute(null);
                }
                flightRoute.setIsDeleted(true);
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

    @Override
    public FlightRouteEntity getMainFlightRoute(Long id) throws FlightRouteDoesNotExistException, FlightRouteIsNotMainRouteException {
        FlightRouteEntity fr = em.find(FlightRouteEntity.class, id);
        if (fr == null) {
            throw new FlightRouteDoesNotExistException("Flight route does not exist!");
        } else {

            if (fr.isIsDeleted() || !fr.isMainRoute()) {
                throw new FlightRouteIsNotMainRouteException("Flight route chosen is not the main route!");
            } else {
                fr.getDestinationLocation();
                fr.getOriginLocation();
                fr.getReturnRoute();
                return fr;
            }
        }
    }

    @Override
    public List<FlightRouteEntity> viewListOfAllFlightRoute() {

        Query query = em.createQuery("SELECT f FROM FlightRouteEntity f WHERE f.isDeleted =:isDeleted").setParameter("isDeleted", false);
        List<FlightRouteEntity> listOfFlightRoute = query.getResultList();
        listOfFlightRoute.size();
        return listOfFlightRoute;
    }

    @Override
    public void retrieveOD(String oIataCode) throws FlightRouteDoesNotExistException {

        Query q1 = em.createQuery("SELECT fr from FlightRouteEntity fr WHERE fr.originLocation.iataAirportCode =:oIataCode")
                .setParameter("oIataCode", oIataCode);
        List< FlightRouteEntity> fr = q1.getResultList();
        if (fr.isEmpty()) {
            throw new FlightRouteDoesNotExistException();
        }

    }

    @Override
    public FlightRouteEntity getFlightRouteOD(String oIATA, String dIATA) throws FlightRouteODPairExistException {
        try {
            Query query = em.createQuery("SELECT a from FlightRouteEntity a WHERE a.originLocation.iataAirportCode =:origin AND a.destinationLocation.iataAirportCode =:destination")
                    .setParameter("origin", oIATA).setParameter("destination", dIATA);
            FlightRouteEntity tempFrEntity = (FlightRouteEntity) query.getSingleResult();
            return tempFrEntity;
        } catch (NoResultException ex) {
            throw new FlightRouteODPairExistException();
        }

    }

}
