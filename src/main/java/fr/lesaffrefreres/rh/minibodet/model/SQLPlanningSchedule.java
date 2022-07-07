package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLPlanningSchedule implements WorkSchedule{

    private long idSchedulePlanning;
    private int bufferedTotalHours;
    private int bufferedNightHours;

    public SQLPlanningSchedule(long idsp) {
        idSchedulePlanning = idsp;
        updateBuffer();
    }

    public void updateBuffer() {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT TOTAL_HOURS, NIGHT_HOURS FROM SCHEDULE_PLANNING WHERE ID_SCHEDULE_PLANNING = ?;");
            ps.setLong(1, idSchedulePlanning);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                bufferedTotalHours = rs.getInt(1);
                bufferedNightHours = rs.getInt(2);
            } else {
                throw new IllegalArgumentException("given id doesn't exist in DB");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public int getNightHours() {
        return bufferedNightHours;
    }

    @Override
    public int getTotalHours() {
        return bufferedTotalHours;
    }

    @Override
    public void setNightHours(int nh) {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SCHEDULE_PLANNING SET NIGHT_HOURS = ? WHERE ID_SCHEDULE_PLANNING = ?;");
            ps.setInt(1, nh);
            ps.setLong(2, idSchedulePlanning);
            ps.executeUpdate();
            ps.close();
            bufferedNightHours = nh;
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void setTotalHours(int th) {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SCHEDULE_PLANNING SET TOTAL_HOURS = ? WHERE ID_SCHEDULE_PLANNING = ?;");
            ps.setInt(1, th);
            ps.setLong(2, idSchedulePlanning);
            ps.executeUpdate();
            ps.close();
            bufferedNightHours = th;
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }
}
