package com.ankoma88.personnelsystem.server.dao.interfaces;

import com.ankoma88.personnelsystem.server.dao.exceptions.PersistenceException;
import com.ankoma88.personnelsystem.model.Employee;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeDao {

    Employee create(Employee e) throws PersistenceException;
    Employee read(int key) throws PersistenceException;
    Employee update(Employee employee) throws PersistenceException;
    void delete(int key) throws PersistenceException;
    List<Employee> getAll() throws SQLException;

    List<Employee> getSupervisors() throws SQLException;

    List<Employee> getSubordinates(int key) throws SQLException;


    void updateCancelSupervisorOfFormerSubordinates(int key) throws SQLException;
}
