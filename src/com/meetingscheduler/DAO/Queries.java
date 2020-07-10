package com.meetingscheduler.DAO;

import com.meetingscheduler.Model.Address;
import com.meetingscheduler.Model.Appointment;
import com.meetingscheduler.Model.Customer;
import com.meetingscheduler.Model.User;
import com.meetingscheduler.Utils.TimeFunctions;
import com.meetingscheduler.ViewController.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Queries {

    private static final String QUERY_ALL_CUSTOMERS = "SELECT customer.customerId, customer.customerName, " +
            "address.address, address.address2, address.postalCode, address.phone, city.city, country.country " +
            "FROM customer INNER JOIN address ON customer.addressId = address.addressId " +
            "INNER JOIN city ON address.cityId = city.cityId " +
            "INNER JOIN country ON city.countryId = country.countryId";
    private static final String QUERY_ALL_USERS = "SELECT userId, username, password FROM user";
    private static final String INSERT_COUNTRY = "INSERT INTO country (country, createDate, createdBy, " +
            "lastUpdateBy) VALUES(?, ?, ?, ?)";
    private static final String INSERT_CITY = "INSERT INTO city (city, countryId, createDate, createdBy, " +
            "lastUpdateBy) VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_ADDRESS = "INSERT INTO address (address, address2, cityId, postalCode, " +
            "phone, createDate, createdBy, lastUpdateBy) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_CUSTOMER = "INSERT INTO customer (customerName, addressId, active, " +
            "createDate, createdBy, lastUpdateBy) VALUES(?, ?, ?, ?, ?, ?)";
    private static final String DELETE_CUSTOMER_DATA = "DELETE customer, address, city, country " +
            "FROM customer INNER JOIN address on address.addressId = customer.addressId " +
            "INNER JOIN city on city.cityId = address.cityId " +
            "INNER JOIN country on country.countryId = city.countryId " +
            "WHERE customer.customerId = ?";
    private static final String DELETE_ALL_CUST_APPT = "DELETE FROM appointment WHERE appointment.customerId = ?";
    private static final String UPDATE_CUSTOMER_DATA = "UPDATE customer " +
            "INNER JOIN address on address.addressId = customer.addressId " +
            "INNER JOIN city on city.cityId = address.cityId " +
            "INNER JOIN country on country.countryId = city.countryId " +
            "SET customer.customerName = ?, customer.lastUpdateBy = ?, address.address = ?, " +
            "address.address2 = ?, address.postalCode = ?, address.phone = ?, address.lastUpdateBy = ?, " +
            "city.city = ?, city.lastUpdateBy = ?, country.country = ?, country.lastUpdateBy = ? " +
            "WHERE customer.customerId = ?";
    private static final String QUERY_ALL_APPOINTMENTS = "SELECT * FROM appointment INNER JOIN " +
            "customer on appointment.customerId = customer.customerId";
    private static final String INSERT_APPOINTMENT = "INSERT INTO appointment (customerId, userId, title, description, " +
            "location, contact, type, url, start, end, createDate, createdBy, lastUpdateBy) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_APPOINTMENT_DATA = "DELETE FROM appointment WHERE appointmentId = ?";
    private static final String UPDATE_APPOINTMENT_DATA = "UPDATE appointment SET customerId = ?, userId = ?, " +
            "type = ?, start = ?, end = ?, lastUpdateBy = ? WHERE appointmentId = ?";
    private static final String QUERY_APPOINTMENT_FOR_CUST = "SELECT * FROM appointment WHERE customerId = ?";
    public static final String REPORT_APPT_TYPE_BY_MONTH = "SELECT type as 'Type', COUNT(type) as 'Number of Type', " +
            "MONTHNAME(start) as 'Month' FROM appointment GROUP BY MONTH(start), type";
    public static final String REPORT_APPTS_FOREACH_CONSULTANT = "SELECT customer.customerName as 'Customer Name', appointment.start " +
            "as 'Appt Start', appointment.end as 'Appt End', appointment.type as 'Appt Type', user.username as 'Consultant' " +
            "from customer INNER JOIN appointment ON appointment.customerId = customer.customerId " +
            "INNER JOIN user on user.userId = appointment.userId ORDER BY user.username";
    public static final String REPORT_NUM_OF_APPTS_PER_CUST = "SELECT customer.customerName as 'Customer Name', " +
            "COUNT(customer.customerName) as 'Number of Appointments' from customer INNER JOIN appointment " +
            "ON appointment.customerId = customer.customerId GROUP BY customer.customerName";

    public static User validateUser(String submittedUsername, String submittedPassword) {
        try {
            Statement statement = DBConnection.connection.createStatement();
            ResultSet result = statement.executeQuery(QUERY_ALL_USERS);
            User user = new User();

            while (result.next()) {
                user.setUserId(result.getInt("userId"));
                user.setUserName(result.getString("username").toLowerCase());
                user.setPassword(result.getString("password").toLowerCase());

                if (submittedUsername.equalsIgnoreCase(user.getUserName()) &&
                        submittedPassword.equals(user.getPassword())) {
                    result.close();
                    statement.close();
                    return user;
                }
            }
            result.close();
            statement.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Customer> getAllCustomers() {
        try {
            Statement statement = DBConnection.connection.createStatement();
            ResultSet result = statement.executeQuery(QUERY_ALL_CUSTOMERS);
            ObservableList<Customer> customers = FXCollections.observableArrayList();

            while (result.next()) {
                int customerId = result.getInt("customer.customerId");
                String customerName = result.getString("customer.customerName");
                String address1 = result.getString("address.address");
                String address2 = result.getString("address.address2");
                String postalCode = result.getString("address.postalCode");
                String phone = result.getString("address.phone");
                String city = result.getString("city.city");
                String country = result.getString("country.country");

                Customer customer = new Customer(customerId, customerName, address1, address2,
                        postalCode, phone, city, country);
                customers.add(customer);
            }
            return customers;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addCustomer(Customer customer) {
        try {
            //Begin DB entry by adding records to the DB in appropriate order per the ERD:
            int countryId = addCountry(customer.getCountry());
            int cityId = addCity(customer.getCity(), countryId);
            customer.setCityId(cityId);
            int addressId = addAddress(customer.getAddress());

            PreparedStatement insertIntoCustomer;
            //Setup Customer PreparedStatement with information:
            insertIntoCustomer = DBConnection.connection.prepareStatement(INSERT_CUSTOMER);
            insertIntoCustomer.setString(1, customer.getCustomerName());
            insertIntoCustomer.setInt(2, addressId);
            insertIntoCustomer.setInt(3, 1);
            insertIntoCustomer.setTimestamp(4, TimeFunctions.timeUtc());
            insertIntoCustomer.setString(5, LoginController.currentUser);
            insertIntoCustomer.setString(6, LoginController.currentUser);

            //Attempt to execute PreparedStatement:
            int affectedRows = insertIntoCustomer.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Error when inserting Customer record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int addCountry(String country) {
        PreparedStatement insertIntoCountry;

        //Setup Country PreparedStatement with information:
        try {
            insertIntoCountry = DBConnection.connection.prepareStatement(INSERT_COUNTRY, Statement.RETURN_GENERATED_KEYS);
            insertIntoCountry.setString(1, country);
            insertIntoCountry.setTimestamp(2, TimeFunctions.timeUtc());
            insertIntoCountry.setString(3, LoginController.currentUser);
            insertIntoCountry.setString(4, LoginController.currentUser);

            //Attempt to execute PreparedStatement:
            int affectedRows = insertIntoCountry.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Error when inserting Country record.");
            }

            //Get auto generated ID of new record:
            ResultSet generatedKey = insertIntoCountry.getGeneratedKeys();
            if (generatedKey.next()) {
                return generatedKey.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int addCity(String city, int countryId) {
        PreparedStatement insertIntoCity;

        //Setup City PreparedStatement with information:
        try {
            insertIntoCity = DBConnection.connection.prepareStatement(INSERT_CITY, Statement.RETURN_GENERATED_KEYS);
            insertIntoCity.setString(1, city);
            insertIntoCity.setInt(2, countryId);
            insertIntoCity.setTimestamp(3, TimeFunctions.timeUtc());
            insertIntoCity.setString(4, LoginController.currentUser);
            insertIntoCity.setString(5, LoginController.currentUser);

            //Attempt to execute PreparedStatement:
            int affectedRows = insertIntoCity.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Error when inserting City record.");
            }

            //Get auto generated ID of new record:
            ResultSet generatedKey = insertIntoCity.getGeneratedKeys();
            if (generatedKey.next()) {
                return generatedKey.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int addAddress(Address address) {
        PreparedStatement insertIntoAddress;

        //Setup Address PreparedStatement with information:
        try {
            insertIntoAddress = DBConnection.connection.prepareStatement(INSERT_ADDRESS, Statement.RETURN_GENERATED_KEYS);
            insertIntoAddress.setString(1, address.getAddress());
            insertIntoAddress.setString(2, address.getAddress2());
            insertIntoAddress.setInt(3, address.getCityId());
            insertIntoAddress.setString(4, address.getPostalCode());
            insertIntoAddress.setString(5, address.getPhone());
            insertIntoAddress.setTimestamp(6, TimeFunctions.timeUtc());
            insertIntoAddress.setString(7, LoginController.currentUser);
            insertIntoAddress.setString(8, LoginController.currentUser);

            //Attempt to execute PreparedStatement:
            int affectedRows = insertIntoAddress.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Error when inserting Address record.");
            }

            //Get auto generated ID of new record:
            ResultSet generatedKey = insertIntoAddress.getGeneratedKeys();
            if (generatedKey.next()) {
                return generatedKey.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void deleteCustomer(int customerId) {
        PreparedStatement deleteCustData;

        try {
            deleteCustData = DBConnection.connection.prepareStatement(DELETE_CUSTOMER_DATA);
            deleteCustData.setInt(1, customerId);

            int affectedRows = deleteCustData.executeUpdate();
            if (affectedRows != 4) {
                throw new SQLException("Error when deleting customer!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllCustAppt(int customerId) {
        PreparedStatement deleteCustData;

        try {
            deleteCustData = DBConnection.connection.prepareStatement(DELETE_ALL_CUST_APPT);
            deleteCustData.setInt(1, customerId);
            deleteCustData.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCustomer(Customer customer) {
        PreparedStatement updateCustData;

        try {
            updateCustData = DBConnection.connection.prepareStatement(UPDATE_CUSTOMER_DATA);
            updateCustData.setString(1, customer.getCustomerName());
            updateCustData.setString(2, LoginController.currentUser);
            updateCustData.setString(3, customer.getAddress1());
            updateCustData.setString(4, customer.getAddress2());
            updateCustData.setString(5, customer.getPostalCode());
            updateCustData.setString(6, customer.getPhone());
            updateCustData.setString(7, LoginController.currentUser);
            updateCustData.setString(8, customer.getCity());
            updateCustData.setString(9, LoginController.currentUser);
            updateCustData.setString(10, customer.getCountry());
            updateCustData.setString(11, LoginController.currentUser);
            updateCustData.setInt(12, customer.getCustomerId());

            //Attempt to execute PreparedStatement:
            int affectedRows = updateCustData.executeUpdate();
            if (affectedRows != 4) {
                throw new SQLException("Error when updating records.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Appointment> getAllAppointments() {
        try {
            Statement statement = DBConnection.connection.createStatement();
            ResultSet result = statement.executeQuery(QUERY_ALL_APPOINTMENTS);
            ObservableList<Appointment> appointments = FXCollections.observableArrayList();

            while (result.next()) {
                int appointmentId = result.getInt("appointment.appointmentId");
                int customerId = result.getInt("appointment.customerId");

                String type = result.getString("appointment.type");
                Timestamp start = TimeFunctions.utcTimestampToLocalTimestamp(result.getTimestamp("appointment.start"));
                Timestamp end = TimeFunctions.utcTimestampToLocalTimestamp(result.getTimestamp("appointment.end"));
                String customerName = result.getString("customer.customerName");

                Appointment appointment = new Appointment(appointmentId, customerId, type, start,
                        end, customerName);
                appointments.add(appointment);
            }
            return appointments;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addAppointment(Appointment appointment) {
        PreparedStatement insertIntoAppointment;

        //Setup Address PreparedStatement with information:
        try {
            insertIntoAppointment = DBConnection.connection.prepareStatement(INSERT_APPOINTMENT);
            insertIntoAppointment.setInt(1, appointment.getCustomerId());
            insertIntoAppointment.setInt(2, LoginController.user.getUserId());
            insertIntoAppointment.setString(3, "not needed");
            insertIntoAppointment.setString(4, "not needed");
            insertIntoAppointment.setString(5, "not needed");
            insertIntoAppointment.setString(6, "not needed");
            insertIntoAppointment.setString(7, appointment.getType());
            insertIntoAppointment.setString(8, "not needed");
            insertIntoAppointment.setTimestamp(9, TimeFunctions.localTimestamptoUtcTimestamp(appointment.getStart()));
            insertIntoAppointment.setTimestamp(10, TimeFunctions.localTimestamptoUtcTimestamp(appointment.getEnd()));
            insertIntoAppointment.setTimestamp(11, TimeFunctions.timeUtc());
            insertIntoAppointment.setString(12, LoginController.currentUser);
            insertIntoAppointment.setString(13, LoginController.currentUser);

            //Attempt to execute PreparedStatement:
            int affectedRows = insertIntoAppointment.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Error when inserting Appointment record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAppointment(int appointmentId) {
        PreparedStatement deleteApptData;

        try {
            deleteApptData = DBConnection.connection.prepareStatement(DELETE_APPOINTMENT_DATA);
            deleteApptData.setInt(1, appointmentId);

            int affectedRows = deleteApptData.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Error when deleting appointment!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateAppointment(Appointment appointment) {
        PreparedStatement updateApptData;

        try {
            updateApptData = DBConnection.connection.prepareStatement(UPDATE_APPOINTMENT_DATA);
            updateApptData.setInt(1, appointment.getCustomerId());
            updateApptData.setInt(2, LoginController.user.getUserId());
            updateApptData.setString(3, appointment.getType());
            updateApptData.setTimestamp(4, TimeFunctions.localTimestamptoUtcTimestamp(appointment.getStart()));
            updateApptData.setTimestamp(5, TimeFunctions.localTimestamptoUtcTimestamp(appointment.getEnd()));
            updateApptData.setString(6, LoginController.currentUser);
            updateApptData.setInt(7, appointment.getAppointmentId());

            //Attempt to execute PreparedStatement:
            int affectedRows = updateApptData.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Error when updating records.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean queryAppointmentForCustomer(int customerId) {
        PreparedStatement queryApptforCust;

        try {
            queryApptforCust = DBConnection.connection.prepareStatement(QUERY_APPOINTMENT_FOR_CUST);
            queryApptforCust.setInt(1, customerId);

            ResultSet result = queryApptforCust.executeQuery();
            if(result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String runReport(String reportQuery) {
        try {
            Statement statement = DBConnection.connection.createStatement();
            ResultSet result = statement.executeQuery(reportQuery);
            ResultSetMetaData resultSetMetaData = result.getMetaData();
            int resultColumnCount = resultSetMetaData.getColumnCount();
            String resultString = new String("");

            while (result.next()) {
                for(int i = 1; i <= resultColumnCount; ++i) {
                    resultString += resultSetMetaData.getColumnLabel(i) +": ";
                    resultString += result.getString(i);
                    if(i != resultColumnCount) {
                        resultString += ", ";
                    }
                }
                resultString += "\n";
            }
            result.close();
            statement.close();

            return resultString;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
