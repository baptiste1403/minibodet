package fr.lesaffrefreres.rh.minibodet.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

public class SQLEmployee implements Employee, SQLObject{

    private StringProperty bufferedLastname;
    private StringProperty bufferedFirstname;

    private SQLPlanning planning;
    private SQLWorkCalendar calendar;

    private long idEmployee;

    public SQLEmployee(long id) {
        if(id < 0) {
            throw new IllegalArgumentException("given id must be greater or equal to 0 and must match the id of an existing employee in DB");
        }
        idEmployee = id;
        bufferedLastname = new SimpleStringProperty();
        bufferedFirstname = new SimpleStringProperty();
        updateBuffer();
    }

    public void updateBuffer() {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_CALENDAR, FIRSTNAME, LASTNAME, ID_PLANNING FROM EMPLOYEE NATURAL JOIN UTILISE WHERE EMPLOYEE.ID_EMPLOYEE = ?;");
            ps.setLong(1, idEmployee);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                planning = new SQLPlanning(rs.getLong(4));
                calendar = new SQLWorkCalendar(rs.getLong(1), this,  LocalDate.now().getYear());
                bufferedFirstname.set(rs.getString(2));
                bufferedLastname.set(rs.getString(3));
            } else {
                ps.close();
                throw new IllegalArgumentException("the given id doesn't exist in DB");
            }
            ps.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

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

            PreparedStatement psPlanning = conn.prepareStatement(
                    "INSERT INTO PLANNING VALUES ();", Statement.RETURN_GENERATED_KEYS);
            psPlanning.execute();
            ResultSet rsPlanning = psPlanning.getGeneratedKeys();
            rsPlanning.first();
            PreparedStatement psUse = conn.prepareStatement(
                    "INSERT INTO UTILISE(ID_EMPLOYEE, START_SATE, END_DATE, ID_PLANNING) VALUES (?, ?, ?, ?);");
            psUse.setLong(1, rsEmployee.getLong(1));
            psUse.setLong(4, rsPlanning.getLong(1));
            psUse.setDate(2, Date.valueOf(LocalDate.MIN));
            psUse.setDate(3, Date.valueOf(LocalDate.MAX));

            psUse.execute();


            PreparedStatement psSchedulePlanning = conn.prepareStatement(
                    "INSERT INTO SCHEDULE_PLANNING(ID_PLANNING, NUM_DAY, TOTAL_HOURS, NIGHT_HOURS, ID_LABEL) VALUES" +
                            "(?, 1, 7, 0, ?)," +
                            "(?, 2, 7, 0, ?)," +
                            "(?, 3, 7, 0, ?)," +
                            "(?, 4, 7, 0, ?)," +
                            "(?, 5, 7, 0, ?)," +
                            "(?, 6, 0, 0, ?)," +
                            "(?, 7, 0, 0, ?);");

            long undefinedId = SQLDayLabelManager.getInstance().getUndefinedDayLabelId();
            //monday
            psSchedulePlanning.setLong(1, rsPlanning.getLong(1));
            psSchedulePlanning.setLong(2, undefinedId);
            //tuesday
            psSchedulePlanning.setLong(3, rsPlanning.getLong(1));
            psSchedulePlanning.setLong(4, undefinedId);
            //wednesday
            psSchedulePlanning.setLong(5, rsPlanning.getLong(1));
            psSchedulePlanning.setLong(6, undefinedId);
            //thursday
            psSchedulePlanning.setLong(7, rsPlanning.getLong(1));
            psSchedulePlanning.setLong(8, undefinedId);
            //friday
            psSchedulePlanning.setLong(9, rsPlanning.getLong(1));
            psSchedulePlanning.setLong(10, undefinedId);
            //saturday
            psSchedulePlanning.setLong(11, rsPlanning.getLong(1));
            psSchedulePlanning.setLong(12, undefinedId);
            //sunday
            psSchedulePlanning.setLong(13, rsPlanning.getLong(1));
            psSchedulePlanning.setLong(14, undefinedId);

            psSchedulePlanning.execute();

            planning = new SQLPlanning(rsPlanning.getLong(1));
            calendar = new SQLWorkCalendar(rsCalendar.getLong(1), this,  LocalDate.now().getYear());

            psPlanning.close();
            psUse.close();
            psSchedulePlanning.close();
            psCalendar.close();
            psEmployee.close();



        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

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

    @Override
    public String getLastName() {
        return bufferedLastname.get();
    }

    @Override
    public String getFirstName() {
        return bufferedFirstname.get();
    }

    @Override
    public StringProperty FirstNameProperty() {
        return bufferedFirstname;
    }

    @Override
    public StringProperty LastNameProperty() {
        return bufferedLastname;
    }

    @Override
    public void setCalendar(int year) {
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
    }

    @Override
    public Calendar<SQLWorkDay> getCalendar() {
        return calendar;
    }

    @Override
    public void setPlanningNightHours(DayOfWeek dow, int nh) {
        planning.getSchedule(dow).setNightHours(nh);
    }

    @Override
    public void setPlanningTotalHours(DayOfWeek dow, int th) {
        planning.getSchedule(dow).setTotalHours(th);
    }

    @Override
    public void setPlanningLabelId(DayOfWeek dow, long id) {
        planning.setDayLabel(dow, id);
    }

    @Override
    public int getPlanningNightHours(DayOfWeek dow) {
        return planning.getSchedule(dow).getNightHours();
    }

    @Override
    public int getPlanningTotalHours(DayOfWeek dow) {
        return planning.getSchedule(dow).getTotalHours();
    }

    @Override
    public long getPlanningLabelId(DayOfWeek dow) {
        return planning.getDayLabelId(dow);
    }

    @Override
    public long getId() {
        return idEmployee;
    }

    @Override
    public long create() {
        return idEmployee;
    }

    @Override
    public String toString() {
        return bufferedFirstname.get() + " " + bufferedLastname.get();
    }
}
