package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

  @Autowired
  private AddressDao addressDao;

  @Autowired
  private CustomerAddressDao customerAddressDao;

  @Autowired
  private StateDao stateDao;

  /**
   * Returns state for a given UUID
   *
   * @param stateUUID UUID of the state entity
   * @return StateEntity object.
   * @throws AddressNotFoundException If given uuid does not exist in database.
   */
  public StateEntity getStateByUUID(final String stateUUID) throws AddressNotFoundException {

    StateEntity state = stateDao.findStateByUUID(stateUUID);
    if (state == null) {
      throw new AddressNotFoundException("ANF-002", "No state by this id");
    }
    return state;
  }

  /**
   * This method implements the logic for 'saving the address' endpoint.
   *
   * @param address new address will be created from given AddressEntity object.
   * @param state   saves the address of the given customer.
   * @return AddressEntity object.
   * @throws SaveAddressException exception if any of the validation fails on customer details.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AddressEntity saveAddress(AddressEntity address, StateEntity state)
      throws AddressNotFoundException, SaveAddressException {
    if (addressFieldsEmpty(address)) {
      throw new SaveAddressException("SAR-001", "No field can be empty");
    }
    if (!validPincode(address.getPincode())) {
      throw new SaveAddressException("SAR-002", "Invalid pincode");
    }
    address.setState(state);

    return addressDao.saveAddress(address);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public CustomerAddressEntity saveCustomerAddress(CustomerEntity customer, AddressEntity address) {
    // saves the provided address of the customer
    CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
    customerAddressEntity.setCustomer(customer);
    customerAddressEntity.setAddress(address);
    CustomerAddressEntity createdCustomerAddress = customerAddressDao
        .saveCustomerAddress(customerAddressEntity);
    return createdCustomerAddress;
  }

  /**
   * Returns all the addresses of a given customer.
   *
   * @param customer Customer whose addresses are to be returned.
   * @return List<AddressEntity> object.
   */
  public List<AddressEntity> getAllAddress(CustomerEntity customer) {
    // Gets list of addresses based on customer entity
    List<AddressEntity> addressEntities = new LinkedList<>();
    List<CustomerAddressEntity> customerAddressEntities = addressDao
        .getAddressesByCustomer(customer);
    if (customerAddressEntities != null) {
      customerAddressEntities.forEach(customerAddressEntity ->
          addressEntities.add(customerAddressEntity.getAddress()));
    }
    return addressEntities;
  }

  // Checks if any of the required address fields are empty and returns a boolean response
  private boolean addressFieldsEmpty(AddressEntity address) {
    return address.getFlatBuilNo().isEmpty() ||
        address.getLocality().isEmpty() ||
        address.getCity().isEmpty() ||
        address.getPincode().isEmpty();
  }

  // Verifies if the provided PinCode is valid
  private boolean validPincode(String pincode) throws SaveAddressException {
    Pattern p = Pattern.compile("\\d{6}\\b");
    Matcher m = p.matcher(pincode);
    return (m.find() && m.group().equals(pincode));

  }

  /**
   * This method implements logic for getting the Address using address uuid.
   *
   * @param addressId Address UUID.
   * @param customer  Customer whose addresses has to be fetched.
   * @return AddressEntity object.
   * @throws AddressNotFoundException     If any validation on address fails.
   * @throws AuthorizationFailedException If any validation on customer fails.
   */
  public AddressEntity getAddressByUUID(final String addressId, final CustomerEntity customer)
      throws AddressNotFoundException, AuthorizationFailedException {

    if (addressId == null) {
      throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
    }
    AddressEntity address = addressDao.getAddressByAddressId(addressId);
    if (address == null) {
      throw new AddressNotFoundException("ANF-003", "No address by this id");
    }

    // Queries for Customer address based on address Entity
    CustomerAddressEntity customerAddressEntity = customerAddressDao
        .getCustomerAddressByAddress(address);
    if (!customerAddressEntity.getCustomer().getUuid().equals(customer.getUuid())) {
      throw new AuthorizationFailedException("ATHR-004",
          "You are not authorized to view/update/delete any one else's address");
    }
    return address;
  }

  /**
   * Deletes given address from database if no orders placed using the given address.
   *
   * @param addressEntity Address to delete.
   * @return AddressEntity type object.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AddressEntity deleteAddress(final AddressEntity addressEntity) {
    // Deletes the corresponding address from the database
    AddressEntity deletedAddress = addressDao.deleteAddress(addressEntity);
    return deletedAddress;
  }

  /**
   * This method implements the logic to get All the States from database.
   *
   * @return List<StateEntity> object.
   */
  public List<StateEntity> getAllStates() {
    // fetches all the states from the DB
    List<StateEntity> states = stateDao.getAllStates();
    return states;
  }
}