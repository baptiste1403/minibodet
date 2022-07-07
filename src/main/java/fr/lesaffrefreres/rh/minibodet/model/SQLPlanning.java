package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.EnumMap;

public class SQLPlanning implements Planning{

    private EnumMap<DayOfWeek, SQLPlanningSchedule> bufferedSchedules;
    private EnumMap<DayOfWeek, Long> bufferedLabels;
    private long idPlanning;

    public SQLPlanning(long idp) {
        idPlanning = idp;
        bufferedSchedules = new EnumMap<>(DayOfWeek.class);
        bufferedLabels = new EnumMap<>(DayOfWeek.class);
        updateBuffer();
    }

    public void updateBuffer() {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ID_SCHEDULE_PLANNING, NUM_DAY, ID_LABEL FROM SCHEDULE_PLANNING WHERE ID_PLANNING = ?;");
            ps.setLong(1, idPlanning);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                DayOfWeek dow = DayOfWeek.of(rs.getByte(2));
                bufferedLabels.put(dow, rs.getLong(3));
                bufferedSchedules.put(dow, new SQLPlanningSchedule(rs.getLong(1)));
            }
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public WorkSchedule getSchedule(DayOfWeek dow) {
        return bufferedSchedules.get(dow);
    }

    @Override
    public long getDayLabelId(DayOfWeek dow) {
        return bufferedLabels.get(dow);
    }

    @Override
    public void setDayLabel(DayOfWeek dow, long id) {
        bufferedLabels.put(dow, id);
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SCHEDULE_PLANNING SET ID_LABEL = ? WHERE ID_PLANNING = ? AND NUM_DAY = ?;");
            ps.setLong(1, id);
            ps.setLong(2, idPlanning);
            ps.setByte(3, (byte) dow.getValue());
            ps.executeUpdate();
            ps.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }
}
