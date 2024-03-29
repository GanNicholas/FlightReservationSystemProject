/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.CabinClassConfigurationEntity;
import entity.SeatEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AircraftConfigurationNotExistException;
import util.exception.CabinClassExceedMaxCapacity;
import util.exception.NoAircraftTypeAvailableException;

/**
 *
 * @author sohqi
 */
@Stateless
public class AircraftSessionBean implements AircraftSessionBeanRemote, AircraftSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createAircraftConfiguration(AircraftConfigurationEntity aircraftConfigurationEntity, AircraftTypeEntity aircarAircraftTypeEntity, List<CabinClassConfigurationEntity> listOfCabinClassConfig) {
        int totalCabinPeople = 0;
        em.merge(aircarAircraftTypeEntity);
        em.flush();
        aircraftConfigurationEntity.setAircraftType(aircarAircraftTypeEntity);
        em.persist(aircraftConfigurationEntity);
        em.flush();

        //check all the cabin class configuration greater than the aircraftconfiguration max seating capacity
        for (CabinClassConfigurationEntity cabin : listOfCabinClassConfig) {
            String[] getSeatingPerRow = cabin.getSeatingConfig().split("-");
            int numPplPerRow = 0;
            for (int i = 0; i < getSeatingPerRow.length; i++) {
                numPplPerRow += Integer.parseInt(getSeatingPerRow[i]);
            }
            int maxPerCabinType = numPplPerRow * cabin.getNumRows();
            cabin.setAvailableSeats(maxPerCabinType);
            cabin.setBalancedSeats(maxPerCabinType);
            cabin.setReservedSeats(0);
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<CabinClassConfigurationEntity>> errors = validator.validate(cabin);
            if (errors.size() != 0) {
                for (ConstraintViolation error : errors) {
                    //System.out.println("******************Came error*******************************");
                    System.err.println("******* Error with initialisation: " + error.getPropertyPath() + "; " + error.getInvalidValue() + "; " + error.getMessage());
                }
            } else {
                em.persist(cabin);

            }
        }
        // aircraftConfigurationEntity.setCabinClasses(listOfCabinClassConfig);
        aircraftConfigurationEntity.setCabinClasses(listOfCabinClassConfig);
        //generate seat into the database
        SeatEntity s = null;
        String cabinCol = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int seatNumber = 1;
        //System.out.println("aircraftConfigurationEntity.getCabinClasses()" + aircraftConfigurationEntity.getCabinClasses().size());
        for (CabinClassConfigurationEntity cabin : aircraftConfigurationEntity.getCabinClasses()) {

            String[] getSeatingPerRow = cabin.getSeatingConfig().split("-");
            int numPplPerRow = 0;

            for (int i = 0; i < getSeatingPerRow.length; i++) {
                numPplPerRow += Integer.parseInt(getSeatingPerRow[i]);

            }

            for (int noOfRow = 0; noOfRow < cabin.getNumRows(); noOfRow++) {
                for (int col = 0; col < numPplPerRow; col++) {
                    s = new SeatEntity();
                    s.setSeatNumber((seatNumber) + "" + cabinCol.charAt(col));
                    s.setReserved(false);
                    s.setCabinType(cabin.getCabinclassType());
                    em.persist(s);
                    em.flush();
                    aircraftConfigurationEntity.getSeatingPlan().add(s);
                }
                seatNumber++;
            }

        }
        return aircraftConfigurationEntity.getAircraftConfigId();
    }

    @Override
    public List<AircraftConfigurationEntity> viewAircraftConfiguration() {
        Query query = em.createQuery("SELECT a FROM AircraftConfigurationEntity AS a ORDER BY a.aircraftType.aircraftTypeName asc, a.aircraftName asc");
        List<AircraftConfigurationEntity> aircraftConfiguration = query.getResultList();

        return aircraftConfiguration;
    }

    @Override
    public AircraftConfigurationEntity viewDetailAircraftConfiguration(Long index) throws AircraftConfigurationNotExistException {
        AircraftConfigurationEntity aircraft = null;
        try {

            Query query = em.createQuery("SELECT a FROM AircraftConfigurationEntity a WHERE a.aircraftConfigId=:aircraftId").setParameter("aircraftId", index);
            aircraft = (AircraftConfigurationEntity) query.getSingleResult();
            aircraft.getCabinClasses().size();
        } catch (NoResultException ex) {
            throw new AircraftConfigurationNotExistException("No such record");
        }
        return aircraft;
    }

    @Override
    public List<AircraftTypeEntity> getAircraftTypes() throws NoAircraftTypeAvailableException {
        List<AircraftTypeEntity> listOfAircraftType = em.createQuery("SELECT a FROM AircraftTypeEntity a").getResultList();

        if (listOfAircraftType.isEmpty()) {
            throw new NoAircraftTypeAvailableException("No aircraft type available!");
        } else {
            return listOfAircraftType;
        }
    }

    @Override
    public AircraftTypeEntity getAircraftType(Long id) throws NoAircraftTypeAvailableException {
        AircraftTypeEntity ac = em.find(AircraftTypeEntity.class, id);

        if (ac == null) {
            throw new NoAircraftTypeAvailableException("No aircraft type available!");
        } else {
            return ac;
        }
    }

}
