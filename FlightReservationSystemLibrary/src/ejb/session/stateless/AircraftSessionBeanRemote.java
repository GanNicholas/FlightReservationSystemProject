/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.CabinClassConfigurationEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AircraftConfigurationNotExistException;
import util.exception.CabinClassExceedMaxCapacity;
import util.exception.NoAircraftTypeAvailableException;

/**
 *
 * @author sohqi
 */
@Remote
public interface AircraftSessionBeanRemote {

    public Long createAircraftConfiguration(AircraftConfigurationEntity aircraftConfigurationEntity, AircraftTypeEntity aircarAircraftTypeEntity, List<CabinClassConfigurationEntity> listOfCabinClassConfig);

    public List<AircraftConfigurationEntity> viewAircraftConfiguration();

    public AircraftConfigurationEntity viewDetailAircraftConfiguration(Long index) throws AircraftConfigurationNotExistException;

    public List<AircraftTypeEntity> getAircraftTypes() throws NoAircraftTypeAvailableException;

    public AircraftTypeEntity getAircraftType(Long id) throws NoAircraftTypeAvailableException;
}
