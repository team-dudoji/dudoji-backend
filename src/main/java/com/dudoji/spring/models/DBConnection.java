package com.dudoji.spring.models;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DBConnection {

    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection con = null;

        Class.forName(driverClassName);

        con = DriverManager.getConnection(url, username, password);

        System.out.println("DB Connection created successfully");
        return con;
    }
}