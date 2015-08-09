package com.ankoma88.personnelsystem.server.dao.impl;

import com.ankoma88.personnelsystem.server.dao.interfaces.DaoFactory;
import com.ankoma88.personnelsystem.server.dao.interfaces.EmployeeDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.ankoma88.personnelsystem.util.Settings.*;

public class DaoFactoryImpl implements DaoFactory {

    public DaoFactoryImpl() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
    }

    @Override
    public EmployeeDao getEmployeeDao(Connection connection) {
        return new EmployeeDaoImpl(connection);
    }
}

