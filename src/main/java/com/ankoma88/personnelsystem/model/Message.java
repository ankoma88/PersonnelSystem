package com.ankoma88.personnelsystem.model;


import java.io.Serializable;
import java.util.List;

/**
 * Client and server receive and send Messages to each other
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private Command command = Command.CREATE;
    private Employee employee;
    private List<Employee> employees;
    private int key;

    public Message(Command command, List<Employee> employees) {
        this.command = command;
        this.employees = employees;
    }

    public Message(Command command, int key) {
        this.command = command;
        this.key = key;
    }

    public Message(Command command, Employee employee) {
        this.command = command;
        this.employee = employee;
    }

    public Message(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return "Message: " + command;
    }
}
