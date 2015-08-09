package com.ankoma88.personnelsystem.server.dao.impl;

import com.ankoma88.personnelsystem.model.Employee;
import com.ankoma88.personnelsystem.server.dao.exceptions.PersistenceException;
import com.ankoma88.personnelsystem.server.dao.interfaces.EmployeeDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class EmployeeDaoImpl implements EmployeeDao {
    private static final Logger log = Logger.getLogger(EmployeeDaoImpl.class.getName());

    private final Connection connection;

    public EmployeeDaoImpl(Connection connection) {
        this.connection = connection;
    }



    public String getReadQuery() {
        return "SELECT * FROM dbo.Employees WHERE id = ?;";
    }

    public String getSelectQuery() {
        return "SELECT * FROM dbo.Employees ";
    }

    public String getCreateQuery() {
        return "INSERT INTO dbo.Employees (full_name, department, supervisor, comment) \n" +
                "VALUES (?, ?, ?, ?);";
    }

    public String getUpdateQuery() {
        return "UPDATE dbo.Employees \n" +
                "SET full_name = ?, department  = ?, supervisor = ?, comment = ? \n" +
                "WHERE id = ?;";
    }

    public String getCancelSupervisorUpdateQuery() {
        return "UPDATE dbo.Employees \n" +
                "SET supervisor = 0 \n" +
                "WHERE supervisor = ?;";
    }

    public String getDeleteQuery() {
        return "DELETE FROM dbo.Employees WHERE id= ?;";
    }

    @Override
    public Employee create(Employee employee) throws PersistenceException {
        if (employee.getId() != null) {
            throw new PersistenceException("Such record already exists");
        }
        String sql = getCreateQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatementForInsert(statement, employee);
            int count = statement.executeUpdate();
            if (count != 1) {
                throw new PersistenceException("More than 1 record modified on persist: " + count);
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setId(generatedKeys.getInt(1));
                } else {
                    throw new PersistenceException("Creating employee failed. More than 1 id obtained");
                }
            }
        } catch (Exception e) {
            throw new PersistenceException();
        }
        log.info("New employee created with id: " + employee.getId() );
        return employee;
    }

    @Override
    public Employee read(int key) throws PersistenceException {
        List<Employee> list;
        String sql = getReadQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, key);
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            throw new PersistenceException("Received more than 1 record: " + list.size());
        }

        return list.listIterator().next();
    }

    @Override
    public Employee update(Employee employee) throws PersistenceException {
        String sql = getUpdateQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            prepareStatementForUpdate(statement, employee);

            int count = statement.executeUpdate();
            if (count != 1) {
                throw new PersistenceException("More than one record modified on update: " + count);
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }

        return read(employee.getId());
    }

    @Override
    public void delete(int key) throws PersistenceException {
        String sql = getDeleteQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, key);
            int count = statement.executeUpdate();
            if (count != 1) {
                throw new PersistenceException("More than one record modified on delete: " + count);
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Employee> getAll()  {
        List<Employee> list = null;
        String sql = getSelectQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Employee> getSupervisors() throws SQLException {
        List<Employee> list = null;
        String sql = getSelectQuery() + "WHERE id IN (SELECT DISTINCT supervisor FROM dbo.Employees) OR supervisor = 0";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Employee> getSubordinates(int key) throws SQLException {
        List<Employee> list = null;
        String sql = getSelectQuery() + "WHERE supervisor = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, key);
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateCancelSupervisorOfFormerSubordinates(int key) throws SQLException {
        String sql = getCancelSupervisorUpdateQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, key);
            statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected List<Employee> parseResultSet(ResultSet rs) {
        LinkedList<Employee> result = new LinkedList<>();
        try {
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setFullName(rs.getString("full_name"));
                employee.setDepartment(rs.getString("department"));
                employee.setSupervisor(rs.getInt("supervisor"));
                employee.setComment(rs.getString("comment"));

                employee.setId(rs.getInt("id"));

                result.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    protected void prepareStatementForUpdate(PreparedStatement statement, Employee employee) throws PersistenceException {
        try {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getDepartment());
            statement.setInt(3, employee.getSupervisor());
            statement.setString(4, employee.getComment());

            statement.setInt(5, employee.getId());
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }


    protected void prepareStatementForInsert(PreparedStatement statement, Employee object) throws PersistenceException {
        try {
            statement.setString(1, object.getFullName());
            statement.setString(2, object.getDepartment());
            statement.setInt(3, object.getSupervisor());
            statement.setString(4, object.getComment());
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }




}
