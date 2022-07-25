package fr.lesaffrefreres.rh.minibodet.model;

import javafx.scene.control.MenuItem;
import org.apache.ibatis.jdbc.SQL;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class represent the persistent implementation of the Calendar with WorkDays.
 * This class is used to represent he calendars of SQLEmployee objects.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see SQLWorkDay
 * @see SQLEmployee
 * @see Calendar
 */
public class SQLWorkCalendar implements Calendar<WorkDay>, SQLObject, StatusListPickerMenuItemProvider {

    private Map<LocalDate, SQLWorkDay> bufferedDay;

    private long idCalendar;

    private SQLEmployee employee;

    private int nbHoliday;
    private int year;

    /**
     * Creates a new SQLWorkCalendar object by mapping it to a calendar in the database with the given id.
     * @param idc the id of the calendar in the database.
     * @param emp the employee who owns the calendar.
     * @param y the year of the calendar.
     */
    public SQLWorkCalendar(long idc, SQLEmployee emp, int y) {
        idCalendar = idc;
        year = y;
        employee = emp;
        updateBuffer();
    }

    /**
     * refresh the buffered days of the calendar by querying the database.
     */
    public void updateBuffer() {
        bufferedDay = new TreeMap<>();
        employee.updateBuffer();
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_LABELDAY, DAY_DATE FROM LABELDAY WHERE ID_CALENDAR = ? AND DAY_DATE BETWEEN ? AND ?;");
            ps.setLong(1, idCalendar);
            ps.setDate(2, Date.valueOf(LocalDate.of(year, 1, 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))));
            ps.setDate(3, Date.valueOf(LocalDate.of(year, 12, 31).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bufferedDay.put(rs.getDate(2).toLocalDate(), new SQLWorkDay(this, rs.getLong(1)));
            }
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Inserts all the days of the given month in the database. So they can't be modified anymore by automatic change (planning change or global calendar change).
     * @param year the year of the month.
     * @param m the month of the year.
     */
    public void saveMonth(int year, Month m) {
        LocalDate start = LocalDate.of(year, m, 1);
        List<LocalDate> month = start.datesUntil(start.plusMonths(1)).toList();
        for(LocalDate cur : month) {
            getDay(cur).create();
        }
    }

    /**
     * Check if the given month is complitally saved in the database. (all the days are inserted) see {@link #saveMonth(int, Month)}.
     * @param year
     * @param m
     * @return
     */
    public boolean isSavedMonth(int year, Month m) {
        LocalDate start = LocalDate.of(year, m, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
        Connection conn = DataBase.getInstance().getConnection();
        int nbSavedDay = 0;
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM LABELDAY WHERE ID_CALENDAR = ? AND DAY_DATE BETWEEN ? AND ?;");
            ps.setLong(1, idCalendar);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                nbSavedDay = rs.getInt(1);
            } else {
                ps.close();
                throw new IllegalStateException("given calendar doesn't match any calendar in the database");
            }
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return nbSavedDay == start.until(end.plusDays(1), ChronoUnit.DAYS);
    }

    /**
     * This method is called by the {@link SQLWorkDay} class to inform the calendar that a day has been modified.
     * And that it need to be saved in the database if it's not the case.
     * The given day is then added to the buffered days.
     * @param swd the day that needs to be saved.
     */
    public void createdDay(SQLWorkDay swd) {
        bufferedDay.put(swd.getDate(), swd);
    }

    /**
     * Returns the day of the calendar at the given date.
     * If the days is in the buffer, it mean that its in the database and is returned.
     * If the day is not in the buffer, it means that it is not in the database and the returned day is based on:
     * - 1 : the global calendar if there is a day at the given date.
     * - 2 : the planning of the employee if not.
     * @param date the date of the day to return.
     * @return the day at the given date.
     */
    @Override
    public SQLWorkDay getDay(LocalDate date) {
        SQLWorkDay res = bufferedDay.get(date);
        SQLEmployeeManager emp = SQLEmployeeManager.getInstance();
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        if(res == null) {
            res = new SQLWorkDay(this, date, idCalendar, "");
            if(emp.getCalendar().getDay(date).getLabelId() == dlm.getUndefinedDayLabelId()) {
                res.setLabelId(employee.getPlanningLabelId(date.getDayOfWeek()), false);
            } else {
                res.setLabelId(emp.getCalendar().getDay(date).getLabelId(), false);
            }

            SQLWorkSchedule tmp = (SQLWorkSchedule) res.getSchedule();
            tmp.setTotalHours(employee.getPlanningTotalHours(date.getDayOfWeek()), false);
            tmp.setNightHours(employee.getPlanningNightHours(date.getDayOfWeek()), false);
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getYear() {
        return year;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setYear(int y) {
        year = y;
        updateBuffer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return idCalendar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long create() {
        return idCalendar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MenuItem> getMenuItemList() {
        DayLabelManager dlm = SQLDayLabelManager.getInstance();

        List<MenuItem> res = new ArrayList<>();
        for(DayLabel cur : dlm.getAllDayLabels()) {
            res.add(new MenuItem(cur.getText()));
        }
        return res;
    }
}
