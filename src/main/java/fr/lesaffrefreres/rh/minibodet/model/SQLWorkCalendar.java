package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class SQLWorkCalendar implements Calendar<SQLWorkDay>, SQLObject{

    private Map<LocalDate, SQLWorkDay> bufferedDay;

    private long idCalendar;

    private SQLEmployee employee;
    private int year;

    public SQLWorkCalendar(long idc, SQLEmployee emp, int y) {
        idCalendar = idc;
        year = y;
        bufferedDay = new TreeMap<>();
        updateBuffer();
        employee = emp;
    }

    public void updateBuffer() {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_DAY, DAY_DATE FROM LABELDAY WHERE ID_CALENDAR = ? AND DAY_DATE BETWEEN ? AND ?;");
            ps.setLong(1, idCalendar);
            ps.setDate(2, Date.valueOf(LocalDate.of(year, 1, 1)));
            ps.setDate(3, Date.valueOf(LocalDate.of(year, 12, 31)));
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                bufferedDay.put(rs.getDate(2).toLocalDate(), new SQLWorkDay(rs.getLong(1)));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public SQLWorkDay getDay(LocalDate date) {
        SQLWorkDay res = bufferedDay.get(date);
        if(res == null) {
            res = new SQLWorkDay(date, idCalendar, "");
            res.setLabelId(employee.getPlanningLabelId(date.getDayOfWeek()), false);
            SQLWorkSchedule tmp = (SQLWorkSchedule) res.getSchedule();
            tmp.setTotalHours(employee.getPlanningTotalHours(date.getDayOfWeek()), false);
            tmp.setNightHours(employee.getPlanningNightHours(date.getDayOfWeek()), false);
            bufferedDay.put(date, res);
        }
        return res;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public long getId() {
        return idCalendar;
    }

    @Override
    public long create() {
        return idCalendar;
    }
}
