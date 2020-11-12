/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.FlightReservationSessionBeanLocal;
import ejb.session.stateless.FlightRouteSessionBeanLocal;
import ejb.session.stateless.FlightScheduleSessionBeanLocal;
import entity.FareEntity;
import entity.FlightBundle;
import entity.FlightEntity;
import entity.FlightReservationEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.IndividualFlightReservationEntity;
import entity.PartnerEntity;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.enumeration.CabinClassType;
import util.exception.AccessFromWrongPortalException;
import util.exception.CustomerHasNoReservationException;
import util.exception.CustomerLoginInvalid;
import util.exception.FlightReservationDoesNotExistException;
import util.exception.FlightRouteDoesNotExistException;

/**
 *
 * @author nickg
 */
@WebService(serviceName = "PartnerReservationSystem")
@Stateless
public class PartnerReservationSystem {

    @EJB
    private FlightRouteSessionBeanLocal flightRouteSessionBean;

    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBean;

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;

    @EJB
    private FlightReservationSessionBeanLocal flightReservationSessionBean;

    @WebMethod(operationName = "retrieveListOfReservation")
    public List<FlightReservationEntity> retrieveListOfReservation(@WebParam(name = "custId") Long custId) throws CustomerHasNoReservationException {
        List<FlightReservationEntity> listOfFlightReservation = flightReservationSessionBean.retrieveListOfUnmanagedReservation(custId);
        for (FlightReservationEntity fr : listOfFlightReservation) {
            for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
                indivFr.setFlightReservation(null);
            }
        }
        return listOfFlightReservation;
    }

    @WebMethod(operationName = "loginPartner")
    public PartnerEntity loginPartner(String loginId, String loginPw) throws CustomerLoginInvalid, AccessFromWrongPortalException {
        PartnerEntity partner = (PartnerEntity) customerSessionBean.customerLoginUnmanaged(loginId, loginPw);

        return partner;
    }

    @WebMethod(operationName = "retrieveIndividualFlightReservation")
    public FlightReservationEntity retrieveIndividualFlightReservation(Long frId) throws FlightReservationDoesNotExistException {
        FlightReservationEntity fr = flightReservationSessionBean.getIndividualFlightReservationUnmanaged(frId);

        for (IndividualFlightReservationEntity indivFr : fr.getListOfIndividualFlightRes()) {
            indivFr.setFlightReservation(null);
            //flightscheudle - flight || flight route - flight route || flight - flight || flightschedule - flightscheduleplan || 
            FlightScheduleEntity fs = indivFr.getFlightSchedule();
            FlightSchedulePlanEntity fsp = fs.getFlightSchedulePlan();
            fsp.getListOfFlightSchedule().clear(); // clear all associations to list of FS
            fsp.setReturnFlightSchedulePlan(null);

            FlightEntity flight = fsp.getFlightEntity();
            flight.setReturnFlight(null);
            flight.getListOfFlightSchedulePlan().clear(); // clear all associations to list of FSP

            FlightRouteEntity flightRoute = flight.getFlightRoute();
            flightRoute.setReturnRoute(null);

        }

        return fr;

    }

    @WebMethod(operationName = "getDirectFlight")
    public List<FlightBundle> getDirectFlight(GregorianCalendar gStart, GregorianCalendar gEnd, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {

        List<FlightBundle> listOfflightBundle = flightScheduleSessionBean.getDirectFlightUnmanaged(gStart, gEnd, departureAirport, destinationAirport);
        for (FlightBundle currentFb : listOfflightBundle) {
            FlightScheduleEntity fs1 = currentFb.getDepartOne();

            FlightScheduleEntity fs2 = currentFb.getDepartTwo();

            FlightScheduleEntity fs3 = currentFb.getDepartThree();

            FlightScheduleEntity returnFs1 = currentFb.getReturnOne();

            FlightScheduleEntity returnFs2 = currentFb.getReturnTwo();

            FlightScheduleEntity returnFs3 = currentFb.getReturnThree();

            fs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

        }

        return listOfflightBundle;

    }

    @WebMethod(operationName = "retrieveOD")
    public void retrieveOD(@WebParam(name = "oIataCode") String oIataCode) throws FlightRouteDoesNotExistException {
        flightRouteSessionBean.retrieveOD(oIataCode);
    }

    @WebMethod(operationName = "listOfConnectingFlightRecordsAftThreeDays")
    public List<FlightBundle> listOfConnectingFlightRecordsAftThreeDays(Date actual, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {

        List<FlightBundle> listOfflightBundle = flightScheduleSessionBean.listOfConnectingFlightRecordsAftThreeDaysUnmanaged(actual, departureAirport, destinationAirport);
        for (FlightBundle currentFb : listOfflightBundle) {
            FlightScheduleEntity fs1 = currentFb.getDepartOne();

            FlightScheduleEntity fs2 = currentFb.getDepartTwo();

            FlightScheduleEntity fs3 = currentFb.getDepartThree();

            FlightScheduleEntity returnFs1 = currentFb.getReturnOne();

            FlightScheduleEntity returnFs2 = currentFb.getReturnTwo();

            FlightScheduleEntity returnFs3 = currentFb.getReturnThree();

            fs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

        }

        return listOfflightBundle;

    }

    @WebMethod(operationName = "listOfConnectingFlightRecordsLessThreeDays")
    public List<FlightBundle> listOfConnectingFlightRecordsLessThreeDays(Date actualDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {
        List<FlightBundle> listOfflightBundle = flightScheduleSessionBean.listOfConnectingFlightRecordsLessThreeDaysUnmanaged(actualDate, departureAirport, destinationAirport);

        for (FlightBundle currentFb : listOfflightBundle) {
            FlightScheduleEntity fs1 = currentFb.getDepartOne();

            FlightScheduleEntity fs2 = currentFb.getDepartTwo();

            FlightScheduleEntity fs3 = currentFb.getDepartThree();

            FlightScheduleEntity returnFs1 = currentFb.getReturnOne();

            FlightScheduleEntity returnFs2 = currentFb.getReturnTwo();

            FlightScheduleEntity returnFs3 = currentFb.getReturnThree();

            fs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

        }

        return listOfflightBundle;
    }

    @WebMethod(operationName = "listOfConnectingFlightRecords")
    public List<FlightBundle> listOfConnectingFlightRecords(Date departureDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {
        List<FlightBundle> listOfflightBundle = flightScheduleSessionBean.listOfConnectingFlightRecordsUnmanaged(departureDate, departureAirport, destinationAirport);

        for (FlightBundle currentFb : listOfflightBundle) {
            FlightScheduleEntity fs1 = currentFb.getDepartOne();

            FlightScheduleEntity fs2 = currentFb.getDepartTwo();

            FlightScheduleEntity fs3 = currentFb.getDepartThree();

            FlightScheduleEntity returnFs1 = currentFb.getReturnOne();

            FlightScheduleEntity returnFs2 = currentFb.getReturnTwo();

            FlightScheduleEntity returnFs3 = currentFb.getReturnThree();

            fs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            fs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            fs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            fs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            fs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs1.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs1.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs1.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs1.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs2.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs2.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs2.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs2.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

            returnFs3.getFlightSchedulePlan().getListOfFlightSchedule().clear();
            returnFs3.getFlightSchedulePlan().getFlightEntity().setReturnFlight(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getListOfFlightSchedulePlan().clear();
            returnFs3.getFlightSchedulePlan().setReturnFlightSchedulePlan(null);
            returnFs3.getFlightSchedulePlan().getFlightEntity().getFlightRoute().setReturnRoute(null);

        }

        return listOfflightBundle;
    }

}
