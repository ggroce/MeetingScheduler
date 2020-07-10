package com.meetingscheduler.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String PROTOCOL = "jdbc";
    private static final String VENDORNAME = ":mysql:";
    private static final String IPADDRESS = "//3.227.166.251/U05wF5";
    private static final String JDBCURL = PROTOCOL + VENDORNAME + IPADDRESS;
    private static final String MYSQLJDBCDRIVER = "com.mysql.jdbc.Driver";
    private static final String USERNAME = "U05wF5";
    private static final String PASSWORD = "53688628485";
    public static Connection connection = null;


    public static Connection createConnection() {
        try {
            Class.forName(MYSQLJDBCDRIVER);
            connection = DriverManager.getConnection(JDBCURL, USERNAME, PASSWORD);
            System.out.println("Connection successful.");
        } catch(ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}

