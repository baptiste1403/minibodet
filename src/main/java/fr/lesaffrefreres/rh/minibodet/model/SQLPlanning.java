package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.EnumMap;

/**
 * This classe represent a persistent implementation of the Planning interface.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see Planning
 */
public class SQLPlanning implements Planning{

    private EnumMap<DayOfWeek, SQLPlanningSchedule> bufferedSchedules;
    private EnumMap<DayOfWeek, Long> bufferedLabels;
    private long idEmployee;

    /**
     * Creates a new SQLPlanning object by mapping it with a planning in the database with the given id.
     * @param ide the id of the planning owner in the database.
     */
    public SQLPlanning(long ide) {
        idEmployee = ide;
        bufferedSchedules = new EnumMap<>(DayOfWeek.class);
        bufferedLabels = new EnumMap<>(DayOfWeek.class);
        updateBuffer();
    }

    /**
     * update the buffered values of the planning by reading the database.
     */
    public void updateBuffer() {
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement psPlanning = conn.prepareStatement("SELECT ID_LABEL, TOTAL_HOURS, NIGHT_HOURS, NUM_DAY, ID_SCHEDULE FROM SCHEDULE WHERE ID_EMPLOYEE = ?");
            psPlanning.setLong(1, idEmployee);
            ResultSet rsPlanning = psPlanning.executeQuery();
            while (rsPlanning.next()) {
                setDayLabel(DayOfWeek.of(rsPlanning.getByte(4)), rsPlanning.getLong(1));
                bufferedSchedules.put(DayOfWeek.of(rsPlanning.getByte(4)), new SQLPlanningSchedule(rsPlanning.getLong(5)));
                getSchedule(DayOfWeek.of(rsPlanning.getByte(4))).setTotalHours(rsPlanning.getDouble(2));
                getSchedule(DayOfWeek.of(rsPlanning.getByte(4))).setNightHours(rsPlanning.getDouble(3));
            }
            psPlanning.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkSchedule getSchedule(DayOfWeek dow) {
        return bufferedSchedules.get(dow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getDayLabelId(DayOfWeek dow) {
        return bufferedLabels.get(dow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDayLabel(DayOfWeek dow, long id) {
        bufferedLabels.put(dow, id);
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SCHEDULE SET ID_LABEL = ? WHERE ID_EMPLOYEE = ? AND NUM_DAY = ?;");
            ps.setLong(1, id);
            ps.setLong(2, idEmployee);
            ps.setByte(3, (byte) dow.getValue());
            ps.executeUpdate();
            ps.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }
}
