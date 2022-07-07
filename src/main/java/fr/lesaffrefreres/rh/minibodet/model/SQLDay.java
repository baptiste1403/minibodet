package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.*;
import java.time.LocalDate;

public class SQLDay implements Day, SQLObject{

    protected long idDay;
    private long bufferedIdCalendar;
    private LocalDate bufferedDate;
    private long bufferedIdLabel;

    public SQLDay(long idd) {
        if(idd < 0) {
            throw new IllegalArgumentException("giveen id must be greater or equals to 0, and this object must exist in database");
        }
        idDay = idd;
        updateBuffer();
    }

    public SQLDay(LocalDate date, long idc) {
        idDay = -1;
        bufferedDate = date;
        bufferedIdCalendar = idc;
        bufferedIdLabel = SQLDayLabelManager.getInstance().getUndefinedDayLabelId();
    }

    public void updateBuffer() {
        if(idDay < 0) {
            return;
        }
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT LABEL_ID, DAY_DATE, ID_CALENDAR FROM LABELDAY WHERE ID_DAY = ?;");
            ps.setLong(1, idDay);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                bufferedIdLabel = rs.getInt(1);
                bufferedDate = rs.getDate(2).toLocalDate();
                bufferedIdCalendar = rs.getLong(3);
            } else {
                ps.close();
                throw new IllegalArgumentException("the given id doesn't exist in DB");
            }
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public long getLabelId() {
        return bufferedIdLabel;
    }

    @Override
    public LocalDate getDate() {
        return bufferedDate;
    }

    @Override
    public void setLabelId(long id) { // TODO test if given id exist
        if(bufferedIdLabel == id) {
            return;
        }
        bufferedIdLabel = id;
        if(idDay < 0){
            create();
        }
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE LABELDAY SET LABEL_ID = ? WHERE ID_DAY = ?;");
            ps.setLong(1, id);
            ps.setLong(2, idDay);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void setLabelId(long id, boolean mustSave) {
        if(mustSave || idDay >= 0) {
            setLabelId(id);
        } else {
            bufferedIdLabel = id;
        }
    }

    @Override
    public long getId() {
        return idDay;
    }

    @Override
    public long create() {
        if(idDay >= 0) {
            return idDay;
        }
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO LABELDAY (DAY_DATE, ID_CALENDAR, LABEL_ID) VALUES (?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, Date.valueOf(bufferedDate));
            ps.setLong(2, bufferedIdCalendar);
            ps.setLong(3, bufferedIdLabel);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.first();
            idDay = rs.getLong(1);
            ps.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }
        return idDay;
    }
}
