package fr.lesaffrefreres.rh.minibodet.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represent a persistent implementation of the {@link Employee} interface.
 * Its usually provided and managed by a {@link SQLEmployeeManager}
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see Employee
 * @see SQLEmployeeManager
 */
public class SQLEmployee implements Employee, SQLObject{

    private StringProperty bufferedLastname;
    private StringProperty bufferedFirstname;

    private SQLPlanning planning;
    private SQLWorkCalendar calendar;

    private long idEmployee;

    /**
     * Creates a new Employee mapped to the employee with the given id in the database.
     * @param id the id of the employee in the database
     * @throws SQLException if an error occurs while accessing the database (usually when the given id doesn't match an employee in the database)
     * @throws IllegalArgumentException if the given id is not a positive integer
     */
    public SQLEmployee(long id) {
        if(id < 0) {
            throw new IllegalArgumentException("given id must be greater or equal to 0 and must match the id of an existing employee in DB");
        }
        idEmployee = id;
        bufferedLastname = new SimpleStringProperty();
        bufferedFirstname = new SimpleStringProperty();
        planning = new SQLPlanning(idEmployee);
        updateBuffer();
    }

    /**
     * Updates the buffered values of the employee with the values from the database.
     */
    public void updateBuffer() {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_CALENDAR, FIRSTNAME, LASTNAME FROM EMPLOYEE WHERE EMPLOYEE.ID_EMPLOYEE = ?;");
            ps.setLong(1, idEmployee);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                bufferedFirstname.set(rs.getString(2));
                bufferedLastname.set(rs.getString(3));
            } else {
                ps.close();
                throw new IllegalArgumentException("the given id doesn't exist in DB");
            }
            planning.updateBuffer();
            ps.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Creates a new Employee with the given firstname and lastname that is not in the database. and inserts it in the database.
     * During the insertion, the employee is assigned to a default planning. (the planning is also inserted in the database)
     * the employee is also assigned to a default work calendar. (the calendar is also inserted in the database)
     * @param fn the firstname of the employee
     * @param ln the lastname of the employee
     */
    public SQLEmployee(String fn, String ln) {
        bufferedFirstname = new SimpleStringProperty();
        bufferedLastname = new SimpleStringProperty();
        bufferedLastname.set(Objects.requireNonNull(ln));
        bufferedFirstname.set(Objects.requireNonNull(fn));

        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement psCalendar = conn.prepareStatement("INSERT INTO CALENDAR VALUES ();",
                    Statement.RETURN_GENERATED_KEYS);
            psCalendar.execute();
            ResultSet rsCalendar = psCalendar.getGeneratedKeys();
            rsCalendar.first();


            PreparedStatement psEmployee = conn.prepareStatement(
                    "INSERT INTO EMPLOYEE(FIRSTNAME, LASTNAME, ID_CALENDAR) VALUES (?, ?, ?);",
                         Statement.RETURN_GENERATED_KEYS);
            psEmployee.setString(1, fn);
            psEmployee.setString(2, ln);
            psEmployee.setLong(3, rsCalendar.getLong(1));

            psEmployee.execute();
            ResultSet rsEmployee = psEmployee.getGeneratedKeys();
            rsEmployee.first();
            idEmployee = rsEmployee.getLong(1);

            PreparedStatement psSchedulePlanning = conn.prepareStatement(
                    "INSERT INTO SCHEDULE(NUM_DAY, TOTAL_HOURS, NIGHT_HOURS, ID_LABEL, ID_EMPLOYEE) VALUES" +
                            "(1, 7, 0, ?, ?)," +
                            "(2, 7, 0, ?, ?)," +
                            "(3, 7, 0, ?, ?)," +
                            "(4, 7, 0, ?, ?)," +
                            "(5, 7, 0, ?, ?)," +
                            "(6, 0, 0, ?, ?)," +
                            "(7, 0, 0, ?, ?);");

            SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
            //monday
            psSchedulePlanning.setLong(1, dlm.getWorkDayLabelId());
            psSchedulePlanning.setLong(2, idEmployee);
            //tuesday
            psSchedulePlanning.setLong(3, dlm.getWorkDayLabelId());
            psSchedulePlanning.setLong(4, idEmployee);
            //wednesday
            psSchedulePlanning.setLong(5, dlm.getWorkDayLabelId());
            psSchedulePlanning.setLong(6, idEmployee);
            //thursday
            psSchedulePlanning.setLong(7, dlm.getWorkDayLabelId());
            psSchedulePlanning.setLong(8, idEmployee);
            //friday
            psSchedulePlanning.setLong(9, dlm.getWorkDayLabelId());
            psSchedulePlanning.setLong(10, idEmployee);
            //saturday
            psSchedulePlanning.setLong(11, dlm.getRestDayLabelId());
            psSchedulePlanning.setLong(12, idEmployee);
            //sunday
            psSchedulePlanning.setLong(13, dlm.getRestDayLabelId());
            psSchedulePlanning.setLong(14, idEmployee);

            psSchedulePlanning.execute();

            planning = new SQLPlanning(idEmployee);

            psSchedulePlanning.close();
            psCalendar.close();
            psEmployee.close();



        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Set the lastname of the employee.
     * @param ln the name of the employee
     */
    @Override
    public void setLastName(String ln) {
        bufferedLastname.set(ln);
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE EMPLOYEE SET LASTNAME = ? WHERE ID_EMPLOYEE = ?");
            ps.setString(1, ln);
            ps.setLong(2, idEmployee);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Set the firstname of the employee.
     * @param fn the name of the employee
     */
    @Override
    public void setFirstName(String fn) {
        bufferedFirstname.set(fn);
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE EMPLOYEE SET FIRSTNAME = ? WHERE ID_EMPLOYEE = ?");
            ps.setString(1, fn);
            ps.setLong(2, idEmployee);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }


    /**
     * Return the lastname of the employee.
     * @return the lastname of the employee
     */
    @Override
    public String getLastName() {
        return bufferedLastname.get();
    }

    /**
     * Return the firstname of the employee.
     * @return the firstname of the employee
     */
    @Override
    public String getFirstName() {
        return bufferedFirstname.get();
    }

    /**
     * Return the StringProperty of the firstname of the employee.
     * @return the StringProperty of the firstname of the employee
     */
    @Override
    public StringProperty FirstNameProperty() {
        return bufferedFirstname;
    }

    /**
     * Return the StringProperty of the lastname of the employee.
     * @return the StringProperty of the lastname of the employee
     */
    @Override
    public StringProperty LastNameProperty() {
        return bufferedLastname;
    }

    /**
     * Return the work calendar of the employee for the given year.
     * @param year the year of the calendar to look for
     * @return the work calendar of the employee for the given year
     */
    @Override
    public Calendar<WorkDay> getCalendar(int year) {
        if(calendar != null && calendar.getYear() == year) {
            return calendar;
        }
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_CALENDAR FROM EMPLOYEE WHERE ID_EMPLOYEE = ?;");
            ps.setLong(1, idEmployee);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                calendar = new SQLWorkCalendar(rs.getLong(1), this,  year);
            } else {
                ps.close();
                throw new IllegalArgumentException("given id doesn't exist in DB");
            }
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return calendar;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void setPlanningNightHours(DayOfWeek dow, double nh) {
        planning.getSchedule(dow).setNightHours(nh);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void setPlanningTotalHours(DayOfWeek dow, double th) {
        planning.getSchedule(dow).setTotalHours(th);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void setPlanningLabelId(DayOfWeek dow, long id) {
        planning.setDayLabel(dow, id);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public double getPlanningNightHours(DayOfWeek dow) {
        return planning.getSchedule(dow).getNightHours();
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public double getPlanningTotalHours(DayOfWeek dow) {
        return planning.getSchedule(dow).getTotalHours();
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public long getPlanningLabelId(DayOfWeek dow) {
        return planning.getDayLabelId(dow);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public long getId() {
        return idEmployee;
    }

    /**
     * This method can be called by other objects that need the employee to be created in the database before
     * creating themselves, as SQLEmployees are always inserted in the database when they are created. This method doesn't
     * do much than returning the id of the employee.
     * @return the id of the employee
     */
    @Override
    public long create() {
        return idEmployee;
    }

    @Override
    public String toString() {
        return bufferedFirstname.get() + " " + bufferedLastname.get();
    }
}
