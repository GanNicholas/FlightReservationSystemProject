/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AirportEntity;
import entity.FlightBundle;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.SeatEntity;
import entity.SingleFlightScheduleEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import sun.util.calendar.Gregorian;
import util.exception.FlightRouteDoesNotExistException;
import util.exception.FlightScheduleExistException;

/**
 *
 * @author nickg
 */
@Stateless
public class FlightScheduleSessionBean implements FlightScheduleSessionBeanRemote, FlightScheduleSessionBeanLocal {

    @EJB
    private FlightRouteSessionBeanLocal flightRouteSessionBean;

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @Resource
    EJBContext eJBContext;

    @Override
    public FlightScheduleEntity createFlightSchedule(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight) throws FlightScheduleExistException {
        String flightNumber = flight.getFlightNumber();

        AirportEntity originAirport = flight.getFlightRoute().getOriginLocation();
        AirportEntity destinationAirport = flight.getFlightRoute().getDestinationLocation();

        int departTimeZone = (originAirport.getTimeZoneHour() * 60) + originAirport.getTimeZoneMin();
        int arriveTimeZone = (destinationAirport.getTimeZoneHour() * 60) + destinationAirport.getTimeZoneMin();
        int timeDiff = arriveTimeZone - departTimeZone;

        GregorianCalendar arrivalDateTime = (GregorianCalendar) departureDateTime.clone();
        arrivalDateTime.add(GregorianCalendar.MINUTE, flightDuration);
        arrivalDateTime.add(GregorianCalendar.MINUTE, timeDiff);

        this.checkSchedules(arrivalDateTime, departureDateTime, flightNumber);

        //create flight schedule
        FlightScheduleEntity flightSchedule = new FlightScheduleEntity(departureDateTime, flightDuration, fsp, arrivalDateTime);
        //get seatingplan - need get seating plan from aircraft config, add to flight schedule
        List<SeatEntity> seatingPlan = flight.getAircraftConfig().getSeatingPlan();

        for (SeatEntity seat : flight.getAircraftConfig().getSeatingPlan()) {
            flightSchedule.getSeatingPlan().add(seat);
        }

        return flightSchedule;
    }

    private void checkSchedules(Calendar arrivalDateTime, Calendar departureDateTime, String flightNumber) throws FlightScheduleExistException {

        List<FlightSchedulePlanEntity> listOfFlightSchedulePlan = em.createQuery("SELECT c FROM FlightSchedulePlanEntity c WHERE c.flightNumber =:flightNum AND c.isDeleted = FALSE").setParameter("flightNum", flightNumber).getResultList();
        for (FlightSchedulePlanEntity tempFSP : listOfFlightSchedulePlan) {
            List<FlightScheduleEntity> tempFlight = tempFSP.getListOfFlightSchedule();
            for (FlightScheduleEntity tempFlightSchedule : tempFlight) {

                GregorianCalendar tempFSPDepartureDate = tempFlightSchedule.getDepartureDateTime();
                GregorianCalendar tempFSPArrivalDate = tempFlightSchedule.getArrivalDateTime();

                boolean newArriveBeforeOldDepart = tempFSPDepartureDate.after(arrivalDateTime);
                boolean newDepartureBeforeOldDepart = tempFSPDepartureDate.after(departureDateTime);
                boolean newArriveAfterOldArrive = tempFSPArrivalDate.before(arrivalDateTime);
                boolean newDepartAfterOldArrive = tempFSPArrivalDate.before(departureDateTime);
                if (!((newArriveBeforeOldDepart && newDepartureBeforeOldDepart) || (newArriveAfterOldArrive && newDepartAfterOldArrive))) {
                    eJBContext.setRollbackOnly();
                    throw new FlightScheduleExistException("Flight Schedule has conflict with existing flight schedule!");
                }

            }
        }

    }

