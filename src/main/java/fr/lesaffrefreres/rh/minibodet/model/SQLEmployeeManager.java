package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.controller.extractor.EmployeeExtractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SQLEmployeeManager implements EmployeeManager<Employee>{

    private static SQLEmployeeManager instance;

    public static SQLEmployeeManager getInstance() {
        if(instance == null) {
            instance = new SQLEmployeeManager();
        }
        return instance;
    }

    private static ObservableList<Employee> employees;

    private static SQLCalendar calendar;

    protected SQLEmployeeManager() {
        employees = FXCollections.observableArrayList(new EmployeeExtractor());
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_EMPLOYEE FROM EMPLOYEE;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                employees.add(new SQLEmployee(rs.getLong(1)));
            }
            ps.close();

            PreparedStatement psc = conn.prepareStatement(
                    "SELECT ID_CALENDAR FROM CALENDAR WHERE ID_CALENDAR = 1");
            ResultSet rsc = psc.executeQuery();
            if(!rsc.first()) {
                psc.close();
                psc = conn.prepareStatement("INSERT INTO CALENDAR VALUES ();");
                psc.execute();
            }
            psc.close();
            calendar = new SQLCalendar(1, LocalDate.now().getYear());
            calendar = new SQLCalendar(1, LocalDate.now().getYear());
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public Calendar<SQLDay> getCalendar() {
        return calendar;
    }

    @Override
    public ObservableList<Employee> getEmployeesList() {
        return FXCollections.unmodifiableObservableList(employees);
    }

    @Override
    public Employee getEmployee(String firstName, String lastName) {
        for(Employee cur : employees) {
            if(cur.getFirstName().equals(firstName) && cur.getLastName().equals(lastName)) {
                return (SQLEmployee) cur;
            }
        }
        return null;
    }

    public Employee getEmployee(long id) {
        for(Employee cur : employees) {
            if(((SQLEmployee)cur).getId() == id) {
                return cur;
            }
        }
        return null;
    }

    public boolean employeeExist(String firstName, String lastName) {
        for(Employee cur : employees) {
            if(cur.getFirstName().equals(firstName) && cur.getLastName().equals(lastName)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Employee createEmployee(String firstName, String lastName) {
        if(employeeExist(firstName, lastName)) {
            throw new IllegalArgumentException("employee with first name " + firstName + " and lastname " + lastName + " already exist");
        }

        SQLEmployee res = new SQLEmployee(firstName, lastName);
        employees.add(res);
        return res;
    }
}
