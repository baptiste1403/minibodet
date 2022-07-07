package fr.lesaffrefreres.rh.minibodet.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class SimpleEmployeeManager implements EmployeeManager<Employee> {
    private final ObservableList<Employee> employeesList;
    private final SimpleCalendar calendar;

    private static SimpleEmployeeManager instance;

    private SimpleEmployeeManager(int calendarYear) {
        employeesList = FXCollections.observableArrayList();
        calendar = new SimpleCalendar(calendarYear);
    }

    public static SimpleEmployeeManager getInstance() {
        if(instance == null) {
            instance = new SimpleEmployeeManager(LocalDate.now().getYear());
        }
        return instance;
    }

    public Calendar<SimpleDay> getCalendar() {
        return calendar;
    }

    public ObservableList<Employee> getEmployeesList() {
        return employeesList;
    }

    public SimpleEmployee getEmployee(String firstName, String lastName) {
        for(Employee cur : employeesList) {
            if(cur.getFirstName().equals(firstName) && cur.getLastName().equals(lastName)) {
                return (SimpleEmployee) cur;
            }
        }
        return null;
    }

    private boolean employeeExist(String firstName, String lastName) {
        for(Employee cur : employeesList) {
            if(cur.getFirstName().equals(firstName) && cur.getLastName().equals(lastName)) {
                return true;
            }
        }
        return false;
    }

    public Employee createEmployee(String firstName, String lastName) {
        if(employeeExist(firstName, lastName)) {
            throw new IllegalArgumentException("employee with first name " + firstName + " and lastname " + lastName + " already exist");
        }

        SimpleEmployee emp = new SimpleEmployee(firstName, lastName);
        employeesList.add(emp);
        return emp;
    }
}
