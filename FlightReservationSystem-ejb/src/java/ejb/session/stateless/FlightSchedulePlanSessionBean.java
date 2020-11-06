/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AirportEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.MultipleFlightScheduleEntity;
import entity.RecurringScheduleEntity;
import entity.RecurringWeeklyScheduleEntity;
import entity.SeatEntity;
import entity.SingleFlightScheduleEntity;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.FareCannotBeDeletedException;
import util.exception.FareDoesNotExistException;
import util.exception.FlightDoesNotExistException;
import util.exception.FlightScheduleDoesNotExistException;
import util.exception.FlightScheduleExistException;
import util.exception.FlightSchedulePlanDoesNotExistException;
import util.exception.FlightSchedulePlanIsEmptyException;

/**
 *
 * @author nickg
 */
@Stateless
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanSessionBeanRemote, FlightSchedulePlanSessionBeanLocal {

    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBean;

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    //for single/multiple flight schedule plan
    @Override
    public String createNonRecurrentFlightSchedulePlan(String flightNumber, List<GregorianCalendar> listOfDepartureDateTime, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover) throws FlightDoesNotExistException, FlightScheduleExistException {
        FlightEntity flight;
        try {
            flight = (FlightEntity) em.createNamedQuery("retrieveActiveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight with flight number does not exist!");
        }

        flight.setLayOver(layover);
        FlightSchedulePlanEntity fsp;
        if (listOfDepartureDateTime.size() == 1) {
            fsp = new SingleFlightScheduleEntity(flightNumber, false, flight);
        } else {
            fsp = new MultipleFlightScheduleEntity(flightNumber, false, flight);
        }

//        em.persist(fsp);
        for (FareEntity fare : listOfFares) {
//            em.persist(fare);
            fsp.getListOfFare().add(fare);
        }

        for (int i = 0; i < listOfDepartureDateTime.size(); i++) {
            GregorianCalendar departureDateTime = listOfDepartureDateTime.get(i);
            try {
                FlightScheduleEntity fe1 = flightScheduleSessionBean.createFlightSchedule(departureDateTime, flightDuration, fsp, flight);
//                em.persist(fe1);
                //link flight schedule to FSP
                fsp.getListOfFlightSchedule().add(fe1);
            } catch (FlightScheduleExistException ex) {
                throw new FlightScheduleExistException(ex.getMessage());
            }
        }

        for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
            em.persist(fs);
        }

        em.persist(fsp);
        em.flush();

        //return Flight entity
        FlightEntity returnFlight = null;
        if (createReturnFlightSchedule) {

            AirportEntity origin = flight.getFlightRoute().getDestinationLocation();
            AirportEntity destination = flight.getFlightRoute().getOriginLocation();
            try {
                FlightRouteEntity returnFlightRoute = (FlightRouteEntity) em.createNamedQuery("findFlightRoute").setParameter("origin", origin).setParameter("destination", destination).getSingleResult();
                returnFlight = (FlightEntity) em.createNamedQuery("retrieveReturnFlightUsingMainFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
            } catch (NoResultException ex) {
                throw new FlightDoesNotExistException("Flight does not exist!");
            }

            returnFlight.setLayOver(layover);

            //create return FSP
            FlightSchedulePlanEntity returnFSP;
            if (listOfDepartureDateTime.size() == 1) {
                returnFSP = new SingleFlightScheduleEntity(returnFlight.getFlightNumber(), false, returnFlight);
            } else {
                returnFSP = new MultipleFlightScheduleEntity(returnFlight.getFlightNumber(), false, returnFlight);
            }

            for (FareEntity fare : listOfFares) {
                returnFSP.getListOfFare().add(fare);
            }

//            em.persist(returnFSP);
            // GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                GregorianCalendar returnFlightDeparture = (GregorianCalendar) fs.getArrivalDateTime().clone();
                returnFlightDeparture.add(GregorianCalendar.MINUTE, layover);
                try {
                    FlightScheduleEntity fe1 = flightScheduleSessionBean.createFlightSchedule(returnFlightDeparture, flightDuration, returnFSP, returnFlight);
                    //link flight schedule to FSP
                    returnFSP.getListOfFlightSchedule().add(fe1);
                    //get seatingplan - need get seating plan from aircraft config, add to flight schedule
//                    List<SeatEntity> returnSeatingPlan = returnFlight.getAircraftConfig().getSeatingPlan();
//                    returnSeatingPlan.size();
//                    for (SeatEntity seat : returnSeatingPlan) {
//                        fe1.getSeatingPlan().add(seat);
//                    }

                } catch (FlightScheduleExistException ex) {
                    throw new FlightScheduleExistException(ex.getMessage());
                }

            }

            for (FlightScheduleEntity fs : returnFSP.getListOfFlightSchedule()) {
                em.persist(fs);
            }

            fsp.setReturnFlightSchedulePlan(returnFSP);

            em.persist(returnFSP);
            em.flush();

        }

        if (createReturnFlightSchedule && returnFlight != null) {
            return "Flight Schedule Plan created for " + flight.getFlightNumber() + " \nReturn Flight Schedule Plan created for " + returnFlight.getFlightNumber();
        } else {
            return "Flight Schedule Plan created for " + flight.getFlightNumber();
        }
    }

    @Override
    public String createRecurrentFlightSchedulePlan(String flightNumber, GregorianCalendar departureDateTime, GregorianCalendar endDate, Integer flightDuration, boolean createReturnFlightSchedule, List<FareEntity> listOfFares, Integer layover, Integer recurrency) throws FlightDoesNotExistException, FlightScheduleExistException {
        FlightEntity flight;
        try {
            flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight with flight number does not exist!");
        }

        flight.setLayOver(layover);
        FlightSchedulePlanEntity fsp;
        if (recurrency != 7) {
            fsp = new RecurringScheduleEntity(flightNumber, false, flight, endDate, recurrency);
        } else { //MUST CHECK RECURRENCY 7 OR LESSER
            fsp = new RecurringWeeklyScheduleEntity(flightNumber, false, flight, endDate);
        }

        for (FareEntity fare : listOfFares) {
            fsp.getListOfFare().add(fare);
        }

        boolean departBeforeEndDate = departureDateTime.before(endDate);

        while (departBeforeEndDate) {
            try {
                FlightScheduleEntity fe1 = flightScheduleSessionBean.createFlightSchedule(departureDateTime, flightDuration, fsp, flight);
                //link flight schedule to FSP
                fsp.getListOfFlightSchedule().add(fe1);
            } catch (FlightScheduleExistException ex) {
                throw new FlightScheduleExistException(ex.getMessage());
            }

            //check this issue!
            departureDateTime = (GregorianCalendar) departureDateTime.clone();
            departureDateTime.add(GregorianCalendar.DAY_OF_MONTH, recurrency);
            departBeforeEndDate = departureDateTime.before(endDate);
        }

        for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
            em.persist(fs);
        }

        em.persist(fsp);
        em.flush();

        //return Flight entity
        FlightEntity returnFlight = null;
        if (createReturnFlightSchedule) {

            AirportEntity origin = flight.getFlightRoute().getDestinationLocation();
            AirportEntity destination = flight.getFlightRoute().getOriginLocation();
            try {
                FlightRouteEntity returnFlightRoute = (FlightRouteEntity) em.createNamedQuery("findFlightRoute").setParameter("origin", origin).setParameter("destination", destination).getSingleResult();
                returnFlight = (FlightEntity) em.createNamedQuery("retrieveReturnFlightUsingMainFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
            } catch (NoResultException ex) {
                throw new FlightDoesNotExistException("Flight does not exist!");
            }

            returnFlight.setLayOver(layover);

            //create return FSP
            FlightSchedulePlanEntity returnFSP;
            if (recurrency != 7) {
                returnFSP = new RecurringScheduleEntity(returnFlight.getFlightNumber(), false, returnFlight, endDate, recurrency);
            } else { //MUST CHECK RECURRENCY 7 OR LESSER
                returnFSP = new RecurringWeeklyScheduleEntity(returnFlight.getFlightNumber(), false, returnFlight, endDate);
            }

//            em.persist(returnFSP);
            for (FareEntity fare : listOfFares) {
                returnFSP.getListOfFare().add(fare);
            }

            // GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight
            for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
                GregorianCalendar returnFlightDeparture = (GregorianCalendar) fs.getArrivalDateTime().clone();
                returnFlightDeparture.add(GregorianCalendar.MINUTE, layover);
                try {
                    FlightScheduleEntity fe1 = flightScheduleSessionBean.createFlightSchedule(returnFlightDeparture, flightDuration, returnFSP, returnFlight);
                    //link flight schedule to FSP
                    returnFSP.getListOfFlightSchedule().add(fe1);
                    //get seatingplan - need get seating plan from aircraft config, add to flight schedule
//                    List<SeatEntity> returnSeatingPlan = returnFlight.getAircraftConfig().getSeatingPlan();
//
//                    for (SeatEntity seat : returnSeatingPlan) {
//                        fe1.getSeatingPlan().add(seat);
//                    }
                } catch (FlightScheduleExistException ex) {
                    throw new FlightScheduleExistException(ex.getMessage());
                }

            }

            for (FlightScheduleEntity fs : returnFSP.getListOfFlightSchedule()) {
                em.persist(fs);
            }

            fsp.setReturnFlightSchedulePlan(returnFSP);

            em.persist(returnFSP);
            em.flush();

        }

        if (createReturnFlightSchedule && returnFlight != null) {
            return "Flight Schedule Plan created for " + flight.getFlightNumber() + " \nReturn Flight Schedule Plan created for " + returnFlight.getFlightNumber();
        } else {
            return "Flight Schedule Plan created for " + flight.getFlightNumber();
        }
    }

    @Override
    public List<FlightSchedulePlanEntity> viewAllFlightSchedulePlan() throws FlightSchedulePlanIsEmptyException {

        List<FlightSchedulePlanEntity> listOfFsp = em.createQuery("SELECT f FROM FlightSchedulePlanEntity f, IN (f.listOfFlightSchedule) fs  WHERE f.isDeleted = FALSE AND f.flightEntity.isMainRoute = TRUE ORDER BY f.flightEntity.flightNumber ASC, fs.departureDateTime DESC").getResultList();
//        List<FlightSchedulePlanEntity> listOfFsp = em.createQuery("SELECT f FROM FlightSchedulePlanEntity f, IN (f.listOfFlightSchedule) fs  WHERE f.returnFlightSchedulePlan != NULL AND f.isDeleted = FALSE AND f.flightEntity.isMainRoute = TRUE ORDER BY f.flightEntity.flightNumber ASC, fs.departureDateTime DESC").getResultList();

        if (listOfFsp.isEmpty()) {
            throw new FlightSchedulePlanIsEmptyException("No flight schedule plan exists");
        }

        List<FlightSchedulePlanEntity> fspToReturn = new ArrayList<>();
        int counter = 0;
//        for (int i = 0; i < listOfFsp.size(); i++) {
//            if (listOfFsp.get(i).getFlightEntity().isIsMainRoute() && listOfFsp.get(i).getReturnFlightSchedulePlan() != null && !listOfAllFsp.contains(listOfFsp.get(i).getReturnFlightSchedulePlan()) && !listOfAllFsp.contains(listOfFsp.get(i))) {
//                System.out.println("=====================================" + listOfFsp.get(i).getFlightNumber() + " =======================================");
//                listOfAllFsp.add(counter, listOfFsp.get(i));
//                listOfAllFsp.add(counter + 1, listOfFsp.get(i).getReturnFlightSchedulePlan());
//                counter += 2;
//            } else if (!listOfAllFsp.contains(listOfFsp.get(i)) && listOfFsp.get(i).getFlightEntity().isIsMainRoute() && listOfFsp.get(i).getReturnFlightSchedulePlan() == null) {
//                System.out.println("=====================================" + listOfFsp.get(i).getFlightNumber() + " =======================================");
//                listOfAllFsp.add(counter, listOfFsp.get(i));
//                counter++;
//            }
//        }

        List<FlightScheduleEntity> listOfFs = new ArrayList<>();

        for (FlightSchedulePlanEntity fsp : listOfFsp) {
            if (!fspToReturn.contains(fsp) && fsp.getFlightEntity().isIsMainRoute() == true && !listOfFs.contains(fsp.getListOfFlightSchedule().get(0))) {
                listOfFs.addAll(fsp.getListOfFlightSchedule());
                fspToReturn.add(fsp);
                if (fsp.getReturnFlightSchedulePlan() != null && fsp.getReturnFlightSchedulePlan().isIsDeleted() == false) {
                    fspToReturn.add(fsp.getReturnFlightSchedulePlan());
                }
            }
        }

        return fspToReturn;

    }

    private void sortFlightSchedule(FlightSchedulePlanEntity fsp) {
        List<FlightScheduleEntity> listOfFlightSchedule = fsp.getListOfFlightSchedule();

        for (int i = 0; i < fsp.getListOfFlightSchedule().size(); i++) {
            for (int j = i + 1; j < fsp.getListOfFlightSchedule().size(); j++) {
                FlightScheduleEntity fs1 = fsp.getListOfFlightSchedule().get(i);
                FlightScheduleEntity fs2 = fsp.getListOfFlightSchedule().get(j);
                if (fs1.getDepartureDateTime().after(fs2.getDepartureDateTime())) {
                    FlightScheduleEntity temp = fsp.getListOfFlightSchedule().get(i);
                    fsp.getListOfFlightSchedule().set(i, fs2);
                    fsp.getListOfFlightSchedule().set(j, temp);
                }

            }
        }

    }

    @Override
    public FlightSchedulePlanEntity viewFlightSchedulePlan(Long fspId) throws FlightSchedulePlanDoesNotExistException {
//            FlightSchedulePlanEntity fsp = (FlightSchedulePlanEntity) em.createNamedQuery("queryFSPwithFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
        FlightSchedulePlanEntity fsp = em.find(FlightSchedulePlanEntity.class, fspId);
        if (fsp == null) {
            throw new FlightSchedulePlanDoesNotExistException("Flight schedule plan does not exist!");
        }

        fsp.getListOfFlightSchedule().size();
        fsp.getListOfFare().size();

        for (FlightScheduleEntity fs : fsp.getListOfFlightSchedule()) {
            fs.getSeatingPlan().size();
        }

        if (fsp.getReturnFlightSchedulePlan() != null) {
            FlightSchedulePlanEntity returnFsp = fsp.getReturnFlightSchedulePlan();
            returnFsp.getListOfFlightSchedule().size();
            for (FlightScheduleEntity fs : returnFsp.getListOfFlightSchedule()) {
                fs.getSeatingPlan().size();
            }
        }

        fsp.getFlightEntity();
        fsp.getFlightEntity().getFlightRoute().getOriginLocation();
        fsp.getFlightEntity().getFlightRoute().getDestinationLocation();

        return fsp;

    }

    @Override
    public void updateSingleFspDate(FlightEntity flight, GregorianCalendar newDepartureDateTime, FlightSchedulePlanEntity specificFsp, FlightScheduleEntity fs) throws FlightSchedulePlanDoesNotExistException, FlightScheduleExistException, FlightDoesNotExistException {

        flight = em.find(FlightEntity.class, flight.getFlightId());

        specificFsp = viewFlightSchedulePlan(specificFsp.getFlightSchedulePlanId());

        fs = em.find(FlightScheduleEntity.class, fs.getFlightScheduleId());

        FlightScheduleEntity newFs = flightScheduleSessionBean.updateFlightSchedule(newDepartureDateTime, fs.getFlightDuration(), specificFsp, flight, fs);

        GregorianCalendar replacementDepartDateTime = (GregorianCalendar) newFs.getDepartureDateTime().clone();
        GregorianCalendar replacementArrivalDateTime = (GregorianCalendar) newFs.getArrivalDateTime().clone();

        fs.setDepartureDateTime(replacementDepartDateTime);
        fs.setArrivalDateTime(replacementArrivalDateTime);

//        int indexOfOldFs = specificFsp.getListOfFlightSchedule().indexOf(fs);
//        specificFsp.getListOfFlightSchedule().set(indexOfOldFs, fs);
        //do i need to merge FS
//        flight = em.find(FlightEntity.class, flight.getFlightId());
//        if (flight == null) {
//            throw new FlightDoesNotExistException("Flight does not exist!");
//        }
        //potential fault - flight unmanaged instancce
//        if (flight.getReturnFlight() != null) {
//            int flightDuration = fs.getFlightDuration();
//            int layOver = flight.getLayOver();
//
//            FlightEntity returnFlight = null;
//            try {
//                String flightNumber = specificFsp.getFlightNumber();
//                returnFlight = (FlightEntity) em.createNamedQuery("retrieveReturnFlightUsingMainFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
//            } catch (NoResultException ex) {
//                throw new FlightDoesNotExistException("Flight does not exist!");
//            }
//
//            FlightSchedulePlanEntity returnFSP = specificFsp.getReturnFlightSchedulePlan();
//            FlightScheduleEntity returnFs = returnFSP.getListOfFlightSchedule().get(0);
//
//            GregorianCalendar newReturnDepart = (GregorianCalendar) replacementArrivalDateTime.clone();
//            newReturnDepart.add(GregorianCalendar.MINUTE, layOver);
//
//            FlightScheduleEntity newReturnFs = flightScheduleSessionBean.updateFlightSchedule(newReturnDepart, flightDuration, returnFSP, returnFlight, returnFs);
//
//            newReturnDepart = (GregorianCalendar) newReturnFs.getDepartureDateTime().clone();
//            GregorianCalendar newReturnArrive = (GregorianCalendar) newReturnFs.getArrivalDateTime().clone();
//            returnFs.setDepartureDateTime(newReturnDepart);
//            returnFs.setArrivalDateTime(newReturnArrive);
//
//        }
    }

    @Override
    public void mergeFSPForFare(FlightSchedulePlanEntity fsp) throws FlightSchedulePlanDoesNotExistException {
        viewFlightSchedulePlan(fsp.getFlightSchedulePlanId());

        for (FareEntity fare : fsp.getListOfFare()) {
            em.merge(fare);
        }

        em.merge(fsp);
        em.flush();

    }

    @Override
    public void mergeFSPWithNewFlightDuration(int newFlightDuration, FlightSchedulePlanEntity fsp, GregorianCalendar updatedDepartureDateTime, FlightScheduleEntity fs) throws FlightSchedulePlanDoesNotExistException, FlightScheduleExistException, FlightDoesNotExistException {
        fsp = viewFlightSchedulePlan(fsp.getFlightSchedulePlanId());
        fs = em.find(FlightScheduleEntity.class, fs.getFlightScheduleId());

        FlightScheduleEntity newFs = flightScheduleSessionBean.updateFlightSchedule(updatedDepartureDateTime, newFlightDuration, fsp, fsp.getFlightEntity(), fs);

        GregorianCalendar newDepartureDateTime = (GregorianCalendar) newFs.getDepartureDateTime().clone();
        GregorianCalendar newArrivalDateTime = (GregorianCalendar) newFs.getArrivalDateTime().clone();
        fs.setFlightDuration(newFlightDuration);
        fs.setDepartureDateTime(newDepartureDateTime);
        fs.setArrivalDateTime(newArrivalDateTime);

//        FlightEntity returnFlight = null;
//        if (fsp.getFlightEntity().getReturnFlight() != null) {
//            try {
//                String flightNumber = fsp.getFlightNumber();
//                returnFlight = (FlightEntity) em.createNamedQuery("retrieveReturnFlightUsingMainFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
//            } catch (NoResultException ex) {
//                throw new FlightDoesNotExistException("Flight does not exist!");
//            }
//            int layover = fsp.getFlightEntity().getLayOver();
//
//            FlightSchedulePlanEntity returnFsp = fsp.getReturnFlightSchedulePlan();
//            // GregorianCalendar departureDateTime, Integer flightDuration, FlightSchedulePlanEntity fsp, FlightEntity flight
//            int index = 0;
//            GregorianCalendar returnFlightDeparture = (GregorianCalendar) fs.getArrivalDateTime().clone();
//            returnFlightDeparture.add(GregorianCalendar.MINUTE, layover);
//            FlightScheduleEntity fe1 = flightScheduleSessionBean.updateFlightSchedule(returnFlightDeparture, newFlightDuration, returnFsp, returnFlight, fs);
//            //link flight schedule to FSP
//            FlightScheduleEntity returnFs = returnFsp.getListOfFlightSchedule().get(index);
//
//            GregorianCalendar newDepartDateTime = (GregorianCalendar) fe1.getDepartureDateTime().clone();
//            GregorianCalendar newArriveDateTime = (GregorianCalendar) fe1.getArrivalDateTime().clone();
//
//            returnFs.setDepartureDateTime(newDepartDateTime);
//            returnFs.setArrivalDateTime(newArriveDateTime);
//            returnFs.setFlightDuration(newFlightDuration);
//        }
    }

    @Override
    public void addNewFlightSchedule(GregorianCalendar departureDateTime, FlightSchedulePlanEntity fsp) throws FlightSchedulePlanDoesNotExistException, FlightScheduleExistException {
        fsp = em.find(FlightSchedulePlanEntity.class, fsp.getFlightSchedulePlanId());
        if (fsp == null) {
            throw new FlightSchedulePlanDoesNotExistException("Flight schedulde plan does not exist!");
        } else {
            FlightEntity flight = fsp.getFlightEntity();
            int flightDuration = fsp.getListOfFlightSchedule().get(0).getFlightDuration();

            FlightScheduleEntity newFsToAdd = flightScheduleSessionBean.createFlightSchedule(departureDateTime, flightDuration, fsp, flight);
            em.persist(newFsToAdd);
            em.flush();

            fsp.getListOfFlightSchedule().add(newFsToAdd);

            FlightEntity returnFlight = flight.getReturnFlight();

            if (returnFlight != null) {
                FlightSchedulePlanEntity returnFsp = fsp.getReturnFlightSchedulePlan();
                GregorianCalendar returnFlightDepartureDate = (GregorianCalendar) newFsToAdd.getArrivalDateTime().clone();
                returnFlightDepartureDate.add(GregorianCalendar.MINUTE, flight.getLayOver());

                FlightScheduleEntity newReturnFs = flightScheduleSessionBean.createFlightSchedule(returnFlightDepartureDate, flightDuration, returnFsp, returnFlight);

                em.persist(newReturnFs);
                em.flush();

                returnFsp.getListOfFlightSchedule().add(newReturnFs);

            }

        }
    }

    @Override
    public FlightScheduleEntity getFlightScheduleUsingID(Long fsId) throws FlightScheduleDoesNotExistException {
        FlightScheduleEntity fs = em.find(FlightScheduleEntity.class, fsId);
        if (fs == null) {
            throw new FlightScheduleDoesNotExistException("Flight Schedule does not exist!");
        } else {
            fs.getFlightSchedulePlan();
            FlightSchedulePlanEntity fsp = fs.getFlightSchedulePlan();
            fsp.getListOfFare().size();
            fsp.getListOfFlightSchedule().size();
            fsp.getFlightEntity();
            fs.getSeatingPlan().size();
            return fs;
        }
    }

    @Override
    public void deleteFlightSchedule(Long fsId) throws FlightScheduleDoesNotExistException {
        FlightScheduleEntity fs = em.find(FlightScheduleEntity.class, fsId);
        if (fs == null) {
            throw new FlightScheduleDoesNotExistException("Flight Schedule does not exist!");
        } else {
            fs.getFlightSchedulePlan().getListOfFlightSchedule().remove(fs);
            fs.getSeatingPlan().clear();
            em.remove(fs);

        }

    }

    @Override
    public FareEntity retrieveFare(Long fareId) throws FareDoesNotExistException {
        FareEntity fare = em.find(FareEntity.class, fareId);
        if (fare == null) {
            throw new FareDoesNotExistException("Fare does not exist!");
        } else {
            return fare;
        }
    }

    @Override
    public void mergeFare(FareEntity fare) throws FareDoesNotExistException {
        try {
            em.merge(fare);
            em.flush();
        } catch (IllegalArgumentException ex) {
            throw new FareDoesNotExistException("Fare does not exist!");
        }
    }

    @Override
    public void deleteFare(Long fareId, FlightSchedulePlanEntity fsp) throws FareDoesNotExistException, FlightSchedulePlanDoesNotExistException, FareCannotBeDeletedException {
        FareEntity fare = em.find(FareEntity.class, fareId);
        fsp = em.find(FlightSchedulePlanEntity.class, fsp.getFlightSchedulePlanId());

        if (fare == null) {
            throw new FareDoesNotExistException("Fare does not exist!");
        } else if (fsp == null) {
            throw new FlightSchedulePlanDoesNotExistException("Flight schedule plan does not exist!");
        } else {
            boolean canDelete = false;
            for (FareEntity fareInList : fsp.getListOfFare()) {
                if (fareInList.getCabinType().equals(fare.getCabinType())) {
                    fsp.getListOfFare().remove(fare);
                    if (fsp.getReturnFlightSchedulePlan() != null) {
                        fsp.getReturnFlightSchedulePlan().getListOfFare().remove(fare);
                    }

                    em.remove(fare);
                    canDelete = true;
                    break;
                }
            }

            if (!canDelete) {
                throw new FareCannotBeDeletedException("You need at least one fare per cabin type!");
            }

        }
    }

    @Override
    public void updateRecurrentFSP(String flightNumber, GregorianCalendar departureDateTime, GregorianCalendar endDate, Integer flightDuration, Integer recurrency, FlightSchedulePlanEntity currentFsp) throws FlightDoesNotExistException, FlightScheduleExistException, FlightSchedulePlanDoesNotExistException {
        FlightEntity flight;
        try {
            flight = (FlightEntity) em.createNamedQuery("retrieveFlightUsingFlightNumber").setParameter("flightNum", flightNumber).getSingleResult();
        } catch (NoResultException ex) {
            throw new FlightDoesNotExistException("Flight with flight number does not exist!");
        }

        currentFsp = em.find(FlightSchedulePlanEntity.class, currentFsp.getFlightSchedulePlanId());
        if (currentFsp == null) {
            throw new FlightSchedulePlanDoesNotExistException("Flight Schedule Plan does not exist!");
        }

        boolean departBeforeEndDate = departureDateTime.before(endDate);

//        if(recurrency == 7){
//            currentFsp = (RecurringWeeklyScheduleEntity) currentFsp;
//        } else {
//            currentFsp = (RecurringScheduleEntity) currentFsp;
//        }
//        
        int counter = 0;

        while (departBeforeEndDate) {
            try {
                FlightScheduleEntity fe1 = flightScheduleSessionBean.updateReccurentFlightSchedule(departureDateTime, flightDuration, currentFsp, flight);

                //link flight schedule to FSP
                if (counter < currentFsp.getListOfFlightSchedule().size()) {
                    FlightScheduleEntity currentFs = currentFsp.getListOfFlightSchedule().get(counter);

                    GregorianCalendar newDepartureDate = (GregorianCalendar) fe1.getDepartureDateTime().clone();
                    GregorianCalendar newArrivalDate = (GregorianCalendar) fe1.getArrivalDateTime().clone();

                    currentFs.setDepartureDateTime(newDepartureDate);
                    currentFs.setArrivalDateTime(newArrivalDate);
                    currentFs.setFlightDuration(flightDuration);
                    counter++;
                } else {
                    //need to add addtional schedules into fsp
                    em.persist(fe1);
                    em.flush();

                    currentFsp.getListOfFlightSchedule().add(fe1);
                }

            } catch (FlightScheduleExistException ex) {
                throw new FlightScheduleExistException(ex.getMessage());
            }

            //check this issue!
            departureDateTime = (GregorianCalendar) departureDateTime.clone();
            departureDateTime.add(GregorianCalendar.DAY_OF_MONTH, recurrency);
            departBeforeEndDate = departureDateTime.before(endDate);
        }

    }
}
