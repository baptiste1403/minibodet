package fr.lesaffrefreres.rh.minibodet.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SQLWorkSchedule implements WorkSchedule, SQLObject{

    private long idWorkDay;
    private int bufferedTotalHours;
    private int bufferedNightHours;
    private SQLObject parent;
    public SQLWorkSchedule(SQLObject so, long idwd) {
        idWorkDay = idwd;
        parent = so;
        updateBuffer();
    }


    public void updateBuffer() {
        if(idWorkDay < 0) {
            return;
        }
        Connection conn = DataBase.getInstance().getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT TOTAL_HOURS, NIGHT_HOURS FROM WORKDAY WHERE ID_WORK_DAY = ?;");
            ps.setLong(1, idWorkDay);

            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                bufferedTotalHours = rs.getInt(1);
                bufferedNightHours = rs.getInt(2);
            } else {
                ps.close();
                throw new IllegalArgumentException("given workday id doesn't exist in DB");
            }
            ps.close();
        } catch(SQLException se) {
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
                    "UPDATE WORKDAY SET NIGHT_HOURS = ? WHERE ID_WORK_DAY = ?;");
            ps.setInt(1, nh);
            ps.setLong(2, idWorkDay);
            ps.executeUpdate();
            ps.close();
            bufferedNightHours = nh;
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void setNightHours(int nh, boolean mustSave) {
        if(mustSave || idWorkDay >= 0) {
            setNightHours(nh);
        } else {
            bufferedNightHours = nh;
        }
    }

    @Override
    public void setTotalHours(int th) {
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
                    "UPDATE WORKDAY SET TOTAL_HOURS = ? WHERE ID_WORK_DAY = ?;");
            ps.setInt(1, th);
            ps.setLong(2, idWorkDay);
            ps.executeUpdate();
            ps.close();
            bufferedTotalHours = th;
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void setTotalHours(int th, boolean mustSave) {
        if(mustSave || idWorkDay >= 0) {
            setTotalHours(th);
        } else {
            bufferedTotalHours = th;
        }
    }

    @Override
    public long getId() {
        return idWorkDay;
    }

    @Override
    public long create() { // TODO create
        idWorkDay = parent.create();
        return idWorkDay;
    }
}
