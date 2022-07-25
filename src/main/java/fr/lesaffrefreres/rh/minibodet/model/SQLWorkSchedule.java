package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * This class represent a persistent implementation of the WorkSchedule as a work day schedule.
 * As the schedule for a planning and the schedule for a work day are not the same in the database,
 * there is also an other persistent implementation of the WorkSchedule used by the planning. {@link SQLPlanning}
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see SQLWorkDay
 */
public class SQLWorkSchedule implements WorkSchedule, SQLObject{

    private long idWorkDay;
    private double bufferedTotalHours;
    private double bufferedNightHours;
    private SQLObject parent;

    /**
     * Creates a new SQLWorkSchedule by mapping it to a work day in the database with the given id.
     * @param so the SQLObject parent of the schedule.
     * @param idwd the id of the work day in the database.
     */
    public SQLWorkSchedule(SQLObject so, long idwd) {
        idWorkDay = idwd;
        parent = so;
        updateBuffer();
    }


    /**
     * update the buffered values of the schedule by reading the database.
     */
    public void updateBuffer() {
        if(idWorkDay < 0) {
            return;
        }
        Connection conn = DataBase.getInstance().getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT TOTAL_HOURS, NIGHT_HOURS FROM WORKDAY WHERE ID_LABELDAY = ?;");
            ps.setLong(1, idWorkDay);

            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                bufferedTotalHours = rs.getDouble(1);
                bufferedNightHours = rs.getDouble(2);
            } else {
                ps.close();
                throw new IllegalArgumentException("given workday id doesn't exist in DB");
            }
            ps.close();
        } catch(SQLException se) {
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
     * change the night hours of the schedule. and create the parent work day in the database if it doesn't exist and if the given night hours change from the buffer. by calling the method {@link #create()}
     * @param nh the new night hours of the schedule.
     */
    @Override
    public void setNightHours(double nh) {
        if(nh == bufferedNightHours) {
            return;
        }
        bufferedNightHours = nh;
        if(idWorkDay < 0) {
            create();
            return;
        }

        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE WORKDAY SET NIGHT_HOURS = ? WHERE ID_LABELDAY = ?;");
            ps.setDouble(1, nh);
            ps.setLong(2, idWorkDay);
            ps.executeUpdate();
            ps.close();
            bufferedNightHours = nh;
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * change the night hours of the schedule. and create the parent work day in the database if it doesn't exist and
     * if the given night hours change from the buffer <strong>and if must save id true</strong>. by calling the method {@link #create()}
     * @param nh the new night hours of the schedule.
     */
    public void setNightHours(double nh, boolean mustSave) {
        if(mustSave || idWorkDay >= 0) {
            setNightHours(nh);
        } else {
            bufferedNightHours = nh;
        }
    }

    /**
     * change the total hours of the schedule. and create the parent work day in the database if it doesn't exist and if the given total hours change from the buffer. by calling the method {@link #create()}
     * @param th the new total hours of the schedule.
     */
    @Override
    public void setTotalHours(double th) {
        if(th == bufferedTotalHours) {
            return;
        }
        bufferedTotalHours = th;
        if(idWorkDay < 0) {
            create();
            return;
        }
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE WORKDAY SET TOTAL_HOURS = ? WHERE ID_LABELDAY = ?;");
            ps.setDouble(1, th);
            ps.setLong(2, idWorkDay);
            ps.executeUpdate();
            ps.close();
            bufferedTotalHours = th;
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * change the total hours of the schedule. and create the parent work day in the database if it doesn't exist and
     * if the given total hours change from the buffer <strong>and if must save id true</strong>. by calling the method {@link #create()}
     * @param th the new total hours of the schedule.
     */
    public void setTotalHours(double th, boolean mustSave) {
        if(mustSave || idWorkDay >= 0) {
            setTotalHours(th);
        } else {
            bufferedTotalHours = th;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return idWorkDay;
    }

    /**
     * This method is called when the schedule is changed,
     * It call the create methods of the parent workday and return the returned id.
     * As workday and workschedule are the same entity in the database, the id of the workday is the same as the id of the workschedule.
     * @return the id of the schedule in the database.
     */
    @Override
    public long create() { // TODO create
        idWorkDay = parent.create();
        return idWorkDay;
    }
}
