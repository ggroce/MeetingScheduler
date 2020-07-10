package com.meetingscheduler.Model;

import java.sql.Timestamp;

public class Appointment {
    private int appointmentId;
    private int customerId;
    private int userId;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String url;
    private Timestamp start;
    private Timestamp end;
    private Customer customer = new Customer();

    public Appointment(int appointmentId, int customerId, String type, Timestamp start, Timestamp end, String customerName) {
        this.appointmentId = appointmentId;
        this.setCustomerId(customerId);
        this.type = type;
        this.start = start;
        this.end = end;
        this.setCustomerName(customerName);
    }

    public Appointment() {

    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getCustomerId() {
        return this.customer.getCustomerId();
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        this.customer.setCustomerId(customerId);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCustomerName() {
        return this.customer.getCustomerName();
    }

    public void setCustomerName(String customerName) {
        this.customer.setCustomerName(customerName);
    }
}
