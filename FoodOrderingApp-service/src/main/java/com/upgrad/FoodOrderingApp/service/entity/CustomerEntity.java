package com.upgrad.FoodOrderingApp.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "customer")
@NamedQueries({
    @NamedQuery(name = "customerByUUID", query = "select c from CustomerEntity c where c.uuid = :uuid"),
    @NamedQuery(name = "customerByContactNumber", query = "select c from CustomerEntity c where c.contactNumber = :contactNumber"),
})

public class CustomerEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "uuid")
  @Size(max = 200)
  @NotNull
  private String uuid;

  @Column(name = "firstname")
  @Size(max = 30)
  @NotNull
  private String firstName;

  @Column(name = "lastname")
  @Size(max = 30)
  private String lastName;

  @Column(name = "email")
  @Size(max = 50)
  @NotNull
  private String email;

  @Column(name = "contact_number")
  @Size(max = 30)
  @NotNull
  private String contactNumber;

  @Column(name = "password")
  @Size(max = 255)
  @NotNull
  private String password;

  @Column(name = "salt")
  @Size(max = 255)
  @NotNull
  private String salt;

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<CustomerAddressEntity> customerAddresses = new ArrayList<>();

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderEntity> orders = new ArrayList<>();

  @OneToMany
  @JoinTable(name = "customer_address", joinColumns = @JoinColumn(name = "customer_id"),
      inverseJoinColumns = @JoinColumn(name = "address_id"))
  private List<AddressEntity> addresses = new ArrayList<>();

  public CustomerEntity() {
  }

  public List<AddressEntity> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<AddressEntity> addresses) {
    this.addresses = addresses;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public List<CustomerAddressEntity> getCustomerAddresses() {
    return customerAddresses;
  }

  public void setCustomerAddresses(List<CustomerAddressEntity> customerAddresses) {
    this.customerAddresses = customerAddresses;
  }

  public List<OrderEntity> getOrders() {
    return orders;
  }

  public void setOrders(List<OrderEntity> orders) {
    this.orders = orders;
  }
}
