/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.AircraftTypeEntity;
import entity.CabinClassConfigurationEntity;
import entity.CustomerEntity;
import entity.FRSCustomerEntity;
import entity.FlightEntity;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.UserRole;
import util.exception.CustomerExistException;
import util.exception.CustomerLoginInvalid;

/**
 *
 * @author sohqi
 */
public class Customer {

    CustomerEntity customer = null;
    private CustomerSessionBeanRemote customerSessionBean;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private FlightSessionBeanRemote flightSessionBean;

    public Customer() {

        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public Customer(CustomerSessionBeanRemote customerSessionBean, FlightSessionBeanRemote flightSessionBean) {
        this();
        this.customerSessionBean = customerSessionBean;
        this.flightSessionBean = flightSessionBean;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        System.out.println("1. Register Customer");
        System.out.println("2. Customer Login");
        String input = sc.nextLine();
        //while (true) {
        if (input.equals("1")) {
            registerCustomer();
        } else if (input.equals("2")) {
            customerLogin();
        }
        //}
    }

    public void registerCustomer() {//need to do validation factory to for bean validation @unique for user name;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter First Name:");
        String fName = sc.nextLine().trim();
        System.out.println("Enter Last Name:");
        String lName = sc.nextLine().trim();
        System.out.println("Enter Email:");
        String email = sc.nextLine().trim();
        System.out.println("Enter Mobile phone:");
        String mobilePhone = sc.nextLine().trim();
        System.out.println("Enter Address:");
        String address = sc.nextLine().trim();
        System.out.println("Enter Login:");
        String login = sc.nextLine().trim();
        System.out.println("Enter Password:");
        String password = sc.nextLine().trim();

        CustomerEntity customer = new FRSCustomerEntity(login, password, UserRole.CUSTOMER, fName, lName, email, mobilePhone, address);
        Set<ConstraintViolation<CustomerEntity>> constraintViolationsCabin = validator.validate(customer);
        if (!constraintViolationsCabin.isEmpty()) {
            System.out.println("\nInput data validation error!:");
            for (ConstraintViolation constraintViolation : constraintViolationsCabin) {
                System.out.println("\t" + constraintViolation.getMessage());

            }

        } else {
            try {
                Long id = customerSessionBean.registerCustomer(customer);
                System.out.println("You have successfully created an account with the id" + id);
            } catch (CustomerExistException ex) {
                System.out.println("The account is already exist");
            }
        }
    }

    public void customerLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your user name");
        String username = sc.nextLine();
        System.out.println("Please enter your password");
        String password = sc.nextLine();

        try {
            if (username.length() > 5 && username.length() <= 16 && password.length() > 7 && password.length() <= 16) {
                customer = customerSessionBean.customerLogin(username, password);
                System.out.println("You have successfully login");
            } else {
                System.out.println("Please fill in your login credential. Username should have at least 6 characters and maximum of 16 characters. Password should have at least 8 character and maximum of 16 characters");
            }
        } catch (CustomerLoginInvalid ex) {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    public void SearchFlight() { // no validation yet
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter trip type:");
        String tripType = sc.nextLine();
        System.out.println("Enter departure airport:");
        String departureAirport = sc.nextLine();
        System.out.println("Enter destination airport:");
        String destinationAirport = sc.nextLine();
        System.out.println("Enter depature date:");
        String departureDate = sc.nextLine();
        System.out.println("Enter return date:");
        String returnDate = sc.nextLine();
        System.out.println("Enter number of passenger:");
        String passenger = sc.nextLine();
        List<FlightEntity> listOfFlight = flightSessionBean.listOfFlightRecords(tripType, departureAirport, destinationAirport, departureDate, returnDate, passenger);
        for (int i = 0; i < listOfFlight.size(); i++) {
            for(int j = 0 ; j<listOfFlight.size(); j++){
               // if(listOfFlight.get(j).getFlightRoute().getDestinationLocation().equalsIgnoreCase(departureAirport) && listOfFlight.get(i).getFlightRoute().getOriginLocation().equals(listOfFlight.get(j).getFlightRoute().get))
            }
        }
    }
}
