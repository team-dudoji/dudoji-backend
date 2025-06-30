package com.dudoji.spring.models;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@Slf4j
@Deprecated
public class DBConnection {

    private final DataSource dataSource;

    public DBConnection(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection con = dataSource.getConnection();
        log.info("DB Connection retrieved from pool: {}", con);
        return con;
    }
}