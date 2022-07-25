package fr.lesaffrefreres.rh.minibodet.model;

import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Color;
import org.h2.store.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * This class represents a persistent implementation of a {@link WorkDay}.
 * This class is usually used by the SQLWorkCalendar to store the work days.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see WorkDay
 * @see SQLWorkCalendar
 */
public class SQLWorkDay extends SQLDay implements WorkDay, SQLObject, CalendarElement{

    private String bufferedComment;
    private SQLWorkSchedule schedule;

    private SQLWorkCalendar parent;

    /**
     * Creates a new work day by mapping it to a workday in the database with the given id.
     * @param calendar The calendar to which this work day belongs.
     * @param id The id of the work day in the database.
     */
    public SQLWorkDay(SQLWorkCalendar calendar, long id) {
        super(id);
        parent = calendar;
    }

    /**
     * Creates a new work day that is not yet in the database.
     * @param calendar The calendar to which this work day belongs.
     * @param date The date of the work day.
     * @param idc The id of the calendar in the database.
     * @param com The comment of the work day.
     */
    public SQLWorkDay(SQLWorkCalendar calendar, LocalDate date, long idc, String com) {
        super(date, idc);
        bufferedComment = com;
        schedule = new SQLWorkSchedule(this, -1);
        parent = calendar;
    }

    /**
     * update the buffered values of the work day by querying the database.
     * if the work day is not in the database, it does nothing.
     */
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
                    "SELECT COMMENT FROM WORKDAY WHERE ID_LABELDAY = ?;");
            ps.setLong(1, idDay);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                bufferedComment = rs.getString(1);
                ps.close();
            } else {
                throw new IllegalArgumentException("given id doesn't exist in DB");
            }
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Returns the work schedule of this work day.
     * @return The work schedule of this work day.
     */
    @Override
    public WorkSchedule getSchedule() {
        return schedule;
    }

    /**
     * Returns the comment of this work day.
     * @return The comment of this work day.
     */
    @Override
    public String getComment() {
        return bufferedComment;
    }

    /**
     * Sets the comment of this work day.
     * If the work day is not in the database, it is added to the database. by calling {@link #create()}.
     * @param com The comment of this work day.
     */
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
                    "UPDATE WORKDAY SET COMMENT = ? WHERE ID_LABELDAY = ?");
            if(bufferedComment.length() > 255) {
                bufferedComment = bufferedComment.substring(0, 255); // if the size is too big
            }
            ps.setString(1, bufferedComment);
            ps.setLong(2, idDay);
            ps.executeUpdate();
            ps.close();
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return idDay;
    }

    /**
     * This method is called when this work day is change and if it is not in the database and by other objects that need
     * this work day to be in the database before creating themselves.
     * It insert this work day in the database and return the id of this work day in the database.
     * @return The id of this work day in the database.
     */
    @Override
    public long create() {
        if(idDay >= 0) {
            return idDay;
        }
        idDay = super.create();
        Connection conn = DataBase.getInstance().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO WORKDAY (ID_LABELDAY, COMMENT, TOTAL_HOURS, NIGHT_HOURS) VALUES ( ?, ?, ?, ?)");
            ps.setLong(1, idDay);
            if(bufferedComment.length() > 255) {
                bufferedComment = bufferedComment.substring(0, 255); // if the size is too big
            }
            ps.setString(2, bufferedComment);
            ps.setDouble(3, schedule.getTotalHours());
            ps.setDouble(4, schedule.getNightHours());
            ps.execute();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        parent.createdDay(this);
        return idDay;
    }

    @Override
    public String toString() {
        return getSchedule().getTotalHours()+"";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setView(Label label) {
        super.setView(label);

        SQLEmployeeManager emp = SQLEmployeeManager.getInstance();
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        label.setBackground(new Background(new BackgroundFill(dlm.getDayLabelById(getLabelId()).getColor(), null, null)));

        if(getSchedule().getTotalHours() > 10.0) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.BLACK);
        }

        if(getSchedule().getTotalHours() > 0.0 && emp.getCalendar().getDay(getDate()).getLabelId() == dlm.getBankHolidayDayLabelId()) {
            label.setText(" " + getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()) + " : " + toString() + " (férié)");
        } else {
            label.setText(" " + getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()) + " : " + toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setViewOnSelected(Label label) {
        super.setViewOnSelected(label);

        SQLEmployeeManager emp = SQLEmployeeManager.getInstance();
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        label.setBackground(new Background(new BackgroundFill(dlm.getDayLabelById(getLabelId()).getColor().darker(), null, null)));

        if(getSchedule().getTotalHours() > 10.0) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.BLACK);
        }

        if(getSchedule().getTotalHours() > 0.0 && emp.getCalendar().getDay(getDate()).getLabelId() == dlm.getBankHolidayDayLabelId()) {
            label.setText(" " + getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()) + " : " + toString() + " (férié)");
        } else {
            label.setText(" " + getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()) + " : " + toString());
        }
    }
}
