package com.ankoma88.personnelsystem.model;


import java.io.Serializable;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String fullName;
    private String department;
    private Integer supervisor;
    private String comment;

    public Employee() {
        this.supervisor = 0;
    }

    public Employee(String fullName, String department, Integer supervisor, String comment) {
        this.fullName = fullName;
        this.department = department;
        this.supervisor = supervisor;
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Integer supervisor) {
        this.supervisor = supervisor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        if (id != employee.id) return false;
        if (!department.equals(employee.department)) return false;
        if (!fullName.equals(employee.fullName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + fullName.hashCode();
        result = 31 * result + department.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
