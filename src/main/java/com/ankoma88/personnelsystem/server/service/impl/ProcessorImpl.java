package com.ankoma88.personnelsystem.server.service.impl;


import com.ankoma88.personnelsystem.model.Command;
import com.ankoma88.personnelsystem.model.Employee;
import com.ankoma88.personnelsystem.model.Message;
import com.ankoma88.personnelsystem.server.dao.exceptions.PersistenceException;
import com.ankoma88.personnelsystem.server.dao.impl.DaoFactoryImpl;
import com.ankoma88.personnelsystem.server.dao.interfaces.DaoFactory;
import com.ankoma88.personnelsystem.server.dao.interfaces.EmployeeDao;
import com.ankoma88.personnelsystem.server.service.interfaces.Processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ProcessorImpl implements Processor {

    private static final Logger log = Logger.getLogger(ProcessorImpl.class.getName());
    private static final DaoFactory daoFactory = new DaoFactoryImpl();


    /**
     * Process client's message and send response
     * @param input is input message from client
     * @return output is message from server
     */
    @Override
    public Message processMessage(Message input) {
        switch (input.getCommand()) {
            case CREATE:
                return processCreate(input);
            case GET_ALL:
                return processGetAll(input);
            case GET_SUPERVISORS:
                return processGetSupervisors(input);
            case GET_SUBORDINATES:
                return processGetSubordinates(input);
            case UPDATE:
                return processUpdate(input);
            case DELETE:
                return processDelete(input);
            case READ:
                return processRead(input);
            case CANCEL_SUPERVISOR:
                return processCancelSupervisor(input);

            default:
                return new Message(Command.ERROR);
        }
    }

    private Message processCancelSupervisor(Message input) {
        List<Employee> resultList;
        try (Connection con = daoFactory.getConnection()) {
            EmployeeDao dao = daoFactory.getEmployeeDao(con);
            dao.updateCancelSupervisorOfFormerSubordinates(input.getKey());
        } catch (SQLException e) {
            e.printStackTrace();
            return new Message(Command.ERROR);
        }
        return new Message(Command.SUCCESS);
    }

    @Override
    public Message processRead(Message input) {
        Employee resultEmp = new Employee();
        try (Connection con = daoFactory.getConnection()) {
            EmployeeDao dao = daoFactory.getEmployeeDao(con);
            resultEmp = dao.read(input.getKey());
        } catch (SQLException | PersistenceException e) {
            e.printStackTrace();
            return new Message(Command.ERROR);
        }
        return new Message(Command.SUCCESS, resultEmp);
    }

    @Override
    public Message processDelete(Message input) {
        try (Connection con = daoFactory.getConnection()) {
            EmployeeDao dao = daoFactory.getEmployeeDao(con);
            dao.delete(input.getKey());
        } catch (SQLException | PersistenceException e) {
            e.printStackTrace();
            return new Message(Command.ERROR);
        }
        return new Message(Command.SUCCESS);
    }

    @Override
    public Message processUpdate(Message input) {
        Employee resultEmp = new Employee();
        try (Connection con = daoFactory.getConnection()) {
            EmployeeDao dao = daoFactory.getEmployeeDao(con);
            resultEmp = dao.update(input.getEmployee());
            log.info("Updated: "+resultEmp);
        } catch (SQLException | PersistenceException e) {
            e.printStackTrace();
            return new Message(Command.ERROR);
        }
        return new Message(Command.SUCCESS, resultEmp);
    }

    @Override
    public Message processGetSubordinates(Message input) {
        List<Employee> subordinatesList = new LinkedList<>();
        try (Connection con = daoFactory.getConnection()) {
            EmployeeDao dao = daoFactory.getEmployeeDao(con);
            subordinatesList = dao.getSubordinates(input.getKey());
        } catch (SQLException e) {
            e.printStackTrace();
            return new Message(Command.ERROR);
        }
        return new Message(Command.SUCCESS, subordinatesList);
    }

    @Override
    public Message processGetSupervisors(Message input) {
        List<Employee> supervisorsList = new LinkedList<>();
        try (Connection con = daoFactory.getConnection()) {
            EmployeeDao dao = daoFactory.getEmployeeDao(con);
            supervisorsList = dao.getSupervisors();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Message(Command.ERROR);
        }
        return new Message(Command.SUCCESS, supervisorsList);
    }

    @Override
    public Message processGetAll(Message input) {
        List<Employee> allEmployeesList = new LinkedList<>();
        try (Connection con = daoFactory.getConnection()) {
            EmployeeDao dao = daoFactory.getEmployeeDao(con);
            allEmployeesList = dao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Message(Command.ERROR);
        }
        return new Message(Command.SUCCESS, allEmployeesList);
    }

    @Override
    public Message processCreate(Message input) {
        System.out.println(input.getEmployee());
        Employee resultEmp = new Employee();
        try (Connection con = daoFactory.getConnection()) {
            EmployeeDao dao = daoFactory.getEmployeeDao(con);
            resultEmp = dao.create(input.getEmployee());
        } catch (SQLException | PersistenceException e) {
            e.printStackTrace();
            return new Message(Command.ERROR);
        }
        return new Message(Command.SUCCESS, resultEmp);
    }
}
