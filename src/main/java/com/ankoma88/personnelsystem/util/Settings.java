package com.ankoma88.personnelsystem.util;

public abstract class Settings {

    /**
     * Database connectivity settings
     */
    public static final String CONNECTION_URL = "jdbc:sqlserver://localhost:1433;databaseName=PersonnelSystem";
    public static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String USER = "sa";
    public static final String PASSWORD = "Ak221188";

    /**
     * Network settings
     */
    public static final String IP = "127.0.0.1";
    public static final int PORT = 11111;


}
