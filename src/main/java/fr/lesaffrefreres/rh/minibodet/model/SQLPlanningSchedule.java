package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represent a persistent implementation of the WorkSchedule as a plannings schedule.
 * As the schedule for a planning a nd the schedule for a work day are not the same in the database,
 * there is also an other persistent implementation of the WorkSchedule used by the work day. {@link SQLWorkSchedule}
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see SQLPlanning
 */
public class SQLPlanningSchedule implements WorkSchedule{

    private long idSchedulePlanning;
    private double bufferedTotalHours;
    private double bufferedNightHours;

    /**
     * Creates a new SQLPlanningSchedule by mapping it to an object in the database with the given id.
     * @param idsp the id of the schedule in the database.
     */
    public SQLPlanningSchedule(long idsp) {
        idSchedulePlanning = idsp;
        updateBuffer();
    }

    /**
     * Update the buffered values of the schedule by reading the database.
     * @throws SQLException if an error occurs while reading the database. (usually when the given id doesn't match any schedule in the database)
     */
    public void updateBuffer() {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT TOTAL_HOURS, NIGHT_HOURS FROM SCHEDULE WHERE ID_SCHEDULE = ?;");
            ps.setLong(1, idSchedulePlanning);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                bufferedTotalHours = rs.getDouble(1);
                bufferedNightHours = rs.getDouble(2);
            } else {
                throw new IllegalArgumentException("given id doesn't exist in DB");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNightHours() {
        return bufferedNightHours;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTotalHours() {
        return bufferedTotalHours;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNightHours(double nh) {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SCHEDULE SET NIGHT_HOURS = ? WHERE ID_SCHEDULE = ?;");
            ps.setDouble(1, nh);
            ps.setLong(2, idSchedulePlanning);
            ps.executeUpdate();
            ps.close();
            bufferedNightHours = nh;
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTotalHours(double th) {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SCHEDULE SET TOTAL_HOURS = ? WHERE ID_SCHEDULE = ?;");
            ps.setDouble(1, th);
            ps.setLong(2, idSchedulePlanning);
            ps.executeUpdate();
            ps.close();
            bufferedTotalHours = th;
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }
}
