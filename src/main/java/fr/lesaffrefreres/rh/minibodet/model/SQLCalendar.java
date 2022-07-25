package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.*;
import java.text.DateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class represents a persistent calendar containing days.
 * It's usually used to represent a global calendar.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class SQLCalendar implements Calendar<SQLDay>, SQLObject{

    private Map<LocalDate, SQLDay> bufferedDay;

    private long idCalendar;
    private int year;

    /**
     * Constructor
     * creates a new calendar representing an existing calendar in the database at the given year.
     * @param idc the id of the calendar int the database
     * @param y the year of the calendar
     */
    public SQLCalendar(long idc, int y) {
        idCalendar = idc;
        year = y;
        updateBuffer();
    }

    /**
     * Refresh the buffer of the calendar by fetching the days from the database.
     * @throws SQLException if an error occurs while accessing the database (usually if the given id doesn't match a calendar in the database)
     */
    public void updateBuffer() {
        bufferedDay = new TreeMap<>();
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_LABELDAY, DAY_DATE FROM LABELDAY WHERE ID_CALENDAR = ? AND DAY_DATE BETWEEN ? AND ?;");
            ps.setLong(1, idCalendar);
            ps.setDate(2, Date.valueOf(LocalDate.of(year, 1, 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))));
            ps.setDate(3, Date.valueOf(LocalDate.of(year, 12, 31).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))));
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                bufferedDay.put(rs.getDate(2).toLocalDate(), new SQLDay(rs.getLong(1)));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Return a day from the calendar at the given date.
     * if the day doesn't exist in the calendar, it returns a default day with the label "undefined".
     * @param date the date of the day to return.
     * @return the day corresponding to the given date.
     */
    @Override
    public SQLDay getDay(LocalDate date) {
        SQLDay res = bufferedDay.get(date);
        if(res == null) {
            res = new SQLDay(date, idCalendar);
            bufferedDay.put(date, res);
        }
        return res;
    }

    /**
     * Return the year of the calendar.
     * All the days of the calendar are in the same year.
     * @return the year of the calendar.
     */
    @Override
    public int getYear() {
        return year;
    }

    /**
     * Change the year of the calendar.
     * it will cause the calendar to be refreshed. @see {@link #updateBuffer()}
     * @param y the year of the calendar.
     */
    @Override
    public void setYear(int y) {
        year = y;
        updateBuffer();
    }

    /**
     * Return the id of the calendar in the database.
     * @return the id of the calendar in the database.
     */
    @Override
    public long getId() {
        return idCalendar;
    }

    /**
     * can be called by other object if they need that the calendar exists in the database before they can use it to create themsleves.
     * @return the calendar id in the database. the calendar is always already in the database before it is instanciated.
     * So this method does nothing more than returning the id.
     */
    @Override
    public long create() {
        return idCalendar;
    }
}