    //checks if flight has been booked by anyone
    @Override
    public boolean checkFlightScheduleSeats(FlightScheduleEntity fs) {
        for (SeatEntity seat : fs.getSeatingPlan()) {
            if (seat.getPassenger() != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<FlightBundle> listOfConnectingFlightRecords(Date departureDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {

        GregorianCalendar gDepart = new GregorianCalendar();
        gDepart.setTime(departureDate);

        GregorianCalendar gEndDate = (GregorianCalendar) gDepart.clone();
        gEndDate.add(GregorianCalendar.HOUR_OF_DAY, 24);
        gEndDate.add(GregorianCalendar.SECOND, -1);
        List<FlightScheduleEntity> listOfFS = null;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDepart = format.format(gDepart.getTime());
        String strEndDate = format.format(gEndDate.getTime());
        System.out.println("current record:" + strDepart + " strEndDate" + strEndDate);
        Query query = null;
        try {
            flightRouteSessionBean.retrieveOD(departureAirport);

            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE  fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode =:iataCode AND fs.flightSchedulePlan.isDeleted = false  AND fs.departureDateTime BETWEEN :firstDate AND :firstEndDate ORDER By fs.departureDateTime ASC");
            query.setParameter("iataCode", departureAirport);
            query.setParameter("firstDate", gDepart);
            query.setParameter("firstEndDate", gEndDate);
            listOfFS = query.getResultList();
        } catch (NoResultException ex) {
            throw new FlightRouteDoesNotExistException();
        }
        List<FlightBundle> listOFlightSchedules = new ArrayList<>();
        for (FlightScheduleEntity firstFs : listOfFS) {
            GregorianCalendar firstStartTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            GregorianCalendar firstEndTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            firstStartTime.add(GregorianCalendar.HOUR, 2);
            firstEndTime.add(GregorianCalendar.HOUR, 24);
            String firstDepart = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode();
            String firstDestination = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();
            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE fs.flightSchedulePlan.isDeleted = false AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :firstDestination AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode <> :firstDepart AND fs.departureDateTime BETWEEN :firstStart AND :firstEnd ORDER BY fs.departureDateTime ASC");
            query.setParameter("firstDestination", firstDestination);
            query.setParameter("firstDepart", firstDepart);
            query.setParameter("firstStart", firstStartTime);
            query.setParameter("firstEnd", firstEndTime);
            List<FlightScheduleEntity> secondBoundFs = query.getResultList();
            if (secondBoundFs.size() <= 0) {
                continue;
            }
            for (FlightScheduleEntity secondFs : secondBoundFs) {
                GregorianCalendar secondStartTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                GregorianCalendar secondEndTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                secondStartTime.add(GregorianCalendar.HOUR, 2);
                secondEndTime.add(GregorianCalendar.HOUR, 24);
                String secondFlightDestination = secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();

                if (secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode().equals(destinationAirport)) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, null));
                    continue;
                }
                query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs "
                        + "WHERE fs.flightSchedulePlan.isDeleted = false "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :secondDestination "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode = :finalDestination "
                        + "AND fs.departureDateTime BETWEEN :secondStart "
                        + "AND :secondEnd ORDER BY fs.departureDateTime ASC");
                query.setParameter("secondDestination", secondFlightDestination);
                query.setParameter("finalDestination", destinationAirport);
                query.setParameter("secondStart", secondStartTime);
                query.setParameter("secondEnd", secondEndTime);

                List<FlightScheduleEntity> lastHop = query.getResultList();
                for (FlightScheduleEntity thirdFs : lastHop) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, thirdFs));
                }

            }
        }
        System.out.println("listOFlightSchedules.size():" + listOFlightSchedules.size());
        for (FlightBundle cfs : listOFlightSchedules) {

            cfs.getDepartOne().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartOne().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartOne().getSeatingPlan().size();
            cfs.getDepartOne().getFlightSchedulePlan().getListOfFlightSchedule().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartTwo().getSeatingPlan().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFlightSchedule().size();
            if (cfs.getDepartThree() != null) {
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFare().size();
                cfs.getDepartThree().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
                cfs.getDepartThree().getSeatingPlan().size();
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFlightSchedule().size();

            }
        }
        return listOFlightSchedules;

    }

    @Override
    public List<FlightBundle> listOfConnectingFlightRecordsLessThreeDays(Date actualDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        GregorianCalendar actual = new GregorianCalendar();
        actual.setTime(actualDate);

        GregorianCalendar gDepart = (GregorianCalendar) actual.clone();
        gDepart.add(GregorianCalendar.SECOND, -1);
        actual.add(GregorianCalendar.DATE, -3);
        GregorianCalendar gEndDate = (GregorianCalendar) actual.clone();

        String strDepart = format.format(gDepart.getTime());
        String strEndDate = format.format(gEndDate.getTime());
        System.out.println("3 days less strDepart" + strDepart + " strEndDate" + strEndDate);

        List<FlightScheduleEntity> listOfFS = null;
        Query query = null;
        try {
            flightRouteSessionBean.retrieveOD(departureAirport);

            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE  fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode =:iataCode AND fs.flightSchedulePlan.isDeleted = false  AND fs.departureDateTime BETWEEN :firstDate AND :firstEndDate ORDER By fs.departureDateTime ASC");
            query.setParameter("iataCode", departureAirport);
            query.setParameter("firstDate", gEndDate);
            query.setParameter("firstEndDate", gDepart);
            listOfFS = query.getResultList();
        } catch (NoResultException ex) {
            throw new FlightRouteDoesNotExistException();
        }
        List<FlightBundle> listOFlightSchedules = new ArrayList<>();
        for (FlightScheduleEntity firstFs : listOfFS) {
            GregorianCalendar firstStartTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            GregorianCalendar firstEndTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            firstStartTime.add(GregorianCalendar.HOUR, 2);
            firstEndTime.add(GregorianCalendar.HOUR, 24);
            String firstDepart = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode();
            String firstDestination = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();
            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE fs.flightSchedulePlan.isDeleted = false AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :firstDestination AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode <> :firstDepart AND fs.departureDateTime BETWEEN :firstStart AND :firstEnd ORDER BY fs.departureDateTime ASC");
            query.setParameter("firstDestination", firstDestination);
            query.setParameter("firstDepart", firstDepart);
            query.setParameter("firstStart", firstStartTime);
            query.setParameter("firstEnd", firstEndTime);
            List<FlightScheduleEntity> secondBoundFs = query.getResultList();
            if (secondBoundFs.size() <= 0) {
                continue;
            }
            for (FlightScheduleEntity secondFs : secondBoundFs) {
                GregorianCalendar secondStartTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                GregorianCalendar secondEndTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                secondStartTime.add(GregorianCalendar.HOUR, 2);
                secondEndTime.add(GregorianCalendar.HOUR, 24);
                String secondFlightDestination = secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();

                if (secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode().equals(destinationAirport)) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, null));
                    continue;
                }
                query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs "
                        + "WHERE fs.flightSchedulePlan.isDeleted = false "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :secondDestination "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode = :finalDestination  "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode <> :secondFs "
                        + "AND fs.departureDateTime BETWEEN :secondStart "
                        + "AND :secondEnd ORDER BY fs.departureDateTime ASC");
                query.setParameter("secondDestination", secondFlightDestination);
                query.setParameter("finalDestination", destinationAirport);
                query.setParameter("secondFs", secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode());
                query.setParameter("secondStart", secondStartTime);
                query.setParameter("secondEnd", secondEndTime);

                List<FlightScheduleEntity> lastHop = query.getResultList();
                for (FlightScheduleEntity thirdFs : lastHop) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, thirdFs));
                }

            }
        }
        System.out.println("listOFlightSchedules.size():" + listOFlightSchedules.size());
        for (FlightBundle cfs : listOFlightSchedules) {

            cfs.getDepartOne().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartOne().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartOne().getSeatingPlan().size();
            cfs.getDepartOne().getFlightSchedulePlan().getListOfFlightSchedule().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartTwo().getSeatingPlan().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFlightSchedule().size();
            if (cfs.getDepartThree() != null) {
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFare().size();
                cfs.getDepartThree().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
                cfs.getDepartThree().getSeatingPlan().size();
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFlightSchedule().size();

            }
        }
        return listOFlightSchedules;
    }

    @Override
    public List<FlightBundle> listOfConnectingFlightRecordsAftThreeDays(Date actual, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {

        GregorianCalendar gActual = new GregorianCalendar();
        gActual.setTime(actual);

        GregorianCalendar gDepart = (GregorianCalendar) gActual.clone();
        gDepart.add(GregorianCalendar.DATE, 4);
        gDepart.add(GregorianCalendar.SECOND, -1);

        GregorianCalendar gEndDate = (GregorianCalendar) gActual.clone();
        gEndDate.add(GregorianCalendar.DATE, 1);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDepart = format.format(gDepart.getTime());
        String strEndDate = format.format(gEndDate.getTime());
        System.out.println("listOfConnectingFlightRecordsAftThreeDays" + strEndDate + " || " + strDepart);
        List<FlightScheduleEntity> listOfFS = null;
        Query query = null;
        try {
            flightRouteSessionBean.retrieveOD(departureAirport);

            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE  fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode =:iataCode AND fs.flightSchedulePlan.isDeleted = false  AND fs.departureDateTime BETWEEN :firstDate AND :firstEndDate ORDER By fs.departureDateTime ASC");
            query.setParameter("iataCode", departureAirport);
            query.setParameter("firstDate", gEndDate);
            query.setParameter("firstEndDate", gDepart);
            listOfFS = query.getResultList();
        } catch (NoResultException ex) {
            throw new FlightRouteDoesNotExistException();
        }
        List<FlightBundle> listOFlightSchedules = new ArrayList<>();
        for (FlightScheduleEntity firstFs : listOfFS) {
            GregorianCalendar firstStartTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            GregorianCalendar firstEndTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            firstStartTime.add(GregorianCalendar.HOUR, 2);
            firstEndTime.add(GregorianCalendar.HOUR, 24);
            String firstDepart = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode();
            String firstDestination = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();
            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE fs.flightSchedulePlan.isDeleted = false AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :firstDestination AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode <> :firstDepart AND fs.departureDateTime BETWEEN :firstStart AND :firstEnd ORDER BY fs.departureDateTime ASC");
            query.setParameter("firstDestination", firstDestination);
            query.setParameter("firstDepart", firstDepart);
            query.setParameter("firstStart", firstStartTime);
            query.setParameter("firstEnd", firstEndTime);
            List<FlightScheduleEntity> secondBoundFs = query.getResultList();
            if (secondBoundFs.size() <= 0) {
                continue;
            }
            for (FlightScheduleEntity secondFs : secondBoundFs) {
                GregorianCalendar secondStartTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                GregorianCalendar secondEndTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                secondStartTime.add(GregorianCalendar.HOUR, 2);
                secondEndTime.add(GregorianCalendar.HOUR, 24);
                String secondFlightDestination = secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();

                if (secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode().equals(destinationAirport)) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, null));
                    continue;
                }
                query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs "
                        + "WHERE fs.flightSchedulePlan.isDeleted = false "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :secondDestination "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode = :finalDestination "
                        + "AND fs.departureDateTime BETWEEN :secondStart "
                        + "AND :secondEnd ORDER BY fs.departureDateTime ASC");
                query.setParameter("secondDestination", secondFlightDestination);
                query.setParameter("finalDestination", destinationAirport);
                query.setParameter("secondStart", secondStartTime);
                query.setParameter("secondEnd", secondEndTime);

                List<FlightScheduleEntity> lastHop = query.getResultList();
                for (FlightScheduleEntity thirdFs : lastHop) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, thirdFs));
                }

            }
        }
        System.out.println("listOFlightSchedules.size():" + listOFlightSchedules.size());
        for (FlightBundle cfs : listOFlightSchedules) {

            cfs.getDepartOne().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartOne().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartOne().getSeatingPlan().size();
            cfs.getDepartOne().getFlightSchedulePlan().getListOfFlightSchedule().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartTwo().getSeatingPlan().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFlightSchedule().size();
            if (cfs.getDepartThree() != null) {
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFare().size();
                cfs.getDepartThree().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
                cfs.getDepartThree().getSeatingPlan().size();
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFlightSchedule().size();

            }
        }
        return listOFlightSchedules;
    }

    @Override
    public List<FlightBundle> getDirectFlight(GregorianCalendar gStart, GregorianCalendar gEnd, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {
        Query query = null;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.println("-------------------gStart" + format.format(gStart.getTime()) + " gEnd" + format.format(gEnd.getTime()) + "--------------");
        List<FlightBundle> listOfFS = new ArrayList<>();
        List<FlightScheduleEntity> fs = null;
        try {
            flightRouteSessionBean.retrieveOD(departureAirport);

            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs "
                    + "WHERE  fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode =:iataCode "
                    + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode=:destination "
                    + "AND fs.flightSchedulePlan.isDeleted = false  "
                    + "AND fs.departureDateTime BETWEEN :firstDate AND :firstEndDate ORDER By fs.departureDateTime ASC");
            query.setParameter("iataCode", departureAirport);
            query.setParameter("destination", destinationAirport);
            query.setParameter("firstDate", gStart);
            query.setParameter("firstEndDate", gEnd);
            fs = query.getResultList();
            for (int i = 0; i < fs.size(); i++) {
                FlightBundle fb = new FlightBundle();
                fb.setDepartOne(fs.get(i));
                listOfFS.add(fb);
            }
            System.out.println("listOfFS direct flight:" + fs.size());
        } catch (FlightRouteDoesNotExistException ex) {
            throw new FlightRouteDoesNotExistException("Flight does not exist");
        }
        for (FlightBundle cfs : listOfFS) {

            cfs.getDepartOne().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartOne().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartOne().getSeatingPlan().size();
            cfs.getDepartOne().getFlightSchedulePlan().getListOfFlightSchedule().size();

        }
        return listOfFS;
    }

    @Override
    public FlightScheduleEntity updateFlightSchedule(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight, FlightScheduleEntity currentFs) throws FlightScheduleExistException {
        String flightNumber = flight.getFlightNumber();

        AirportEntity originAirport = flight.getFlightRoute().getOriginLocation();
        AirportEntity destinationAirport = flight.getFlightRoute().getDestinationLocation();

        int departTimeZone = (originAirport.getTimeZoneHour() * 60) + originAirport.getTimeZoneMin();
        int arriveTimeZone = (destinationAirport.getTimeZoneHour() * 60) + destinationAirport.getTimeZoneMin();
        int timeDiff = arriveTimeZone - departTimeZone;

        GregorianCalendar arrivalDateTime = (GregorianCalendar) departureDateTime.clone();
        arrivalDateTime.add(GregorianCalendar.MINUTE, flightDuration);
        arrivalDateTime.add(GregorianCalendar.MINUTE, timeDiff);

        this.checkUpdateSchedules(arrivalDateTime, departureDateTime, flightNumber, currentFs);

        //create flight schedule
        FlightScheduleEntity flightSchedule = new FlightScheduleEntity(departureDateTime, flightDuration, fsp, arrivalDateTime);
        //get seatingplan - need get seating plan from aircraft config, add to flight schedule
        List<SeatEntity> seatingPlan = flight.getAircraftConfig().getSeatingPlan();

        for (SeatEntity seat : flight.getAircraftConfig().getSeatingPlan()) {
            flightSchedule.getSeatingPlan().add(seat);
        }

        return flightSchedule;
    }

    private void checkUpdateSchedules(Calendar arrivalDateTime, Calendar departureDateTime, String flightNumber, FlightScheduleEntity currentFs) throws FlightScheduleExistException {

        List<FlightSchedulePlanEntity> listOfFlightSchedulePlan = em.createQuery("SELECT c FROM FlightSchedulePlanEntity c WHERE c.flightNumber =:flightNum AND c.isDeleted = FALSE").setParameter("flightNum", flightNumber).getResultList();
        for (FlightSchedulePlanEntity tempFSP : listOfFlightSchedulePlan) {
            List<FlightScheduleEntity> tempFlight = tempFSP.getListOfFlightSchedule();
            for (FlightScheduleEntity tempFlightSchedule : tempFlight) {

                GregorianCalendar tempFSPDepartureDate = tempFlightSchedule.getDepartureDateTime();
                GregorianCalendar tempFSPArrivalDate = tempFlightSchedule.getArrivalDateTime();

                boolean newArriveBeforeOldDepart = tempFSPDepartureDate.after(arrivalDateTime);
                boolean newDepartureBeforeOldDepart = tempFSPDepartureDate.after(departureDateTime);
                boolean newArriveAfterOldArrive = tempFSPArrivalDate.before(arrivalDateTime);
                boolean newDepartAfterOldArrive = tempFSPArrivalDate.before(departureDateTime);

                if (!((newArriveBeforeOldDepart && newDepartureBeforeOldDepart) || (newArriveAfterOldArrive && newDepartAfterOldArrive)) && !tempFlightSchedule.getFlightScheduleId().equals(currentFs.getFlightScheduleId())) {
                    eJBContext.setRollbackOnly();
                    throw new FlightScheduleExistException("Flight Schedule has conflict with existing flight schedule!");
                }

            }
        }

    }

    @Override
    public FlightScheduleEntity updateReccurentFlightSchedule(GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight) throws FlightScheduleExistException {
        String flightNumber = flight.getFlightNumber();

        AirportEntity originAirport = flight.getFlightRoute().getOriginLocation();
        AirportEntity destinationAirport = flight.getFlightRoute().getDestinationLocation();

        int departTimeZone = (originAirport.getTimeZoneHour() * 60) + originAirport.getTimeZoneMin();
        int arriveTimeZone = (destinationAirport.getTimeZoneHour() * 60) + destinationAirport.getTimeZoneMin();
        int timeDiff = arriveTimeZone - departTimeZone;

        GregorianCalendar arrivalDateTime = (GregorianCalendar) departureDateTime.clone();
        arrivalDateTime.add(GregorianCalendar.MINUTE, flightDuration);
        arrivalDateTime.add(GregorianCalendar.MINUTE, timeDiff);

        this.checkUpdateRecurrentSchedules(arrivalDateTime, departureDateTime, flightNumber, fsp);

        //create flight schedule
        FlightScheduleEntity flightSchedule = new FlightScheduleEntity(departureDateTime, flightDuration, fsp, arrivalDateTime);
        //get seatingplan - need get seating plan from aircraft config, add to flight schedule
        List<SeatEntity> seatingPlan = flight.getAircraftConfig().getSeatingPlan();

        for (SeatEntity seat : flight.getAircraftConfig().getSeatingPlan()) {
            flightSchedule.getSeatingPlan().add(seat);
        }

        return flightSchedule;
    }

    private void checkUpdateRecurrentSchedules(Calendar arrivalDateTime, Calendar departureDateTime, String flightNumber, FlightSchedulePlanEntity currentFsp) throws FlightScheduleExistException {

        List<FlightSchedulePlanEntity> listOfFlightSchedulePlan = em.createQuery("SELECT c FROM FlightSchedulePlanEntity c WHERE c.flightNumber =:flightNum AND c.isDeleted = FALSE").setParameter("flightNum", flightNumber).getResultList();
        for (FlightSchedulePlanEntity tempFSP : listOfFlightSchedulePlan) {

            if (!tempFSP.getFlightSchedulePlanId().equals(currentFsp.getFlightSchedulePlanId())) {

                List<FlightScheduleEntity> tempFlight = tempFSP.getListOfFlightSchedule();
                for (FlightScheduleEntity tempFlightSchedule : tempFlight) {

                    GregorianCalendar tempFSPDepartureDate = tempFlightSchedule.getDepartureDateTime();
                    GregorianCalendar tempFSPArrivalDate = tempFlightSchedule.getArrivalDateTime();

                    boolean newArriveBeforeOldDepart = tempFSPDepartureDate.after(arrivalDateTime);
                    boolean newDepartureBeforeOldDepart = tempFSPDepartureDate.after(departureDateTime);
                    boolean newArriveAfterOldArrive = tempFSPArrivalDate.before(arrivalDateTime);
                    boolean newDepartAfterOldArrive = tempFSPArrivalDate.before(departureDateTime);

                    if (!((newArriveBeforeOldDepart && newDepartureBeforeOldDepart) || (newArriveAfterOldArrive && newDepartAfterOldArrive))) {
                        eJBContext.setRollbackOnly();
                        throw new FlightScheduleExistException("Flight Schedule has conflict with existing flight schedule!");
                    }

                }
            }
        }

    }

    @Override
    public List<FlightBundle> getDirectFlightUnmanaged(GregorianCalendar gStart, GregorianCalendar gEnd, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {
        Query query = null;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.println("-------------------gStart" + format.format(gStart.getTime()) + " gEnd" + format.format(gEnd.getTime()) + "--------------");
        List<FlightBundle> listOfFS = new ArrayList<>();
        List<FlightScheduleEntity> fs = null;
        try {
            flightRouteSessionBean.retrieveOD(departureAirport);

            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs "
                    + "WHERE  fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode =:iataCode "
                    + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode=:destination "
                    + "AND fs.flightSchedulePlan.isDeleted = false  "
                    + "AND fs.departureDateTime BETWEEN :firstDate AND :firstEndDate ORDER By fs.departureDateTime ASC");
            query.setParameter("iataCode", departureAirport);
            query.setParameter("destination", destinationAirport);
            query.setParameter("firstDate", gStart);
            query.setParameter("firstEndDate", gEnd);
            fs = query.getResultList();
            for (int i = 0; i < fs.size(); i++) {
                FlightBundle fb = new FlightBundle();
                fb.setDepartOne(fs.get(i));
                listOfFS.add(fb);
            }
            System.out.println("listOfFS direct flight:" + fs.size());
        } catch (FlightRouteDoesNotExistException ex) {
            throw new FlightRouteDoesNotExistException("Flight does not exist");
        }
        for (FlightBundle cfs : listOfFS) {

            cfs.getDepartOne().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartOne().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartOne().getSeatingPlan().size();
            cfs.getDepartOne().getFlightSchedulePlan().getListOfFlightSchedule().size();

        }

        for (FlightBundle currentFb : listOfFS) {
            em.detach(currentFb.getDepartOne());
            em.detach(currentFb.getDepartTwo());
            em.detach(currentFb.getDepartThree());
            em.detach(currentFb.getDepartOneFare());
            em.detach(currentFb.getDepartTwoFare());
            em.detach(currentFb.getDepartThreeFare());
            em.detach(currentFb.getReturnOne());
            em.detach(currentFb.getReturnTwo());
            em.detach(currentFb.getReturnThree());
            em.detach(currentFb.getReturnOne());
            em.detach(currentFb.getReturnOneFare());
            em.detach(currentFb.getReturnTwoFare());
            em.detach(currentFb.getReturnThreeFare());
        }
        return listOfFS;
    }

    @Override
    public List<FlightBundle> listOfConnectingFlightRecordsAftThreeDaysUnmanaged(Date actual, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {

        GregorianCalendar gActual = new GregorianCalendar();
        gActual.setTime(actual);

        GregorianCalendar gDepart = (GregorianCalendar) gActual.clone();
        gDepart.add(GregorianCalendar.DATE, 4);
        gDepart.add(GregorianCalendar.SECOND, -1);

        GregorianCalendar gEndDate = (GregorianCalendar) gActual.clone();
        gEndDate.add(GregorianCalendar.DATE, 1);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDepart = format.format(gDepart.getTime());
        String strEndDate = format.format(gEndDate.getTime());
        System.out.println("listOfConnectingFlightRecordsAftThreeDays" + strEndDate + " || " + strDepart);
        List<FlightScheduleEntity> listOfFS = null;
        Query query = null;
        try {
            flightRouteSessionBean.retrieveOD(departureAirport);

            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE  fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode =:iataCode AND fs.flightSchedulePlan.isDeleted = false  AND fs.departureDateTime BETWEEN :firstDate AND :firstEndDate ORDER By fs.departureDateTime ASC");
            query.setParameter("iataCode", departureAirport);
            query.setParameter("firstDate", gEndDate);
            query.setParameter("firstEndDate", gDepart);
            listOfFS = query.getResultList();
        } catch (NoResultException ex) {
            throw new FlightRouteDoesNotExistException();
        }
        List<FlightBundle> listOFlightSchedules = new ArrayList<>();
        for (FlightScheduleEntity firstFs : listOfFS) {
            GregorianCalendar firstStartTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            GregorianCalendar firstEndTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            firstStartTime.add(GregorianCalendar.HOUR, 2);
            firstEndTime.add(GregorianCalendar.HOUR, 24);
            String firstDepart = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode();
            String firstDestination = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();
            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE fs.flightSchedulePlan.isDeleted = false AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :firstDestination AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode <> :firstDepart AND fs.departureDateTime BETWEEN :firstStart AND :firstEnd ORDER BY fs.departureDateTime ASC");
            query.setParameter("firstDestination", firstDestination);
            query.setParameter("firstDepart", firstDepart);
            query.setParameter("firstStart", firstStartTime);
            query.setParameter("firstEnd", firstEndTime);
            List<FlightScheduleEntity> secondBoundFs = query.getResultList();
            if (secondBoundFs.size() <= 0) {
                continue;
            }
            for (FlightScheduleEntity secondFs : secondBoundFs) {
                GregorianCalendar secondStartTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                GregorianCalendar secondEndTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                secondStartTime.add(GregorianCalendar.HOUR, 2);
                secondEndTime.add(GregorianCalendar.HOUR, 24);
                String secondFlightDestination = secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();

                if (secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode().equals(destinationAirport)) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, null));
                    continue;
                }
                query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs "
                        + "WHERE fs.flightSchedulePlan.isDeleted = false "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :secondDestination "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode = :finalDestination "
                        + "AND fs.departureDateTime BETWEEN :secondStart "
                        + "AND :secondEnd ORDER BY fs.departureDateTime ASC");
                query.setParameter("secondDestination", secondFlightDestination);
                query.setParameter("finalDestination", destinationAirport);
                query.setParameter("secondStart", secondStartTime);
                query.setParameter("secondEnd", secondEndTime);

                List<FlightScheduleEntity> lastHop = query.getResultList();
                for (FlightScheduleEntity thirdFs : lastHop) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, thirdFs));
                }

            }
        }
        System.out.println("listOFlightSchedules.size():" + listOFlightSchedules.size());
        for (FlightBundle cfs : listOFlightSchedules) {

            cfs.getDepartOne().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartOne().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartOne().getSeatingPlan().size();
            cfs.getDepartOne().getFlightSchedulePlan().getListOfFlightSchedule().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartTwo().getSeatingPlan().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFlightSchedule().size();
            if (cfs.getDepartThree() != null) {
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFare().size();
                cfs.getDepartThree().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
                cfs.getDepartThree().getSeatingPlan().size();
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFlightSchedule().size();

            }
        }

        for (FlightBundle currentFb : listOFlightSchedules) {
           em.detach(currentFb.getDepartOne());
            em.detach(currentFb.getDepartTwo());
            em.detach(currentFb.getDepartThree());
            em.detach(currentFb.getDepartOneFare());
            em.detach(currentFb.getDepartTwoFare());
            em.detach(currentFb.getDepartThreeFare());
            em.detach(currentFb.getReturnOne());
            em.detach(currentFb.getReturnTwo());
            em.detach(currentFb.getReturnThree());
            em.detach(currentFb.getReturnOne());
            em.detach(currentFb.getReturnOneFare());
            em.detach(currentFb.getReturnTwoFare());
            em.detach(currentFb.getReturnThreeFare());
        }

        return listOFlightSchedules;
    }

    @Override
    public List<FlightBundle> listOfConnectingFlightRecordsLessThreeDaysUnmanaged(Date actualDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        GregorianCalendar actual = new GregorianCalendar();
        actual.setTime(actualDate);

        GregorianCalendar gDepart = (GregorianCalendar) actual.clone();
        gDepart.add(GregorianCalendar.SECOND, -1);
        actual.add(GregorianCalendar.DATE, -3);
        GregorianCalendar gEndDate = (GregorianCalendar) actual.clone();

        String strDepart = format.format(gDepart.getTime());
        String strEndDate = format.format(gEndDate.getTime());
        System.out.println("3 days less strDepart" + strDepart + " strEndDate" + strEndDate);

        List<FlightScheduleEntity> listOfFS = null;
        Query query = null;
        try {
            flightRouteSessionBean.retrieveOD(departureAirport);

            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE  fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode =:iataCode AND fs.flightSchedulePlan.isDeleted = false  AND fs.departureDateTime BETWEEN :firstDate AND :firstEndDate ORDER By fs.departureDateTime ASC");
            query.setParameter("iataCode", departureAirport);
            query.setParameter("firstDate", gEndDate);
            query.setParameter("firstEndDate", gDepart);
            listOfFS = query.getResultList();
        } catch (NoResultException ex) {
            throw new FlightRouteDoesNotExistException();
        }
        List<FlightBundle> listOFlightSchedules = new ArrayList<>();
        for (FlightScheduleEntity firstFs : listOfFS) {
            GregorianCalendar firstStartTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            GregorianCalendar firstEndTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            firstStartTime.add(GregorianCalendar.HOUR, 2);
            firstEndTime.add(GregorianCalendar.HOUR, 24);
            String firstDepart = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode();
            String firstDestination = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();
            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE fs.flightSchedulePlan.isDeleted = false AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :firstDestination AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode <> :firstDepart AND fs.departureDateTime BETWEEN :firstStart AND :firstEnd ORDER BY fs.departureDateTime ASC");
            query.setParameter("firstDestination", firstDestination);
            query.setParameter("firstDepart", firstDepart);
            query.setParameter("firstStart", firstStartTime);
            query.setParameter("firstEnd", firstEndTime);
            List<FlightScheduleEntity> secondBoundFs = query.getResultList();
            if (secondBoundFs.size() <= 0) {
                continue;
            }
            for (FlightScheduleEntity secondFs : secondBoundFs) {
                GregorianCalendar secondStartTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                GregorianCalendar secondEndTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                secondStartTime.add(GregorianCalendar.HOUR, 2);
                secondEndTime.add(GregorianCalendar.HOUR, 24);
                String secondFlightDestination = secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();

                if (secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode().equals(destinationAirport)) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, null));
                    continue;
                }
                query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs "
                        + "WHERE fs.flightSchedulePlan.isDeleted = false "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :secondDestination "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode = :finalDestination  "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode <> :secondFs "
                        + "AND fs.departureDateTime BETWEEN :secondStart "
                        + "AND :secondEnd ORDER BY fs.departureDateTime ASC");
                query.setParameter("secondDestination", secondFlightDestination);
                query.setParameter("finalDestination", destinationAirport);
                query.setParameter("secondFs", secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode());
                query.setParameter("secondStart", secondStartTime);
                query.setParameter("secondEnd", secondEndTime);

                List<FlightScheduleEntity> lastHop = query.getResultList();
                for (FlightScheduleEntity thirdFs : lastHop) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, thirdFs));
                }

            }
        }
        System.out.println("listOFlightSchedules.size():" + listOFlightSchedules.size());
        for (FlightBundle cfs : listOFlightSchedules) {

            cfs.getDepartOne().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartOne().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartOne().getSeatingPlan().size();
            cfs.getDepartOne().getFlightSchedulePlan().getListOfFlightSchedule().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartTwo().getSeatingPlan().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFlightSchedule().size();
            if (cfs.getDepartThree() != null) {
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFare().size();
                cfs.getDepartThree().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
                cfs.getDepartThree().getSeatingPlan().size();
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFlightSchedule().size();

            }
        }

        for (FlightBundle currentFb : listOFlightSchedules) {
           em.detach(currentFb.getDepartOne());
            em.detach(currentFb.getDepartTwo());
            em.detach(currentFb.getDepartThree());
            em.detach(currentFb.getDepartOneFare());
            em.detach(currentFb.getDepartTwoFare());
            em.detach(currentFb.getDepartThreeFare());
            em.detach(currentFb.getReturnOne());
            em.detach(currentFb.getReturnTwo());
            em.detach(currentFb.getReturnThree());
            em.detach(currentFb.getReturnOne());
            em.detach(currentFb.getReturnOneFare());
            em.detach(currentFb.getReturnTwoFare());
            em.detach(currentFb.getReturnThreeFare());
        }

        return listOFlightSchedules;
    }

    @Override
    public List<FlightBundle> listOfConnectingFlightRecordsUnmanaged(Date departureDate, String departureAirport, String destinationAirport) throws FlightRouteDoesNotExistException {

        GregorianCalendar gDepart = new GregorianCalendar();
        gDepart.setTime(departureDate);

        GregorianCalendar gEndDate = (GregorianCalendar) gDepart.clone();
        gEndDate.add(GregorianCalendar.HOUR_OF_DAY, 24);
        gEndDate.add(GregorianCalendar.SECOND, -1);
        List<FlightScheduleEntity> listOfFS = null;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDepart = format.format(gDepart.getTime());
        String strEndDate = format.format(gEndDate.getTime());
        System.out.println("current record:" + strDepart + " strEndDate" + strEndDate);
        Query query = null;
        try {
            flightRouteSessionBean.retrieveOD(departureAirport);

            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE  fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode =:iataCode AND fs.flightSchedulePlan.isDeleted = false  AND fs.departureDateTime BETWEEN :firstDate AND :firstEndDate ORDER By fs.departureDateTime ASC");
            query.setParameter("iataCode", departureAirport);
            query.setParameter("firstDate", gDepart);
            query.setParameter("firstEndDate", gEndDate);
            listOfFS = query.getResultList();
        } catch (NoResultException ex) {
            throw new FlightRouteDoesNotExistException();
        }
        List<FlightBundle> listOFlightSchedules = new ArrayList<>();
        for (FlightScheduleEntity firstFs : listOfFS) {
            GregorianCalendar firstStartTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            GregorianCalendar firstEndTime = (GregorianCalendar) firstFs.getDepartureDateTime().clone();
            firstStartTime.add(GregorianCalendar.HOUR, 2);
            firstEndTime.add(GregorianCalendar.HOUR, 24);
            String firstDepart = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getOriginLocation().getIataAirportCode();
            String firstDestination = firstFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();
            query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs WHERE fs.flightSchedulePlan.isDeleted = false AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :firstDestination AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode <> :firstDepart AND fs.departureDateTime BETWEEN :firstStart AND :firstEnd ORDER BY fs.departureDateTime ASC");
            query.setParameter("firstDestination", firstDestination);
            query.setParameter("firstDepart", firstDepart);
            query.setParameter("firstStart", firstStartTime);
            query.setParameter("firstEnd", firstEndTime);
            List<FlightScheduleEntity> secondBoundFs = query.getResultList();
            if (secondBoundFs.size() <= 0) {
                continue;
            }
            for (FlightScheduleEntity secondFs : secondBoundFs) {
                GregorianCalendar secondStartTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                GregorianCalendar secondEndTime = (GregorianCalendar) secondFs.getDepartureDateTime().clone();
                secondStartTime.add(GregorianCalendar.HOUR, 2);
                secondEndTime.add(GregorianCalendar.HOUR, 24);
                String secondFlightDestination = secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode();

                if (secondFs.getFlightSchedulePlan().getFlightEntity().getFlightRoute().getDestinationLocation().getIataAirportCode().equals(destinationAirport)) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, null));
                    continue;
                }
                query = em.createQuery("SELECT fs FROM FlightScheduleEntity fs "
                        + "WHERE fs.flightSchedulePlan.isDeleted = false "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.originLocation.iataAirportCode = :secondDestination "
                        + "AND fs.flightSchedulePlan.flightEntity.flightRoute.destinationLocation.iataAirportCode = :finalDestination "
                        + "AND fs.departureDateTime BETWEEN :secondStart "
                        + "AND :secondEnd ORDER BY fs.departureDateTime ASC");
                query.setParameter("secondDestination", secondFlightDestination);
                query.setParameter("finalDestination", destinationAirport);
                query.setParameter("secondStart", secondStartTime);
                query.setParameter("secondEnd", secondEndTime);

                List<FlightScheduleEntity> lastHop = query.getResultList();
                for (FlightScheduleEntity thirdFs : lastHop) {
                    listOFlightSchedules.add(new FlightBundle(firstFs, secondFs, thirdFs));
                }

            }
        }
        System.out.println("listOFlightSchedules.size():" + listOFlightSchedules.size());
        for (FlightBundle cfs : listOFlightSchedules) {

            cfs.getDepartOne().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartOne().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartOne().getSeatingPlan().size();
            cfs.getDepartOne().getFlightSchedulePlan().getListOfFlightSchedule().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFare().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
            cfs.getDepartTwo().getSeatingPlan().size();
            cfs.getDepartTwo().getFlightSchedulePlan().getListOfFlightSchedule().size();
            if (cfs.getDepartThree() != null) {
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFare().size();
                cfs.getDepartThree().getFlightSchedulePlan().getFlightEntity().getAircraftConfig().getCabinClasses().size();
                cfs.getDepartThree().getSeatingPlan().size();
                cfs.getDepartThree().getFlightSchedulePlan().getListOfFlightSchedule().size();

            }
        }

        for (FlightBundle currentFb : listOFlightSchedules) {
       em.detach(currentFb.getDepartOne());
            em.detach(currentFb.getDepartTwo());
            em.detach(currentFb.getDepartThree());
            em.detach(currentFb.getDepartOneFare());
            em.detach(currentFb.getDepartTwoFare());
            em.detach(currentFb.getDepartThreeFare());
            em.detach(currentFb.getReturnOne());
            em.detach(currentFb.getReturnTwo());
            em.detach(currentFb.getReturnThree());
            em.detach(currentFb.getReturnOne());
            em.detach(currentFb.getReturnOneFare());
            em.detach(currentFb.getReturnTwoFare());
            em.detach(currentFb.getReturnThreeFare());
        }

        return listOFlightSchedules;

    }

}
