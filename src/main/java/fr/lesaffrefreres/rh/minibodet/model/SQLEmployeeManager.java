package fr.lesaffrefreres.rh.minibodet.model;

import fr.lesaffrefreres.rh.minibodet.controller.extractor.EmployeeExtractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * This class represent an implementation of the EmployeeManager interface that manage SQLEmployee objects.
 * This class <strong>must</strong> be used to manipulate SQLEmployee objects.
 * This class is a singleton.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see EmployeeManager
 * @see SQLEmployee
 */
public class SQLEmployeeManager implements EmployeeManager<Employee>{

    private static SQLEmployeeManager instance;

    /**
     * Return the instance of the SQLEmployeeManager.
     * @return the instance of the SQLEmployeeManager.
     */
    public static SQLEmployeeManager getInstance() {
        if(instance == null) {
            instance = new SQLEmployeeManager();
        }
        return instance;
    }

    private static ObservableList<Employee> employees;

    private static ObservableList<Employee> archivedEmployees;

    private static SQLCalendar calendar;

    protected SQLEmployeeManager() {
        employees = FXCollections.observableArrayList(new EmployeeExtractor());
        archivedEmployees = FXCollections.observableArrayList(new EmployeeExtractor());
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_EMPLOYEE, ARCHIVED FROM EMPLOYEE;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if(rs.getBoolean(2)) {
                    archivedEmployees.add(new SQLEmployee(rs.getLong(1)));
                } else  {
                    employees.add(new SQLEmployee(rs.getLong(1)));
                }
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
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Calendar<SQLDay> getCalendar() {
        return calendar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<Employee> getEmployeesList() {
        return FXCollections.unmodifiableObservableList(employees);
    }

    /**
     * return the list of archived employees.
     * employees can be archived {@link #archiveEmployee(Employee)} as if they were deleted But they are still in the database. and can be restored. {@link #restoreEmployee(Employee)}
     * @return the list of archived employees.
     */
    public ObservableList<Employee> getArchivedEmployeesList() {
        return FXCollections.unmodifiableObservableList(archivedEmployees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Employee getEmployee(String firstName, String lastName) {
        for(Employee cur : employees) {
            if(cur.getFirstName().equalsIgnoreCase(firstName) && cur.getLastName().equalsIgnoreCase(lastName)) {
                return (SQLEmployee) cur;
            }
        }
        return null;
    }

    /**
     * Return the employee with the given id.
     * @param id the id of the employee.
     * @return the employee with the given id.
     * if the employee doesn't exist, return null.
     */
    public Employee getEmployee(long id) {
        for(Employee cur : employees) {
            if(((SQLEmployee)cur).getId() == id) {
                return cur;
            }
        }
        return null;
    }

    /**
     * Check if the employee with the given name and first name exist in the database.
     * @param firstName the first name of the employee.
     * @param lastName the last name of the employee.
     * @return true if the employee exist, false otherwise.
     */
    public boolean employeeExist(String firstName, String lastName) {
        for(Employee cur : employees) {
            if(cur.getFirstName().equalsIgnoreCase(firstName) && cur.getLastName().equalsIgnoreCase(lastName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Employee createEmployee(String firstName, String lastName) {
        if(employeeExist(firstName, lastName)) {
            throw new IllegalArgumentException("employee with first name " + firstName + " and lastname " + lastName + " already exist");
        }

        SQLEmployee res = new SQLEmployee(firstName.toUpperCase(), lastName.toUpperCase());
        employees.add(res);
        return res;
    }

    /**
     * Remove employee from the employees list. As if it was deleted. And put it in the archived employees list.
     * @param employee the employee to archive.
     */
    public void archiveEmployee(Employee employee) {
        Objects.requireNonNull(employee);

        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE EMPLOYEE SET ARCHIVED = TRUE WHERE ID_EMPLOYEE = ?;");
            ps.setLong(1, ((SQLEmployee)employee).getId());
            ps.executeUpdate();
            employees.remove(employee);
            archivedEmployees.add(employee);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Delete employee from the database, this action cannot be rollback !
     * @param employee the employee to delete
     */
    public void deleteEmployee(Employee employee) {
        Objects.requireNonNull(employee);

        Connection conn = DataBase.getInstance().getConnection();

        try {
            PreparedStatement psDeleteEmployee = conn.prepareStatement("DELETE FROM EMPLOYEE WHERE ID_EMPLOYEE = ?");
            psDeleteEmployee.setLong(1, ((SQLEmployee)employee).getId());
            PreparedStatement psDeleteCalendar = conn.prepareStatement("DELETE FROM CALENDAR WHERE ID_CALENDAR = ?");
            psDeleteCalendar.setLong(1, ((SQLWorkCalendar)employee.getCalendar(2020)).getId()); // sale...
            psDeleteEmployee.execute();
            psDeleteCalendar.execute();
            archivedEmployees.remove(employee);
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Restore employee from the archived employees list. and put it back in the employees list.
     * @param employee the employee to restore.
     */
    public void restoreEmployee(Employee employee) {
        Objects.requireNonNull(employee);

        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE EMPLOYEE SET ARCHIVED = FALSE WHERE ID_EMPLOYEE = ?;");
            ps.setLong(1, ((SQLEmployee)employee).getId());
            ps.executeUpdate();
            employees.add(employee);
            archivedEmployees.remove(employee);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}

