package fr.lesaffrefreres.rh.minibodet.model;

import org.h2.store.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.time.LocalDate;

public class SQLWorkDay extends SQLDay implements WorkDay, SQLObject{

    private String bufferedComment;
    private SQLWorkSchedule schedule;

    private SQLWorkCalendar parent;

    public SQLWorkDay(SQLWorkCalendar calendar, long id) {
        super(id);
        parent = calendar;
    }

    public SQLWorkDay(SQLWorkCalendar calendar, LocalDate date, long idc, String com) {
        super(date, idc);
        bufferedComment = com;
        schedule = new SQLWorkSchedule(this, -1);
        parent = calendar;
    }

    @Override
    public void updateBuffer() {
        if(idDay < 0) {
            return;
        }
        super.updateBuffer();
        schedule = new SQLWorkSchedule(this, idDay);

        schedule.updateBuffer();
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COMMENTARY FROM WORKDAY WHERE ID_WORK_DAY = ?;");
            ps.setLong(1, idDay);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                BufferedReader br = new BufferedReader(rs.getCharacterStream(1));
                StringBuilder sb = new StringBuilder();
                String buf;
                while((buf = br.readLine()) != null) {
                    sb.append(buf);
                }
                bufferedComment = sb.toString();
                br.close();
                ps.close();
            } else {
                throw new IllegalArgumentException("given id doesn't exist in DB");
            }
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public WorkSchedule getSchedule() {
        return schedule;
    }

    @Override
    public String getComment() {
        return bufferedComment;
    }

    @Override
    public WorkWeek getWeek() {
        return null; // TODO week
    }

    @Override
    public void setComment(String com) {
        bufferedComment = com;
        if(idDay < 0) {
            create();
            return;
        }
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE WORKDAY SET COMMENTARY = ? WHERE ID_WORK_DAY = ?");
            Reader reader = new StringReader(com);
            ps.setCharacterStream(1, reader);
            ps.setLong(2, idDay);
            ps.executeUpdate();
            ps.close();
        } catch(SQLException se) {
            se.printStackTrace();
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
        idDay = super.create();
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO WORKDAY (ID_WORK_DAY, COMMENTARY, ILLNESS_INFO, TOTAL_HOURS, NIGHT_HOURS) VALUES ( ?, ?, '', ?, ?)");
            ps.setLong(1, idDay);
            Reader reader = new StringReader(bufferedComment);
            ps.setCharacterStream(2, reader);
            ps.setInt(3, schedule.getTotalHours());
            ps.setInt(4, schedule.getNightHours());
            ps.execute();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        parent.createdDay(this);
        return idDay;
    }
}
