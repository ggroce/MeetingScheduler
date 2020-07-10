package com.meetingscheduler.Model;

public class Customer {
    private int customerId;
    private String customerName;
    private int addressId;
    private int active;
    private  Address address = new Address();
    private City city = new City();
    private Country country = new Country();

    public Customer() {

    }

    public Customer(int customerId, String customerName, String address1, String address2, String postalCode,
                    String phone, String city, String country) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.setAddress1(address1);
        this.setAddress2(address2);
        this.setPostalCode(postalCode);
        this.setPhone(phone);
        this.setCity(city);
        this.setCountry(country);
    }

    public Customer(int customerId, String customerName, int addressId) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String toString() {
        return customerName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getAddress1() {
        return this.address.getAddress();
    }

    public void setAddress1(String address1) {
        this.address.setAddress(address1);
    }

    public String getAddress2() {
        return this.address.getAddress2();
    }

    public void setAddress2(String address2) {
        this.address.setAddress2(address2);
    }

    public String getPostalCode() {
        return this.address.getPostalCode();
    }

    public void setPostalCode(String postalCode) {
        this.address.setPostalCode(postalCode);
    }

    public String getPhone() {
        return this.address.getPhone();
    }

    public void setPhone(String phone) {
        this.address.setPhone(phone);
    }

    public String getCity() {
        return this.city.getCity();
    }

    public void setCity(String city) {
        this.city.setCity(city);
    }

    public String getCountry() {
        return this.country.getCountry();
    }

    public void setCountry(String country) {
        this.country.setCountry(country);
    }

    public int getCityId() {
        return this.address.getCityId();
    }

    public void setCityId(int cityId) {
        this.address.setCityId(cityId);
    }

    public int getCountryId() {
        return this.country.getCountryId();
    }

    public void setCountryId(int countryId) {
        this.country.setCountryId(countryId);
    }
}
