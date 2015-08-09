package com.ankoma88.personnelsystem.server.dao.interfaces;


import java.sql.Connection;
import java.sql.SQLException;

/** DAO factory for interaction with database */
public interface DaoFactory<Context> {

    public Connection getConnection() throws SQLException;
    public EmployeeDao getEmployeeDao(Connection connection);
}
