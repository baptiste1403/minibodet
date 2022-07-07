package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class SQLCalendar implements Calendar<SQLDay>, SQLObject{

    private Map<LocalDate, SQLDay> bufferedDay;

    private long idCalendar;
    private int year;

    public SQLCalendar(long idc, int y) {
        idCalendar = idc;
        year = y;
        bufferedDay = new TreeMap<>();
        updateBuffer();
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
                bufferedDay.put(rs.getDate(2).toLocalDate(), new SQLDay(rs.getLong(1)));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public SQLDay getDay(LocalDate date) {
        SQLDay res = bufferedDay.get(date);
        if(res == null) {
            res = new SQLDay(date, idCalendar);
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
