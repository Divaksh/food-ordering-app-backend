package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
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

  //Service Class method that is called to get the Address entity when the Address UUID is passed

  public StateEntity getStateByUUID(final String stateUUID) throws AddressNotFoundException {

    StateEntity state = stateDao.findStateByUUID(stateUUID);
    if (state == null) {
      throw new AddressNotFoundException("ANF-002", "No state by this id");
    }
    return state;
  }

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
}

